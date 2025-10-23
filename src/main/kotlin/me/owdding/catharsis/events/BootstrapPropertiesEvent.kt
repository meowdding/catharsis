package me.owdding.catharsis.events

import com.mojang.serialization.MapCodec
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import java.util.function.BiConsumer

data class BootstrapConditionalPropertiesEvent(
    private val consumer: BiConsumer<ResourceLocation, MapCodec<out ConditionalItemModelProperty>>
) : SkyBlockEvent() {
    fun register(location: ResourceLocation, codec: MapCodec<out ConditionalItemModelProperty>) = consumer.accept(location, codec)
}

data class BootstrapSelectPropertiesEvent(
    private val consumer: BiConsumer<ResourceLocation, SelectItemModelProperty.Type<*, *>>
) : SkyBlockEvent() {
    fun register(location: ResourceLocation, type: SelectItemModelProperty.Type<*, *>) = consumer.accept(location, type)
}

data class BootstrapNumericPropertiesEvent(
    private val consumer: BiConsumer<ResourceLocation, MapCodec<out RangeSelectItemModelProperty>>
) : SkyBlockEvent() {
    fun register(location: ResourceLocation, codec: MapCodec<out RangeSelectItemModelProperty>) = consumer.accept(location, codec)
}

