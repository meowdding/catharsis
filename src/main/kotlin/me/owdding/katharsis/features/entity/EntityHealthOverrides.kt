package me.owdding.katharsis.features.entity

import me.owdding.katharsis.events.FinishRepoLoadEvent
import me.owdding.katharsis.events.StartRepoLoadEvent
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.types.FloatPredicate
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import tech.thatgravyboat.skyblockapi.api.data.MayorPerk
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.serverValue
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData

@Module
object EntityHealthOverrides {

    private var cache: HealthModifiers? = null

    fun doesHealthMatch(entity: LivingEntity, predicate: FloatPredicate, useModifiers: Boolean): Boolean {
        val health = entity.getAttribute(Attributes.MAX_HEALTH)?.serverValue ?: return false
        if (!useModifiers) return predicate.contains(health)
        val modifiers = cache ?: return predicate.contains(health)

        if (predicate.contains(health)) return true

        var mayorMultiplier = 1.0f
        modifiers.mayorPerks.forEach { (perk, multiplier) ->
            if (perk.active) {
                mayorMultiplier *= multiplier
            }
        }
        if (mayorMultiplier == 0.0f) return false

        if (predicate.contains(health / mayorMultiplier)) return true

        val runicMultiplier = mayorMultiplier * modifiers.runic
        if (runicMultiplier != 0.0f && predicate.contains(health / runicMultiplier)) return true

        if (SkyBlockIsland.THE_CATACOMBS.inIsland()) {
            val healthyMultiplier = mayorMultiplier * modifiers.healthy
            if (healthyMultiplier != 0.0f && predicate.contains(health / healthyMultiplier)) return true

            val bothMultiplier = runicMultiplier * modifiers.healthy
            if (bothMultiplier != 0.0f && predicate.contains(health / bothMultiplier)) return true
        }

        return false
    }

    @Subscription
    private fun StartRepoLoadEvent.start() {
        cache = null
    }

    @Subscription
    private fun FinishRepoLoadEvent.finish() {
        cache = getAsJson("health_modifiers.json")?.toData(HealthModifiers.CODEC) ?: return
    }

    @GenerateCodec
    data class HealthModifiers(
        val mayorPerks: Map<MayorPerk, Float>,
        val runic: Float,
        val healthy: Float,
    ) {
        companion object {
            val CODEC = KatharsisCodecs.getCodec<HealthModifiers>()
        }
    }
}
