package me.owdding.catharsis.utils.extensions


fun <Key, OriginalValue, NewValue> Map<Key, OriginalValue>.mapValuesNotNull(transform: (Map.Entry<Key, OriginalValue>) -> NewValue?): Map<Key, NewValue> {
    return this.mapNotNull { transform(it)?.let { value -> it.key to value } }.toMap()
}

fun <OriginalKey, NewKey, Value> Map<OriginalKey, Value>.mapKeysNotNull(transform: (Map.Entry<OriginalKey, Value>) -> NewKey?): Map<NewKey, Value> {
    return this.mapNotNull { transform(it)?.let { key -> key to it.value } }.toMap()
}

fun <OriginalKey, NewKey, OriginalValue, NewValue> Map<OriginalKey, OriginalValue>.mapBothNotNull(transform: (Map.Entry<OriginalKey, OriginalValue>) -> Pair<NewKey?, NewValue?>?): Map<NewKey, NewValue> {
    return buildMap { this@mapBothNotNull.forEach { entry -> transform(entry)?.let { put(it.first ?: return@forEach, it.second ?: return@forEach) } } }
}
