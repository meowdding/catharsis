package me.owdding.catharsis.features.gui.modifications.elements

import kotlin.math.abs

enum class ButtonShape(val isInside: (Int, Int, Int, Int) -> Boolean) {
    RECTANGLE({ x, y, width, height -> x in 0..<width && y >= 0 && y < height }),
    ELLIPSE(
        { x, y, width, height ->
            val centerX = width / 2.0
            val centerY = height / 2.0
            val dx = (x - centerX) / centerX
            val dy = (y - centerY) / centerY
            (dx * dx) + (dy * dy) <= 1
        },
    ),
    DIAMOND(
        { x, y, width, height ->
            val centerX = width / 2.0
            val centerY = height / 2.0
            val dx = abs(x - centerX)
            val dy = abs(y - centerY)
            (dx / centerX) + (dy / centerY) <= 1
        },
    ),
    ;
}
