package me.owdding.catharsis.features.pack

import com.google.gson.JsonPrimitive
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.pack.config.PackConfigHandler
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions
import net.minecraft.resources.RegistryOps

@GenerateCodec
data class ConfigResourceCondition(val pack: String, val id: String, val value: String?) : ResourceCondition {

    override fun getType(): ResourceConditionType<*> = TYPE
    override fun test(registryInfo: RegistryOps.RegistryInfoLookup?): Boolean {
        val entry = PackConfigHandler.getConfig(pack).get(id) as? JsonPrimitive ?: return false
        return when {
            entry.isBoolean -> entry.asBoolean
            entry.isString -> entry.asString == this.value
            entry.isJsonArray -> entry.asJsonArray.any { it.asString == this.value }
            else -> false
        }
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
