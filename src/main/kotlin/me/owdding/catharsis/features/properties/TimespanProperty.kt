package me.owdding.catharsis.features.properties

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.timespan.Timespans
import me.owdding.catharsis.generated.CatharsisCodecs
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
    @FieldName("timespan") @NamedCodec("catharsis_identifier") val identifier: Identifier,
) : ConditionalItemModelProperty {
    companion object {
        val ID = Catharsis.id("timespan")
        val CODEC = CatharsisCodecs.TimespanPropertyCodec
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
