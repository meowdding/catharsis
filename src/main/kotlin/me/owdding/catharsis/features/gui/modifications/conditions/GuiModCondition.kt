package me.owdding.catharsis.features.gui.modifications.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs

interface GuiModCondition {

    val codec: MapCodec<out GuiModCondition>
}

object GuiModConditions {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out GuiModCondition>>()

    @IncludedCodec
    val CODEC: Codec<GuiModCondition> = ID_MAPPER.codec(ResourceLocation.CODEC).dispatch(GuiModCondition::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("definition"), CatharsisCodecs.getMapCodec<GuiModDefinitionCondition>())
    }

}
