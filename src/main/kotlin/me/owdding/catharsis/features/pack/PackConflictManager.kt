package me.owdding.catharsis.features.pack

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.hooks.pack.PackMetadataHook
import me.owdding.ktmodules.Module
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient

@Module
object PackConflictManager : SimplePreparableReloadListener<Boolean>() {
    @JvmStatic
    var overrideHypixel: Boolean = true
        private set

    override fun prepare(manager: ResourceManager, profiler: ProfilerFiller): Boolean {
        val packs = McClient.self.resourcePackRepository.selectedPacks
        var foundServerPack = false

        for (pack in packs.reversed()) {
            if (LocationAPI.isOnSkyBlock && pack.id.startsWith("server")) {
                foundServerPack = true
                continue
            }

            if (pack is PackMetadataHook) {
                val meta = pack.`catharsis$getMetadata`()
                if (meta != null) {
                    return if (!foundServerPack) true
                    else meta.overrideHypixelPack
                }
            }
        }

        return true
    }

    override fun apply(result: Boolean, manager: ResourceManager, profiler: ProfilerFiller) {
        this.overrideHypixel = result
    }

    init {
        Catharsis.registerClientReloadListener(Catharsis.id("pack_conflict_manager"), this)
    }
}
