package me.owdding.catharsis.utils.codecs

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

object IncludedCodecs {

    @IncludedCodec val regexCodec = Codec.STRING.xmap({ str -> Regex(str) }, { regex -> regex.pattern })
    @IncludedCodec val reosurceLocation = ResourceLocation.CODEC

    // Registries
    // TODO this is broken because of the generic
    //@IncludedCodec(keyable = true) val menuCodec = BuiltInRegistries.MENU.byNameCodec()
    @IncludedCodec val itemCodec = BuiltInRegistries.ITEM.byNameCodec()
}
