package me.owdding.catharsis.features.dev

import com.google.gson.JsonElement
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import me.owdding.catharsis.utils.ItemUtils
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.extensions.unsafeCast
import me.owdding.catharsis.utils.types.colors.CatppuccinColors
import me.owdding.catharsis.utils.types.commands.CommandFlag
import me.owdding.catharsis.utils.types.commands.FlagArgument
import me.owdding.catharsis.utils.types.commands.SkyBlockIdArgument
import me.owdding.catharsis.utils.types.suggestion.IterableSuggestionProvider
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.core.component.DataComponents
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemContainerContents
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.getLore
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick
import java.util.concurrent.CompletableFuture
import kotlin.math.min


@Module
// TODO: move into package
object GiveCommands {

    @Subscription
    private fun RegisterCommandsEvent.onRegister() {
        register("catharsis dev give item") {
            callback {
                val item = McClient.clipboard.readJson<JsonElement>().toData(ItemStack.CODEC)
                if (item == null) {
                    Text.of("Failed to read item from clipboard!", CatppuccinColors.Mocha.red).sendWithPrefix("catharsis-dev-give-failed-decode")
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
                    }.sendWithPrefix("catharsis-dev-give-not-found")
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
                    }.sendWithPrefix("catharsis-dev-give-not-found")
                    return@thenCallback
                }
                tryGive(id.toItem())
            }
        }
        register("catharsis dev") {
            then("find names") {
                val nameCallback: CommandContext<FabricClientCommandSource>.() -> Unit = {
                    val flags = runCatching { argument<Map<FindFlag, Any>>("flags") }.getOrDefault(emptyMap())
                    val search = argument<String>("filter")
                    findBy(flags, search) { it.toItem().hoverName.stripped }
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
        val give = flags.containsKey(FindFlag.GIVE)
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
                }.sendWithPrefix("catharsis-dev-find")

                if (!give) {
                    items.take(limit).forEachIndexed { index, id ->
                        val stack = id.toItem()
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
                                    val location = ItemUtils.getCustomLocation(stack)
                                    if (location == null) {
                                        Text.of("No model id for item!", CatppuccinColors.Frappe.red).sendWithPrefix("catharsis-dev-find-location-not-found")
                                        return@onClick
                                    }
                                    Text.of("Copied model id to clipboard!", CatppuccinColors.Frappe.yellow).sendWithPrefix("catharsis-dev-find-copied-location")
                                    McClient.clipboard = location.path
                                }
                            }
                        }.send("catharsis-find-result-$index")
                    }
                } else {
                    val limitedItems = items.take(limit)
                    if (limitedItems.size > 20) {
                        fillAndGiveShulkers(limitedItems.map { it.toItem() })
                    } else {
                        limitedItems.forEach { tryGive(it.toItem()) }
                    }
                }
            }
        }
    }

    fun fillAndGiveShulkers(items: List<ItemStack>) {
        val maxAmount = items.size
        items.chunked(28).mapIndexed { index, items ->
            when ((index + 10) % 16) {
                0 -> Items.WHITE_SHULKER_BOX
                1 -> Items.ORANGE_SHULKER_BOX
                2 -> Items.MAGENTA_SHULKER_BOX
                3 -> Items.LIGHT_BLUE_SHULKER_BOX
                4 -> Items.YELLOW_SHULKER_BOX
                5 -> Items.LIME_SHULKER_BOX
                6 -> Items.PINK_SHULKER_BOX
                7 -> Items.GRAY_SHULKER_BOX
                8 -> Items.LIGHT_GRAY_SHULKER_BOX
                9 -> Items.CYAN_SHULKER_BOX
                10 -> Items.PURPLE_SHULKER_BOX
                11 -> Items.BLUE_SHULKER_BOX
                12 -> Items.BROWN_SHULKER_BOX
                13 -> Items.GREEN_SHULKER_BOX
                14 -> Items.RED_SHULKER_BOX
                15 -> Items.BLACK_SHULKER_BOX
                else -> TODO("no.")
            }.defaultInstance.apply {
                set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items))
                set(DataComponents.CUSTOM_NAME, Text.of("Items ${index * 27}-${min((index + 1) * 27, maxAmount)}") {
                    italic = false
                })
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
        GIVE('g', group = null)
        ;

        override val longName = (longName ?: name).lowercase()

        constructor(shortName: Char, argumentType: ArgumentType<*>? = null, group: String? = OPERATOR_GROUP) : this(shortName, null, argumentType, group)
    }

    fun tryGive(itemStack: ItemStack) {
        val item = itemStack.copyWithCount(1)
        if (McPlayer.self?.gameMode()?.isCreative != true && McClient.self.isSingleplayer) {
            Text.of("Not in singleplayer and creative!", CatppuccinColors.Mocha.red).sendWithPrefix("catharsis-dev-give-singleplayer")
            return
        }
        Text.of("Added ") {
            append(item.hoverName) {
                color = CatppuccinColors.Mocha.peach
            }
            append(" to your inventory!")
            color = CatppuccinColors.Frappe.green
        }.sendWithPrefix("catharsis-dev-give-added-${item.getSkyBlockId() ?: item.hoverName.stripped}")

        val freeSlot = McClient.self.player?.inventory?.freeSlot ?: -1
        McClient.self.player?.inventory?.setItem(freeSlot, item)
        McClient.connection?.send(ServerboundSetCreativeModeSlotPacket(36 + freeSlot, item))
        McClient.self.player?.containerMenu?.broadcastChanges()
    }

}
