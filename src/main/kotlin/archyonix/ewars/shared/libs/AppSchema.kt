package archyonix.ewars.shared.libs

import archyonix.ewars.entities.team.BannerRenderer
import archyonix.ewars.entities.team.GameTeam
import archyonix.ewars.game
import dev.hypera.scaffolding.region.Region
import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.block.Block
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import net.minestom.server.sound.SoundEvent
import net.minestom.server.utils.Direction
import org.jglrxavpok.hephaistos.nbt.CompressedProcesser
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTException
import org.jglrxavpok.hephaistos.nbt.NBTReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import kotlin.math.round

class AppSchema(path: String) {
    private var width: Short = 0
    private var height: Short = 0
    private var length: Short = 0
    private lateinit var blocksData: ByteArray
    private var read = false
    private lateinit var nbtTag: NBTCompound

    init {
        val inputStream = File(path).inputStream()
        read(inputStream)
        inputStream.close()
    }

    @Throws(IOException::class, NBTException::class)
    private fun read(inputStream: InputStream) {
        val reader = NBTReader(inputStream, CompressedProcesser.GZIP)
        this.read((reader.readNamed().second as NBTCompound))
        reader.close()
        inputStream.close()
    }

    @Throws(NBTException::class)
    private fun read(nbtTag: NBTCompound) {
        readSizes(nbtTag)
        this.nbtTag = nbtTag
        read = true
    }

    @Throws(NBTException::class)
    private fun readSizes(nbtTag: NBTCompound) {
        val width = nbtTag.getShort("Width")
        if (width == null) {
            throw NBTException("Invalid Schematic: No Width")
        } else {
            this.width = width
            val height = nbtTag.getShort("Height")
            if (height == null) {
                throw NBTException("Invalid Schematic: No Height")
            } else {
                this.height = height
                val length = nbtTag.getShort("Length")
                if (length == null) {
                    throw NBTException("Invalid Schematic: No Length")
                } else {
                    this.length = length
                }
            }
        }
    }

    @Throws(NBTException::class)
    private fun readBlockPalette(direction: Direction): Map<Int, String> {
        val palette: MutableMap<Int, String> = HashMap()
        val maxPalette = nbtTag.getInt("PaletteMax")
        if (maxPalette == null) {
            throw NBTException("Invalid Schematic: No PaletteMax")
        } else {
            val nbtPalette = nbtTag["Palette"] as NBTCompound?
            if (nbtPalette == null) {
                throw NBTException("Invalid Schematic: No Palette")
            } else {
                val keys: Set<String> = nbtPalette.keys
                if (keys.size != maxPalette) {
                    throw NBTException("Invalid Schematic: PaletteMax does not match Palette size")
                } else {
                    val var5: Iterator<*> = keys.iterator()
                    while (var5.hasNext()) {
                        val key = var5.next() as String
                        val value = nbtPalette.getInt(key)
                            ?: throw NBTException("Invalid Schematic: Palette contains invalid value")
                        palette[value] = key
                            .facingWithDir(direction)
                            .rotationWithDir(direction)
                            .withDirection(direction)
                    }
                    val blocksData = nbtTag.getByteArray("BlockData")
                    if (blocksData != null && blocksData.size != 0) {
                        this.blocksData = blocksData.copyArray()
                    } else {
                        throw NBTException("Invalid Schematic: No BlockData")
                    }
                }
            }
        }
        return palette
    }

    @Throws(NBTException::class)
    private fun readBlocks(direction: Direction): List<Region.Block> {
        val palette = readBlockPalette(direction)
        val regionBlocks: MutableList<Region.Block> = ArrayList()
        var i = 0
        var index = 0
        while (i < blocksData.size) {
            var value = 0
            var varIntLength = 0
            while (true) {
                value = value or (blocksData[i].toInt() and 127 shl varIntLength++ * 7)
                if (varIntLength > 5) {
                    throw NBTException("Invalid Schematic: BlockData has invalid length")
                }
                if (blocksData[i].toInt() and 128 != 128) {
                    ++i
                    val x = index % (width * length) % width
                    val y = index / (width * length)
                    val z = index % (width * length) / width
                    val stateId = getStateId(palette[value]!!)
                    regionBlocks.add(
                        Region.Block(
                            Pos(
                                x.toDouble(), y.toDouble(), z.toDouble()
                            ), stateId
                        )
                    )
                    break
                }
                ++i
            }
            ++index
        }
        return regionBlocks
    }

