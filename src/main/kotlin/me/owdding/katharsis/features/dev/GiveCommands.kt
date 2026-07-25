package me.owdding.katharsis.features.dev

import com.google.gson.JsonElement
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import me.owdding.katharsis.utils.SkyBlockIdentifierResolver
import me.owdding.katharsis.utils.extensions.sendSyncWithPrefix
import me.owdding.katharsis.utils.extensions.sendWithPrefix
import me.owdding.katharsis.utils.extensions.unsafeCast
import me.owdding.katharsis.utils.types.colors.CatppuccinColors
import me.owdding.katharsis.utils.types.commands.CommandFlag
import me.owdding.katharsis.utils.types.commands.FlagArgument
import me.owdding.katharsis.utils.types.commands.SkyBlockIdArgument
import me.owdding.katharsis.utils.types.suggestion.IterableSuggestionProvider
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.arguments.NbtTagArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.ItemContainerContents
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity
import net.minecraft.world.level.block.entity.SignBlockEntity
import net.minecraft.world.level.block.entity.SignText
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.impl.ColoredBlocks
import tech.thatgravyboat.skyblockapi.utils.builders.ItemBuilder
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getLore
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.extentions.toTitleCase
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick
import java.util.concurrent.CompletableFuture
import kotlin.math.max
import kotlin.math.min

@Module
// TODO: move into package
object GiveCommands {

    @Subscription
    private fun RegisterCommandsEvent.onRegister() {
        register("katharsis dev give item") {
            callback {
                val item = McClient.clipboard.readJson<JsonElement>().toData(ItemStack.CODEC)
                if (item == null) {
                    Text.of("Failed to read item from clipboard!", CatppuccinColors.Mocha.red).sendWithPrefix("katharsis-dev-give-failed-decode")
                    return@callback
                }
                tryGive(item)
            }

            val allIds = SimpleItemAPI.getAllIds()
            thenCallback("id id", SkyBlockIdArgument(allIds)) {
                val id = argument<SkyBlockId>("id")
                if (!allIds.contains(id)) {
                    Text.of("Unable to find item with id ") {
                        color = CatppuccinColors.Mocha.red
                        append(id.cleanId, CatppuccinColors.Mocha.peach)
                        append("!")
                    }.sendWithPrefix("katharsis-dev-give-not-found")
                    return@thenCallback
                }
                tryGive(id.toItem())
            }
            thenCallback("name name", StringArgumentType.greedyString(), IterableSuggestionProvider(SimpleItemAPI.getAllNames())) {
                val name = argument<String>("name")
                val id = SimpleItemAPI.findIdByName(name)
                if (id == null) {
                    Text.of("Unable to find item for name ") {
                        color = CatppuccinColors.Mocha.red
                        append(name, CatppuccinColors.Mocha.peach)
                        append("!")
                    }.sendWithPrefix("katharsis-dev-give-not-found")
                    return@thenCallback
                }
                tryGive(id.toItem())
            }
        }
        register("katharsis dev") {
            then("find names") {
                val nameCallback: CommandContext<FabricClientCommandSource>.() -> Unit = {
                    val flags = runCatching { argument<Map<FindFlag, Any>>("flags") }.getOrDefault(emptyMap())
                    val search = argument<String>("filter")
                    findBy(flags, search) { it.toItem().cleanName }
                }
                then("flags", FlagArgument.enum<FindFlag>()) {
                    thenCallback("filter", StringArgumentType.greedyString(), block = nameCallback)
                }
                thenCallback("filter", StringArgumentType.greedyString(), block = nameCallback)
            }

            then("find ids") {
                val idCallback: CommandContext<FabricClientCommandSource>.() -> Unit = {
                    val flags = runCatching { argument<Map<FindFlag, Any>>("flags") }.getOrDefault(emptyMap())
                    val search = argument<String>("filter")
                    findBy(flags, search) { it.id }
                }
                then("flags", FlagArgument.enum<FindFlag>()) {
                    thenCallback("filter", StringArgumentType.greedyString(), block = idCallback)
                }
                thenCallback("filter", StringArgumentType.greedyString(), block = idCallback)
            }
        }
    }

