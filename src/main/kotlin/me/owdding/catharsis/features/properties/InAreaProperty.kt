package me.owdding.catharsis.features.properties

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.area.Areas
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

@GenerateCodec
data class InAreaProperty(
    val area: ResourceLocation,
) : ConditionalItemModelProperty{

    companion object {
        val ID = Catharsis.id("in_area")
        val CODEC = CatharsisCodecs.InAreaPropertyCodec
    }

    override fun type(): MapCodec<out ConditionalItemModelProperty> = CODEC

    override fun get(
        stack: ItemStack,
        level: ClientLevel?,
        entity: LivingEntity?,
        seed: Int,
        displayContext: ItemDisplayContext,
    ): Boolean = Areas.isPlayerInArea(area)
}
