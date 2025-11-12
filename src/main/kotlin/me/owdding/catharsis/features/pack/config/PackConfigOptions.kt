package me.owdding.catharsis.features.pack.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.network.chat.Component
import net.minecraft.util.ExtraCodecs

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
        val default: Boolean = false
    ) : PackConfigOption {

        override val type: MapCodec<out PackConfigOption> = CatharsisCodecs.getMapCodec<Bool>()
        override val asJson: JsonElement get() = JsonPrimitive(default)
    }

    companion object {
        val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out PackConfigOption>>()

        @IncludedCodec
        val CODEC: MapCodec<PackConfigOption> = ID_MAPPER.codec(Codec.STRING).dispatchMap(PackConfigOption::type) { it }

        init {
            ID_MAPPER.put("separator", CatharsisCodecs.getMapCodec<Separator>())
            ID_MAPPER.put("boolean", CatharsisCodecs.getMapCodec<Bool>())
        }
    }
}
