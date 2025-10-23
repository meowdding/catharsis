package me.owdding.catharsis.utils.extensions

import kotlin.reflect.KClass

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
