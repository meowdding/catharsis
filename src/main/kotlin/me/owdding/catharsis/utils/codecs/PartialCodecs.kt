package me.owdding.catharsis.utils.codecs

import com.mojang.datafixers.util.Pair
import com.mojang.datafixers.util.Unit
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.Lifecycle
import com.mojang.serialization.MapCodec
import com.mojang.serialization.MapLike
import com.mojang.serialization.RecordBuilder
import java.util.stream.Stream

fun <T> Codec<T>.nonPartialListOf(): Codec<List<T>> {
    return NonPartialList(this)
}

fun <T> Codec<T>.nonPartialFieldOf(name: String): MapCodec<T> {
    return NonPartialFieldCodec(this, name)
}

data class NonPartialList<E>(val codec: Codec<E>) : Codec<List<E>> {

    override fun <T> encode(input: List<E>, ops: DynamicOps<T>, prefix: T): DataResult<T> {
        val builder = ops.listBuilder()
        for (element in input) {
            builder.add(this.codec.encodeStart(ops, element))
        }
        return builder.build(prefix)
    }

    override fun <T> decode(
        ops: DynamicOps<T>,
        input: T,
    ): DataResult<Pair<List<E>, T>> {
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap { stream ->
            val elements = mutableListOf<E>()
            val failures = Stream.builder<T>()
            var result = DataResult.success(Unit.INSTANCE, Lifecycle.stable())

            stream.accept { element ->
                val data = this.codec.parse(ops, element)
                data.error().ifPresent { failures.add(element) }
                data.result().ifPresent(elements::add)

                result = result.apply2stable({ result, _ -> result}, data)
            }

            val partial = Pair.of(elements.toList(), ops.createList(failures.build()))
            result.map { partial }.setPartial(partial)
        }
    }
}

data class NonPartialFieldCodec<A>(val codec: Codec<A>, val name: String) : MapCodec<A>() {

    override fun <T> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<A> {
        val value = input.get(this.name) ?: return DataResult.error { "No key $name in $input" }
        val result = this.codec.parse(ops, value)

        return result.error().map<DataResult<A>> { DataResult.error(it.messageSupplier(), it.lifecycle) }.orElse(result)
    }

    override fun <T> encode(input: A, ops: DynamicOps<T>, prefix: RecordBuilder<T>): RecordBuilder<T> {
        return prefix.add(this.name, this.codec.encodeStart(ops, input))
    }

    override fun <T> keys(ops: DynamicOps<T>): Stream<T> {
        return Stream.of(ops.createString(this.name))
    }

}
