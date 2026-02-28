package me.owdding.catharsis.utils.codecs

import com.mojang.serialization.Codec
import net.minecraft.resources.Identifier

interface SavableData<T : SavableData<T>> {

    val codec: Codec<T>
    fun toFileName(identifier: Identifier): Identifier

}
