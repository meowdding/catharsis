package me.owdding.catharsis.features.pack.config

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.JsonOps
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.PackResources
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.GsonHelper
import tech.thatgravyboat.skyblockapi.utils.text.Text

sealed interface PackConfigOption {

    val type: MapCodec<out PackConfigOption>

    val id: String? get() = null

    val asJson: JsonElement? get() = null

    fun addToDefault(json: JsonObject) {
        val id = id ?: return
        val value = asJson ?: return
        if (json.has(id)) Catharsis.error("Duplicate pack config option id '$id' found when generating default config!")
        json.add(id, value)
    }

    fun title(value: String?): Component
    fun description(value: String?): Component

    @GenerateCodec
    data class Separator(val title: Component = Component.empty(), val description: Component = Component.empty()) : PackConfigOption {
        override val type: MapCodec<out PackConfigOption> = CatharsisCodecs.getMapCodec<Separator>()
        override val id: String? = null
        override fun title(value: String?): Component = title
        override fun description(value: String?): Component = description
    }

    @GenerateCodec
    data class Information(val title: Component, val description: Component) : PackConfigOption {
        override val type: MapCodec<out PackConfigOption> = CatharsisCodecs.getMapCodec<Information>()
        override val id: String? = null
        override fun title(value: String?): Component = title
        override fun description(value: String?): Component = description
    }

    @GenerateCodec
    data class Bool(
        override val id: String,
        val title: Component,
        val description: Component = Component.empty(),
        val descriptions: Map<String, Component> = mapOf(),
        val default: Boolean = false,
    ) : PackConfigOption {

        override val type: MapCodec<out PackConfigOption> = CatharsisCodecs.getMapCodec<Bool>()
        override val asJson: JsonElement get() = JsonPrimitive(default)
        override fun title(value: String?): Component = title
        override fun description(value: String?): Component = descriptions[value] ?: description
    }

    @GenerateCodec
    data class Dropdown(
        override val id: String,
        val title: Component,
        val description: Component = Component.empty(),
        val descriptions: Map<String, Component> = mapOf(),
        val options: List<Entry>,
    ) : PackConfigOption {

        val default: Entry by lazy { this.options.first(Entry::default) }

        override val type: MapCodec<out PackConfigOption> = CODEC
        override val asJson: JsonElement get() = JsonPrimitive(default.value)
        override fun title(value: String?): Component = title
        override fun description(value: String?): Component = descriptions[value] ?: description

        @GenerateCodec
        data class Entry(val value: String, val text: Component, val default: Boolean = false)

        companion object {

            val CODEC: MapCodec<out PackConfigOption> = CatharsisCodecs.getMapCodec<Dropdown>().validate {
                val values = it.options.map(Entry::value).toSet()
                val defaults = it.options.filter(Entry::default)

                when {
                    values.size != it.options.size -> DataResult.error { "Dropdown values have duplicate values" }
                    defaults.size > 1 -> DataResult.error { "Dropdown has more than 1 default value" }
                    defaults.isEmpty() -> DataResult.error { "Dropdown must have 1 default value" }
                    else -> DataResult.success(it)
                }
            }
        }
    }

    @GenerateCodec
    data class Select(
        override val id: String,
        val title: Component,
        val description: Component = Component.empty(),
        val descriptions: Map<String, Component> = mapOf(),
        val options: List<SelectEntry>,
        val single: Boolean = false,
    ) : PackConfigOption {

        val default: List<SelectEntry> by lazy { this.options.filter(SelectEntry::selected) }

        override val type: MapCodec<out PackConfigOption> = CODEC
        override val asJson: JsonElement get() = if (single) {
            JsonPrimitive(default.firstOrNull()?.value ?: "")
        } else {
            JsonArray(default.size).also { array -> default.forEach { array.add(it.value) } }
        }
        override fun title(value: String?): Component = title
        override fun description(value: String?): Component = descriptions[value] ?: description

        @GenerateCodec
        data class SelectEntry(
            val value: String,
            val text: Either<Component, SelectEntryText>,
            val selected: Boolean = false
        ) {

            val selectedText: Component = text.map({ Text.join("> ", it) }, SelectEntryText::selected)
            val unselectedText: Component = text.map({ it }, SelectEntryText::unselected)
        }

        @GenerateCodec
        data class SelectEntryText(
            val unselected: Component,
            val selected: Component,
        )

        companion object {

            val CODEC: MapCodec<out PackConfigOption> = CatharsisCodecs.getMapCodec<Select>().validate {
                val values = it.options.map(SelectEntry::value).toSet()

                when {
                    values.size != it.options.size -> DataResult.error { "Select values have duplicate values" }
                    it.options.count(SelectEntry::selected) > 1 && it.single -> DataResult.error { "Single select cannot have more than 1 default value" }
                    else -> DataResult.success(it)
                }
            }
        }
    }

    @GenerateCodec
    data class Color(
        override val id: String,
        val title: Component,
        val description: Component,
        val default: Int = 0,
        val alpha: Boolean = false,
    ) : PackConfigOption {

        override val type: MapCodec<out PackConfigOption> = CatharsisCodecs.getMapCodec<Color>()
        override val asJson: JsonElement get() = JsonPrimitive(default)
        override fun title(value: String?): Component = title
        override fun description(value: String?): Component = description
    }

    @GenerateCodec
    data class Tab(
        val title: Component,
        val options: List<PackConfigOption>,
    ) : PackConfigOption {

        override val type: MapCodec<out PackConfigOption> = CODEC
        override val id: String? = null
        override fun title(value: String?): Component = title
        override fun description(value: String?): Component = Component.empty()

        override fun addToDefault(json: JsonObject) {
            for (option in options) {
                option.addToDefault(json)
            }
        }

        companion object {
            val CODEC: MapCodec<Tab> = CatharsisCodecs.getMapCodec<Tab>().validate {
                when {
                    it.options.filterIsInstance<Tab>().isNotEmpty() -> DataResult.error { "Tabs cannot contain other tabs" }
                    it.options.isEmpty() -> DataResult.error { "Tabs must contain at least one option inside" }
                    else -> DataResult.success(it)
                }
            }
        }
    }

    companion object {
        val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out PackConfigOption>>()

        @IncludedCodec
        val CODEC: MapCodec<PackConfigOption> = ID_MAPPER.codec(Codec.STRING).dispatchMap(PackConfigOption::type) { it }

        init {
            ID_MAPPER.put("separator", CatharsisCodecs.getMapCodec<Separator>())
            ID_MAPPER.put("information", CatharsisCodecs.getMapCodec<Information>())
            ID_MAPPER.put("boolean", CatharsisCodecs.getMapCodec<Bool>())
            ID_MAPPER.put("dropdown", Dropdown.CODEC)
            ID_MAPPER.put("select", Select.CODEC)
            ID_MAPPER.put("color", CatharsisCodecs.getMapCodec<Color>())
            ID_MAPPER.put("tab", Tab.CODEC)
        }

        @JvmStatic
        fun fromResource(resources: PackResources): List<PackConfigOption>? {
            return Catharsis.runCatching("Loading pack config options from resources") {
                resources.getRootResource("config.catharsis.json")?.get()?.use { stream ->
                    stream.reader().use { reader ->
                        CODEC.codec().listOf().parse(JsonOps.INSTANCE, GsonHelper.parseArray(reader))
                            .ifError { Catharsis.error("Failed to parse config for pack: $it") }
                            .result()
                            .orElse(null)
                    }
                }
            }
        }
    }
}
