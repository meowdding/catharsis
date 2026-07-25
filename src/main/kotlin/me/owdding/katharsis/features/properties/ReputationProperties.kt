package me.owdding.katharsis.features.properties

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.profile.reputation.Faction
import tech.thatgravyboat.skyblockapi.api.profile.reputation.ReputationAPI

object SelectedFactionProperty : SelectItemModelProperty<Faction> {
    val ID = Katharsis.id("selected_faction")
    val TYPE: SelectItemModelProperty.Type<out SelectItemModelProperty<Faction>, Faction> = SelectItemModelProperty.Type.create(
        MapCodec.unit { SelectedFactionProperty },
        KatharsisCodecs.getCodec(),
    )

    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, displayContext: ItemDisplayContext): Faction? = ReputationAPI.currentFaction

    override fun valueCodec(): Codec<Faction> = KatharsisCodecs.getCodec()

    override fun type(): SelectItemModelProperty.Type<out SelectItemModelProperty<Faction>, Faction> = TYPE
}

object FactionReputationProperty : RangeSelectItemModelProperty {
    val ID = Katharsis.id("faction_reputation")
    val CODEC: MapCodec<FactionReputationProperty> = MapCodec.unit { FactionReputationProperty }

    override fun get(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): Float {
        return ReputationAPI.currentReputation.toFloat()
    }

    override fun type(): MapCodec<out RangeSelectItemModelProperty> = CODEC
}
