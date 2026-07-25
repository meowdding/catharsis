package me.owdding.katharsis.features.gui.modifications.elements.conditions

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.features.gui.definitions.GuiDefinitions
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import tech.thatgravyboat.skyblockapi.helpers.McScreen

interface GuiElementCondition {
    val codec: MapCodec<out GuiElementCondition>
    fun check(): Boolean
}

@GenerateCodec
data class HasSlotGuiElementCondition(val slot: Identifier) : GuiElementCondition {
    override val codec: MapCodec<out GuiElementCondition> = KatharsisCodecs.getMapCodec<HasSlotGuiElementCondition>()
    override fun check(): Boolean {
        val menu = McScreen.asMenu?.menu ?: return false
        return menu.slots.any { GuiDefinitions.getSlot(it.index) == slot }
    }
}

@GenerateCodec
data class AndGuiElementCondition(val conditions: List<GuiElementCondition>) : GuiElementCondition {
    override val codec: MapCodec<out GuiElementCondition> = KatharsisCodecs.getMapCodec<AndGuiElementCondition>()
    override fun check(): Boolean = conditions.all { it.check() }
}

@GenerateCodec
data class OrGuiElementCondition(val conditions: List<GuiElementCondition>) : GuiElementCondition {
    override val codec: MapCodec<out GuiElementCondition> = KatharsisCodecs.getMapCodec<OrGuiElementCondition>()
    override fun check(): Boolean = conditions.any { it.check() }
}

@GenerateCodec
data class XorGuiElementCondition(val conditions: List<GuiElementCondition>) : GuiElementCondition {
    override val codec: MapCodec<out GuiElementCondition> = KatharsisCodecs.getMapCodec<XorGuiElementCondition>()
    override fun check(): Boolean = conditions.count { it.check() } == 1
}

@GenerateCodec
data class NotGuiElementCondition(val condition: GuiElementCondition) : GuiElementCondition {
    override val codec: MapCodec<out GuiElementCondition> = KatharsisCodecs.getMapCodec<NotGuiElementCondition>()
    override fun check(): Boolean = !condition.check()
}

object GuiElementConditions {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out GuiElementCondition>>()

    @IncludedCodec
    val CODEC: MapCodec<GuiElementCondition> = ID_MAPPER.codec(IncludedCodecs.katharsisIdentifier)
        .dispatchMap(GuiElementCondition::codec) { it }

    init {
        ID_MAPPER.put(Katharsis.id("has_slot"), KatharsisCodecs.getMapCodec<HasSlotGuiElementCondition>())
        ID_MAPPER.put(Katharsis.id("and"), KatharsisCodecs.getMapCodec<AndGuiElementCondition>())
        ID_MAPPER.put(Katharsis.id("or"), KatharsisCodecs.getMapCodec<OrGuiElementCondition>())
        ID_MAPPER.put(Katharsis.id("xor"), KatharsisCodecs.getMapCodec<XorGuiElementCondition>())
        ID_MAPPER.put(Katharsis.id("not"), KatharsisCodecs.getMapCodec<NotGuiElementCondition>())
    }
}
