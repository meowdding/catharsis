package me.owdding.katharsis.utils.types

typealias Base64String = String

fun String.requireBase64Padding(): Base64String {
    if (this.length % 4 == 0) return this
    return this + "=".repeat(4 - this.length % 4)
}
