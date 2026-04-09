package me.owdding.catharsis.features.pack.config

import me.owdding.catharsis.utils.HsbColor
import me.owdding.catharsis.utils.HsbState
import me.owdding.catharsis.utils.ui.BaseWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.util.CommonColors
import net.minecraft.util.Mth
import tech.thatgravyboat.skyblockapi.platform.drawFilledBox
import tech.thatgravyboat.skyblockapi.platform.drawGradient
import tech.thatgravyboat.skyblockapi.platform.drawOutline
import kotlin.math.min
import kotlin.math.round

class SaturationBrightnessSelector(width: Int, height: Int, val state: HsbState) : BaseWidget(0, 0, width, height) {

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        graphics.drawOutline(
            this.x - 1, this.y - 1,
            this.width + 2, this.height + 2,
            CommonColors.LIGHTER_GRAY
        )

        val color = state.color
        val posX = round(color.saturation * this.width).toInt()
        val posY = this.height - round(color.brightness * this.height).toInt()

        val tileWidth = round(this.width / 10f).toInt()
        val tileHeight = round(this.height / 10f).toInt()

        for (dy in 0 until 10) {
            val minB = dy / 10f
            val maxB = (dy + 1) / 10f
            for (dx in 0 until 10) {
                val minS = dx / 10f
                val maxS = (dx + 1) / 10f
                graphics.drawGradient(
                    this.x + dx * tileWidth, this.y + (10 - dy - 1) * tileHeight,
                    tileWidth, tileHeight,
                    state.color.toRgba(saturation = minS, brightness = maxB, alpha = 255),
                    state.color.toRgba(saturation = minS, brightness = minB, alpha = 255),
                    state.color.toRgba(saturation = maxS, brightness = minB, alpha = 255),
                    state.color.toRgba(saturation = maxS, brightness = maxB, alpha = 255),
                )
            }
        }

        graphics.drawOutline(
            this.x + posX - 1, this.y + posY - 1,
            3, 3,
            CommonColors.BLACK
        )
    }

    override fun mouseClicked(event: MouseButtonEvent, isDoubleClick: Boolean): Boolean {
        if (event.button() == 0 && this.isMouseOver(event.x, event.y)) {
            val localX = event.x - this.x
            val localY = event.y - this.y
            if (localX >= 0 && localX < this.width && localY >= 0 && localY < this.height) {
                state.color = state.color.copy(
                    saturation = Mth.clamp(localX.toFloat() / this.width.toFloat(), 0f, 1f),
                    brightness = 1f - Mth.clamp(localY.toFloat() / this.height.toFloat(), 0f, 1f)
                )
                return true
            }
        }
        return false
    }

    override fun mouseDragged(event: MouseButtonEvent, mouseX: Double, mouseY: Double): Boolean {
        return this.mouseClicked(event, false)
    }
}

class HueSelector(width: Int, height: Int, val state: HsbState) : BaseWidget(0, 0, width, height) {

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        graphics.drawOutline(
            this.x - 1, this.y - 1,
            this.width + 2, this.height + 2,
            CommonColors.LIGHTER_GRAY
        )

        val color = state.color

        for (pixel in 0 until this.width) {
            graphics.drawFilledBox(
                this.x + pixel, this.y,
                1, this.height,
                HsbColor.toRgba(hue = pixel.toFloat() / this.width.toFloat())
            )
        }

        graphics.drawOutline(
            this.x + round(color.hue * (this.width - 1)).toInt() - 1,
            this.y - 1,
            3, this.height + 2,
            CommonColors.BLACK
        )
    }

    override fun mouseClicked(event: MouseButtonEvent, isDoubleClick: Boolean): Boolean {
        if (event.button() == 0 && this.isMouseOver(event.x, event.y)) {
            val localX = event.x - this.x
            if (localX >= 0 && localX < this.width) {
                state.color = state.color.copy(hue = Mth.clamp(localX.toFloat() / this.width.toFloat(), 0f, 1f))
                return true
            }
        }
        return false
    }

    override fun mouseDragged(event: MouseButtonEvent, mouseX: Double, mouseY: Double): Boolean {
        return this.mouseClicked(event, false)
    }
}

class AlphaSelector(width: Int, height: Int, val state: HsbState) : BaseWidget(0, 0, width, height) {

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        graphics.drawOutline(
            this.x - 1, this.y - 1,
            this.width + 2, this.height + 2,
            CommonColors.LIGHTER_GRAY
        )

        val color = state.color

        val size = min(this.width, this.height) / 2
        for (dx in 0 until this.width step size) {
            for (dy in 0 until this.height step size) {
                val isEven = (dx / size + dy / size) % 2 == 0
                graphics.drawFilledBox(
                    this.x + dx, this.y + dy,
                    size, size,
                    if (isEven) CommonColors.LIGHT_GRAY else CommonColors.GRAY
                )
            }
        }

        for (pixel in 0 until this.width) {
            val alpha = Mth.clamp(pixel.toFloat() / this.width.toFloat(), 0f, 1f)
            graphics.drawFilledBox(
                this.x + pixel, this.y,
                1, this.height,
                this.state.color.toRgba(alpha = (alpha * 255).toInt())
            )
        }

        graphics.drawOutline(
            this.x + round(color.alpha / 255f * (this.width - 1)).toInt() - 1,
            this.y - 1,
            3, this.height + 2,
            CommonColors.BLACK
        )
    }

    override fun mouseClicked(event: MouseButtonEvent, isDoubleClick: Boolean): Boolean {
        if (event.button() == 0 && this.isMouseOver(event.x, event.y)) {
            val localX = event.x - this.x
            val alpha = Mth.clamp(localX.toFloat() / this.width.toFloat(), 0f, 1f)
            state.color = state.color.copy(alpha = Mth.ceil(alpha * 255))
            return true
        }
        return false
    }

    override fun mouseDragged(event: MouseButtonEvent, mouseX: Double, mouseY: Double): Boolean {
        return mouseClicked(event, false)
    }
}
