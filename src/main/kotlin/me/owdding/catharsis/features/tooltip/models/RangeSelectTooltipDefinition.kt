package me.owdding.catharsis.features.tooltip.models

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.tooltip.TooltipDefinition
import me.owdding.catharsis.features.tooltip.TooltipDefinitions
import me.owdding.catharsis.utils.TypedResourceManager
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.resources.Identifier
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack
import java.util.*
import kotlin.jvm.optionals.getOrNull

class RangeSelectTooltipDefinition(
    private val property: RangeSelectItemModelProperty,
    private val scale: Float,
    private val thresholds: FloatArray,
    private val models: Array<TooltipDefinition>,
    private val fallback: TooltipDefinition?,
) : TooltipDefinition {

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

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?): Identifier? {
        val value = property.get(stack, level, owner?.asLivingEntity(), 0) * scale
        val model = if (value.isNaN()) fallback else models.getOrNull(lastIndexLessThanOrEqual(value)) ?: fallback
        return model?.resolve(stack, level, owner)
    }

    class Unbaked(
        val property: RangeSelectItemModelProperty,
        val scale: Float,
        val entries: List<Pair<Float, TooltipDefinition.Unbaked>>,
        val fallback: Optional<TooltipDefinition.Unbaked>,
    ) : TooltipDefinition.Unbaked {

        override val codec: MapCodec<out TooltipDefinition.Unbaked> = CODEC

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): TooltipDefinition {
            val sortedEntries = entries.sortedWith(Comparator.comparingDouble { it.first.toDouble() })
            val thresholds = FloatArray(sortedEntries.size) { i -> sortedEntries[i].first }
            val models = Array(sortedEntries.size) { i -> sortedEntries[i].second.bake(swapper, resources) }
            val fallback = fallback.map { it.bake(swapper, resources) }.getOrNull()

            return RangeSelectTooltipDefinition(property, scale, thresholds, models, fallback)
        }

        companion object {

            val ENTRY_CODEC: Codec<Pair<Float, TooltipDefinition.Unbaked>> = RecordCodecBuilder.create { it.group(
                Codec.FLOAT.fieldOf("threshold").forGetter { p -> p.first },
                TooltipDefinitions.CODEC.fieldOf("model").forGetter { p -> p.second },
            ).apply(it, ::Pair) }

            val CODEC: MapCodec<Unbaked> = RecordCodecBuilder.mapCodec { it.group(
                RangeSelectItemModelProperties.MAP_CODEC.forGetter(Unbaked::property),
                Codec.FLOAT.optionalFieldOf("scale", 1f).forGetter(Unbaked::scale),
                ENTRY_CODEC.listOf().fieldOf("entries").forGetter(Unbaked::entries),
                TooltipDefinitions.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback),
            ).apply(it, ::Unbaked) }
        }
    }
}
