package me.owdding.katharsis.features.properties

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.features.timespan.Timespans
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

@GenerateCodec
data class TimespanProperty(
    @FieldName("timespan") @NamedCodec("katharsis_identifier") val identifier: Identifier,
) : ConditionalItemModelProperty {
    companion object {
        val ID = Katharsis.id("timespan")
        val CODEC = KatharsisCodecs.TimespanPropertyCodec
    }

    val timespan by lazy {
        Timespans.getLoadedTimespans()[identifier]?.apply {
            markUsed()
        } ?: run {
            ItemProperties.warn("Requested unknown timespan $identifier!")
            null
        }
    }

    override fun type(): MapCodec<out ConditionalItemModelProperty> = CODEC

    override fun get(
        stack: ItemStack,
        level: ClientLevel?,
        entity: LivingEntity?,
        seed: Int,
        displayContext: ItemDisplayContext
    ): Boolean = timespan?.test() == true
}
