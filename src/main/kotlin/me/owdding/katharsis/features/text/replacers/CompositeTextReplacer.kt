package me.owdding.katharsis.features.text.replacers

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.network.chat.Component

@GenerateCodec
data class CompositeTextReplacer(
    val replacers: List<TextReplacer>,
    val propagate: Boolean = true,
) : TextReplacer {

    override val codec: MapCodec<out TextReplacer> = KatharsisCodecs.getMapCodec<CompositeTextReplacer>()

    override fun replace(text: Component): ReplacementResult {
        var text = text
        var replaced = false

        for (replacer in replacers) {
            val result = replacer.replace(text)
            text = result.text
            replaced = replaced || result.replaced
            if (result is ReplacementResult.Break) {
                if (!this.propagate) {
                    return ReplacementResult.Break(text, replaced)
                }
                break
            }
        }

        return ReplacementResult.Continue(text, replaced)
    }

}
