package me.owdding.catharsis.features.properties

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

object SkyBlockIslandProperty : SelectItemModelProperty<String> {

    val ID = Catharsis.id("skyblock_island")
    val CODEC: MapCodec<SkyBlockIsland> = Codec.STRING.fieldOf("skyblock_island").xmap(
        { id -> SkyBlockIsland.getById(id) },
        { property -> property.id }
    )
    val TYPE: SelectItemModelProperty.Type<out SelectItemModelProperty<String>, String> = SelectItemModelProperty.Type.create(MapCodec.unit { SkyBlockIslandProperty }, Codec.STRING)

    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, displayContext: ItemDisplayContext): String {
        return LocationAPI.island?.id ?: ""
    }

    override fun valueCodec(): Codec<String> = Codec.STRING
    override fun type(): SelectItemModelProperty.Type<out SelectItemModelProperty<String>, String> = TYPE
}
