package me.owdding.catharsis.features.item

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.events.FinishRepoLoadEvent
import me.owdding.catharsis.events.StartRepoLoadEvent
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.repo.CatharsisRemoteRepo
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.CatharsisLogger.Companion.featureLogger
import me.owdding.catharsis.utils.extensions.readWithCodec
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.extentions.getTexture
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData

@Module
object MiscItemModels : SimplePreparableReloadListener<List<MiscItemModels.MiscItems>>(), CatharsisLogger by Catharsis.featureLogger() {

    init {
        Catharsis.registerClientReloadListener(Catharsis.id("misc_items"), this)
    }

    private var cache: MiscItems? = null
    private var extra: MutableList<MiscItems> = mutableListOf()

    @JvmStatic
    fun getModel(stack: ItemStack): Identifier? {
        val skin = stack.getTexture() ?: return null
        return cache?.reverseMap[skin] ?: extra.firstNotNullOfOrNull { it.reverseMap[skin] }
    }

    @Subscription
    private fun StartRepoLoadEvent.start() {
        cache = null
    }

    @Subscription
    private fun FinishRepoLoadEvent.finish() {
        cache = CatharsisRemoteRepo.getFileContentAsJson("misc_items.json")?.toData(CatharsisCodecs.getCodec<MiscItems>()) ?: return
    }

    override fun prepare(
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ): List<MiscItems> {
        return resourceManager.getResourceStack(Catharsis.id("misc_items.json")).map {
            it.readWithCodec(CatharsisCodecs.MiscItemsCodec.codec())
        }
    }

    override fun apply(
        value: List<MiscItems>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ) {
        this.extra.clear()
        this.extra.addAll(value)
    }

    @GenerateCodec
    data class MiscItems(
        val textures: MutableMap<Identifier, @Compact List<String>>,
    ) {
        val reverseMap = buildMap {
            textures.entries.forEach { (key, value) -> putAll(value.associateWith { key }) }
        }
    }
}
