package me.owdding.catharsis.features.text.utils

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentMatchResult
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex

data class ExpandedReplacement(val text: Component) {

    fun resolve(result: ComponentMatchResult): Component = GROUP_REGEX.replace(text) { match ->
        val group = match.getPlain(1)?.drop(1) ?: return@replace match.value()
        val content = if (group.startsWith("{") && group.endsWith("}")) {
            val inner = group.substring(1, group.length - 1)
            inner.toIntOrNull()?.let(result::get) ?: runCatching { result[inner] }.getOrNull()
        } else {
            group.toIntOrNull()?.let(result::get)
        }

        content ?: match.value()
    }

    companion object {

        private val GROUP_REGEX = ComponentRegex("(?<!\\\\)(\\$\\{[a-zA-Z0-9]+}|\\d+)")

        @IncludedCodec
        val CODEC: Codec<ExpandedReplacement> = ComponentSerialization.CODEC.xmap(::ExpandedReplacement, ExpandedReplacement::text)
    }
}
