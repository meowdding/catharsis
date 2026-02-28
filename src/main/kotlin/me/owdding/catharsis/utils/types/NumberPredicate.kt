package me.owdding.catharsis.utils.types

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import it.unimi.dsi.fastutil.floats.FloatArraySet
import it.unimi.dsi.fastutil.floats.FloatSet
import it.unimi.dsi.fastutil.floats.FloatSets
import it.unimi.dsi.fastutil.ints.IntArraySet
import it.unimi.dsi.fastutil.ints.IntSet
import it.unimi.dsi.fastutil.ints.IntSets
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.util.ExtraCodecs

sealed interface IntPredicate {

    operator fun contains(value: Int): Boolean
    operator fun iterator(): IntIterator

    @GenerateCodec
    data class Range(val min: Int, val max: Int) : IntPredicate {
        override fun contains(value: Int): Boolean = value in min..max
        override fun iterator(): IntIterator = (min..max).iterator()
    }

    data class Set(val set: IntSet) : IntPredicate {
        override fun contains(value: Int): Boolean = value in set
        override fun iterator(): IntIterator = set.toIntArray().iterator()
    }

    companion object {

        private val setCodec: Codec<Set> = ExtraCodecs.compactListCodec(Codec.INT).xmap(
            { list -> Set(IntSets.unmodifiable(IntArraySet(list))) },
            { set -> set.set.toList() },
        )

        @IncludedCodec
        val CODEC: Codec<IntPredicate> = Codec.either(CatharsisCodecs.getCodec<Range>(), setCodec)
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

sealed interface FloatPredicate {
    operator fun contains(value: Float): Boolean

    data class Range(val min: Float, val max: Float) : FloatPredicate {
        override fun contains(value: Float) = value in min..max
    }

    data class Set(val set: FloatSet) : FloatPredicate {
        override fun contains(value: Float) = value in set
    }

    companion object {
        private val setCodec: Codec<Set> = ExtraCodecs.compactListCodec(Codec.FLOAT).xmap(
            { list -> Set(FloatSets.unmodifiable(FloatArraySet(list))) },
            { set -> set.set.toList() },
        )

        @IncludedCodec
        val CODEC: Codec<FloatPredicate> = Codec.either(CatharsisCodecs.getCodec<Range>(), setCodec)
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
