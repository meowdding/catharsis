package me.owdding.catharsis.utils.ui

import net.minecraft.client.gui.layouts.AbstractLayout
import net.minecraft.client.gui.layouts.LayoutElement
import java.util.function.Consumer
import kotlin.math.max

object ResizingEqualSpacingLayout {

    class Horizontal(width: Int) : AbstractLayout(0, 0, width, 0) {

        private val children: MutableList<LayoutElement> = mutableListOf()

        override fun arrangeElements() {
            super.arrangeElements()
            if (this.children.size > 1) {

                var height = 0
                var extraSpace = this.width

                for (element in this.children) {
                    height = max(height, element.height)
                    extraSpace -= element.width
                }

                val gaps = this.children.size - 1
                val gap = extraSpace / gaps
                val remainingGap = extraSpace - gap * gaps
                val remainingGapIndex = if (this.children.size % 2 == 0) this.children.size / 2 - 1 else this.children.size / 2

                var x = this.x
                for ((index, element) in this.children.withIndex()) {
                    element.setPosition(x, this.y)
                    x += element.width + gap
                    if (index == remainingGapIndex) {
                        x += remainingGap
                    }
                }

                this.height = height
            } else if (this.children.isNotEmpty()) {
                this.children[0].setPosition(this.x, this.y)
                this.height = this.children[0].height
            }
        }

        //? >= 26.2
        override fun removeChildren() { this.children.clear() }

        fun <T : LayoutElement> addChild(child: T): T {
            this.children.add(child)
            return child
        }

        override fun visitChildren(visitor: Consumer<LayoutElement>) {
            this.children.forEach(visitor)
        }
    }
}
