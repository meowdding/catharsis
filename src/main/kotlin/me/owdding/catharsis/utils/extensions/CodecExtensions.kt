package me.owdding.catharsis.utils.extensions

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.KeyDispatchCodec

fun <I : Any, O : Any> Codec<I>.dispatchLenientMap(
    key: String,
    type: (O) -> DataResult<out I>,
    codec: (I) -> DataResult<MapCodec<out O>>,
): MapCodec<O> {
    return KeyDispatchCodec(this.fieldOf(key), type, codec)
}
