package me.owdding.catharsis.utils.codecs

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.item.Item
import org.joml.Quaternionf
import org.joml.Vector2i
import org.joml.Vector2ic
import java.net.URI

object IncludedCodecs {

    @IncludedCodec val regexCodec: Codec<Regex> = Codec.STRING.xmap({ str -> Regex(str) }, { regex -> regex.pattern })
    @IncludedCodec val reosurceLocationCodec: Codec<ResourceLocation> = ResourceLocation.CODEC
    @IncludedCodec val vec2iCodec: Codec<Vector2i> = RecordCodecBuilder.create { it.group(
        Codec.INT.fieldOf("x").forGetter(Vector2ic::x),
        Codec.INT.fieldOf("y").forGetter(Vector2ic::y),
    ).apply(it, ::Vector2i) }
    @IncludedCodec(named = "size") val sizeCodec: Codec<Vector2i> = RecordCodecBuilder.create { it.group(
        Codec.INT.fieldOf("width").forGetter(Vector2ic::x),
        Codec.INT.fieldOf("height").forGetter(Vector2ic::y),
    ).apply(it, ::Vector2i) }
    @IncludedCodec val quaternionCodec: Codec<Quaternionf> = ExtraCodecs.QUATERNIONF
    @IncludedCodec val componentCodec: Codec<Component> = ComponentSerialization.CODEC
    @IncludedCodec val uriCodec: Codec<URI> = ExtraCodecs.UNTRUSTED_URI // This is actually "trusted", it requires https and http

    // Registries
    // TODO this is broken because of the generic
    //@IncludedCodec(keyable = true) val menuCodec = BuiltInRegistries.MENU.byNameCodec()
    @IncludedCodec val itemCodec: Codec<Item> = BuiltInRegistries.ITEM.byNameCodec()
}
