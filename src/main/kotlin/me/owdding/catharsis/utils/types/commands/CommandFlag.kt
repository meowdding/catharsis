package me.owdding.catharsis.utils.types.commands

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.SharedSuggestionProvider
import java.util.concurrent.CompletableFuture
import kotlin.math.max

interface CommandFlag {
    val shortName: Char
    val longName: String
    val flagType: ArgumentType<*>?
    val group: String? get() = null

    companion object {
        fun of(longName: String, shortName: Char = longName.first(), flagType: ArgumentType<*>? = null, group: String? = null) = object : CommandFlag {
            override val shortName: Char = shortName
            override val longName: String = longName
            override val flagType: ArgumentType<*>? = flagType
            override val group: String? = group
        }
    }
}

class FlagArgument<T : CommandFlag>(val flags: Iterable<T>) : ArgumentType<Map<T, Any>> {
    companion object {
        inline fun <reified T> enum(): FlagArgument<T> where T : Enum<T>, T : CommandFlag = FlagArgument(T::class.java.enumConstants.toList())
    }

    override fun parse(reader: StringReader): Map<T, Any> {
        val map = mutableMapOf<T, Any>()
        val minCursor = reader.cursor

        val consumedGroups: MutableSet<String> = mutableSetOf()
        while (reader.canRead() && reader.peek() == '-') {
            val cursor = reader.cursor
            reader.skip()
            if (!reader.canRead()) break
            val filteredFlags = flags.filterUnused(map.keys, consumedGroups)

            val flag = if (reader.peek() == '-') {
                reader.skip()
                val beforeRead = reader.cursor
                val content = runCatching {
                    reader.readStringUntil(' ')
                }.getOrElse {
                    reader.cursor = beforeRead
                    reader.readString()
                }
                reader.cursor -= 1
                filteredFlags.find { it.longName == content }
            } else {
                val content = reader.read()
                filteredFlags.find { it.shortName == content }
            }

            if (flag == null) {
                reader.cursor = max(minCursor, cursor - 1)
                return map
            }
            val flagType = flag.flagType
            flag.group?.let(consumedGroups::add)
            if (flagType == null) {
                map[flag] = Unit
            } else {
                reader.skipWhitespace()
                if (reader.canRead())
                    map[flag] = flagType.parse(reader) as Any
            }
            if (reader.remainingLength >= 2 && reader.peek(1) == '-') reader.skipWhitespace()
        }

        return map
    }

    private fun Iterable<T>.filterUnused(used: Iterable<T>, usedGroups: Set<String>) = this.filterNot { usedGroups.contains(it.group) || used.contains(it) }

    override fun <S : Any?> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val reader = StringReader(builder.input)
        reader.cursor = builder.start

        val consumedTypes: MutableSet<T> = mutableSetOf()
        val consumedGroups: MutableSet<String> = mutableSetOf()
        while (reader.canRead() && reader.peek() == '-') {
            reader.skip()
            val cursor = reader.cursor
            val filteredFlags = flags.filterUnused(consumedTypes, consumedGroups)
            if (!reader.canRead()) {
                val offset = builder.createOffset(reader.cursor)
                filteredFlags.forEach {
                    offset.suggest(it.shortName.toString())
                    offset.suggest("-" + it.longName)
                }
                return offset.buildFuture()
            }

            val flag = runCatching {
                if (reader.peek() == '-') {
                    reader.skip()
                    val content = reader.readStringUntil(' ')
                    reader.cursor -= 1
                    filteredFlags.find { it.longName == content }
                } else {
                    val content = reader.read()
                    filteredFlags.find { it.shortName == content }
                }
            }.getOrNull()

            if (flag == null) {
                reader.cursor = cursor
                if (reader.canRead() && reader.peek() == '-') {
                    reader.read()
                    val offset = builder.createOffset(reader.cursor)
                    SharedSuggestionProvider.suggest(filteredFlags.map { it.longName }, offset)
                    return offset.buildFuture()
                }
                return builder.buildFuture()
            }

            consumedTypes.add(flag)
            flag.group?.let(consumedGroups::add)
            val flagType = flag.flagType
            if (flagType != null) {
                reader.skipWhitespace()
                val offset = builder.createOffset(reader.cursor)
                if (reader.canRead()) {
                    flagType.parse(reader)
                }
                if (reader.remainingLength >= 2 && reader.peek(1) == '-') {
                    reader.skipWhitespace()
                    continue
                }
                return flagType.listSuggestions(context, offset)
            }
            if (reader.remainingLength >= 2 && reader.peek(1) == '-') reader.skipWhitespace()
        }

        return super.listSuggestions(context, builder)
    }
}
