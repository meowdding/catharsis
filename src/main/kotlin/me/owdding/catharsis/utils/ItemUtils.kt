package me.owdding.catharsis.utils

import com.mojang.serialization.Codec
import me.owdding.catharsis.features.imc.ImcHandler.getCatharsisId
import me.owdding.catharsis.features.item.MiscItemModels
import me.owdding.catharsis.features.properties.DataTypeEntry
import me.owdding.catharsis.features.properties.DataTypeProperties
import me.owdding.catharsis.features.properties.NumbericalDataTypeEntry
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.extensions.unsafeCast
import me.owdding.catharsis.utils.types.colors.CatppuccinColors
import me.owdding.ktmodules.Module
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getDataTypes
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.RepoAttributeAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.UNKNOWN
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.builders.TooltipBuilder
import tech.thatgravyboat.skyblockapi.utils.extentions.get
import tech.thatgravyboat.skyblockapi.utils.extentions.toTitleCase
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick
import java.util.function.Function
import java.util.function.Predicate

typealias PropertyList<T> = List<ItemUtils.DataTypeProperty<T, out Any?>>
@Module
object ItemUtils {

    @Subscription
    private fun RegisterCommandsEvent.registerCommands() {
        registerWithCallback("catharsis dev hand_id") {
            val item = McPlayer.heldItem

            if (item.isEmpty) {
                Text.of("Not holding any item!").sendWithPrefix("catharsis-held-item-id-error")
            } else {
                val id = getCustomLocation(item)

                if (id == null) {
                    Text.of("Item has no custom id!").sendWithPrefix("catharsis-held-item-no-id")
                } else {
                    Text.of("Held item has id ") {
                        append(id.toString()) {
                            color = CatppuccinColors.Frappe.red
                        }
                        append("! ")

                        append("[COPY]") {
                            this.color = CatppuccinColors.Latte.lavender
                            onClick {
                                Text.of("Copied item id to clipboard!", CatppuccinColors.Frappe.yellow).sendWithPrefix("catharsis-held-item-copied-id")
                                McClient.clipboard = id.path
                            }
                        }
                    }.sendWithPrefix("catharsis-held-item-id-$id")
                }
            }
        }

        registerWithCallback("catharsis dev hand_data_types") {
            val item = McPlayer.heldItem

            if (item.isEmpty) {
                Text.of("Not holding any item!").sendWithPrefix("catharsis-held-item-id-error")
            } else {
                val properties: Collection<PropertyList<Any>> = listOf(
                    DataTypeProperties.conditionalTypes.values().map { DataTypeProperty(it, Function.identity(), Codec.BOOL, PropertyType.CONDITION) },
                    DataTypeProperties.numericalTypes.values().map { createConverter(it) },
                    DataTypeProperties.stringTypes.values().map { createConverter(it) },
                ).flatten().groupBy { it.dataType }.filterValues { it.isNotEmpty() }.values.unsafeCast()


                val dataTypeSize = properties.mapNotNull { entries ->
                    entries.size.takeIf { item.getDataTypes().containsKey(entries.first().dataType) }
                }.sum()
                Text.of("Item has $dataTypeSize data type${"s".takeUnless { dataTypeSize == 1 }.orEmpty()}") {
                    val entries = mutableListOf<String>()

                    if (dataTypeSize == 0) return@of
                    hover = TooltipBuilder().apply {
                        add {
                            append("id", CatppuccinColors.Mocha.mauve)
                            append(": ")
                            append("conditional", CatppuccinColors.Mocha.blue)
                            append("/")
                            append("range", CatppuccinColors.Mocha.peach)
                            append("/")
                            append("switch", CatppuccinColors.Mocha.green)
                        }
                        space()
                        properties.forEach { properties ->
                            entries.add(append(item, properties) ?: return@forEach)
                        }
                    }.build()

                    onClick {
                        Text.of("Copied item property data to clipboard!").sendWithPrefix("catharsis-hand-data-copy")
                        McClient.clipboard = buildString {
                            append("```")
                            append("$dataTypeSize data type${"s".takeUnless { dataTypeSize == 1 }.orEmpty()} present on item:")
                            append("\n")
                            append(entries.joinToString("\n\n"))
                            append("```")
                        }
                    }

                }.sendWithPrefix("catharsis-hand-data-types")
            }
        }
    }

