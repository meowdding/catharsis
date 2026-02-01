package me.owdding.catharsis.utils

import net.minecraft.resources.Identifier

object Utils {

    fun resourceLocationWithDifferentFallbackNamespace(location: String, separator: Char, namespace: String): Identifier {
        val i = location.indexOf(separator)
        return if (i >= 0) {
            val string = location.substring(i + 1)

            if (i != 0) {
                Identifier.fromNamespaceAndPath(location.take(i), string)
            } else {
                Identifier.fromNamespaceAndPath(namespace, string)
            }
        } else {
            Identifier.fromNamespaceAndPath(namespace, location)
        }
    }

}
