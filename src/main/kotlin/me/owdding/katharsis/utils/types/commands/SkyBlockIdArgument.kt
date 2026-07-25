package me.owdding.katharsis.utils.types.commands

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import me.owdding.katharsis.utils.types.suggestion.KatharsisSuggestionProvider
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import java.util.concurrent.CompletableFuture

data class SkyBlockIdArgument(val skyblockIds: Iterable<SkyBlockId> = SimpleItemAPI.getAllIds(), val filter: (SkyBlockId) -> Boolean = { true }) : ArgumentType<SkyBlockId>, KatharsisSuggestionProvider {

    override fun parse(reader: StringReader): SkyBlockId {
        val cursor = reader.cursor
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip()
        }
        val string = reader.string.substring(cursor, reader.cursor)


        return skyblockIds.find { it.id == string.lowercase() } ?: SkyBlockId.unsafe(string.lowercase())
    }

    override fun <S : Any> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        skyblockIds.forEach {
            suggest(builder, it.id.lowercase())
        }
        return builder.buildFuture()
    }

    override fun getSuggestions(
        p0: CommandContext<FabricClientCommandSource>,
        p1: SuggestionsBuilder,
    ) = listSuggestions(p0, p1)
}
