package me.owdding.catharsis.features.properties

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

object ItemModelProperty : SelectItemModelProperty<ResourceLocation> {

    val ID = Catharsis.id("item_model")
    val CODEC: MapCodec<ResourceLocation> = Codec.STRING.fieldOf("item_model").xmap(
        { loc -> ResourceLocation.parse(loc) },
        { property -> property.toString() },
    )
    val TYPE: SelectItemModelProperty.Type<out SelectItemModelProperty<ResourceLocation>, ResourceLocation> =
        SelectItemModelProperty.Type.create(MapCodec.unit { ItemModelProperty }, ResourceLocation.CODEC)

    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, displayContext: ItemDisplayContext): ResourceLocation {
        return stack.get(DataComponents.ITEM_MODEL) ?: BuiltInRegistries.ITEM.getKey(stack.item)
    }

    override fun valueCodec(): Codec<ResourceLocation> = ResourceLocation.CODEC
    override fun type(): SelectItemModelProperty.Type<out SelectItemModelProperty<ResourceLocation>, ResourceLocation> = TYPE
}
