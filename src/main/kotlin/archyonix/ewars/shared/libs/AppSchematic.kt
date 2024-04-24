package archyonix.ewars.shared.libs

import dev.hypera.scaffolding.region.Region
import dev.hypera.scaffolding.schematic.Schematic
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.block.Block
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTException
import java.io.OutputStream
import java.util.concurrent.CompletableFuture

class AppSchematic : Schematic {
    private val regionBlocks: MutableList<Region.Block> = ArrayList()
    private var width: Short = 0
    private var height: Short = 0
    private var length: Short = 0
    private var palette: MutableMap<String, Int> = HashMap()
    private lateinit var blocksData: ByteArray
    private var read = false
    private var offsetX = 0
    private var offsetY = 0
    private var offsetZ = 0

    @Throws(NBTException::class)
    override fun read(nbtTag: NBTCompound) {
        readSizes(nbtTag)
        readBlockPalette(nbtTag)
        readOffsets(nbtTag)
        readBlocks()
        read = true
    }

    @Throws(NBTException::class)
    private fun readOffsets(nbtTag: NBTCompound) {
        val metaData = nbtTag.getCompound("Metadata")
        if (metaData == null) {
            throw NBTException("Invalid Schematic: No Metadata")
        } else {
            val weOffsetX = metaData.getInt("WEOffsetX")
            if (weOffsetX == null) {
                throw NBTException("Invalid Schematic: No WEOffsetX In Metadata")
            } else {
                offsetX = weOffsetX
                val weOffsetY = metaData.getInt("WEOffsetY")
                if (weOffsetY == null) {
                    throw NBTException("Invalid Schematic: No WEOffsetY In Metadata")
                } else {
                    offsetY = weOffsetY
                    val weOffsetZ = metaData.getInt("WEOffsetZ")
                    if (weOffsetZ == null) {
                        throw NBTException("Invalid Schematic: No WEOffsetZ In Metadata")
                    } else {
                        offsetZ = weOffsetZ
                    }
                }
            }
        }
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
                        palette[key] = value
                    }
                    palette = palette.entries.stream().sorted(java.util.Map.Entry.comparingByValue()).collect(
                        { LinkedHashMap() },
                        { map: LinkedHashMap<String, Int>, (key, value): Map.Entry<String, Int> ->
                            map[key] = value
                        },
                        { obj: LinkedHashMap<String, Int>, m: LinkedHashMap<String, Int>? ->
                            obj.putAll(
                                m!!
                            )
                        })
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
        val paletteKeys: List<String?> = ArrayList(palette.keys)
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
                    val stateId = getStateId(paletteKeys[value]!!)
                    regionBlocks.add(
                        Region.Block(
                            Pos(
                                (x + offsetX).toDouble(),
                                (y + offsetY).toDouble(),
                                (z + offsetZ).toDouble()
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

    override fun write(outputStream: OutputStream, region: Region) {}

    override fun build(instance: Instance, position: Point): CompletableFuture<Region?> {
        return if (!read) {
            throw IllegalStateException("Schematic not read")
        } else {
            val future: CompletableFuture<Region?> = CompletableFuture()
            CompletableFuture.runAsync {
                val blockBatch = AbsoluteBlockBatch()
                val futures: MutableList<CompletableFuture<Void>> = ArrayList()
                val var6: Iterator<*> = regionBlocks.iterator()
                while (var6.hasNext()) {
                    val regionBlock =
                        var6.next() as Region.Block
                    val absoluteBlockPosition =
                        regionBlock.position().add(position)
                    val stateId = regionBlock.stateId()
                    val block =
                        Block.fromStateId(stateId)
                    if (block != null) {
                        futures.add(instance.loadOptionalChunk(absoluteBlockPosition).thenRun {
                            blockBatch.setBlock(
                                absoluteBlockPosition,
                                block
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

    fun blocks(): Map<String, Region.Block> {
        return if (!read) {
            throw IllegalStateException("Schematic not read")
        } else {
            val blocks: MutableMap<String, Region.Block> = HashMap()
            val var6: Iterator<*> = regionBlocks.iterator()
            while (var6.hasNext()) {
                val regionBlock = var6.next() as Region.Block
                val pos = regionBlock.position.sub(offsetX.toDouble(), offsetY.toDouble(), offsetZ.toDouble())
                blocks["${pos.blockX()}:${pos.blockY()}:${pos.blockZ()}"] = regionBlock
            }
            blocks
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

    override fun getWidth(): Short {
        return width
    }

    override fun getHeight(): Short {
        return height
    }

    override fun getLength(): Short {
        return length
    }

    override fun getOffsetX(): Int {
        return offsetX
    }

    override fun getOffsetY(): Int {
        return offsetY
    }

    override fun getOffsetZ(): Int {
        return offsetZ
    }

    override fun apply(setter: Block.Setter) {
        val var2: Iterator<*> = regionBlocks.iterator()
        while (var2.hasNext()) {
            val block = var2.next() as Region.Block
            val pos = block.position()
            val minestomBlock = Block.fromStateId(block.stateId())
                ?: throw IllegalStateException("Invalid block state id: " + block.stateId())
            setter.setBlock(pos, minestomBlock)
        }
    }
}