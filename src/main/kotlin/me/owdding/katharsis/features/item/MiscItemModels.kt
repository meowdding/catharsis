package me.owdding.katharsis.features.item

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.events.FinishRepoLoadEvent
import me.owdding.katharsis.events.StartRepoLoadEvent
import me.owdding.katharsis.generated.CodecUtils
import me.owdding.katharsis.repo.KatharsisRemoteRepo
import me.owdding.katharsis.utils.KatharsisLogger
import me.owdding.katharsis.utils.KatharsisLogger.Companion.featureLogger
import me.owdding.katharsis.utils.codecs.IncludedCodecs
import me.owdding.katharsis.utils.extensions.base64Texture
import me.owdding.katharsis.utils.extensions.readWithCodec
import me.owdding.katharsis.utils.types.Base64String
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
object MiscItemModels : SimplePreparableReloadListener<List<MiscItemModels.MiscItems>>(), KatharsisLogger by Katharsis.featureLogger() {

    init {
        Katharsis.registerClientReloadListener(Katharsis.id("misc_items"), this)
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

    @Subscription
    private fun StartRepoLoadEvent.start() {
        cache = null
    }

    @Subscription
    private fun FinishRepoLoadEvent.finish() {
        cache = KatharsisRemoteRepo.getFileContentAsJson("misc_items.json")?.toData(MiscItems.CODEC) ?: return
    }

    override fun prepare(
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ): List<MiscItems> {
        return resourceManager.getResourceStack(Katharsis.id("misc_items.json")).map {
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
