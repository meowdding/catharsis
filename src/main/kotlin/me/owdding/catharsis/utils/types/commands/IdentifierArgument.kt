package me.owdding.catharsis.utils.types.commands

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import me.owdding.catharsis.utils.types.suggestion.CatharsisSuggestionProvider
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.utils.text.Text
import java.util.concurrent.CompletableFuture

data class IdentifierArgument(val ids: () -> Iterable<Identifier>, val defaultNamespace: String = "catharsis", val filter: (Identifier) -> Boolean = { true }) :
    ArgumentType<Identifier>, CatharsisSuggestionProvider {

    override fun String.sanitize(): String = this

    override fun parse(reader: StringReader): Identifier {
        val cursor = reader.cursor
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip()
        }
        val string = reader.string.substring(cursor, reader.cursor)


        return ids().find { it == Identifier.tryParse(string) } ?: ids().find {
            it == Identifier.tryBuild(defaultNamespace, string)
        } ?: throw SimpleCommandExceptionType(Text.of("Unknown ")).createWithContext(reader)
    }

    override fun <S : Any> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        ids().forEach {
            suggest(builder, it.toString())
            if (it.namespace == defaultNamespace && !builder.remaining.lowercase().contains(":"))
                suggest(builder, it.path)
        }
        return builder.buildFuture()
    }

    override fun getSuggestions(
        p0: CommandContext<FabricClientCommandSource>,
        p1: SuggestionsBuilder,
    ) = listSuggestions(p0, p1)
}
