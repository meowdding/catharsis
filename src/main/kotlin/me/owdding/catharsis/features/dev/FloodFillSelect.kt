@file:OptIn(ExperimentalCoroutinesApi::class)

package me.owdding.catharsis.features.dev

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.owdding.catharsis.utils.codecs.PosCodecs
import me.owdding.catharsis.utils.extensions.PREFIX
import me.owdding.catharsis.utils.extensions.identifier
import me.owdding.catharsis.utils.extensions.renderLineBox
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.extensions.toBlockPos
import me.owdding.catharsis.utils.extensions.toReadableTime
import me.owdding.catharsis.utils.extensions.toVector3i
import me.owdding.catharsis.utils.types.boundingboxes.BoundingBox
import me.owdding.catharsis.utils.types.commands.CommandFlag
import me.owdding.catharsis.utils.types.commands.FlagArgument
import me.owdding.catharsis.utils.types.suggestion.IterableSuggestionProvider
import me.owdding.ktmodules.Module
import net.minecraft.commands.arguments.ResourceKeyArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import org.joml.Vector3i
import org.joml.component1
import org.joml.component2
import org.joml.component3
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.platform.identifier
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJsonOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

@Module
object FloodFillSelect {

    data class CurrentJob(
        val job: Job,
        val totalBlocks: Long,
        val lastBlock: AtomicReference<BlockPos> = AtomicReference(null),
        val blocksChecked: AtomicLong = AtomicLong(0),
        val queued: AtomicInteger = AtomicInteger(0),
    ) {
        fun isRunning() = job.isActive
    }

    var job: CurrentJob? = null

    enum class HighlightType {
        REGION,
        INDIVIDUAL,
        NONE,
    }

    enum class SerializationFormat {
        RAW,
        JSON,
        ;
    }

    data class Region(val blocks: List<Vector3i>, val aabb: BoundingBox, var highlightType: HighlightType = HighlightType.NONE) {
        constructor(blocks: List<Vector3i>) : this(blocks, BoundingBox.encapsulatingVectors(blocks)!!)

        fun serialize(format: SerializationFormat): String = when (format) {
            SerializationFormat.RAW -> {
                val (min, max) = aabb
                val (minX, minY, minZ) = min
                val (maxX, maxY, maxZ) = max
                "$minX, $minY, $minZ : $maxX, $maxY, $maxZ"
            }

            SerializationFormat.JSON -> {
                aabb.toJsonOrThrow(BoundingBox.CODEC).toPrettyString()
            }
        }
    }

    private val finishedRegions = mutableMapOf<UUID, Region>()

    private val validBlocks = mutableSetOf<Block>()

    private const val STATE_GROUP = "state"
    private const val EXPORT_FORMAT_GROUP = "export_format"

    enum class FloodFillFlags(
        override val shortName: Char,
        longName: String?,
        override val flagType: ArgumentType<*>?,
        override val group: String?,
    ) : CommandFlag {
        DIAGONAL('d', group = null),
        EXPOSED('e'),
        COVERED('c'),
        JSON('j', group = EXPORT_FORMAT_GROUP),
        RAW('r', group = EXPORT_FORMAT_GROUP),
        OUTLINE('o', group = null),
        AUTO_DISPATCH('a', group = null),
        MAX_Y('t', argumentType = IntegerArgumentType.integer(), group = null),
        MIN_Y('b', argumentType = IntegerArgumentType.integer(), group = null),
        ;

        override val longName = (longName ?: name).lowercase()

        constructor(shortName: Char, argumentType: ArgumentType<*>? = null, group: String? = STATE_GROUP) : this(shortName, null, argumentType, group)
    }

