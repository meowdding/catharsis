package me.owdding.katharsis.features.text.replacers

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.features.text.utils.ExpandedReplacement
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex

@GenerateCodec
data class RegexTextReplacer(
    val regex: ComponentRegex,
    val replacement: ExpandedReplacement,
    val propagate: Boolean = true,
) : TextReplacer {

    override val codec: MapCodec<out TextReplacer> = KatharsisCodecs.getMapCodec<RegexTextReplacer>()

    override fun replace(text: Component): ReplacementResult {
        var replaced = false
        val replacement = this.regex.replace(text) { result ->
            replaced = true
            this.replacement.resolve(result)
        }
        return if (this.propagate) ReplacementResult.Continue(replacement, replaced) else ReplacementResult.Break(replacement, replaced)
    }

}
