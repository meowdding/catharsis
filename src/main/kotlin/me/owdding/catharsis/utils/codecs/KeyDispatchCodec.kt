package me.owdding.catharsis.utils.codecs

import com.mojang.serialization.*
import java.util.function.Function
import java.util.stream.Stream

// This is a copy of Mojangs KeyDispatchCodec, but to support older versions as in <=1.21.10
// there is no support for custom map codecs so it has to be a single key without support for optional keys

private const val COMPRESSED_VALUE_KEY = "value"

fun <A, E> Codec<A>.optionalDispatch(
    typeKey: String,
    type: Function<in E, out A>,
    fallback: A,
    codec: Function<in A, out MapCodec<out E>>
): MapCodec<E> {
    return KeyDispatchCodec(
        this.optionalFieldOf(typeKey, fallback),
        type.andThen(DataResult<String>::success),
        codec.andThen(DataResult<MapCodec<out E>>::success)
    )
}

class KeyDispatchCodec<K, V> private constructor(
    private val keyCodec: MapCodec<K>,
    private val type: Function<in V, out DataResult<out K>>,
    private val decoder: Function<in K, out DataResult<out MapDecoder<out V>>>,
    private val encoder: Function<in V, out DataResult<out MapEncoder<V>>>
) : MapCodec<V>() {

    @Suppress("UNCHECKED_CAST")
    constructor(
        keyCodec: MapCodec<K>,
        type: Function<in V, out DataResult<out K>>,
        codec: Function<in K, out DataResult<out MapCodec<out V>>>
    ) : this(
        keyCodec,
        type,
        codec,
        { value ->
            type.apply(value).flatMap { codec.apply(it).map(Function.identity()) }.map { it as MapEncoder<V> }
        }
    )

    override fun <T> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<V> {
        return keyCodec.decode(ops, input).flatMap { type ->
            decoder.apply(type).flatMap {
                if (ops.compressMaps()) {
                    val value = input.get(ops.createString(COMPRESSED_VALUE_KEY)) ?: return@flatMap DataResult.error { "Input does not have a \"value\" entry: $input" }
                    it.decoder().parse(ops, value).map(Function.identity())
                } else {
                    it.decode(ops, input).map(Function.identity())
                }
            }
        }
    }

    override fun <T> encode(input: V, ops: DynamicOps<T>, prefix: RecordBuilder<T>): RecordBuilder<T> {
        val encoderResult = encoder.apply(input)
        val typeResult = this.type.apply(input)

        val builder = prefix.withErrorsFrom(encoderResult).withErrorsFrom(typeResult)
        if (encoderResult.isError || typeResult.isError) {
            return builder
        }

        val elementEncoder = encoderResult.getOrThrow()
        val type: K = typeResult.getOrThrow()

        if (ops.compressMaps()) {
            return keyCodec.encode(type, ops, builder).add(COMPRESSED_VALUE_KEY, elementEncoder.encoder().encodeStart(ops, input))
        }

        // Encode key AFTER value
        // This is important for fixing types with remainder, since it will contain old fields, including type
        val encodedContents = elementEncoder.encode(input, ops, builder)
        return keyCodec.encode(type, ops, encodedContents)
    }

    override fun <T> keys(ops: DynamicOps<T?>): Stream<T?> {
        return Stream.concat<T?>(
            keyCodec.keys<T?>(ops),
            Stream.of<T?>(ops.createString(COMPRESSED_VALUE_KEY))
        )
    }

    override fun toString(): String = "KeyDispatchCodec[$keyCodec $type $decoder]"
}
