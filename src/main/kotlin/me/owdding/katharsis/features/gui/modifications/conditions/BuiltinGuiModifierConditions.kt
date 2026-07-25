package me.owdding.katharsis.features.gui.modifications.conditions

import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.resources.Identifier

@GenerateCodec
data class GuiModifierDefinitionCondition(val definition: Identifier): GuiModifierCondition {
    override val codec = KatharsisCodecs.getMapCodec<GuiModifierDefinitionCondition>()
}
