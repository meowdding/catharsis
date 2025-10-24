package me.owdding.catharsis.features.properties

import me.owdding.catharsis.events.BootstrapConditionalPropertiesEvent
import me.owdding.catharsis.events.BootstrapNumericPropertiesEvent
import me.owdding.catharsis.events.BootstrapSelectPropertiesEvent
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription

@Module
object ItemProperties {

    @Subscription
    fun onBooleanProperties(event: BootstrapConditionalPropertiesEvent) {
        event.register(HoveredItemProperty.ID, HoveredItemProperty.CODEC)
        event.register(DataTypeProperties.ID, DataTypeProperties.ConditionalDataTypeItemProperty.CODEC)
    }

    @Subscription
    fun onSelectProperties(event: BootstrapSelectPropertiesEvent) {
        event.register(DataTypeProperties.ID, DataTypeProperties.SelectDataTypeItemProperty.TYPE)
        event.register(SkyBlockIslandProperty.ID, SkyBlockIslandProperty.TYPE)
        event.register(SkyBlockAreaProperty.ID, SkyBlockAreaProperty.TYPE)
        event.register(ItemModelProperty.ID, ItemModelProperty.TYPE)
    }

    @Subscription
    fun onRangeProperties(event: BootstrapNumericPropertiesEvent) {
        event.register(DataTypeProperties.ID, DataTypeProperties.RangeDataTypeItemProperty.CODEC)
    }
}
