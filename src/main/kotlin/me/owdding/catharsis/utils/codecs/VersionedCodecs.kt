package me.owdding.catharsis.utils.codecs

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.KeyDispatchCodec

object VersionedCodecs {

    fun <I : Any, O : Any> Codec<I>.dispatchLenientMap(
        key: String,
        type: (O) -> DataResult<out I>,
        codec: (I) -> DataResult<MapCodec<out O>>,
    ): MapCodec<O> {
        //? if =1.21.11 {
        return KeyDispatchCodec(this.fieldOf(key), type, codec)
        //?} else {
        /*return KeyDispatchCodec(key, this, type, codec)
        *///?}
    }
}
