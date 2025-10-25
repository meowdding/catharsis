package me.owdding.catharsis.features.dev

import com.mojang.brigadier.arguments.IntegerArgumentType
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.extensions.toReadableTime
import me.owdding.catharsis.utils.suggestion.IterableSuggestionProvider
import me.owdding.ktmodules.Module
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShapeRenderer
import net.minecraft.commands.arguments.ResourceKeyArgument
import net.minecraft.commands.arguments.UuidArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJsonOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.command
import java.util.*
import java.util.concurrent.CompletableFuture

@Module
object FloodFillSelect {

    enum class HighlightType {
        REGION,
        INDIVIDUAL,
        NONE,
    }

    data class Region(val blocks: List<BlockPos>, val aabb: BoundingBox, var highlightType: HighlightType = HighlightType.NONE) {
        constructor(blocks: List<BlockPos>) : this(blocks, BoundingBox.encapsulatingPositions(blocks).get())
    }

    private val finishedRegions = mutableMapOf<UUID, Region>()

    private val validBlocks = mutableSetOf<Block>()

    @Subscription
    private fun RegisterCommandsEvent.register() {
        register("catharsis dev area_select") {
            thenCallback("add block", ResourceKeyArgument.key(Registries.BLOCK)) {
                val resourceKey = argument<ResourceKey<Block>>("block")!!
                validBlocks.add(BuiltInRegistries.BLOCK.getValueOrThrow(resourceKey))
                Text.of {
                    append("Added ")
                    append(resourceKey.location().toString()) {
                        this.color = TextColor.GREEN
                    }
                    append(", new total is ${validBlocks.size}")
                }.sendWithPrefix()            }
            thenCallback("remove block", ResourceKeyArgument.key(Registries.BLOCK), IterableSuggestionProvider(validBlocks)) {
                val resourceKey = argument<ResourceKey<Block>>("block")!!
                validBlocks.remove(BuiltInRegistries.BLOCK.getValueOrThrow(resourceKey))
                Text.of {
                    append("Removed ")
                    append(resourceKey.location().toString()) {
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
            thenCallback("run range", IntegerArgumentType.integer(1)) {
                dispatch(argument("range")!!)
            }
            thenCallback("copy area uuid", UuidArgument.uuid(), IterableSuggestionProvider(finishedRegions.keys)) {
                val uuid = argument<UUID>("uuid")!!
                val box = finishedRegions[uuid]!!.aabb.toJsonOrThrow(BoundingBox.CODEC)
                McClient.clipboard = box.toPrettyString()
                Text.of("Copied final area to clipboard!").sendWithPrefix()
            }
            thenCallback("copy blocks uuid", UuidArgument.uuid(), IterableSuggestionProvider(finishedRegions.keys)) {
                val uuid = argument<UUID>("uuid")!!
                val blocks = finishedRegions[uuid]!!.blocks
                val blocksJson = blocks.toJsonOrThrow(BlockPos.CODEC.listOf())
                McClient.clipboard = blocksJson.toPrettyString()
                Text.of("Copied final ${blocks.size} blocks to clipboard!").sendWithPrefix()
            }
            thenCallback("outlines uuid", UuidArgument.uuid(), IterableSuggestionProvider(finishedRegions.keys)) {
                val uuid = argument<UUID>("uuid")!!
                val region = finishedRegions[uuid]!!
                if (region.highlightType == HighlightType.INDIVIDUAL) {
                    region.highlightType = HighlightType.NONE
                    Text.of("Disabled highlight for region!").sendWithPrefix()
                } else {
                    region.highlightType = HighlightType.INDIVIDUAL
                    Text.of("Enabled individual block highlight for region!").sendWithPrefix()
                }
            }
            thenCallback("highlight uuid", UuidArgument.uuid(), IterableSuggestionProvider(finishedRegions.keys)) {
                val uuid = argument<UUID>("uuid")!!
                val region = finishedRegions[uuid]!!
                if (region.highlightType == HighlightType.REGION) {
                    region.highlightType = HighlightType.NONE
                    Text.of("Disabled highlight for region!").sendWithPrefix()
                } else {
                    region.highlightType = HighlightType.REGION
                    Text.of("Enabled box highlight for region!").sendWithPrefix()
                }
            }
        }
    }

    private fun dispatch(range: Int = 100) {
        val hitResult = McClient.self.gameRenderer.pick(McClient.self.cameraEntity!!, 100.0, 100.0, 0f)
        if (hitResult !is BlockHitResult) {
            Text.of("Not targeting any blocks!").sendWithPrefix()
            return
        }
        val startBlock = hitResult.blockPos
        val blocks = validBlocks.toList()
        Text.of("Dispatching select with ${blocks.size} valid blocks!").sendWithPrefix()
        if (range > 100) {
            Text.of("Dispatched select range higher then 100 blocks, expect performance problems!")
        }
        val startedAt = currentInstant()
        CompletableFuture.runAsync {
            val blocks = floodFill(startBlock, range, blocks::contains)
            val time = startedAt.since().toReadableTime(maxUnits = 10, allowMs = true)
            McClient.runNextTick {
                if (blocks.isEmpty()) {
                    Text.of("Unable to find any blocks!").sendWithPrefix()
                    return@runNextTick
                }
                val key = UUID.randomUUID()
                finishedRegions[key] = Region(blocks)
                Text.of("Finished selecting ${blocks.size} in $time!") {
                    append(" [area]") {
                        this.color = TextColor.GREEN
                        this.command = "catharsis dev area_select copy area $key"
                    }
                    append(" [blocks]") {
                        this.color = TextColor.BLUE
                        this.command = "catharsis dev area_select copy blocks $key"
                    }
                    append(" [outline]") {
                        this.color = TextColor.GOLD
                        this.command = "catharsis dev area_select outlines $key"
                    }
                    append(" [highlight]") {
                        this.color = TextColor.AQUA
                        this.command = "catharsis dev area_select highlight $key"
                    }
                }.sendWithPrefix()
            }
        }
    }

    @Subscription
    private fun RenderWorldEvent.AfterTranslucent.render() = atCamera {
        finishedRegions.values.filterNot { it.highlightType == HighlightType.NONE }.forEach {
            if (it.highlightType == HighlightType.INDIVIDUAL) {
                ShapeRenderer.renderLineBox(poseStack.last(), buffer.getBuffer(RenderType.SECONDARY_BLOCK_OUTLINE), AABB.of(it.aabb), 1f, 1f, 1f, 1f)
            } else {
                it.blocks.forEach {
                    ShapeRenderer.renderLineBox(poseStack.last(), buffer.getBuffer(RenderType.SECONDARY_BLOCK_OUTLINE), AABB(it), 1f, 1f, 1f, 1f)
                }
            }
        }
    }

    private fun floodFill(center: BlockPos, radius: Int, filter: (Block) -> Boolean): List<BlockPos> {
        val queue = LinkedList<BlockPos>()

        queue.add(center.immutable())
        val positions = mutableListOf<BlockPos>()

        val directions = Direction.entries
        while (queue.isNotEmpty()) {
            val current = queue.pop()
            directions.forEach { direction ->
                val offset = current.relative(direction)
                if (positions.contains(offset)) return@forEach
                if (offset.distSqr(center) >= radius * radius) return@forEach
                val block = McLevel[offset]
                if (!filter(block.block)) return@forEach
                queue.add(offset)
                positions.add(offset)
            }
        }

        return positions
    }
}
