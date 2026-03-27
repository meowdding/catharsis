package me.owdding.catharsis.utils

import me.owdding.catharsis.utils.extensions.unsafeCast
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import kotlin.time.Duration
import kotlin.time.Instant

@Suppress("ClassName")
private object UNINITIALIZED_VALUE

class CachedValue<Type>(private val timeToLive: Duration = Duration.INFINITE, private val supplier: () -> Type) {
    private var value: Any? = UNINITIALIZED_VALUE
    var lastUpdated: Instant = Instant.DISTANT_PAST

    operator fun <This> getValue(thisRef: This, property: Any?) = getValue()

    fun getValue(): Type {
        if (!hasValue()) {
            this.value = supplier()
            lastUpdated = currentInstant()
        }
        if (value === UNINITIALIZED_VALUE) throw ClassCastException("Failed to initialize value!")
        return value.unsafeCast()
    }

    fun hasValue() = value !== UNINITIALIZED_VALUE && lastUpdated.since() < timeToLive

    fun invalidate() {
        value = UNINITIALIZED_VALUE
    }
}
