package me.owdding.catharsis.utils.extensions

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import com.google.common.collect.Multimaps
import com.google.common.collect.SetMultimap


fun <Key, OriginalValue, NewValue> Map<Key, OriginalValue>.mapValuesNotNull(transform: (Map.Entry<Key, OriginalValue>) -> NewValue?): Map<Key, NewValue> {
    return this.mapNotNull { transform(it)?.let { value -> it.key to value } }.toMap()
}

fun <OriginalKey, NewKey, Value> Map<OriginalKey, Value>.mapKeysNotNull(transform: (Map.Entry<OriginalKey, Value>) -> NewKey?): Map<NewKey, Value> {
    return this.mapNotNull { transform(it)?.let { key -> key to it.value } }.toMap()
}

fun <OriginalKey, NewKey, OriginalValue, NewValue> Map<OriginalKey, OriginalValue>.mapBothNotNull(transform: (Map.Entry<OriginalKey, OriginalValue>) -> Pair<NewKey?, NewValue?>?): Map<NewKey, NewValue> {
    return buildMap { this@mapBothNotNull.forEach { entry -> transform(entry)?.let { put(it.first ?: return@forEach, it.second ?: return@forEach) } } }
}

fun <Type> Iterable<Type>.extremesOf(converter: (Type) -> Int): Pair<Int, Int>? {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return null

    var min = converter(iterator.next())
    var max = min

    for (element in this) {
        val value = converter(element)
        if (value < min) min = value
        if (value > max) max = value
    }

    return min to max
}

fun <Key, Value> buildMultimap(
    creator: () -> Multimap<Key, Value> = { MultimapBuilder.hashKeys().hashSetValues().build() },
    init: Multimap<Key, Value>.() -> Unit
): Multimap<Key, Value> = creator().apply(init)
