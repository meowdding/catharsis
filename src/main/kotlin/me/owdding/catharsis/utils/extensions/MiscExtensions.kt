package me.owdding.catharsis.utils.extensions

import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Suppress("UNCHECKED_CAST")
fun <From, To> From.unsafeCast(): To = this as To

val KClass<*>.isNumber: Boolean get() = java.isNumber
val Class<*>.isNumber: Boolean
    get() {
        if (Number::class.java.isAssignableFrom(this)) return true
        if (this == Int::class.javaPrimitiveType) return true
        if (this == Double::class.javaPrimitiveType) return true
        if (this == Float::class.javaPrimitiveType) return true
        if (this == Long::class.javaPrimitiveType) return true
        if (this == Short::class.javaPrimitiveType) return true
        if (this == Byte::class.javaPrimitiveType) return true
        return false
    }

val KClass<*>.isEnum: Boolean get() = java.isEnum || java.superclass.isEnum

fun Duration.toReadableTime(biggestUnit: DurationUnit = DurationUnit.DAYS, maxUnits: Int = 2, allowMs: Boolean = false): String {
    val units = listOfNotNull(
        DurationUnit.DAYS to this.inWholeDays,
        DurationUnit.HOURS to this.inWholeHours % 24,
        DurationUnit.MINUTES to this.inWholeMinutes % 60,
        DurationUnit.SECONDS to this.inWholeSeconds % 60,
        (DurationUnit.MILLISECONDS to this.inWholeMilliseconds % 1000).takeIf { allowMs },
    )

    val unitNames = listOfNotNull(
        DurationUnit.DAYS to "d",
        DurationUnit.HOURS to "h",
        DurationUnit.MINUTES to "min",
        DurationUnit.SECONDS to "s",
        (DurationUnit.MILLISECONDS to "ms").takeIf { allowMs },
    ).toMap()

    val filteredUnits = units.dropWhile { it.first != biggestUnit }
        .filter { it.second > 0 }
        .take(maxUnits)

    return filteredUnits.joinToString(", ") { (unit, value) ->
        "$value${unitNames[unit]}"
    }.ifEmpty { "0 seconds" }
}
