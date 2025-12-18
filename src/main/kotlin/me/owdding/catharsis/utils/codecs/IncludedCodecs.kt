package me.owdding.catharsis.utils.codecs

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.Utils
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.color.item.ItemTintSource
import net.minecraft.client.color.item.ItemTintSources
import net.minecraft.client.renderer.block.model.BlockModelDefinition
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.sounds.SoundEvent
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.item.Item
import org.joml.Quaternionf
import org.joml.Vector2i
import org.joml.Vector2ic
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import java.net.URI
import java.util.function.UnaryOperator

object IncludedCodecs {

    @IncludedCodec
    val regexCodec: Codec<Regex> = Codec.STRING.xmap({ str -> Regex(str) }, { regex -> regex.pattern })
    @IncludedCodec
    val resourceLocationCodec: Codec<Identifier> = Identifier.CODEC
    @IncludedCodec(named = "catharsis_location")
    val catharsisIdentifier: Codec<Identifier> = Codec.STRING.xmap(
        { Utils.resourceLocationWithDifferentFallbackNamespace(it, Identifier.NAMESPACE_SEPARATOR, Catharsis.MOD_ID) },
        { it.toString() },
    )
    @IncludedCodec
    val vec2iCodec: Codec<Vector2i> = RecordCodecBuilder.create {
        it.group(
            Codec.INT.fieldOf("x").forGetter(Vector2ic::x),
            Codec.INT.fieldOf("y").forGetter(Vector2ic::y),
        ).apply(it, ::Vector2i)
    }
    @IncludedCodec(named = "size")
    val sizeCodec: Codec<Vector2i> = RecordCodecBuilder.create {
        it.group(
            Codec.INT.fieldOf("width").forGetter(Vector2ic::x),
            Codec.INT.fieldOf("height").forGetter(Vector2ic::y),
        ).apply(it, ::Vector2i)
    }
    @IncludedCodec
    val quaternionCodec: Codec<Quaternionf> = ExtraCodecs.QUATERNIONF.xmap(::Quaternionf, UnaryOperator.identity())
    @IncludedCodec
    val componentCodec: Codec<Component> = ComponentSerialization.CODEC
    @IncludedCodec
    val uriCodec: Codec<URI> = ExtraCodecs.UNTRUSTED_URI // This is actually "trusted", it requires https and http
    @IncludedCodec
    val tintSources: Codec<ItemTintSource> = ItemTintSources.CODEC
    @IncludedCodec
    val componentRegex: Codec<ComponentRegex> = Codec.STRING.comapFlatMap(
        { str -> runCatching { DataResult.success(ComponentRegex(str)) }.getOrElse { DataResult.error { it.message } } },
        { regex -> regex.regex().pattern },
    )

    // Registries
    // TODO this is broken because of the generic
    //@IncludedCodec(keyable = true) val menuCodec = BuiltInRegistries.MENU.byNameCodec()
    @IncludedCodec
    val itemCodec: Codec<Item> = BuiltInRegistries.ITEM.byNameCodec()
    @IncludedCodec
    val blockModelDefinitionCodec: MapCodec<BlockModelDefinition> = MapCodec.assumeMapUnsafe(BlockModelDefinition.CODEC)

    @IncludedCodec
    val soundEventCodecRange: Codec<SoundEvent> = Codec.withAlternative(
        RecordCodecBuilder.create {
            it.group(
                resourceLocationCodec.fieldOf("name").forGetter(SoundEvent::location),
                Codec.FLOAT.optionalFieldOf("range").forGetter { event -> event.fixedRange() },
            ).apply(it) { name, range -> SoundEvent(name, range) }
        },
        ResourceLocation.CODEC.xmap(SoundEvent::createVariableRangeEvent, SoundEvent::location),
    )
}
