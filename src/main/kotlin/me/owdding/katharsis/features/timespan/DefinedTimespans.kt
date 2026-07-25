package me.owdding.katharsis.features.timespan

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldNames
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import tech.thatgravyboat.skyblockapi.api.datetime.DateTimeAPI
import tech.thatgravyboat.skyblockapi.api.datetime.SkyBlockSeason
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
    override val codec: MapCodec<SimpleTimespan> = KatharsisCodecs.SimpleTimespanCodec

    override fun test(): Boolean = lastState

    override fun tick() {
        val level = McLevel.selfOrNull ?: return
        val time = level.defaultClockTime % 24_000

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

@GenerateCodec
data class SeasonTimespan(@Compact @FieldNames("season", "seasons") val seasons: Set<SkyBlockSeason>) : TimespanDefinition {
    private var lastState: Boolean = false
        set(value) {
            if (field != value) {
                needsRebuild = true
                field = value
            }
        }

    private var needsRebuild = false

    override var isInUse: Boolean = false
    override val codec: MapCodec<out TimespanDefinition> = KatharsisCodecs.getMapCodec<SeasonTimespan>()


    override fun test(): Boolean = lastState

    override fun tick() {
        DateTimeAPI.season in seasons
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

    val CODEC: MapCodec<TimespanDefinition> = ID_MAPPER.codec(IncludedCodecs.katharsisIdentifier).dispatchMap(TimespanDefinition::codec) { it }

    init {
        ID_MAPPER.put(Katharsis.id("simple"), KatharsisCodecs.getMapCodec<SimpleTimespan>())
        ID_MAPPER.put(Katharsis.id("season"), KatharsisCodecs.getMapCodec<SeasonTimespan>())
    }

}
