package me.owdding.catharsis.utils.suggestion

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

data class ResourceLocationSuggestionProvider<T>(
    val elements: Collection<T>,
    val converter: (T) -> ResourceLocation,
) : CatharsisSuggestionProvider {
    override fun getSuggestions(
        context: CommandContext<FabricClientCommandSource>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        SharedSuggestionProvider.suggestResource(elements.map(converter), builder)
        return builder.buildFuture()
    }

    companion object {
        fun create(elements: Collection<ResourceLocation>) = ResourceLocationSuggestionProvider(elements) { it }
    }
}
