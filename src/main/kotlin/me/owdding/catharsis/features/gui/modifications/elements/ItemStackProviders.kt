package me.owdding.catharsis.features.gui.modifications.elements

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktmodules.Module
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.profile.items.equipment.EquipmentAPI
import tech.thatgravyboat.skyblockapi.api.profile.items.equipment.EquipmentSlot
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

interface ItemStackProvider {
    val codec: MapCodec<out ItemStackProvider>
    fun getItemStack(): ItemStack
}

@Module
object ItemStackProviders {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out ItemStackProvider>>()

    @IncludedCodec
    val CODEC: Codec<ItemStackProvider> = ID_MAPPER.codec(Identifier.CODEC).dispatch(ItemStackProvider::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("json"), CatharsisCodecs.getMapCodec<JsonItemStackProvider>())
        ID_MAPPER.put(Catharsis.id("sbid"), CatharsisCodecs.getMapCodec<SbidItemStackProvider>())
        ID_MAPPER.put(Catharsis.id("equipment"), CatharsisCodecs.getMapCodec<EquipmentItemStackProvider>())
    }
}

@GenerateCodec
data class JsonItemStackProvider(val stack: ItemStack) : ItemStackProvider {
    override val codec: MapCodec<JsonItemStackProvider> = CatharsisCodecs.getMapCodec<JsonItemStackProvider>()

    override fun getItemStack(): ItemStack = stack
}

@GenerateCodec
data class SbidItemStackProvider(val id: SkyBlockId) : ItemStackProvider {
    override val codec: MapCodec<SbidItemStackProvider> = CatharsisCodecs.getMapCodec<SbidItemStackProvider>()

    override fun getItemStack(): ItemStack = id.toItem()
}

@GenerateCodec
data class EquipmentItemStackProvider(val slot: EquipmentSlot) : ItemStackProvider {
    override val codec: MapCodec<EquipmentItemStackProvider> = CatharsisCodecs.getMapCodec<EquipmentItemStackProvider>()

    override fun getItemStack(): ItemStack = EquipmentAPI.getIslandEquipment(slot)
}
