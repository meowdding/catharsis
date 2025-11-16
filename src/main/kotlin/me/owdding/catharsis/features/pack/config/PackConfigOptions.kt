package me.owdding.catharsis.features.pack.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.network.chat.Component
import net.minecraft.util.ExtraCodecs
import tech.thatgravyboat.skyblockapi.utils.text.CommonText

sealed interface PackConfigOption {

    val type: MapCodec<out PackConfigOption>

    val title: Component
    val description: Component
    val id: String? get() = null

    val asJson: JsonElement? get() = null

    @GenerateCodec
    data class Separator(override val title: Component, override val description: Component) : PackConfigOption {

        override val type: MapCodec<out PackConfigOption> = CatharsisCodecs.getMapCodec<Separator>()
        override val id: String? = null
    }

    @GenerateCodec
    data class Bool(
        override val id: String,
        override val title: Component,
        override val description: Component,
        val default: Boolean = false,
    ) : PackConfigOption {

        override val type: MapCodec<out PackConfigOption> = CatharsisCodecs.getMapCodec<Bool>()
        override val asJson: JsonElement get() = JsonPrimitive(default)
    }

    @GenerateCodec
    data class Dropdown(
        override val id: String,
        override val title: Component,
        override val description: Component,
        val options: List<Entry>,
    ) : PackConfigOption {

        val default: Entry by lazy { this.options.first(Entry::default) }

        override val type: MapCodec<out PackConfigOption> = CODEC
        override val asJson: JsonElement get() = JsonPrimitive(options.first { it.default }.value)

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
    data class Tab(
        override val title: Component,
        val options: List<PackConfigOption>,
    ) : PackConfigOption {

        override val type: MapCodec<out PackConfigOption> = CODEC
        override val id: String? = null
        override val description: Component = CommonText.EMPTY

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
            ID_MAPPER.put("boolean", CatharsisCodecs.getMapCodec<Bool>())
            ID_MAPPER.put("dropdown", Dropdown.CODEC)
            ID_MAPPER.put("tab", Tab.CODEC)
        }
    }
}
