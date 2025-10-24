package me.owdding.catharsis.features.gui.modifications.conditions

import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.resources.ResourceLocation

@GenerateCodec
data class GuiModDefinitionCondition(val definition: ResourceLocation): GuiModCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiModDefinitionCondition>()
}
