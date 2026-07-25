package me.owdding.katharsis.features.text

import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.features.text.replacers.ReplacementResult
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.extensions.readWithCodec
import net.minecraft.network.chat.Component
import net.minecraft.resources.FileToIdConverter
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines

abstract class TextReplacements<Context>(path: String) : SimplePreparableReloadListener<List<TextReplacement>>() {

    private val logger = Katharsis.featureLogger("TextReplacements/$path")
    private val converter = FileToIdConverter.json("katharsis/text_replacements/$path")
    private val codec = KatharsisCodecs.getCodec<TextReplacement>()

    private var replacements: List<TextReplacement> = emptyList()
    var cacheKey = 0
        private set

    override fun prepare(manager: ResourceManager, profiler: ProfilerFiller): List<TextReplacement> {
        return converter.listMatchingResources(manager).mapNotNull { (id, resource) ->
            logger.runCatching("Error loading text replacement $id") { resource.readWithCodec(codec) }
        }
    }

    override fun apply(replacements: List<TextReplacement>, manager: ResourceManager, profiler: ProfilerFiller) {
        this.replacements = replacements.sortedBy { it.priority }
        cacheKey++
    }

    fun replace(context: Context, texts: List<Component>): List<Component> {
        when {
            texts.isEmpty() -> return texts
            else -> {
                val original = Text.multiline(texts)
                val originalSplit = original.splitLines()
                val modified = tryReplace(context, original) ?: return texts
                val modifiedSplit = modified.splitLines()

                return modifiedSplit.map { line -> texts.getOrNull(originalSplit.indexOf(line)) ?: line }
            }
        }
    }

    fun replace(context: Context, text: Component): Component {
        return tryReplace(context, text) ?: text
    }

    private fun tryReplace(context: Context, text: Component): Component? {
        var result = text
        var replaced = false
        for (replacement in replacements) {
            val replacement = replacement.replacer.replace(result)
            replaced = replaced || replacement.replaced
            result = replacement.text
            if (replacement is ReplacementResult.Break) {
                break
            }
        }
        return result.takeIf { replaced }
    }
}
