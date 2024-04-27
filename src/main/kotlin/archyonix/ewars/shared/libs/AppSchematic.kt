package archyonix.ewars.shared.libs

import dev.hypera.scaffolding.region.Region
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.Direction
import org.jglrxavpok.hephaistos.nbt.CompressedProcesser
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTException
import org.jglrxavpok.hephaistos.nbt.NBTReader
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import kotlin.math.round

class AppSchematic(
    private val direction: Direction
) {
    private val regionBlocks: MutableList<Region.Block> = ArrayList()
    private var width: Short = 0
    private var height: Short = 0
    private var length: Short = 0
    private var palette: MutableMap<Int, String> = HashMap()
    private lateinit var blocksData: ByteArray
    private var read = false

    @Throws(IOException::class, NBTException::class)
    fun read(inputStream: InputStream) {
        val reader = NBTReader(inputStream, CompressedProcesser.GZIP)
        this.read((reader.readNamed().second as NBTCompound))
        reader.close()
        inputStream.close()
    }

    @Throws(NBTException::class)
    fun read(nbtTag: NBTCompound) {
        readSizes(nbtTag)
        readBlockPalette(nbtTag)
        readBlocks()
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
    private fun readBlockPalette(nbtTag: NBTCompound) {
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
    }

    @Throws(NBTException::class)
    private fun readBlocks() {
        var index = 0
        var i = 0
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
                                x.toDouble(),
                                y.toDouble(),
                                z.toDouble()
                            ), stateId
                        )
                    )
                    break
                }
                ++i
            }
            ++index
        }
    }

    fun build(
        instance: Instance,
        position: Point,
        percent: Float = 1F,
        fill: Block? = null
    ): CompletableFuture<Region?> {
        return if (!read) {
            throw IllegalStateException("Schematic not read")
        } else {
            val future: CompletableFuture<Region?> = CompletableFuture()
            CompletableFuture.runAsync {
                val blockBatch = AbsoluteBlockBatch()
                val futures: MutableList<CompletableFuture<Void>> = ArrayList()
                var n = 1
                val blocks = regionBlocks
                    .filter { if (fill != null) true else Block.fromStateId(it.stateId) != Block.AIR }
                val limit = round(blocks.size * percent)
                val var6: Iterator<*> = blocks.iterator()
                while (var6.hasNext()) {
                    if (n++ > limit) break
                    val regionBlock =
                        var6.next() as Region.Block
                    val absoluteBlockPosition =
                        regionBlock.position().withDirection(direction, width).add(position)
                    val stateId = regionBlock.stateId()
                    val block =
                        Block.fromStateId(stateId)
                    if (block != null && (fill != null || block != Block.AIR)) {
                        futures.add(instance.loadOptionalChunk(absoluteBlockPosition).thenRun {
                            blockBatch.setBlock(
                                absoluteBlockPosition,
                                if (block != Block.AIR) fill ?: block else block
                            )
                        })
                    }
                }
                CompletableFuture.allOf(*futures.toTypedArray<CompletableFuture<*>?>())
                    .join()
                blockBatch.apply(
                    instance
                ) {
                    future.complete(
                        Region(
                            instance,
                            position,
                            position.add(width.toDouble(), height.toDouble(), length.toDouble())
                        )
                    )
                }
            }
            future
        }
    }

    private fun getBlock(input: String): Block? {
        val namespaceId = input.split("\\[".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        return Block.fromNamespaceId(namespaceId)
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