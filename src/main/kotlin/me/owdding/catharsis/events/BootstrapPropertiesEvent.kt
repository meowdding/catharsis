package me.owdding.catharsis.events

import com.mojang.serialization.MapCodec
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

private typealias ConsumerType<Type> = (location: ResourceLocation, codec: MapCodec<out Type>) -> Unit

data class BootstrapConditionalPropertiesEvent(private val consumer: ConsumerType<ConditionalItemModelProperty>) : SkyBlockEvent() {
    fun register(location: ResourceLocation, codec: MapCodec<out ConditionalItemModelProperty>) = consumer(location, codec)
}
data class BootstrapSelectPropertiesEvent(private val consumer: ConsumerType<SelectItemModelProperty<*>>) : SkyBlockEvent() {
    fun register(location: ResourceLocation, codec: MapCodec<out SelectItemModelProperty<*>>) = consumer(location, codec)
}
data class BootstrapNumericPropertiesEvent(private val consumer: ConsumerType<RangeSelectItemModelProperty>) : SkyBlockEvent() {
    fun register(location: ResourceLocation, codec: MapCodec<out RangeSelectItemModelProperty>) = consumer(location, codec)
}

