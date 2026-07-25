package me.owdding.katharsis.features.properties

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import java.util.*
import me.owdding.katharsis.Katharsis
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.core.UUIDUtil
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

object OwnerUuidProperty : SelectItemModelProperty<UUID> {

    val ID = Katharsis.id("owner_uuid")
    val TYPE: SelectItemModelProperty.Type<out SelectItemModelProperty<UUID>, UUID> = SelectItemModelProperty.Type.create(
        MapCodec.unit { OwnerUuidProperty },
        UUIDUtil.STRING_CODEC
    )

    override fun get(
        stack: ItemStack,
        level: ClientLevel?,
        entity: LivingEntity?,
        seed: Int,
        displayContext: ItemDisplayContext,
    ): UUID? = entity?.uuid

    override fun valueCodec(): Codec<UUID> = UUIDUtil.STRING_CODEC

    override fun type(): SelectItemModelProperty.Type<out SelectItemModelProperty<UUID>, UUID> = TYPE
}
