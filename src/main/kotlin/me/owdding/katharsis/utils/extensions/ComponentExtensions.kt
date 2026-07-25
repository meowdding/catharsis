package me.owdding.katharsis.utils.extensions

import eu.pb4.placeholders.api.node.parent.GradientNode
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import net.minecraft.network.chat.TextColor as MCTextColor

val PREFIX = Text.of {
    append("«")
    append(Text.of("Katharsis").gradient(0xAA01AA, 0x730373))
    append("»")
    this.color = TextColor.GRAY
}

fun Component.sendWithPrefix() = Text.join(PREFIX, " ", this).send()
fun Component.sendSyncWithPrefix() = McClient.runOrNextTick { Text.join(PREFIX, " ", this).send() }

fun Component.sendWithPrefixIf(condition: () -> Boolean): Unit {
    if (condition()) {
        Text.join(PREFIX, " ", this).send()
    }
}

fun Component.sendWithPrefix(id: String) = Text.join(PREFIX, " ", this).send(id)
fun Component.sendSyncWithPrefix(id: String) = McClient.runOrNextTick { Text.join(PREFIX, " ", this).send(id) }

fun MutableComponent.gradient(vararg colors: Int): Component = GradientNode.apply(this, GradientNode.GradientProvider.colors(colors.map { MCTextColor.fromRgb(it) }))
