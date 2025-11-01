package me.owdding.catharsis.features.properties

import me.owdding.catharsis.events.BootstrapConditionalPropertiesEvent
import me.owdding.catharsis.events.BootstrapNumericPropertiesEvent
import me.owdding.catharsis.events.BootstrapSelectPropertiesEvent
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription

@Module
object ItemProperties {

    @Subscription
    private fun BootstrapConditionalPropertiesEvent.onBooleanProperties() {
        register(HoveredItemProperty.ID, HoveredItemProperty.CODEC)
        register(DataTypeProperties.ID, DataTypeProperties.ConditionalDataTypeItemProperty.CODEC)
        register(AnyConditionalItemProperty.ID, AnyConditionalItemProperty.CODEC)
        register(AllConditionalItemProperty.ID, AllConditionalItemProperty.CODEC)
        register(InAreaProperty.ID, InAreaProperty.CODEC)
    }

    @Subscription
    private fun BootstrapSelectPropertiesEvent.onSelectProperties() {
        register(DataTypeProperties.ID, DataTypeProperties.SelectDataTypeItemProperty.TYPE)
        register(SkyBlockIslandProperty.ID, SkyBlockIslandProperty.TYPE)
        register(SkyBlockAreaProperty.ID, SkyBlockAreaProperty.TYPE)
    }

    @Subscription
    private fun BootstrapNumericPropertiesEvent.onRangeProperties() {
        register(DataTypeProperties.ID, DataTypeProperties.RangeDataTypeItemProperty.CODEC)
    }
}
