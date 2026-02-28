package me.owdding.catharsis.features.gui.modifications.modifiers

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.OptionalBoolean
import org.joml.Vector2i

@GenerateCodec
data class SlotModifier(
    @OptionalBoolean(false) val hidden: Boolean = false,
    @OptionalBoolean(true) val highlightable: Boolean = true,
    val position: Vector2i?,
    @OptionalBoolean(true) val clickable: Boolean = true,
)
