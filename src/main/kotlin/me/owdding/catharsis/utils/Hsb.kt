package me.owdding.catharsis.utils

import net.minecraft.util.ARGB
import net.minecraft.util.Mth
import kotlin.math.max
import kotlin.math.min

data class HsbColor(
    val hue: Float,
    val saturation: Float,
    val brightness: Float,
    val alpha: Int,
) {

    fun toRgba(
        hue: Float = this.hue,
        saturation: Float = this.saturation,
        brightness: Float = this.brightness,
        alpha: Int = this.alpha
    ): Int {
        return HsbColor.toRgba(hue, saturation, brightness, alpha)
    }

    companion object {

        fun toRgba(hue: Float = 1f, saturation: Float = 1f, brightness: Float = 1f, alpha: Int = 255): Int {
            return Mth.hsvToArgb(hue, saturation, brightness, alpha)
        }

        fun fromRgba(rgba: Int): HsbColor {
            val r = ARGB.red(rgba)
            val g = ARGB.green(rgba)
            val b = ARGB.blue(rgba)
            val a = ARGB.alpha(rgba)

            val cmax = max(max(r, g), b)
            val cmin = min(min(r, g), b)

            val brightness = cmax.toFloat() / 255.0f
            val saturation = if (cmax != 0) (cmax - cmin).toFloat() / cmax.toFloat() else 0f
            var hue: Float

            if (saturation == 0f) {
                hue = 0f
            } else {
                val redc = (cmax - r).toFloat() / (cmax - cmin).toFloat()
                val greenc = (cmax - g).toFloat() / (cmax - cmin).toFloat()
                val bluec = (cmax - b).toFloat() / (cmax - cmin).toFloat()

                hue = if (r == cmax) {
                    bluec - greenc
                } else if (g == cmax) {
                    2.0f + redc - bluec
                } else {
                    4.0f + greenc - redc
                }
                hue /= 6.0f
                if (hue < 0) {
                    hue += 1.0f
                }
            }
            return HsbColor(hue, saturation, brightness, a)
        }
    }
}

class HsbState(
    color: HsbColor,
    private val onChange: (Int) -> Unit,
) {

    val rgba: Int get() = this.color.toRgba()
    var color: HsbColor = color
        set(value) {
            field = value
            this.onChange(value.toRgba())
        }

    constructor(rgba: Int, onChange: (Int) -> Unit) : this(HsbColor.fromRgba(rgba), onChange)
}
