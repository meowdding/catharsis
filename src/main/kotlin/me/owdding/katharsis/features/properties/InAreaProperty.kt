package me.owdding.katharsis.features.properties

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.features.area.Areas
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

@GenerateCodec
data class InAreaProperty(
    val area: Identifier,
) : ConditionalItemModelProperty{

    companion object {
        val ID = Katharsis.id("in_area")
        val CODEC = KatharsisCodecs.InAreaPropertyCodec
    }

    override fun type(): MapCodec<out ConditionalItemModelProperty> = CODEC
    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, displayContext: ItemDisplayContext): Boolean {
        return Areas.isPlayerInArea(area)
    }
}
