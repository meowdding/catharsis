package me.owdding.catharsis.features.pack.resourceconditions

import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.pack.config.PackConfigHandler
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType
import net.minecraft.resources.RegistryOps

@GenerateCodec
data class ConfigResourceCondition(val pack: String, val id: String, val value: String?) : ResourceCondition {

    override fun getType(): ResourceConditionType<*> = TYPE
    override fun test(registryInfo: RegistryOps.RegistryInfoLookup?): Boolean {
        val entry = PackConfigHandler.getConfig(pack).get(id) ?: return false
        return when (entry) {
            is JsonPrimitive if entry.isBoolean -> entry.asBoolean
            is JsonPrimitive if entry.isString-> entry.asString == this.value
            is JsonArray -> entry.asJsonArray.any { it.asString == this.value }
            else -> false
        }
    }

    companion object {
        val TYPE: ResourceConditionType<ConfigResourceCondition> = ResourceConditionType.create(
            Catharsis.id("config"),
            CatharsisCodecs.getMapCodec<ConfigResourceCondition>(),
        )
    }
}
