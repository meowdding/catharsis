package me.owdding.catharsis.features.gui.modifications.elements

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs

enum class GuiElementRenderLayer {
    BACKGROUND,
    FOREGROUND,
}

interface GuiElement {

    val codec: MapCodec<out GuiElement>
    val layer: GuiElementRenderLayer get() = GuiElementRenderLayer.FOREGROUND

    fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle)
}

object GuiElements {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out GuiElement>>()

    @IncludedCodec
    val CODEC: Codec<GuiElement> = ID_MAPPER.codec(ResourceLocation.CODEC).dispatch(GuiElement::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("player"), CatharsisCodecs.getMapCodec<GuiPlayerElement>())
        ID_MAPPER.put(Catharsis.id("sprite"), CatharsisCodecs.getMapCodec<GuiSpriteElement>())
        ID_MAPPER.put(Catharsis.id("text"), CatharsisCodecs.getMapCodec<GuiTextElement>())
    }
}