    @Subscription
    private fun RegisterCommandsEvent.register() {
        register("catharsis dev area_select") {
            thenCallback("add block", ResourceKeyArgument.key(Registries.BLOCK)) {
                val resourceKey = argument<ResourceKey<Block>>("block")
                validBlocks.add(BuiltInRegistries.BLOCK.getValueOrThrow(resourceKey))
                Text.of {
                    append("Added ")
                    append(resourceKey.identifier.toString()) {
                        this.color = TextColor.GREEN
                    }
                    append(", new total is ${validBlocks.size}")
                }.sendWithPrefix()
            }
            thenCallback("remove block", ResourceKeyArgument.key(Registries.BLOCK), IterableSuggestionProvider(validBlocks) {
                it.identifier.toString()
            }) {
                val resourceKey = argument<ResourceKey<Block>>("block")
                validBlocks.remove(BuiltInRegistries.BLOCK.getValueOrThrow(resourceKey))
                Text.of {
                    append("Removed ")
                    append(resourceKey.identifier.toString()) {
                        this.color = TextColor.GREEN
                    }
                    append(", new total is ${validBlocks.size}")
                }.sendWithPrefix()
            }
            thenCallback("clear") {
                Text.of("Cleared block list!").sendWithPrefix()
                validBlocks.clear()
            }
            thenCallback("list") {
                Text.of("Currently allowing ${validBlocks.size} blocks!").sendWithPrefix()
                validBlocks.chunked(2).forEach {
                    Text.join(
                        it.map { block ->
                            Text.of(BuiltInRegistries.BLOCK.getKey(block).toString()) {
                                this.color = TextColor.GREEN
                            }
                        },
                        separator = Text.of(", ") {
                            this.color = TextColor.DARK_GRAY
                        },
                    ).send()
                }
            }
            thenCallback("run") { dispatch() }
            thenCallback("run flags", FlagArgument.enum<FloodFillFlags>()) { dispatch(map = argument("flags")) }
            then("run flags", FlagArgument.enum<FloodFillFlags>()) {
                thenCallback("range", IntegerArgumentType.integer(1)) {
                    dispatch(argument("range"), argument("flags"))
                }
            }
            thenCallback("run range", IntegerArgumentType.integer(1)) {
                dispatch(argument("range"))
            }
            thenCallback("cancel") {
                val job = job
                if (job == null || !job.isRunning()) {
                    Text.of("There is no active job!").sendWithPrefix()
                } else {
                    Text.of("Scheduled cancel!").sendWithPrefix()
                    job.job.cancel("Interrupted by user!")
                    job.job.invokeOnCompletion {
                        McClient.runNextTick {
                            Text.of("Successfully canceled!").sendWithPrefix()
                        }
                    }
                }
            }
        }
    }

    private fun dispatch(range: Int = 100, map: Map<FloodFillFlags, Any> = emptyMap()) {
        val job = job
        if (job != null && job.isRunning()) {
            Text.of("There is currently a job running!").sendWithPrefix()
            return
        }

        val hitResult = McClient.self.cameraEntity!!.pick(100.0, 1f, false)
        if (hitResult !is BlockHitResult) {
            Text.of("Not targeting any blocks!").sendWithPrefix()
            return
        }
        val mustBeExposed = map.containsKey(FloodFillFlags.EXPOSED)
        val mustBeCovered = map.containsKey(FloodFillFlags.COVERED)
        val includeDiagonals = map.containsKey(FloodFillFlags.DIAGONAL)
        val startBlock = hitResult.blockPos
        val blocks = validBlocks.toList()
        Text.of("Dispatching select with ${blocks.size} valid blocks!").sendWithPrefix()
        if (range > 100) {
            Text.of("Dispatched select range higher then 100 blocks, expect potential performance problems!").sendWithPrefix()
        }

        val top = map[FloodFillFlags.MAX_Y] as? Int? ?: 256
        val bottom = map[FloodFillFlags.MIN_Y] as? Int? ?: 0

        this.job = CurrentJob(
            CoroutineScope(Dispatchers.Default).launch {
                if (!map.containsKey(FloodFillFlags.AUTO_DISPATCH)) {
                    dispatchSingle(blocks, startBlock, range, includeDiagonals, mustBeCovered, mustBeExposed, top, bottom, map)
                } else {
                    dispatchMultiple(blocks, startBlock, range, includeDiagonals, mustBeCovered, mustBeExposed, top, bottom, map)
                }
            },
            if (!map.containsKey(FloodFillFlags.AUTO_DISPATCH)) {
                -1
            } else {
                (range.toLong() * 2 + 1) * (range.toLong() * 2 + 1) * (top - bottom)
            },
        )
    }

