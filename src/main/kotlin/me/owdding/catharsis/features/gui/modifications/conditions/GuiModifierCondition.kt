package me.owdding.catharsis.features.gui.modifications.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs

interface GuiModifierCondition {

    val codec: MapCodec<out GuiModifierCondition>
}

object GuiModifierConditions {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out GuiModifierCondition>>()

    @IncludedCodec
    val CODEC: Codec<GuiModifierCondition> = ID_MAPPER.codec(Identifier.CODEC).dispatch(GuiModifierCondition::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("definition"), CatharsisCodecs.getMapCodec<GuiModifierDefinitionCondition>())
    }

}
