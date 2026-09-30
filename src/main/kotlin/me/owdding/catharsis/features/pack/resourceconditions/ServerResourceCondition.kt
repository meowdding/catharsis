package me.owdding.catharsis.features.pack.resourceconditions

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType
import net.minecraft.resources.RegistryOps
import tech.thatgravyboat.skyblockapi.helpers.McClient

// TODO: this would require reloading the textures on server join
@GenerateCodec
data class ServerResourceCondition(val ip: String) : ResourceCondition {
    override fun getType(): ResourceConditionType<*> = TYPE

    override fun test(registryInfo: RegistryOps.RegistryInfoLookup?): Boolean {
        val currentIp = McClient.self.connection?.connection?.getLoggableAddress(true) ?: return false
        return if (!currentIp.contains("*")) {
            ip == currentIp
        } else {
            // idk
            false
        }
    }

    companion object {
        val TYPE: ResourceConditionType<ServerResourceCondition> = ResourceConditionType.create(
            Catharsis.id("server"),
            CatharsisCodecs.getMapCodec<ServerResourceCondition>(),
        )
    }
}
