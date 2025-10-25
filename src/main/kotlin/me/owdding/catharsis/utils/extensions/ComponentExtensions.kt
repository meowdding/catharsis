package me.owdding.catharsis.utils.extensions

import eu.pb4.placeholders.api.node.parent.GradientNode
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import net.minecraft.network.chat.TextColor as MCTextColor

val PREFIX = Text.of {
    append("«")
    append(Text.of("Catharsis").gradient(TextColor.DARK_PURPLE, 0x730373))
    append("»")
    this.color = TextColor.GRAY
}

fun Component.sendWithPrefix() = Text.join(PREFIX, " ", this).send()
fun Component.sendWithPrefix(id: String) = Text.join(PREFIX, " ", this).send(id)

fun MutableComponent.gradient(vararg colors: Int): Component = GradientNode.apply(this, GradientNode.GradientProvider.colors(colors.map { MCTextColor.fromRgb(it) }))
