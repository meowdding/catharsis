package me.owdding.catharsis.features.armor.models

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack
import java.util.*
import kotlin.jvm.optionals.getOrNull

class RangeSelectArmorModel(
    private val property: RangeSelectItemModelProperty,
    private val scale: Float,
    private val thresholds: FloatArray,
    private val models: Array<ArmorModel>,
    private val fallback: ArmorModel?,
) : ArmorModel {

    private fun lastIndexLessThanOrEqual(value: Float): Int {
        if (thresholds.size < 16) {
            for (i in thresholds.indices) {
                if (thresholds[i] > value) {
                    return i - 1
                }
            }
            return thresholds.size - 1
        } else {
            val i = thresholds.binarySearch(value)
            return if (i < 0) i.inv() - 1 else i
        }
    }

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): ResourceLocation {
        val value = property.get(stack, level, owner?.asLivingEntity(), seed) * scale
        val model = if (value.isNaN()) fallback else models.getOrNull(lastIndexLessThanOrEqual(value)) ?: fallback
        return model?.resolve(stack, level, owner, seed) ?: ArmorModels.MISSING_TEXTURE
    }

    class Unbaked(
        val property: RangeSelectItemModelProperty,
        val scale: Float,
        val entries: List<Pair<Float, ArmorModel.Unbaked>>,
        val fallback: Optional<ArmorModel.Unbaked>,
    ) : ArmorModel.Unbaked {

        override val codec: MapCodec<out ArmorModel.Unbaked> = CODEC

        override fun bake(swapper: RegistryContextSwapper?): ArmorModel {
            val sortedEntries = entries.sortedWith(Comparator.comparingDouble { it.first.toDouble() })
            val thresholds = FloatArray(sortedEntries.size) { i -> sortedEntries[i].first }
            val models = Array(sortedEntries.size) { i -> sortedEntries[i].second.bake(swapper) }
            val fallback = fallback.map { it.bake(swapper) }.getOrNull()

            return RangeSelectArmorModel(property, scale, thresholds, models, fallback)
        }

        companion object {

            val ENTRY_CODEC: Codec<Pair<Float, ArmorModel.Unbaked>> = RecordCodecBuilder.create { it.group(
                Codec.FLOAT.fieldOf("threshold").forGetter { p -> p.first },
                ArmorModels.CODEC.fieldOf("model").forGetter { p -> p.second },
            ).apply(it, ::Pair) }

            val CODEC: MapCodec<Unbaked> = RecordCodecBuilder.mapCodec { it.group(
                RangeSelectItemModelProperties.MAP_CODEC.forGetter(Unbaked::property),
                Codec.FLOAT.optionalFieldOf("scale", 1f).forGetter(Unbaked::scale),
                ENTRY_CODEC.listOf().fieldOf("entries").forGetter(Unbaked::entries),
                ArmorModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback),
            ).apply(it, ::Unbaked) }
        }
    }
}
