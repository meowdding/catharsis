package me.owdding.katharsis.features.properties

import com.mojang.datafixers.util.Either
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.util.InclusiveRange
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import kotlin.jvm.optionals.getOrNull

@GenerateCodec
data class EnchantmentProperty(
    @FieldName("enchant_name") val enchantmentName: String,
    @FieldName("enchant_lvl") val enchantmentLevel: Either<Int, InclusiveRange<Int>>,
) : ConditionalItemModelProperty {

    companion object {
        val ID = Katharsis.id("enchantment")
        val CODEC = KatharsisCodecs.getMapCodec<EnchantmentProperty>()
    }

    override fun type(): MapCodec<EnchantmentProperty> = CODEC

    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, context: ItemDisplayContext): Boolean {
        val enchants = stack.getData(DataTypes.ENCHANTMENTS)?.takeUnless { it.isEmpty() } ?: return false
        val level = enchants[enchantmentName] ?: return false
        return enchantmentLevel.left().getOrNull()?.let { it == level } ?: enchantmentLevel.right().getOrNull()?.isValueInRange(level) ?: false
    }
}
