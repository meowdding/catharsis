package me.owdding.catharsis.utils.types

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.ints.IntArraySet
import it.unimi.dsi.fastutil.ints.IntSet
import it.unimi.dsi.fastutil.ints.IntSets
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.util.ExtraCodecs
import java.util.*
import kotlin.jvm.optionals.getOrNull

sealed interface IntPredicate {

    operator fun contains(value: Int): Boolean

    class Range(val min: Optional<Int>, val max: Optional<Int>) : IntPredicate {

        override fun contains(value: Int): Boolean {
            val min = min.getOrNull()
            val max = max.getOrNull()
            if (min != null && value < min) return false
            if (max != null && value > max) return false
            return true
        }
    }

    class Set(val set: IntSet) : IntPredicate {
        override fun contains(value: Int): Boolean = value in set
    }

    companion object {

        private val rangeCodec: Codec<Range> = RecordCodecBuilder.create {
            it.group(
                Codec.INT.optionalFieldOf("min").forGetter(Range::min),
                Codec.INT.optionalFieldOf("max").forGetter(Range::max),
            ).apply(it, ::Range)
        }

        private val setCodec: Codec<Set> = ExtraCodecs.compactListCodec(Codec.INT).xmap(
            { list -> Set(IntSets.unmodifiable(IntArraySet(list))) },
            { set -> set.set.toList() },
        )

        @IncludedCodec
        val CODEC: Codec<IntPredicate> = Codec.either(rangeCodec, setCodec)
            .xmap(
                { Either.unwrap(it) },
                {
                    when (it) {
                        is Range -> Either.left(it)
                        is Set -> Either.right(it)
                    }
                },
            )
    }
}
