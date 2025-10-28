package me.owdding.catharsis.features.gui.modifications

//? >= 1.21.9
import com.mojang.blaze3d.platform.cursor.CursorTypes
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import me.owdding.catharsis.features.gui.modifications.conditions.GuiModCondition
import me.owdding.catharsis.features.gui.modifications.elements.GuiElement
import me.owdding.catharsis.features.gui.modifications.elements.GuiElementRenderLayer
import me.owdding.catharsis.features.gui.modifications.elements.GuiWidgetElement
import me.owdding.catharsis.features.gui.modifications.modifiers.SlotModifier
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.Util
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import org.joml.Vector2i

@GenerateCodec
data class GuiModifier(
    val target: GuiModCondition,

    val overrideLabels: Boolean = false,
    val overrideBackground: Boolean = false,

    @NamedCodec("size") val bounds: Vector2i?,

    val slots: List<SlotModifier> = emptyList(),
    val elements: List<GuiElement> = emptyList(),
    val widgets: List<GuiWidgetElement> = emptyList(),
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

    private val elementsByLayer = (elements + widgets).groupBy { it.layer }

    fun getSlot(id: Int): SlotModifier? {
        return slotsById[id]
    }

    fun renderElements(layer: GuiElementRenderLayer, graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        val elements = elementsByLayer[layer] ?: return
        for (element in elements) {
            if (element is GuiWidgetElement && element.isHovered(mouseX, mouseY, bounds)) {
                //? >= 1.21.9
                graphics.requestCursor(CursorTypes.POINTING_HAND)
            }
            element.render(graphics, mouseX, mouseY, partialTicks, bounds)
        }
    }

    fun handleInteraction(x: Double, y: Double, button: Int, mouseDown: Boolean, bounds: ScreenRectangle): Boolean {
        for (element in widgets) {
            if (element.isHovered(x.toInt(), y.toInt(), bounds)) {
                if (mouseDown) {
                    element.onClick(button)
                }
                return true
            }
        }
        return false
    }
}
