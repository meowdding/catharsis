package me.owdding.catharsis.features.timespan

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import tech.thatgravyboat.skyblockapi.helpers.McLevel

interface TimespanDefinition {

    var isInUse: Boolean
    val codec: MapCodec<out TimespanDefinition>

    fun test(): Boolean
    fun tick()
    fun consumeRebuild(): Boolean
    fun markUsed() {
        isInUse = true
    }

}

@GenerateCodec
data class SimpleTimespan(
    val begin: Int,
    val end: Int,
) : TimespanDefinition {
    private var lastState: Boolean = false
        set(value) {
            if (field != value) {
                needsRebuild = true
                field = value
            }
        }
    private var needsRebuild = false
    override var isInUse: Boolean = false
    override val codec: MapCodec<SimpleTimespan> = CatharsisCodecs.SimpleTimespanCodec

    override fun test(): Boolean = lastState

    override fun tick() {
        if (!McLevel.hasLevel) return
        val time = McLevel.self.dayTime % 24_000

        lastState = time in begin..end
    }

    override fun consumeRebuild(): Boolean {
		if (needsRebuild) {
	        needsRebuild = false
	        return true
	    }
	    return false
    }
}

object TimespanDefinitions {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out TimespanDefinition>>()

    val CODEC: MapCodec<TimespanDefinition> = ID_MAPPER.codec(IncludedCodecs.catharsisIdentifier).dispatchMap(TimespanDefinition::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("simple"), CatharsisCodecs.getMapCodec<SimpleTimespan>())
    }

}
