package me.owdding.catharsis.features.gui.modifications

import com.mojang.blaze3d.platform.cursor.CursorTypes
import com.mojang.serialization.Codec
import me.owdding.catharsis.features.gui.modifications.conditions.GuiModifierCondition
import me.owdding.catharsis.features.gui.modifications.elements.GuiElement
import me.owdding.catharsis.features.gui.modifications.elements.GuiElementRenderLayer
import me.owdding.catharsis.features.gui.modifications.elements.GuiWidgetElement
import me.owdding.catharsis.features.gui.modifications.elements.interactions.GuiNoOpWidgetInteraction
import me.owdding.catharsis.features.gui.modifications.modifiers.SlotModifier
import me.owdding.catharsis.utils.codecs.SavableData
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import me.owdding.ktcodecs.OptionalBoolean
import me.owdding.ktcodecs.OptionalIfEmpty
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.resources.Identifier
import org.joml.Vector2i
import tech.thatgravyboat.skyblockapi.helpers.McFont

@GenerateCodec
data class GuiModifierExclusionZone(val x: Int, val y: Int, val width: Int, val height: Int)

@GenerateCodec
data class GuiModifier(
    val target: GuiModifierCondition,

    @OptionalBoolean(false) val overrideLabels: Boolean = false,
    @OptionalBoolean(false) val overrideBackground: Boolean = false,
    @OptionalBoolean(false) val disableItemList: Boolean = false,

    @NamedCodec("size") val bounds: Vector2i?,

    @OptionalIfEmpty val itemListExclusionZones: List<GuiModifierExclusionZone> = emptyList(),
    @OptionalIfEmpty val slots: Map<Identifier, SlotModifier> = emptyMap(),
    @OptionalIfEmpty val elements: List<GuiElement> = emptyList(),
    @OptionalIfEmpty val widgets: List<GuiWidgetElement> = emptyList(),

    @OptionalIfEmpty val hiddenModElements: List<String> = emptyList(),
) : SavableData<GuiModifier> {
    override val codec: Codec<GuiModifier> get() = GuiModifiers.codec
    override fun toFileName(identifier: Identifier): Identifier = GuiModifiers.converter.idToFile(identifier)

    private val elementsByLayer = (elements + widgets).groupBy { it.layer }

    fun renderElements(layer: GuiElementRenderLayer, graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        val elements = elementsByLayer[layer] ?: return
        for (element in elements) {
            if (element.condition?.check() == false) continue
            if (element is GuiWidgetElement && element.isHovered(mouseX, mouseY, bounds) && element.interaction !is GuiNoOpWidgetInteraction) {
                graphics.requestCursor(CursorTypes.POINTING_HAND)
            }
            element.render(graphics, mouseX, mouseY, partialTicks, bounds)
        }
    }

    fun handleInteraction(x: Double, y: Double, button: Int, mouseDown: Boolean, bounds: ScreenRectangle): Boolean {
        for (element in widgets) {
            if (element.condition?.check() == false) continue
            if (element.isHovered(x.toInt(), y.toInt(), bounds)) {
                if (mouseDown) {
                    element.onClick(button)
                }
                return true
            }
        }
        return false
    }

    fun renderTooltips(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, bounds: ScreenRectangle): Boolean {
        for (widget in widgets) {
            if (widget.condition?.check() == false) continue
            if (widget.isHovered(mouseX, mouseY, bounds)) {
                val tooltip = widget.getParsedTooltip()
                if (!tooltip.isNullOrEmpty()) {
                    graphics.setComponentTooltipForNextFrame(McFont.self, tooltip, mouseX, mouseY)
                    return true
                }
            }
        }
        return false
    }
}
