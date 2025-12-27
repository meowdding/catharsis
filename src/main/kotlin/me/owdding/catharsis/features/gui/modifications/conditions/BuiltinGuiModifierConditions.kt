package me.owdding.catharsis.features.gui.modifications.conditions

import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.resources.Identifier

@GenerateCodec
data class GuiModifierDefinitionCondition(val definition: Identifier): GuiModifierCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiModifierDefinitionCondition>()
}