    fun findBy(flags: Map<FindFlag, Any>, search: String, converter: (SkyBlockId) -> String) {
        val caseInsensitive = !flags.containsKey(FindFlag.MATCH_CASE)
        val placeInWorld = flags.containsKey(FindFlag.PLACE_IN_WORLD)
        val give = flags.containsKey(FindFlag.GIVE) || placeInWorld
        val tag = flags[FindFlag.CUSTOM_DATA] as? Tag
        val searchType: (filter: String, element: String) -> Boolean = when {
            flags.containsKey(FindFlag.REGEX) -> { filter: String, element: String ->
                Regex(
                    filter,
                    buildSet {
                        if (caseInsensitive) add(RegexOption.IGNORE_CASE)
                    },
                ).matches(element)
            }

            flags.containsKey(FindFlag.STARTS_WITH) -> { filter: String, element: String -> element.startsWith(filter, ignoreCase = caseInsensitive) }
            flags.containsKey(FindFlag.ENDS_WITH) -> { filter: String, element: String -> element.endsWith(filter, ignoreCase = caseInsensitive) }
            else -> { filter: String, element: String -> element.contains(filter, ignoreCase = caseInsensitive) }
        }

        val limit = flags.getOrDefault(FindFlag.LIMIT, if (flags.containsKey(FindFlag.ALL)) Int.MAX_VALUE else 100).unsafeCast<Int>()

        CompletableFuture.runAsync {
            val items = SimpleItemAPI.getAllIds().filter {
                searchType(search, converter(it))
            }
            McClient.runNextTick {
                Text.of("Found ") {
                    color = CatppuccinColors.Mocha.green
                    append(items.size) {
                        color = CatppuccinColors.Mocha.peach
                    }
                    append(" items matching the search!")
                }.sendWithPrefix("katharsis-dev-find")
                if (tag !is CompoundTag) {
                    Text.of("Custom data isn't a compound tag, ignoring!", CatppuccinColors.Frappe.red).sendWithPrefix()
                }

                val limitedItems = items.distinct().take(limit).map {
                    ItemBuilder().apply {
                        val original = it.toItem()
                        copyFrom(original)
                        val data = original.get(DataComponents.CUSTOM_DATA)
                        val originTag = data?.copyTag() ?: CompoundTag()
                        set(
                            DataComponents.CUSTOM_DATA,
                            CustomData.of(
                                originTag.apply {
                                    (tag as? CompoundTag)?.forEach { key, value -> this.put(key, value) }
                                },
                            ),
                        )
                    }.build()
                }
                if (!give) {
                    limitedItems.forEachIndexed { index, stack ->
                        Text.of((index + 1).toFormattedString()) {
                            append(". ")
                            color = CatppuccinColors.Mocha.text
                            append(stack.hoverName) {
                                hover = Text.multiline(stack.getLore())
                                onClick { tryGive(stack) }
                            }
                            append(" [id]") {
                                color = CatppuccinColors.Macchiato.pink
                                onClick {
                                    val location = SkyBlockIdentifierResolver.getCustomLocation(stack)
                                    if (location == null) {
                                        Text.of("No model id for item!", CatppuccinColors.Frappe.red).sendWithPrefix("katharsis-dev-find-location-not-found")
                                        return@onClick
                                    }
                                    Text.of("Copied model id to clipboard!", CatppuccinColors.Frappe.yellow).sendWithPrefix("katharsis-dev-find-copied-location")
                                    McClient.clipboard = location.path
                                }
                            }
                        }.send("katharsis-find-result-$index")
                    }
                } else {
                    if (placeInWorld) {
                        fillAndPlaceShulkers(limitedItems)
                    } else if (limitedItems.size > 20) {
                        fillAndGiveShulkers(limitedItems)
                    } else {
                        limitedItems.forEach { tryGive(it) }
                    }
                }
            }
        }
    }

