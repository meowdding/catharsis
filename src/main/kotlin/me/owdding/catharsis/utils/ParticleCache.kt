package me.owdding.catharsis.utils

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import me.owdding.catharsis.features.blocks.BlockReplacements
import me.owdding.catharsis.generated.CatharsisParticleInvalidateable
import me.owdding.catharsis.utils.extensions.toBlockPos
import me.owdding.ktmodules.AutoCollect
import me.owdding.ktmodules.Module
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.SpellParticle
import net.minecraft.core.BlockPos
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.ParticleEmitEvent
import java.util.concurrent.TimeUnit

@Module
object ParticleCache {
    private val cache: Cache<BlockPos, Particle> = CacheBuilder.newBuilder()
        .expireAfterWrite(3, TimeUnit.SECONDS)
        .build()

    fun markDirty(blockPos: BlockPos) {
            BlockReplacements.markChunkDirty(blockPos)
    }

    fun hasEffectParticle(pos: BlockPos): Boolean {
        return cache.getIfPresent(pos) is SpellParticle
    }

    @Subscription
    fun onSpawn(event: ParticleEmitEvent) {
        spawn(event.particle, event.particle.boundingBox.center.toBlockPos().atY(0))
        spawn(event.particle, event.particle.boundingBox.minPosition.toBlockPos().atY(0))
        spawn(event.particle, event.particle.boundingBox.maxPosition.toBlockPos().atY(0))
    }

    fun spawn(particle: Particle, pos: BlockPos) {
        if (cache.getIfPresent(pos) == null) {
            markDirty(pos)
        }
        cache.put(pos, particle)
    }
}

interface CustomBlockProvider {
    fun isCustomBlock(pos: BlockPos): Boolean
}

@AutoCollect
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ParticleInvalidateable
