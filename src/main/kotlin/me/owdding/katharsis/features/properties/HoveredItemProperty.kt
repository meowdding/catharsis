package me.owdding.katharsis.features.properties

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.hooks.items.AbstractContainerScreenHook
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

object HoveredItemProperty : ConditionalItemModelProperty {

    val ID: Identifier = Katharsis.id("hovered")
    val CODEC: MapCodec<HoveredItemProperty> = MapCodec.unit(HoveredItemProperty)

    override fun type(): MapCodec<HoveredItemProperty> = CODEC

    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, context: ItemDisplayContext): Boolean {
        return context == ItemDisplayContext.GUI && AbstractContainerScreenHook.HOVERED.get() == true
    }
}
