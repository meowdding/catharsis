package me.owdding.katharsis.features.gui.modifications.elements

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktmodules.Module
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.item.ItemStack
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
        ID_MAPPER.put(Katharsis.id("json"), KatharsisCodecs.getMapCodec<JsonItemStackProvider>())
        ID_MAPPER.put(Katharsis.id("sbid"), KatharsisCodecs.getMapCodec<SbidItemStackProvider>())
    }
}

@GenerateCodec
data class JsonItemStackProvider(val stack: ItemStack) : ItemStackProvider {
    override val codec: MapCodec<JsonItemStackProvider> = KatharsisCodecs.getMapCodec<JsonItemStackProvider>()

    override fun getItemStack(): ItemStack = stack
}

@GenerateCodec
data class SbidItemStackProvider(val id: SkyBlockId) : ItemStackProvider {
    override val codec: MapCodec<SbidItemStackProvider> = KatharsisCodecs.getMapCodec<SbidItemStackProvider>()

    override fun getItemStack(): ItemStack = id.toItem()
}