    private fun dispatchMultiple(
        block: List<Block>,
        startBlock: BlockPos,
        range: Int,
        includeDiagonals: Boolean,
        mustBeCovered: Boolean,
        mustBeExposed: Boolean,
        top: Int,
        bottom: Int,
        map: Map<FloodFillFlags, Any>,
    ) {
        val vec = Vec3i(range, range, range)
        val start = startBlock.offset(vec).atY(top)
        val end = startBlock.offset(vec.multiply(-1)).atY(bottom)
        val checkedBlocks = mutableSetOf<BlockPos>()
        val startedAt = currentInstant()
        val rawRegions = BlockPos.betweenClosed(start, end).mapNotNull {
            if (checkedBlocks.contains(it)) return@mapNotNull null
            this.job?.lastBlock?.set(it)
            dispatchSingle(block, it, range, includeDiagonals, mustBeCovered, mustBeExposed, top, bottom, checkedBlocks).takeUnless(List<*>::isEmpty)
        }
        val time = startedAt.since().toReadableTime(maxUnits = 10, allowMs = true)
        McClient.runNextTick {
            job = null
            if (rawRegions.isEmpty()) {
                Text.of("Unable to find any regions!").sendWithPrefix()
                return@runNextTick
            }
            val storeIndividual = checkedBlocks.size <= 1_000_000
            if (!storeIndividual) {
                Text.of("Not storing individual blocks due to it being over 1m!").sendWithPrefix()
            }
            val regions = rawRegions.associate {
                val blocks = it.map { block -> block.toVector3i() }
                UUID.randomUUID() to Region(
                    if (storeIndividual) blocks else emptyList(),
                    BoundingBox.encapsulatingVectors(blocks)!!,
                )
            }
            if (map.containsKey(FloodFillFlags.OUTLINE)) {
                regions.forEach { (_, region) -> region.highlightType = HighlightType.REGION }
            }
            finishedRegions.putAll(regions)
            Text.of("Finished collecting ${regions.size} regions and ${regions.map { (_, region) -> region.blocks.size }.sum()} blocks in $time!") {
                append(" [area]") {
                    this.color = TextColor.GREEN
                    onClick {
                        val format = if (map.containsKey(FloodFillFlags.RAW)) SerializationFormat.RAW else SerializationFormat.JSON
                        McClient.clipboard = regions.values.joinToString(if (format == SerializationFormat.RAW) "\n" else ",\n") {
                            it.serialize(format)
                        }
                        Text.of("Copied final areas to clipboard!").sendWithPrefix()
                    }
                }
                append(" [blocks]") {
                    this.color = TextColor.BLUE
                    onClick {
                        val blocks = regions.values.flatMap { it.blocks }
                        val serializedBlocks = if (map.containsKey(FloodFillFlags.RAW)) {
                            blocks.joinToString("\n") { (x, y, z) -> "$x, $y, $z" }
                        } else {
                            val blocksJson = blocks.toJsonOrThrow(PosCodecs.vector3icCodec.listOf())
                            blocksJson.toPrettyString()
                        }
                        McClient.clipboard = serializedBlocks
                        Text.of("Copied final ${blocks.size} blocks to clipboard!").sendWithPrefix()
                    }
                }
                append(" [outline]") {
                    this.color = TextColor.GOLD
                    onClick {
                        regions.values.forEachIndexed { index, region ->
                            if (region.highlightType == HighlightType.INDIVIDUAL) {
                                region.highlightType = HighlightType.NONE
                                if (index == 0) Text.of("Disabled highlight for regions!").sendWithPrefix()
                            } else {
                                region.highlightType = HighlightType.INDIVIDUAL
                                if (index == 0) Text.of("Enabled individual block highlight for regions!").sendWithPrefix()
                            }
                        }
                    }
                }
                append(" [highlight]") {
                    this.color = TextColor.AQUA
                    onClick {
                        regions.values.forEachIndexed { index, region ->
                            if (region.highlightType == HighlightType.REGION) {
                                region.highlightType = HighlightType.NONE
                                if (index == 0) Text.of("Disabled highlight for regions!").sendWithPrefix()
                            } else {
                                region.highlightType = HighlightType.REGION
                                if (index == 0) Text.of("Enabled box highlight for regions!").sendWithPrefix()
                            }
                        }
                    }
                }
            }.sendWithPrefix()
        }
    }

