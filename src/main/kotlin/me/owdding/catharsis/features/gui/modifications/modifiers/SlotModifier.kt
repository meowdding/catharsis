package me.owdding.catharsis.features.gui.modifications.modifiers

import me.owdding.catharsis.utils.types.IntPredicate
import me.owdding.ktcodecs.GenerateCodec
import org.joml.Vector2i

@GenerateCodec
data class SlotModifier(
    val slot: IntPredicate,
    val hidden: Boolean = false,
    val highlightable: Boolean = true,
    val position: Vector2i?,
)
