package me.owdding.catharsis.features.environment.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.environment.conditions.TypelessEnvironmentalModifierCondition.Companion.addTypeless
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.extensions.unsafeCast
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.phys.Vec3

interface TypelessEnvironmentalModifierCondition : EnvironmentalModifierCondition<Any> {
    override val codec: MapCodec<out TypelessEnvironmentalModifierCondition>

    fun applies(pos: Vec3): Boolean

    override fun applies(baseValue: Any, pos: Vec3): Boolean = applies(pos)
    fun <Type : Any> asTyped(): EnvironmentalModifierCondition<Type> = this.unsafeCast()

    companion object {
        val mapper = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out TypelessEnvironmentalModifierCondition>>()

        fun <Value : Any> ExtraCodecs.LateBoundIdMapper<String, MapCodec<out EnvironmentalModifierCondition<Value>>>.addTypeless() {
            put("true", ConstantCondition.True.codec.unsafeCast())
            put("false", ConstantCondition.False.codec.unsafeCast())
            put("in_area", CatharsisCodecs.EnvironmentalAreaConditionCodec.unsafeCast())
        }

        init {
            mapper.unsafeCast<ExtraCodecs.LateBoundIdMapper<String, MapCodec<out EnvironmentalModifierCondition<Any>>>>().addTypeless()
        }

        val CODEC: MapCodec<TypelessEnvironmentalModifierCondition> = mapper.codec(Codec.STRING).dispatchMap(TypelessEnvironmentalModifierCondition::codec) { it }
    }

}

interface EnvironmentalModifierCondition<Value : Any> {
    val codec: MapCodec<out EnvironmentalModifierCondition<Value>>

    fun applies(baseValue: Value, pos: Vec3): Boolean

    companion object {
        fun <Value : Any> createCodec(typeCodec: Codec<Value>): MapCodec<EnvironmentalModifierCondition<Value>> {
            val mapper = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out EnvironmentalModifierCondition<Value>>>()

            mapper.addTypeless()

            return mapper.codec(Codec.STRING).dispatchMap(EnvironmentalModifierCondition<Value>::codec) { it }
        }
    }
}