    private fun dispatchSingle(
        blocks: List<Block>,
        startBlock: BlockPos,
        range: Int,
        includeDiagonals: Boolean,
        mustBeCovered: Boolean,
        mustBeExposed: Boolean,
        maxY: Int,
        minY: Int,
        checkedBlocks: MutableSet<BlockPos> = mutableSetOf(),
    ): List<BlockPos> {
        return floodFill(startBlock, range, includeDiagonals, checkedBlocks) { pos, block ->
            if (pos.y > maxY || pos.y < minY) return@floodFill false

            if (block.block !in blocks) return@floodFill false

            if (mustBeCovered && directions.any { McLevel[pos.offset(it)].isAir }) return@floodFill false

            if (mustBeExposed && directions.none { McLevel[pos.offset(it)].isAir }) return@floodFill false

            true
        }
    }

    private fun dispatchSingle(
        blocks: List<Block>,
        startBlock: BlockPos,
        range: Int,
        includeDiagonals: Boolean,
        mustBeCovered: Boolean,
        mustBeExposed: Boolean,
        top: Int,
        bottom: Int,
        map: Map<FloodFillFlags, Any>,
    ) {
        val startedAt = currentInstant()
        val blocks = dispatchSingle(blocks, startBlock, range, includeDiagonals, mustBeCovered, mustBeExposed, top, bottom)
        val time = startedAt.since().toReadableTime(maxUnits = 10, allowMs = true)
        McClient.runNextTick {
            job = null
            if (blocks.isEmpty()) {
                Text.of("Unable to find any blocks!").sendWithPrefix()
                return@runNextTick
            }
            val key = UUID.randomUUID()
            val region = Region(blocks.map { it.toVector3i() })
            if (map.containsKey(FloodFillFlags.OUTLINE)) {
                region.highlightType = HighlightType.REGION
            }
            finishedRegions[key] = region
            Text.of("Finished selecting ${blocks.size} in $time!") {
                append(" [area]") {
                    this.color = TextColor.GREEN
                    onClick {
                        val format = if (map.containsKey(FloodFillFlags.RAW)) {
                            SerializationFormat.RAW
                        } else {
                            SerializationFormat.JSON
                        }
                        McClient.clipboard = region.serialize(format)
                        Text.of("Copied final area to clipboard!").sendWithPrefix()
                    }
                }
                append(" [blocks]") {
                    this.color = TextColor.BLUE
                    onClick {
                        val serializedBlocks = if (map.containsKey(FloodFillFlags.RAW)) {
                            region.blocks.joinToString("\n") { (x, y, z) -> "$x, $y, $z" }
                        } else {
                            val blocksJson = region.blocks.toJsonOrThrow(PosCodecs.vector3icCodec.listOf())
                            blocksJson.toPrettyString()
                        }
                        McClient.clipboard = serializedBlocks
                        Text.of("Copied final ${blocks.size} blocks to clipboard!").sendWithPrefix()
                    }
                }
                append(" [outline]") {
                    this.color = TextColor.GOLD
                    onClick {
                        if (region.highlightType == HighlightType.INDIVIDUAL) {
                            region.highlightType = HighlightType.NONE
                            Text.of("Disabled highlight for region!").sendWithPrefix()
                        } else {
                            region.highlightType = HighlightType.INDIVIDUAL
                            Text.of("Enabled individual block highlight for region!").sendWithPrefix()
                        }
                    }
                }
                append(" [highlight]") {
                    this.color = TextColor.AQUA
                    onClick {
                        if (region.highlightType == HighlightType.REGION) {
                            region.highlightType = HighlightType.NONE
                            Text.of("Disabled highlight for region!").sendWithPrefix()
                        } else {
                            region.highlightType = HighlightType.REGION
                            Text.of("Enabled box highlight for region!").sendWithPrefix()
                        }
                    }
                }
            }.sendWithPrefix()
        }
    }

