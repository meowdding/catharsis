package me.owdding.catharsis.features.environment.provider

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.codecs.optionalDispatch
import me.owdding.catharsis.utils.extensions.unsafeCast
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.attribute.EnvironmentAttribute
import net.minecraft.world.attribute.SpatialAttributeInterpolator
import net.minecraft.world.attribute.modifier.AttributeModifier
import net.minecraft.world.phys.Vec3

interface EnvironmentalAttributeProvider<Value> {

    val codec: MapCodec<out EnvironmentalAttributeProvider<Value>> get() = TODO()
    fun getValue(base: Value, pos: Vec3, biomeInterpolator: SpatialAttributeInterpolator?): Value?

    companion object {
        fun <Value : Any> createCodec(type: EnvironmentAttribute<Value>): MapCodec<EnvironmentalAttributeProvider<Value>> {
            val mapper = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out EnvironmentalAttributeProvider<Value>>>()

            val modified = ModifiedAttributeProvider.createCodec(type)
            mapper.put("override", OverrideAttributeProvider.createCodec(type.type()))
            mapper.put("modified", modified)

            if (type.type().modifierLibrary() == AttributeModifier.INTEGER_LIBRARY || type.type().modifierLibrary() == AttributeModifier.RGB_COLOR_LIBRARY || type.type().modifierLibrary() == AttributeModifier.ARGB_COLOR_LIBRARY) {
                mapper.put("config", CatharsisCodecs.getMapCodec<ConfigValueAttributeProvider>().unsafeCast())
            }

            return mapper.codec(Codec.STRING).optionalDispatch("type", EnvironmentalAttributeProvider<Value>::codec, modified) { it }
        }
    }
}
