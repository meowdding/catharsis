package me.owdding.catharsis.features.item

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.events.FinishRepoLoadEvent
import me.owdding.catharsis.events.StartRepoLoadEvent
import me.owdding.catharsis.generated.CodecUtils
import me.owdding.catharsis.repo.CatharsisRemoteRepo
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.CatharsisLogger.Companion.featureLogger
import me.owdding.catharsis.utils.codecs.IncludedCodecs
import me.owdding.catharsis.utils.extensions.base64Texture
import me.owdding.catharsis.utils.extensions.buildMultimap
import me.owdding.catharsis.utils.extensions.readWithCodec
import me.owdding.catharsis.utils.types.Base64String
import me.owdding.ktcodecs.Compact
import me.owdding.ktmodules.Module
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData

@Module
object MiscItemModels : SimplePreparableReloadListener<List<MiscItemModels.MiscItems>>(), CatharsisLogger by Catharsis.featureLogger() {

    init {
        Catharsis.registerClientReloadListener(Catharsis.id("misc_items"), this)
    }

    private var cache: MiscItems? = null
    private var extra: MutableList<MiscItems> = mutableListOf()

    @JvmStatic
    fun getBaseModel(stack: ItemStack): Identifier? {
        val skin = stack.base64Texture ?: return null
        return cache?.reverseMap[skin]
    }

    @JvmStatic
    fun getExtraModel(stack: ItemStack): Identifier? {
        val skin = stack.base64Texture ?: return null
        return extra.firstNotNullOfOrNull { it.reverseMap[skin] }
    }

    fun collectItems() = buildMultimap {
        (extra + cache).filterNotNull().flatMap { it.textures.entries }.forEach { (key, values) ->
            putAll(key, values)
        }
    }

    @Subscription
    private fun StartRepoLoadEvent.start() {
        cache = null
    }

    @Subscription
    private fun FinishRepoLoadEvent.finish() {
        cache = CatharsisRemoteRepo.getFileContentAsJson("misc_items.json")?.toData(MiscItems.CODEC) ?: return
    }

    override fun prepare(
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ): List<MiscItems> {
        return resourceManager.getResourceStack(Catharsis.id("misc_items.json")).map {
            it.readWithCodec(MiscItems.CODEC)
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

    data class MiscItems(
        val textures: MutableMap<Identifier, @Compact List</*@NamedCodec("base64_string")*/ Base64String>>,
    ) {
        val reverseMap = buildMap {
            textures.entries.forEach { (key, value) -> putAll(value.associateWith { key }) }
        }

        companion object {
            // TODO remove when NamedCodec works on type parameters
            val CODEC: Codec<MiscItems> = RecordCodecBuilder.create { it.group(
                CodecUtils.map(Identifier.CODEC, CodecUtils.compactList(IncludedCodecs.BASE64_STRING_CODEC)).fieldOf("textures").forGetter(MiscItems::textures)
            ).apply(it, ::MiscItems) }
        }
    }
}
