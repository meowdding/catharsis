//~ item_holder
package me.owdding.catharsis.features.armor.models

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

class RedirectedArmorModel(
    private val slot: EquipmentSlot,
    private val model: ArmorModel,
) : ArmorModel {

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): ArmorModelState {
        val newStack = owner?.asLivingEntity()?.getItemBySlot(this.slot) ?: ItemStack.EMPTY
        return this.model.resolve(newStack, level, owner, seed)
    }

    @GenerateCodec
    data class UnbakedRedirect(
        val slot: EquipmentSlot,
        val model: ArmorModel.Unbaked,
    ) : ArmorModel.Unbaked {

        override val codec: MapCodec<out ArmorModel.Unbaked> = CatharsisCodecs.getMapCodec<UnbakedRedirect>()

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorModel {
            return RedirectedArmorModel(this.slot, this.model.bake(swapper, resources))
        }
    }
}


