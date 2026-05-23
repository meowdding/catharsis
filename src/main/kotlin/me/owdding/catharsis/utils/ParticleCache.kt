package me.owdding.catharsis.utils

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import me.owdding.ktmodules.Module
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.SpellParticle
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.ParticleEmitEvent
import java.util.concurrent.TimeUnit

@Module
object ParticleCache {
    private val cache: Cache<AABB, Particle> = CacheBuilder.newBuilder()
        .expireAfterWrite(3, TimeUnit.SECONDS)
        .build()

    fun hasEffectParticle(pos: BlockPos): Boolean {
        return cache.asMap().toList().find { it.first.contains(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()) }?.second is SpellParticle
    }

    @Subscription
    fun onSpawn(event: ParticleEmitEvent) {
        cache.put(event.particle.boundingBox, event.particle)
    }
}
