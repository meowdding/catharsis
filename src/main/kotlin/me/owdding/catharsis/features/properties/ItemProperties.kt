package me.owdding.catharsis.features.properties

import com.mojang.serialization.MapCodec
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
        register(DataTypeProperties.ID.withPrefix("is_").withSuffix("_present"), DataTypeProperties.DataTypePresentItemProperty.CODEC)
        register(DataTypeProperties.ID.withPrefix("has"), DataTypeProperties.DataTypePresentItemProperty.CODEC.let { MapCodec.of(it, it) { "HasDataType" } })
        register(GemstoneProperty.ID, GemstoneProperty.CODEC)
    }

    @Subscription
    private fun BootstrapSelectPropertiesEvent.onSelectProperties() {
        register(DataTypeProperties.ID, DataTypeProperties.SelectDataTypeItemProperty.TYPE)
        register(SkyBlockIslandProperty.ID, SkyBlockIslandProperty.TYPE)
        register(SkyBlockAreaProperty.ID, SkyBlockAreaProperty.TYPE)
        register(DungeonClassProperty.ID, DungeonClassProperty.TYPE)
        register(OwnerUuidProperty.ID, OwnerUuidProperty.TYPE)
        register(SkyBlockSeasonProperty.ID, SkyBlockSeasonProperty.TYPE)
        register(PetItemProperty.ID, PetItemProperty.TYPE)
        register(PetSkinProperty.ID, PetSkinProperty.TYPE)
    }

    @Subscription
    private fun BootstrapNumericPropertiesEvent.onRangeProperties() {
        register(DataTypeProperties.ID, DataTypeProperties.RangeDataTypeItemProperty.CODEC)
        register(SkyBlockDayProperty.ID, SkyBlockDayProperty.CODEC)
        register(SkyBlockHourProperty.ID, SkyBlockHourProperty.CODEC)
        register(EnchantedBookLevelProperty.ID, EnchantedBookLevelProperty.CODEC)
        register(RuneLevelProperty.ID, RuneLevelProperty.CODEC)
        register(PetCandyUsedProperty.ID, PetCandyUsedProperty.CODEC)
        register(PetExpProperty.ID, PetExpProperty.CODEC)
    }
}
