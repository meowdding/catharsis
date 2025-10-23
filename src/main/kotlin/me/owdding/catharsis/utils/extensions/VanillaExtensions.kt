package me.owdding.catharsis.utils.extensions

import net.minecraft.util.ExtraCodecs

operator fun <Key : Any, Value : Any> ExtraCodecs.LateBoundIdMapper<Key, Value>.set(key: Key, value: Value): ExtraCodecs.LateBoundIdMapper<Key, Value> = this.put(key, value)
