package me.owdding.catharsis.utils.codecs

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CodecUtils
import me.owdding.catharsis.utils.Utils
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.color.item.ItemTintSource
import net.minecraft.client.color.item.ItemTintSources
import net.minecraft.client.renderer.block.model.BlockModelDefinition
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.tags.TagKey
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import org.joml.Quaternionf
import org.joml.Vector2i
import org.joml.Vector2ic
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import java.net.URI
import java.util.function.UnaryOperator
import kotlin.time.Instant

object IncludedCodecs {

    @IncludedCodec
    val regexCodec: Codec<Regex> = Codec.STRING.xmap({ str -> Regex(str) }, { regex -> regex.pattern })

    @IncludedCodec
        (keyable = true)
    val resourceLocationCodec: Codec<Identifier> = Identifier.CODEC

    @IncludedCodec(named = "catharsis_identifier", keyable = true)
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


    @IncludedCodec(keyable = true)
    val menuCodec: Codec<MenuType<*>> = BuiltInRegistries.MENU.byNameCodec()

    @IncludedCodec(keyable = true)
    val itemCodec: Codec<Item> = BuiltInRegistries.ITEM.byNameCodec()

    @IncludedCodec
    val blockModelDefinitionCodec: MapCodec<BlockModelDefinition> = MapCodec.assumeMapUnsafe(BlockModelDefinition.CODEC)

    @IncludedCodec(named = "block_tag_or_list")
    val tagOrBlocksCodec: Codec<Either<TagKey<Block>, Set<Block>>> = Codec.either(
        TagKey.hashedCodec(Registries.BLOCK),
        CodecUtils.compactSet(BuiltInRegistries.BLOCK.byNameCodec()),
    )

    @IncludedCodec(named = "blockstate_properties")
    val blockStatePropertiesCodec: Codec<Map<String, String>> = Codec.unboundedMap(
        Codec.STRING,
        Codec.withAlternative(
            Codec.STRING,
            Codec.withAlternative(
                Codec.BOOL.xmap(Boolean::toString) { it.equals("true", ignoreCase = true) },
                Codec.INT.xmap(Int::toString, String::toInt),
            ),
        ),
    )

    @IncludedCodec
    val soundEventCodecRange: Codec<SoundEvent> = Codec.withAlternative(
        RecordCodecBuilder.create {
            it.group(
                resourceLocationCodec.fieldOf("name").forGetter(SoundEvent::location),
                Codec.FLOAT.optionalFieldOf("range").forGetter { event -> event.fixedRange() },
            ).apply(it) { name, range -> SoundEvent(name, range) }
        },
        Identifier.CODEC.xmap(SoundEvent::createVariableRangeEvent, SoundEvent::location),
    )

    @IncludedCodec(keyable = true)
    val block: Codec<Block> = BuiltInRegistries.BLOCK.byNameCodec()

    @IncludedCodec
    val instant: Codec<Instant> = Codec.LONG.xmap(Instant::fromEpochMilliseconds, Instant::toEpochMilliseconds)

    @IncludedCodec(keyable = true)
    val attribute: Codec<Holder<Attribute>> = BuiltInRegistries.ATTRIBUTE.holderByNameCodec()

    @IncludedCodec
    val entityType: Codec<EntityType<*>> = BuiltInRegistries.ENTITY_TYPE.byNameCodec()
    
    @IncludedCodec
    val dataComponentCodec: Codec<DataComponentType<*>> = BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec()
}
