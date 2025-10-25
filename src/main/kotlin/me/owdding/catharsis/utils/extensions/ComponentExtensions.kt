package me.owdding.catharsis.utils.extensions

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

val PREFIX = Text.of {
    append("«")
    append("Catharsis") {
        this.color = TextColor.DARK_PURPLE
    }
    append("»")
    this.color = TextColor.GRAY
}

fun Component.sendWithPrefix() = Text.join(PREFIX, " ", this).send()
fun Component.sendWithPrefix(id: String) = Text.join(PREFIX, " ", this).send(id)
