package me.owdding.catharsis.features.gui.modifications

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import me.owdding.catharsis.features.gui.modifications.conditions.GuiModCondition
import me.owdding.catharsis.features.gui.modifications.elements.GuiElement
import me.owdding.catharsis.features.gui.modifications.elements.GuiElementRenderLayer
import me.owdding.catharsis.features.gui.modifications.modifiers.SlotModifier
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.Util
import org.joml.Vector2i

@GenerateCodec
data class GuiModifier(
    val target: GuiModCondition,

    val overrideLabels: Boolean = false,
    val overrideBackground: Boolean = false,

    @NamedCodec("size") val bounds: Vector2i?,

    val slots: List<SlotModifier> = emptyList(),
    val elements: List<GuiElement> = emptyList(),
) {

    private val slotsById: Int2ObjectMap<SlotModifier> = Util.make(Int2ObjectArrayMap()) { map ->
        val overlappingSlots = mutableSetOf<Int>()
        for (modifier in this.slots) {
            for (slot in modifier.slot) {
                if (map.put(slot, modifier) != null) {
                    overlappingSlots.add(slot)
                }
            }
        }
        if (overlappingSlots.isNotEmpty()) {
            error("Overlapping slot modifiers for slots: ${overlappingSlots.joinToString(", ")}")
        }
    }

    private val elementsByLayer: Map<GuiElementRenderLayer, List<GuiElement>> = elements.groupBy { it.layer }

    fun getSlot(id: Int): SlotModifier? {
        return slotsById[id]
    }

    fun getElementsForLayer(layer: GuiElementRenderLayer): List<GuiElement> {
        return elementsByLayer[layer] ?: emptyList()
    }
}
