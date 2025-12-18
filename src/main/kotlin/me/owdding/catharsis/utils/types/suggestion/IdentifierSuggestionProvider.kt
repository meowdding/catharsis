package me.owdding.catharsis.utils.types.suggestion

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.resources.Identifier
import java.util.concurrent.CompletableFuture

data class IdentifierSuggestionProvider<T>(
    val elements: Collection<T>,
    val converter: (T) -> Identifier,
) : CatharsisSuggestionProvider {
    override fun getSuggestions(
        context: CommandContext<FabricClientCommandSource>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        SharedSuggestionProvider.suggestResource(elements.map(converter), builder)
        return builder.buildFuture()
    }

    companion object {
        fun create(elements: Collection<Identifier>) = IdentifierSuggestionProvider(elements) { it }
    }
}