    fun fillAndPlaceShulkers(items: List<ItemStack>) {
        val itemSize = items.size
        var offset = 0

        val server = McClient.self.singleplayerServer ?: return Text.of("Not in singleplayer!", CatppuccinColors.Mocha.red).sendWithPrefix("katharsis-dev-give-no-singleplayer")
        server.submit {

            val overworld = server.getLevel(Level.OVERWORLD) ?: return@submit Text.of("No overworld found!", CatppuccinColors.Mocha.red).sendSyncWithPrefix("katharsis-dev-give-no-overworld")
            var itemsUsed = 0
            items.groupBy {
                it.cleanName.filterNot { it.isWhitespace() }.take(1).lowercase()
            }.entries.sortedBy { (key) -> key }.forEach { (_, value) ->
                var max = 0

                var localItemsUsed = 0
                value.groupBy {
                    it.cleanName.filterNot { it.isWhitespace() }.take(2).lowercase()
                }.entries.sortedBy { (key) -> key }.forEachIndexed { index, (key, value) ->
                    val boxes = value.sortedBy { it.cleanName.filterNot { it.isWhitespace() } }.chunked(27)

                    max = max(boxes.size, max)

                    val sign = BlockPos(offset, 0, index * 4)
                    overworld.setBlock(sign, Blocks.OAK_SIGN.defaultBlockState(), Block.UPDATE_CLIENTS, 0)
                    val signEntity = SignBlockEntity(sign, Blocks.OAK_SIGN.defaultBlockState())
                    overworld.setBlockEntity(signEntity)
                    SignText().setMessage(
                        1,
                        Text.of {
                            append((itemsUsed + localItemsUsed).toFormattedString())
                            append(" - ")
                            append((itemsUsed + localItemsUsed + value.size).toFormattedString())
                        },
                    ).setMessage(
                        2,
                        Text.of {
                            append("(")
                            append(value.size.toFormattedString())
                            append(")")
                        },
                    ).setMessage(0, Text.of(key.toTitleCase())).let {
                        signEntity.setText(it, true)
                        signEntity.setText(it, false)
                    }

                    boxes.forEachIndexed { boxIndex, items ->
                        val shulker = getShulkerColor(index + boxIndex).defaultBlockState()
                        val floor = getFloorColor(index + boxIndex).defaultBlockState()
                        val position = BlockPos(offset - boxIndex - 1, 0, index * 4)
                        val sign = position.offset(0, 0, -1)
                        val state = Blocks.OAK_WALL_SIGN.defaultBlockState()

                        overworld.setBlock(sign, state, Block.UPDATE_CLIENTS, 0)
                        val signEntity = SignBlockEntity(sign, state)
                        overworld.setBlockEntity(signEntity)

                        SignText().setMessage(
                            0,
                            Text.of {
                                append(items.first().cleanName.filterNot { it.isWhitespace() }.take(3).lowercase().toTitleCase())
                                append(" - ")
                                append(items.last().cleanName.filterNot { it.isWhitespace() }.take(3).lowercase().toTitleCase())
                            },
                        ).let {
                            signEntity.setText(it, true)
                            signEntity.setText(it, false)
                        }

                        overworld.setBlock(position, shulker, Block.UPDATE_CLIENTS, 0)
                        val shulkerBox = ShulkerBoxBlockEntity(position, shulker)
                        shulkerBox.name = Text.of("Items ${itemsUsed + localItemsUsed}-${itemsUsed + localItemsUsed + items.size}")
                        items.forEachIndexed { index, item ->
                            shulkerBox.setItem(index, item)
                        }
                        overworld.setBlockEntity(shulkerBox)

                        for (x in -1..(if (boxIndex == 0) 1 else 0)) {
                            for (y in -1..1) {
                                overworld.setBlock(position.offset(x, -1, y), floor, Block.UPDATE_CLIENTS, 0)
                            }
                        }

                        Text.of("Placed ") {
                            append("Items ${itemsUsed + localItemsUsed}-${itemsUsed + localItemsUsed + items.size}") {
                                color = CatppuccinColors.Mocha.peach
                            }
                            append(" into the world!")
                            color = CatppuccinColors.Frappe.green
                        }.sendSyncWithPrefix("katharsis-dev-give-placed")
                        localItemsUsed += items.size
                    }
                }

                itemsUsed += value.size
                offset -= max + 4
            }

            Text.of("Placed a total of $itemSize items!") {
                color = CatppuccinColors.Frappe.green
            }.sendSyncWithPrefix("katharsis-dev-give-placed-total")
        }
    }


    fun getFloorColor(index: Int): Block {
        return when ((index + 10) % 16) {
            0 -> ColoredBlocks.WHITE_WOOL
            1 -> ColoredBlocks.ORANGE_WOOL
            2 -> ColoredBlocks.MAGENTA_WOOL
            3 -> ColoredBlocks.LIGHT_BLUE_WOOL
            4 -> ColoredBlocks.YELLOW_WOOL
            5 -> ColoredBlocks.LIME_WOOL
            6 -> ColoredBlocks.PINK_WOOL
            7 -> ColoredBlocks.GRAY_WOOL
            8 -> ColoredBlocks.LIGHT_GRAY_WOOL
            9 -> ColoredBlocks.CYAN_WOOL
            10 -> ColoredBlocks.PURPLE_WOOL
            11 -> ColoredBlocks.BLUE_WOOL
            12 -> ColoredBlocks.BROWN_WOOL
            13 -> ColoredBlocks.GREEN_WOOL
            14 -> ColoredBlocks.RED_WOOL
            15 -> ColoredBlocks.BLACK_WOOL
            else -> TODO("no.")
        }
    }

