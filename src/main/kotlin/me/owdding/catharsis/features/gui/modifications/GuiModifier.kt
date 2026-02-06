package me.owdding.catharsis.features.gui.modifications

//? >= 1.21.9
import com.mojang.blaze3d.platform.cursor.CursorTypes
import me.owdding.catharsis.features.gui.modifications.conditions.GuiModifierCondition
import me.owdding.catharsis.features.gui.modifications.elements.GuiElement
import me.owdding.catharsis.features.gui.modifications.elements.GuiElementRenderLayer
import me.owdding.catharsis.features.gui.modifications.elements.GuiWidgetElement
import me.owdding.catharsis.features.gui.modifications.modifiers.SlotModifier
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.resources.Identifier
import org.joml.Vector2i

@GenerateCodec
data class GuiModifier(
    val target: GuiModifierCondition,

    val overrideLabels: Boolean = false,
    val overrideBackground: Boolean = false,

    @NamedCodec("size") val bounds: Vector2i?,

    val slots: Map<Identifier, SlotModifier> = emptyMap(),
    val elements: List<GuiElement> = emptyList(),
    val widgets: List<GuiWidgetElement> = emptyList(),
) {

    private val elementsByLayer = (elements + widgets).groupBy { it.layer }

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
