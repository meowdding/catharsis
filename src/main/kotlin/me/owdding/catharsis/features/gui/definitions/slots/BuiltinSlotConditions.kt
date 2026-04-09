package me.owdding.catharsis.features.gui.definitions.slots

import com.mojang.datafixers.util.Either
import com.mojang.serialization.MapCodec
import it.unimi.dsi.fastutil.ints.IntArraySet
import me.owdding.catharsis.features.gui.matchers.TextMatcher
import me.owdding.catharsis.features.imc.ImcHandler.isDisabled
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.extensions.extremesOf
import me.owdding.catharsis.utils.types.IntPredicate
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.FieldNames
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.Inline
import me.owdding.ktcodecs.NamedCodec
import me.owdding.ktcodecs.OptionalBoolean
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.getTexture
import kotlin.math.max
import kotlin.math.min

@GenerateCodec
data class SlotAllCondition(
    val conditions: List<SlotCondition>,
) : SlotCondition {
    constructor(vararg conditions: SlotCondition) : this(listOf(*conditions))

    override val codec = CatharsisCodecs.getMapCodec<SlotAllCondition>()
    override val cost: Int = this.conditions.sumOf(SlotCondition::cost) + 1

    override fun optimize(): SlotCondition = SlotAllCondition(
        this.conditions.map(SlotCondition::optimize).sortedBy(SlotCondition::cost)
    )
    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean = this.conditions.all { it.matches(slots, slot, stack) }
}

@GenerateCodec
data class SlotAnyCondition(
    val conditions: List<SlotCondition>,
) : SlotCondition {
    constructor(vararg conditions: SlotCondition) : this(listOf(*conditions))

    override val codec = CatharsisCodecs.getMapCodec<SlotAnyCondition>()
    override val cost: Int = this.conditions.sumOf(SlotCondition::cost) + 1

    override fun optimize(): SlotCondition = SlotAnyCondition(
        this.conditions.map(SlotCondition::optimize).sortedBy(SlotCondition::cost)
    )
    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean = this.conditions.any { it.matches(slots, slot, stack) }
}

@GenerateCodec
data class SlotNotCondition(
    val condition: SlotCondition,
) : SlotCondition {

    override val codec = CatharsisCodecs.getMapCodec<SlotNotCondition>()
    override val cost: Int = this.condition.cost

    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean = !this.condition.matches(slots, slot, stack)
    override fun optimize(): SlotCondition = if (this.condition is SlotNotCondition) this.condition.condition.optimize() else this
}

@GenerateCodec
data class SlotIndexCondition(
    val slot: IntPredicate,
) : SlotCondition {
    constructor(index: Int) : this(IntPredicate.Set(IntArraySet(setOf(index))))
    constructor(vararg index: Int) : this(IntPredicate.Set(IntArraySet(index)))
    constructor(range: IntRange) : this(IntPredicate.Range(range.min(), range.max()))

    override val codec = CatharsisCodecs.getMapCodec<SlotIndexCondition>()
    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean = slot == -1 || slot in this.slot
}

@GenerateCodec
data class SlotSkyBlockIdCondition(
    val ids: Set<String>,
) : SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotSkyBlockIdCondition>()
    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean = this.ids.contains(stack.getData(DataTypes.API_ID))
}

@GenerateCodec
data class SlotItemCondition(
    @Compact val items: Set<Item>,
) : SlotCondition {
    constructor(vararg item: Item) : this(setOf(*item))

    override val codec = CatharsisCodecs.getMapCodec<SlotItemCondition>()
    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean = stack.item in this.items
}

@GenerateCodec
data class SlotNameCondition(
    @Inline @NamedCodec("text_matcher") val matcher: TextMatcher,
) : SlotCondition {

    override val codec = CatharsisCodecs.getMapCodec<SlotNameCondition>()
    override val cost: Int = this.matcher.cost

    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean = this.matcher.matches(stack.cleanName)

}

@GenerateCodec
data class SlotLoreCondition(
    @Inline @NamedCodec("text_matcher") val matcher: TextMatcher,
    @FieldNames("line", "lines") val line: Either<Int, LineRange> = Either.right(LineRange()),
) : SlotCondition {

    val from: Int get() = this.line.map({ it }, { it.from })
    val to: Int get() = this.line.map({ it }, { it.to })

    override val codec = CatharsisCodecs.getMapCodec<SlotLoreCondition>()
    override val cost: Int = this.matcher.cost

    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean {
        val lines = stack.getRawLore().takeUnless(List<*>::isEmpty) ?: return false

        val start = min((lines.size + this.from) % lines.size, (lines.size + this.to) % lines.size)
        val end = max((lines.size + this.from) % lines.size, (lines.size + this.to) % lines.size)

        return when {
            start < 0 || end >= lines.size -> false
            start == end -> this.matcher.matches(lines[start])
            else -> this.matcher.matches(lines.subList(start, end + 1).joinToString("\n"))
        }
    }

    @GenerateCodec
    data class LineRange(val from: Int = 0, val to: Int = -1)
}

@GenerateCodec
data class SlotIslandCondition(
    val islands: Set<SkyBlockIsland>,
) : SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotIslandCondition>()
    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean = SkyBlockIsland.inAnyIsland(islands)
}

@GenerateCodec
data class SlotTextureCondition(
    @Compact val textures: Set<String>,
) : SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotTextureCondition>()
    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean {
        val texture = stack.getTexture() ?: return false
        return texture in this.textures
    }
}

@GenerateCodec
data class HasComponentCondition(
    val component: DataComponentType<*>,
) : SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<HasComponentCondition>()
    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean = stack.has(component)
}

data object IsTooltipHiddenCondition : SlotCondition {
    override val codec: MapCodec<IsTooltipHiddenCondition> = MapCodec.unit { this }
    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean = stack.get(DataComponents.TOOLTIP_DISPLAY)?.hideTooltip == true
}

@GenerateCodec
data class RelativeSlotCondition(
    val offset: Int,
    val condition: SlotCondition,
) : SlotCondition {
    override val codec: MapCodec<out SlotCondition> = CatharsisCodecs.getMapCodec<RelativeSlotCondition>()
    override fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean {
        val movedSlot = slots.find { it.index == slot + offset }?.takeUnless { it.item.isDisabled() } ?: return false
        return condition.matches(slots, movedSlot.index, movedSlot.item)
    }
}

@GenerateCodec
data class SlotBorderCondition(
    @FieldNames("top", "allow_top") @OptionalBoolean(true) val allowTop: Boolean = true,
    @FieldNames("bottom", "allow_bottom") @OptionalBoolean(true) val allowBottom: Boolean = true,
    @FieldNames("left", "allow_left") @OptionalBoolean(true) val allowLeft: Boolean = true,
    @FieldNames("right", "allow_right") @OptionalBoolean(true) val allowRight: Boolean = true,
) : SlotCondition {
    override val codec: MapCodec<SlotBorderCondition> = CatharsisCodecs.getMapCodec<SlotBorderCondition>()
    override fun matches(slots: List<Slot>, slot: Int,  stack: ItemStack): Boolean {
        val slot = slots.getOrNull(slot) ?: return false
        val (minX, maxX) = slots.extremesOf { it.x } ?: return true
        val (minY, maxY) = slots.extremesOf { it.y } ?: return true

        return when {
            minX == slot.x && !allowLeft -> false
            maxX == slot.x && !allowRight -> false
            minY == slot.y && !allowTop -> false
            maxY == slot.y && !allowBottom -> false
            else -> true
        }
    }
}
