package me.owdding.catharsis.features.properties

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.Catharsis
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class AnyConditionalItemProperty(
    val conditions: List<ConditionalItemModelProperty>,
) : ConditionalItemModelProperty {

    override fun type(): MapCodec<AnyConditionalItemProperty> = CODEC
    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, context: ItemDisplayContext): Boolean {
        return conditions.any { it.get(stack, level, entity, seed, context) }
    }

    companion object {
        val ID = Catharsis.id("any")
        val CODEC: MapCodec<AnyConditionalItemProperty> = RecordCodecBuilder.mapCodec { it.group(
            ConditionalItemModelProperties.MAP_CODEC.codec().listOf().fieldOf("conditions").forGetter(AnyConditionalItemProperty::conditions)
        ).apply(it, ::AnyConditionalItemProperty) }
    }
}

class AllConditionalItemProperty(
    val conditions: List<ConditionalItemModelProperty>,
) : ConditionalItemModelProperty {

    override fun type(): MapCodec<AllConditionalItemProperty> = CODEC
    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, context: ItemDisplayContext): Boolean {
        return conditions.all { it.get(stack, level, entity, seed, context) }
    }

    companion object {
        val ID = Catharsis.id("all")
        val CODEC: MapCodec<AllConditionalItemProperty> = RecordCodecBuilder.mapCodec { it.group(
            ConditionalItemModelProperties.MAP_CODEC.codec().listOf().fieldOf("conditions").forGetter(AllConditionalItemProperty::conditions)
        ).apply(it, ::AllConditionalItemProperty) }
    }
}
