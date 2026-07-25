package me.owdding.katharsis.features.item

import me.owdding.katharsis.events.BootstrapItemModelsEvent
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription

@Module
object ItemModels {

    @Subscription
    fun onItemModelBootstrap(event: BootstrapItemModelsEvent) {
        event.register(RedirectedItemModel.Unbaked.ID, RedirectedItemModel.Unbaked.CODEC)
        event.register(FallThroughItemModel.Unbaked.ID, FallThroughItemModel.Unbaked.CODEC)
        event.register(GlintItemModel.Unbaked.ID, GlintItemModel.Unbaked.CODEC)
    }
}
