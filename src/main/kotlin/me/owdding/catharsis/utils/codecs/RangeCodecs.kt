package me.owdding.catharsis.utils.codecs

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec

object RangeCodecs {

    @IncludedCodec val intRange: Codec<IntRange> = create(Codec.INT, ::IntRange)
    @IncludedCodec val longRange: Codec<LongRange> = create(Codec.LONG, ::LongRange)

    private fun <N : Number, R : ClosedRange<N>> create(codec: Codec<N>, factory: (N, N) -> R): Codec<R> {
        val single = codec.xmap({ value -> factory(value, value) }, { range -> range.start })
        val list = codec.listOf(2, 2).xmap({ list -> factory(list[0], list[1]) }, { range -> listOf(range.start, range.endInclusive) })
        return Codec.either(single, list).xmap(
            { either -> Either.unwrap(either) },
            { range -> if (range.start == range.endInclusive) Either.left(range) else Either.right(range) }
        )
    }
}