    @Subscription
    private fun RenderHudEvent.render() {
        val job = job ?: return
        this.graphics.drawCenteredString(
            McFont.self,
            Text.of {
                append(PREFIX)
                append(" Flood fill progress: ${job.blocksChecked.toFormattedString()}/${job.totalBlocks.toFormattedString()} blocks found, ")
                append("${job.queued.toFormattedString()} positions queued")
            },
            graphics.guiWidth() / 2, graphics.guiHeight() / 8 * 7, -1,
        )
    }

    @Subscription
    private fun RenderWorldEvent.AfterTranslucent.render() = atCamera {
        val job = job
        job?.lastBlock?.get()?.let {
            this@render.renderLineBox(AABB(it), secondary = true)
        }

        finishedRegions.values.filterNot { it.highlightType == HighlightType.NONE }.forEach {
            if (it.highlightType == HighlightType.REGION) {
                this@render.renderLineBox(it.aabb.toMinecraftAABB(), secondary = true)
            } else {
                it.blocks.forEach {
                    this@render.renderLineBox(AABB(it.toBlockPos()), secondary = true)
                }
            }
        }
    }

    private val diagonals = buildList {
        for (x in -1..1) {
            for (y in -1..1) {
                for (z in -1..1) {
                    if (x == 0 && y == 0 && z == 0) continue
                    add(Vec3i(x, y, z))
                }
            }
        }
    }

    private val directions = Direction.entries.map { it.unitVec3i }

    private fun floodFill(
        center: BlockPos,
        radius: Int,
        includeDiagonals: Boolean = false,
        checkedBlocks: MutableSet<BlockPos> = mutableSetOf(),
        filter: (position: BlockPos, blockState: BlockState) -> Boolean,
    ): List<BlockPos> {
        val center = center.immutable()
        checkedBlocks.add(center)
        this.job?.lastBlock?.set(center)
        this.job?.blocksChecked?.set(checkedBlocks.size.toLong())
        if (!filter(center, McLevel[center])) {
            return emptyList()
        }

        val queue = LinkedList<BlockPos>()

        queue.add(center)
        val positions = mutableSetOf(center)

        val offsets = if (includeDiagonals) diagonals else directions
        while (queue.isNotEmpty()) {
            this.job?.queued?.set(queue.size)
            val current = queue.pop()
            offsets.forEach { direction ->
                val offset = current.offset(direction)
                this.job?.lastBlock?.set(offset)
                checkedBlocks.add(offset)
                this.job?.blocksChecked?.set(checkedBlocks.size.toLong())
                if (positions.contains(offset)) return@forEach
                if (offset.distSqr(center) >= radius * radius) return@forEach
                val block = McLevel[offset]
                if (!filter(offset, block)) return@forEach
                queue.add(offset)
                positions.add(offset)
            }

        }

        return positions.toList()
    }
}
