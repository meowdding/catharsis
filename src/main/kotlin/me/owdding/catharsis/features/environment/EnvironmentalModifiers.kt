package me.owdding.catharsis.features.environment

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.CatharsisLogger.Companion.featureLogger
import me.owdding.catharsis.utils.extensions.unsafeCast
import me.owdding.ktmodules.Module
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.attribute.EnvironmentAttribute
import net.minecraft.world.attribute.EnvironmentAttributeLayer
import net.minecraft.world.attribute.EnvironmentAttributeSystem
import net.minecraft.world.attribute.EnvironmentAttributes
import net.minecraft.world.level.ColorResolver
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.io.Reader

@Module
object EnvironmentalModifiers : CatharsisLogger by Catharsis.featureLogger(), SimplePreparableReloadListener<List<EnvironmentalModifier>>() {

    data class ModifierInstance<Value : Any>(
        val modifier: List<EnvironmentalAttributeModifier<Value>>,
        val attribute: EnvironmentAttribute<Value>,
    )

    data class BiomeEffectInstance<Value : Any>(
        val modifier: List<BiomeEffectModifier<Value>>,
        val attribute: BiomeEffect<Value>,
    )

    val currentAttributeModifier: MutableList<ModifierInstance<*>> = mutableListOf()
    val currentBiomEffects: MutableList<BiomeEffectInstance<*>> = mutableListOf()


    private val gson = GsonBuilder().create()
    private val allowedAttributes: List<EnvironmentAttribute<out Any>> = listOf(
        // VISUALS
        EnvironmentAttributes.FOG_COLOR,
        EnvironmentAttributes.FOG_START_DISTANCE,
        EnvironmentAttributes.FOG_END_DISTANCE,
        EnvironmentAttributes.SKY_FOG_END_DISTANCE,
        EnvironmentAttributes.CLOUD_FOG_END_DISTANCE,
        EnvironmentAttributes.WATER_FOG_COLOR,
        EnvironmentAttributes.WATER_FOG_START_DISTANCE,
        EnvironmentAttributes.WATER_FOG_END_DISTANCE,
        EnvironmentAttributes.SKY_COLOR,
        EnvironmentAttributes.SUNRISE_SUNSET_COLOR,
        EnvironmentAttributes.CLOUD_COLOR,
        EnvironmentAttributes.CLOUD_HEIGHT,
        EnvironmentAttributes.SUN_ANGLE,
        EnvironmentAttributes.MOON_ANGLE,
        EnvironmentAttributes.STAR_ANGLE,
        EnvironmentAttributes.MOON_PHASE,
        EnvironmentAttributes.STAR_BRIGHTNESS,
        EnvironmentAttributes.BLOCK_LIGHT_TINT,
        EnvironmentAttributes.SKY_LIGHT_COLOR,
        EnvironmentAttributes.SKY_LIGHT_FACTOR,
        EnvironmentAttributes.NIGHT_VISION_COLOR,
        EnvironmentAttributes.AMBIENT_LIGHT_COLOR,
        EnvironmentAttributes.DEFAULT_DRIPSTONE_PARTICLE,
        EnvironmentAttributes.AMBIENT_PARTICLES,

        // AUDIO
        EnvironmentAttributes.BACKGROUND_MUSIC,
        EnvironmentAttributes.MUSIC_VOLUME,
        EnvironmentAttributes.AMBIENT_SOUNDS,
        EnvironmentAttributes.FIREFLY_BUSH_SOUNDS,
    )
    val converter: FileToIdConverter = FileToIdConverter.json("catharsis/environment_modifier")

    fun createEnvironmentalAttributeModifierCodec(): MapCodec<EnvironmentalAttributeModifier<out Any>> {
        val mapper = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<EnvironmentalAttributeModifier<out Any>>>()

        for (attribute in allowedAttributes) {
            val id = BuiltInRegistries.ENVIRONMENT_ATTRIBUTE.getKey(attribute) ?: continue

            mapper.put(id, EnvironmentalAttributeModifier.createCodec(attribute))
        }

        return mapper.codec(Identifier.CODEC).dispatchMap("attribute", { it.codec() }, { it })
    }

    override fun prepare(
        manager: ResourceManager,
        profiler: ProfilerFiller,
    ): List<EnvironmentalModifier> {
        return converter.listMatchingResources(manager).mapNotNull { (id, resource) ->
            runCatching("Error loading environment modifier $id") {
                resource.openAsReader().use { reader -> reader.parse() }
            }
        }
    }

    private fun Reader.parse() = gson.fromJson(this, JsonElement::class.java).toDataOrThrow(EnvironmentalModifier.CODEC)

    override fun apply(
        preparations: List<EnvironmentalModifier>,
        manager: ResourceManager,
        profiler: ProfilerFiller,
    ) {
        currentAttributeModifier.clear()
        currentBiomEffects.clear()
        val attributeModifier = mutableListOf<EnvironmentalAttributeModifier<out Any>>()
        val biomeModifier = mutableListOf<BiomeEffectModifier<out Any>>()
        preparations.forEach {
            when (it) {
                is EnvironmentalAttributeModifier<*> -> attributeModifier.add(it)
                is BiomeEffectModifier<*> -> biomeModifier.add(it)
            }
        }

        currentAttributeModifier.addAll(attributeModifier.groupBy { it.attribute }.map { ModifierInstance(it.value.unsafeCast(), it.key) })
        currentBiomEffects.addAll(biomeModifier.groupBy { it.effect }.map { BiomeEffectInstance(it.value.unsafeCast(), it.key) })
    }

    @JvmStatic
    fun addLayers(builder: EnvironmentAttributeSystem.Builder) {
        allowedAttributes.forEach {
            builder.addPositionalLayer(it, createLayer(it).unsafeCast())
        }
    }

    fun <Value : Any> createLayer(type: EnvironmentAttribute<Value>): EnvironmentAttributeLayer.Positional<Value> {
        return layer@{ value, pos, interpolator ->
            val modifier = currentAttributeModifier.find { it.attribute == type }?.unsafeCast<ModifierInstance<Value>>()?.modifier ?: return@layer value

            var currentValue = value
            for (attributeModifier in modifier) {
                if (attributeModifier.condition.applies(currentValue, pos)) {
                    currentValue = attributeModifier.provider.getValue(currentValue, pos, interpolator)
                }
            }
            currentValue
        }
    }

    fun getColor(pos: BlockPos, baseColor: Int, modifiers: List<BiomeEffectModifier<Int>>): Int {
        modifiers.forEach {
            if (it.condition.applies(baseColor, Vec3(pos))) {
                return it.value
            }
        }

        return baseColor
    }

    @JvmStatic
    fun wrap(level: BlockAndTintGetter, pos: BlockPos, colorResolver: ColorResolver, effect: () -> BiomeEffect<Int>): ColorResolver = modifier@{ biome, x, y ->
        val baseColor = colorResolver.getColor(biome, x, y)

        val list = this.currentBiomEffects.find { it.attribute == effect() }?.modifier?.takeIf { it.isNotEmpty() } ?: return@modifier baseColor

        getColor(pos, baseColor, list.unsafeCast())
    }

    init {
        Catharsis.registerClientReloadListener(Catharsis.id("environment_modifier"), this)
    }
}
