package me.owdding.catharsis.features.properties

import com.mojang.datafixers.util.Either
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.FieldNames
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
    @FieldNames("enchantment_name", "enchant_name") val enchantmentName: String,
    @FieldNames("enchantment_lvl", "enchant_lvl", "enchantment_level", "enchant_level") val enchantmentLevel: Either<Int, InclusiveRange<Int>>,
) : ConditionalItemModelProperty {

    companion object {
        val ID = Catharsis.id("enchantment")
        val CODEC = CatharsisCodecs.getMapCodec<EnchantmentProperty>()
    }

    override fun type(): MapCodec<EnchantmentProperty> = CODEC

    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, context: ItemDisplayContext): Boolean {
        val enchants = stack.getData(DataTypes.ENCHANTMENTS)?.takeUnless { it.isEmpty() } ?: return false
        val level = enchants[enchantmentName] ?: return false
        return enchantmentLevel.left().getOrNull()?.let { it == level } ?: enchantmentLevel.right().getOrNull()?.isValueInRange(level) ?: false
    }
}
