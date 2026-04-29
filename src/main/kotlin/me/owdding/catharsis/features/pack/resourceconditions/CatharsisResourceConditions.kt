package me.owdding.catharsis.features.pack.resourceconditions

import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions

@Module
object CatharsisResourceConditions {
    init {
        ResourceConditions.register(ConfigResourceCondition.TYPE)
        ResourceConditions.register(VersionResourceCondition.TYPE)
    }
}
