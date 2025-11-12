package me.owdding.catharsis.features.pack

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.pack.config.PackConfigHandler
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions
import net.minecraft.resources.RegistryOps
import tech.thatgravyboat.skyblockapi.utils.extentions.asBoolean

@GenerateCodec
data class ConfigResourceCondition(val pack: String, val id: String) : ResourceCondition {

    override fun getType(): ResourceConditionType<*> = TYPE
    override fun test(registryInfo: RegistryOps.RegistryInfoLookup?): Boolean {
        return PackConfigHandler.getConfig(pack).get(id).asBoolean(false)
    }

    @Module
    companion object {

        val TYPE: ResourceConditionType<ConfigResourceCondition> = ResourceConditionType.create(
            Catharsis.id("config"),
            CatharsisCodecs.getMapCodec<ConfigResourceCondition>(),
        )

        init {
            ResourceConditions.register(TYPE)
        }
    }

}