    fun <T> TooltipBuilder.append(item: ItemStack, list: PropertyList<T>): String? = buildString {
        val type = list.first().dataType
        val dataValue = item[type] ?: return null

        add {
            append(type.id, CatppuccinColors.Mocha.mauve)
            append(": ")

            this@buildString.append(type.id).append("\n")
            val entries = mutableListOf<String>()
            append(
                Text.join(
                    list.map { (_, converter, codec, kind) ->
                        val value = converter.apply(dataValue)?.toJson(codec.unsafeCast())

                        entries += buildString {
                            append("- ").append(kind.name.toTitleCase()).append(": ").append(value.toPrettyString())
                        }

                        Text.of(value?.toString() ?: "null", when (kind) {
                            PropertyType.CONDITION -> CatppuccinColors.Mocha.blue
                            PropertyType.RANGE -> CatppuccinColors.Mocha.peach
                            PropertyType.SWITCH -> CatppuccinColors.Mocha.green
                        })
                    },
                    separator = Text.of("/"),
                ),
            )

            this@buildString.append(entries.joinToString("\n"))
        }
    }

    enum class PropertyType {
        SWITCH,
        RANGE,
        CONDITION
    }

    fun <Type, CompareType> createConverter(dataTypeEntry: DataTypeEntry<Type, CompareType>) = DataTypeProperty(dataTypeEntry.type, dataTypeEntry.converter, dataTypeEntry.codec, PropertyType.SWITCH)
    fun <Type, CompareType : Number> createConverter(dataTypeEntry: NumbericalDataTypeEntry<Type, CompareType>) =
        DataTypeProperty(dataTypeEntry.type, dataTypeEntry.converter, dataTypeEntry.codec, PropertyType.RANGE)

    data class DataTypeProperty<Type, CompareType>(val dataType: DataType<Type>, val converter: Function<Type, CompareType>, val codec: Codec<CompareType>, val type: PropertyType)

    fun getCustomLocation(item: ItemStack): Identifier? {
        val itemId = item[DataTypes.SKYBLOCK_ID] ?: return null

        if (itemId.isItem) {
            val path = itemId.cleanId.lowercase().takeIf { Identifier.isValidPath(it) } ?: return null
            return Identifier.tryBuild("skyblock", path)
        }

        return when {
            itemId.isPet -> resolvePet(itemId)
            itemId.isAttribute -> resolveAttribute(itemId)
            itemId.isRune -> resolveRune(itemId)
            itemId.isEnchantment -> resolveEnchantment(itemId)
            else -> null
        }
    }

    fun getHypixelLocation(item: ItemStack): Identifier? {
        val itemId = item[DataTypes.ID]?.lowercase() ?: return null
        return Identifier.tryBuild("skyblock", itemId)
    }

    @JvmStatic
    fun resolveModelId(predicate: Predicate<Identifier>, stack: ItemStack): Identifier? {
        val extraId = stack.getCatharsisId()
        if (extraId != null) {
            return extraId
        }

        val itemId = getCustomLocation(stack)
        if (itemId != null && predicate.test(itemId)) {
            return itemId
        }

        val hypixelId = getHypixelLocation(stack)
        if (hypixelId != null) {
            return hypixelId
        }

        return MiscItemModels.getModel(stack)
    }

    private fun SkyBlockId.cleanOrNull() = this.cleanId.lowercase().takeUnless { it == UNKNOWN }

    fun resolvePet(itemId: SkyBlockId): Identifier? {
        val cleanId = itemId.cleanOrNull() ?: return null

        return Identifier.tryBuild("skyblock", "pets/${cleanId.substringBefore(":").lowercase()}")
    }

    fun resolveEnchantment(itemId: SkyBlockId): Identifier? {
        val cleanId = itemId.cleanOrNull() ?: return null

        return Identifier.tryBuild("skyblock", "enchantments/${cleanId.substringBefore(":").lowercase()}")
    }

    fun resolveRune(itemId: SkyBlockId): Identifier? {
        val cleanId = itemId.cleanOrNull() ?: return null

        return Identifier.tryBuild("skyblock", "runes/${cleanId.substringBefore(":").lowercase()}")
    }

    fun resolveAttribute(itemId: SkyBlockId): Identifier? {
        val attributeId = itemId.cleanOrNull() ?: return null
        val data = RepoAttributeAPI.getAttributeDataById(attributeId) ?: return null
        return Identifier.tryBuild("skyblock", "attributes/${data.shardId.lowercase()}")
    }

}
