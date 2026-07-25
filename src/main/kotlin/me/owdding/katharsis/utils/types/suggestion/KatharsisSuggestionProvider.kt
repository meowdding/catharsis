package me.owdding.katharsis.utils.types.suggestion

import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import tech.thatgravyboat.skyblockapi.utils.extentions.sanitizeForCommandInput

interface KatharsisSuggestionProvider : SuggestionProvider<FabricClientCommandSource> {

    fun suggest(builder: SuggestionsBuilder, name: String) {
        val filtered = name.sanitizeForCommandInput()
        if (filtered.lowercase().contains(builder.remaining.lowercase())) {
            builder.suggest(filtered)
        }
    }

}
