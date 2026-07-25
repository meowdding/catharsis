package me.owdding.katharsis.features.properties

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneQuality
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneSlot
import tech.thatgravyboat.skyblockapi.api.datatype.getData

@GenerateCodec
data class GemstoneProperty(
    val amount: Int,
    val slot: GemstoneSlot?,
    val quality: GemstoneQuality?,
) : ConditionalItemModelProperty {

    companion object {
        val ID = Katharsis.id("has_gemstones")
        val CODEC = KatharsisCodecs.getMapCodec<GemstoneProperty>()
    }

    override fun type(): MapCodec<out ConditionalItemModelProperty> = CODEC
    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, displayContext: ItemDisplayContext): Boolean {
        val gemstones = stack.getData(DataTypes.GEMSTONES) ?: return false
        if (gemstones.size < amount) return false
        var amount = amount

        for (gemstone in gemstones) {
            if (quality != null && gemstone.quality != quality) continue
            if (slot != null && (slot != GemstoneSlot.UNIVERSAL && gemstone.gemstone !in slot.gemstones)) continue
            amount--
            if (amount == 0) {
                return true
            }
        }

        return false
    }
}
