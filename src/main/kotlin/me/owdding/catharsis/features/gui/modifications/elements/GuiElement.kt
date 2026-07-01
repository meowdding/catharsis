package me.owdding.catharsis.features.gui.modifications.elements

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.gui.modifications.elements.interactions.GuiWidgetInteraction
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component

enum class GuiElementRenderLayer {
    BACKGROUND,
    FOREGROUND,
}

enum class GuiElementAlignment {
    START,
    CENTER,
    END,
}

data class GuiElementPosition(
    val offset: Int,
    val alignment: GuiElementAlignment,
) {

    fun calculate(base: Int, size: Int): Int {
        return when (alignment) {
            GuiElementAlignment.START -> base + offset
            GuiElementAlignment.CENTER -> base + (size / 2) + offset
            GuiElementAlignment.END -> base + size + offset
        }
    }

    companion object {

        private val FULL_CODEC = RecordCodecBuilder.create {
            it.group(
                Codec.INT.optionalFieldOf("offset", 0).forGetter(GuiElementPosition::offset),
                CatharsisCodecs.getCodec<GuiElementAlignment>().fieldOf("alignment").forGetter(GuiElementPosition::alignment)
            ).apply(it, ::GuiElementPosition)
        }

        @IncludedCodec
        val CODEC: Codec<GuiElementPosition> = Codec.either(FULL_CODEC, Codec.INT).xmap(
            { it.map({ pos -> pos }, { offset -> GuiElementPosition(offset, GuiElementAlignment.START) }) },
            { Either.left(it) }
        )

        val START = GuiElementPosition(0, GuiElementAlignment.START)
    }
}

interface GuiElement {

    val codec: MapCodec<out GuiElement>
    val layer: GuiElementRenderLayer get() = GuiElementRenderLayer.FOREGROUND

    fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle)
}

interface GuiWidgetElement : GuiElement {

    val interaction: GuiWidgetInteraction
    val tooltip: List<GuiWidgetTooltip>?
    override val codec: MapCodec<out GuiWidgetElement>

    fun isHovered(mouseX: Int, mouseY: Int, bounds: ScreenRectangle): Boolean
    fun onClick(button: Int) = interaction.click(button)
    fun getParsedTooltip(): List<Component>? = tooltip?.mapNotNull { it.getTooltip(this) }?.flatten()
}
