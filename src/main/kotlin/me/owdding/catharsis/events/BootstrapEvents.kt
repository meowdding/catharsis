package me.owdding.catharsis.events

import com.mojang.serialization.MapCodec
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import java.util.function.BiConsumer

data class BootstrapConditionalPropertiesEvent(
    private val consumer: BiConsumer<Identifier, MapCodec<out ConditionalItemModelProperty>>
) : SkyBlockEvent() {
    fun register(location: Identifier, codec: MapCodec<out ConditionalItemModelProperty>) = consumer.accept(location, codec)
}

data class BootstrapSelectPropertiesEvent(
    private val consumer: BiConsumer<Identifier, SelectItemModelProperty.Type<*, *>>
) : SkyBlockEvent() {
    fun register(location: Identifier, type: SelectItemModelProperty.Type<*, *>) = consumer.accept(location, type)
}

data class BootstrapNumericPropertiesEvent(
    private val consumer: BiConsumer<Identifier, MapCodec<out RangeSelectItemModelProperty>>
) : SkyBlockEvent() {
    fun register(location: Identifier, codec: MapCodec<out RangeSelectItemModelProperty>) = consumer.accept(location, codec)
}


data class BootstrapItemModelsEvent(
    private val consumer: BiConsumer<Identifier, MapCodec<out ItemModel.Unbaked>>
) : SkyBlockEvent() {
    fun register(location: Identifier, codec: MapCodec<out ItemModel.Unbaked>) = consumer.accept(location, codec)
}
