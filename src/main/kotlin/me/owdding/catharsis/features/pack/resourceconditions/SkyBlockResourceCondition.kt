package me.owdding.catharsis.features.pack.resourceconditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType
import net.minecraft.client.resources.server.ServerPackManager
import net.minecraft.resources.RegistryOps
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient

// Joining SkyBlock reloads the textures already because of the pack
object SkyBlockResourceCondition : ResourceCondition {
    override fun getType(): ResourceConditionType<*> = TYPE

    override fun test(registryInfo: RegistryOps.RegistryInfoLookup?): Boolean = LocationAPI.onHypixel && hasSkyblockPack

    val hasSkyblockPack
        get() = McClient.self.downloadedPackSource.manager.packs.any {
            it.promptAccepted && it.downloadStatus == ServerPackManager.PackDownloadStatus.DONE && !it.isRemoved &&
                it.url.toString().startsWith("https://resourcepacks.hypixel.net/SkyBlock")
        }

    val TYPE: ResourceConditionType<SkyBlockResourceCondition> = ResourceConditionType.create(
        Catharsis.id("skyblock"),
        MapCodec.unit { SkyBlockResourceCondition },
    )
}