    fun build(
        instance: Instance,
        direction: Direction,
        pos: Point,
        team: GameTeam? = null,
        percentS: Float = 0F,
        percentE: Float = 1F,
        useEffects: Boolean = false
    ): CompletableFuture<Region?> {
        return if (!read) {
            throw IllegalStateException("Schematic not read")
        } else {
            val future: CompletableFuture<Region?> = CompletableFuture()
            CompletableFuture.runAsync {
                val blockBatch = AbsoluteBlockBatch()
                val bannerRenderer = BannerRenderer(team)
                val futures: MutableList<CompletableFuture<Void>> = ArrayList()
                val blocks = readBlocks(direction).filter { Block.fromStateId(it.stateId) != Block.AIR }

                var index = -1
                val startIndex = round(blocks.size * percentS)
                val endIndex = round(blocks.size * percentE)

                val iterator = blocks.iterator()
                while (iterator.hasNext()) {
                    index++
                    val regionBlock = iterator.next()
                    if (index < startIndex) continue
                    if (index >= endIndex) continue
                    val blockPosition = regionBlock.position().withDirection(direction, width).add(pos)
                    val blockState = Block.fromStateId(regionBlock.stateId)
                    if (blockState != null) {
                        futures.add(instance.loadOptionalChunk(blockPosition).thenRun {
                            if (useEffects) showEffect(blockPosition)
                            val block = bannerRenderer.render(blockState)
                            blockBatch.setBlock(blockPosition, block)
                        })
                    }
                }
                CompletableFuture.allOf(*futures.toTypedArray<CompletableFuture<*>?>()).join()
                blockBatch.apply(
                    instance
                ) {
                    future.complete(
                        Region(
                            instance, pos, pos.add(width.toDouble(), height.toDouble(), length.toDouble())
                        )
                    )
                }
            }
            future
        }
    }

    fun fillWithOutAir(
        instance: Instance,
        pos: Point,
        direction: Direction,
        fillingBlock: Block,
    ): CompletableFuture<Region?> {
        return if (!read) {
            throw IllegalStateException("Schematic not read")
        } else {
            val future: CompletableFuture<Region?> = CompletableFuture()
            CompletableFuture.runAsync {
                val blockBatch = AbsoluteBlockBatch()
                val futures: MutableList<CompletableFuture<Void>> = ArrayList()
                val blocks = readBlocks(direction).filter { Block.fromStateId(it.stateId) != Block.AIR }

                val iterator = blocks.iterator()
                while (iterator.hasNext()) {
                    val regionBlock = iterator.next()
                    val blockPosition = regionBlock.position().withDirection(direction, width).add(pos)
                    futures.add(instance.loadOptionalChunk(blockPosition).thenRun {
                        blockBatch.setBlock(blockPosition, fillingBlock)
                    })
                }
                CompletableFuture.allOf(*futures.toTypedArray<CompletableFuture<*>?>()).join()
                blockBatch.apply(
                    instance
                ) {
                    future.complete(
                        Region(
                            instance, pos, pos.add(width.toDouble(), height.toDouble(), length.toDouble())
                        )
                    )
                }
            }
            future
        }
    }

    private fun showEffect(position: Point) {
        val sound = Sound.sound(SoundEvent.BLOCK_WOOD_PLACE, Sound.Source.BLOCK, 1f, 1f)
        val particlePacket = ParticlePacket(
            Particle.CLOUD.id(),
            false,
            position.blockX() + .5,
            position.blockY() + .5,
            position.blockZ() + .5,
            .5f,
            .5f,
            .5f,
            0f,
            4,
            ByteArray(0)
        )
        for (player in game.getPlayers()) {
            player.sendPacket(particlePacket)
            player.playSound(sound, position)
        }
    }

    private fun getBlock(input: String): Block? {
        val namespaceId = input.split("\\[".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        return Block.fromNamespaceId(namespaceId)
    }

    fun getBlockFromPalette(id: Int, direction: Direction): Block {
        return Block.fromStateId(getStateId(readBlockPalette(direction)[id]!!))!!
    }

    private fun getStateId(input: String): Short {
        val block = getBlock(input)
        return if (block == null) {
            0
        } else {
            val states = input.replace(block.name().toRegex(), "")
            if (!states.startsWith("[")) {
                block.stateId()
            } else {
                val stateArray =
                    states.substring(1, states.length - 1).split(",".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val properties: MutableMap<String, String> = HashMap(block.properties())
                val var7 = stateArray.size
                for (var8 in 0 until var7) {
                    val state = stateArray[var8]
                    val split = state.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    properties.replace(split[0], split[1])
                }
                try {
                    block.withProperties(properties).stateId()
                } catch (var11: Exception) {
                    var11.printStackTrace()
                    block.stateId()
                }
            }
        }
    }
}