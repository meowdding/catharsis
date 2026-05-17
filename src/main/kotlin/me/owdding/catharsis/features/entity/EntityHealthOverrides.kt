package me.owdding.catharsis.features.entity

import me.owdding.catharsis.events.FinishRepoLoadEvent
import me.owdding.catharsis.events.StartRepoLoadEvent
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.types.FloatPredicate
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import tech.thatgravyboat.skyblockapi.api.data.MayorPerk
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.serverValue
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData

@Module
object EntityHealthOverrides {

    private var cache: HealthModifiers? = null

    fun doesHealthMatch(entity: LivingEntity, range: FloatPredicate, useModifiers: Boolean): Boolean {
        val health = entity.getAttribute(Attributes.MAX_HEALTH)?.serverValue ?: return false
        if (!useModifiers) return range.contains(health)
        val modifiers = cache ?: return range.contains(health)

        var totalMultiplier = 1.0f

        if (isRunic(entity)) {
            totalMultiplier *= modifiers.runic
        }

        if (isHealthy(entity)) {
            totalMultiplier *= modifiers.healthy
        }

        modifiers.mayorPerks.forEach { (perk, multiplier) ->
            if (perk.active) {
                totalMultiplier *= multiplier
            }
        }

        if (totalMultiplier == 0.0f) return false

        return range.contains(health / totalMultiplier)
    }

    // TODO: Create powerful SkyBlockMob in sbapi that has like mobtype etc
    private fun isRunic(entity: LivingEntity): Boolean {
        return entity.cleanName.contains("Runic")
    }

    private fun isHealthy(entity: LivingEntity): Boolean {
        return entity.cleanName.contains("Healthy")
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
            val CODEC = CatharsisCodecs.getCodec<HealthModifiers>()
        }
    }
}
