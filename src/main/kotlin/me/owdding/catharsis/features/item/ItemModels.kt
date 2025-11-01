package me.owdding.catharsis.features.item

import me.owdding.catharsis.events.BootstrapItemModelsEvent
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription

@Module
object ItemModels {

    @Subscription
    fun onItemModelBootstrap(event: BootstrapItemModelsEvent) {
        event.register(RedirectedItemModel.Unbaked.ID, RedirectedItemModel.Unbaked.CODEC)
    }
}