    fun getShulkerColor(index: Int): Block {
        return when ((index + 10) % 16) {
            0 -> ColoredBlocks.WHITE_SHULKER_BOX
            1 -> ColoredBlocks.ORANGE_SHULKER_BOX
            2 -> ColoredBlocks.MAGENTA_SHULKER_BOX
            3 -> ColoredBlocks.LIGHT_BLUE_SHULKER_BOX
            4 -> ColoredBlocks.YELLOW_SHULKER_BOX
            5 -> ColoredBlocks.LIME_SHULKER_BOX
            6 -> ColoredBlocks.PINK_SHULKER_BOX
            7 -> ColoredBlocks.GRAY_SHULKER_BOX
            8 -> ColoredBlocks.LIGHT_GRAY_SHULKER_BOX
            9 -> ColoredBlocks.CYAN_SHULKER_BOX
            10 -> ColoredBlocks.PURPLE_SHULKER_BOX
            11 -> ColoredBlocks.BLUE_SHULKER_BOX
            12 -> ColoredBlocks.BROWN_SHULKER_BOX
            13 -> ColoredBlocks.GREEN_SHULKER_BOX
            14 -> ColoredBlocks.RED_SHULKER_BOX
            15 -> ColoredBlocks.BLACK_SHULKER_BOX
            else -> TODO("no.")
        }
    }

    fun fillAndGiveShulkers(items: List<ItemStack>) {
        val maxAmount = items.size
        items.chunked(27).mapIndexed { index, items ->
            getShulkerColor(index).asItem().defaultInstance.apply {
                set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items))
                set(
                    DataComponents.CUSTOM_NAME,
                    Text.of("Items ${index * 27}-${min((index + 1) * 27, maxAmount)}") {
                        italic = false
                    },
                )
            }
        }.forEach(::tryGive)
    }

    private const val OPERATOR_GROUP = "operator"
    private const val LIMIT_GROUP = "limits"

    enum class FindFlag(
        override val shortName: Char,
        longName: String?,
        override val flagType: ArgumentType<*>?,
        override val group: String?,
    ) : CommandFlag {
        REGEX('r'),
        CONTAINS('c'),
        STARTS_WITH('s'),
        ENDS_WITH('e'),
        MATCH_CASE('m', group = null),
        LIMIT('l', IntegerArgumentType.integer(0), LIMIT_GROUP),
        ALL('a', group = LIMIT_GROUP),
        GIVE('g', group = "GIVE"),
        CUSTOM_DATA('d', NbtTagArgument.nbtTag(), group = null),
        PLACE_IN_WORLD('p', group = "GIVE")
        ;

        override val longName = (longName ?: name).lowercase()

        constructor(shortName: Char, argumentType: ArgumentType<*>? = null, group: String? = OPERATOR_GROUP) : this(shortName, null, argumentType, group)
    }

    fun tryGive(itemStack: ItemStack) {
        val item = itemStack.copyWithCount(1)
        //? >= 26.2 {
        val isSinglePlayer = !McClient.self.isMultiplayerServer
        //?} else
        //val isSinglePlayer = McClient.self.isSingleplayer
        if (McPlayer.self?.gameMode()?.isCreative != true || !isSinglePlayer) {
            Text.of("Not in singleplayer and creative!", CatppuccinColors.Mocha.red).sendWithPrefix("katharsis-dev-give-singleplayer")
            return
        }
        Text.of("Added ") {
            append(item.hoverName) {
                color = CatppuccinColors.Mocha.peach
            }
            append(" to your inventory!")
            color = CatppuccinColors.Frappe.green
        }.sendWithPrefix("katharsis-dev-give-added-${item.getSkyBlockId() ?: item.cleanName}")

        val freeSlot = McClient.self.player?.inventory?.freeSlot ?: -1
        McClient.self.player?.inventory?.setItem(freeSlot, item)
        McClient.connection?.send(ServerboundSetCreativeModeSlotPacket(36 + freeSlot, item))
        McClient.self.player?.containerMenu?.broadcastChanges()
    }

}
