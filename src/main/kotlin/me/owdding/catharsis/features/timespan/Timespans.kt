package me.owdding.catharsis.features.timespan

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.events.FinishRepoLoadEvent
import me.owdding.catharsis.events.StartRepoLoadEvent
import me.owdding.catharsis.repo.CatharsisRemoteRepo
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.CatharsisLogger.Companion.featureLogger
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.types.colors.CatppuccinColors
import me.owdding.ktmodules.Module
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.io.Reader
import kotlin.io.path.reader

@Module
object Timespans : SimplePreparableReloadListener<List<Pair<Identifier, TimespanDefinition>>>(), CatharsisLogger by Catharsis.featureLogger() {


    private val converter = FileToIdConverter.json("catharsis/timespans")
    private val gson = GsonBuilder().create()
    private val codec: Codec<TimespanDefinition> = TimespanDefinitions.CODEC.codec()

    private val timespans: MutableMap<Identifier, TimespanDefinition> = mutableMapOf()
    private val repoTimespans: MutableMap<Identifier, TimespanDefinition> = mutableMapOf()

    @Subscription
    private fun RegisterCommandsEvent.register() {
        registerWithCallback("catharsis dev timespans") {
            Text.of("Listing ${timespans.size} states!").sendWithPrefix()
            timespans.forEach { (id, timespan) ->
                Text.of {
                    append(id.toString()) {
                        this.color = CatppuccinColors.Mocha.yellow
                    }

                    append(" -> ")

                    append(timespan.test()) {
                        this.color = if (timespan.test()) CatppuccinColors.Frappe.green else CatppuccinColors.Frappe.red
                    }
                }.send()
            }
        }
    }

    override fun prepare(
        manager: ResourceManager,
        profiler: ProfilerFiller,
    ): List<Pair<Identifier, TimespanDefinition>> {
        return converter.listMatchingResources(manager).mapNotNull { (id, resource) ->
            val id = converter.fileToId(id)
            if (id.namespace == Catharsis.MOD_ID) {
                Catharsis.warn("Skipping timespan in restricted namespace! ($id)")
                return@mapNotNull null
            }

            runCatching("Loading timespan definition $id") {
                resource.openAsReader().use { reader ->
                    id to reader.parse()
                }
            }
        }
    }

    private fun Reader.parse() = gson.fromJson(this, JsonElement::class.java).toDataOrThrow(codec)

    @Subscription
    private fun StartRepoLoadEvent.start() {
        repoTimespans.keys.forEach(timespans::remove)
        repoTimespans.clear()
    }

    @Subscription
    private fun FinishRepoLoadEvent.start() {
        CatharsisRemoteRepo.listFilesInDirectory("timespans").forEach { (name, path) ->
            repoTimespans[Catharsis.id(name.removeSuffix(".json"))] = path.reader().parse()
        }
        timespans.putAll(repoTimespans)
    }

    @TimePassed("2t")
    @Subscription(TickEvent::class)
    fun tick() {
        val needsRebuild = timespans.values.map {
            it.tick()
            it.consumeRebuild() && it.isInUse
        }.any { it }

        if (needsRebuild) {
            val chunks = McLevel.level.chunkSource.storage.chunks
            val renderer = McClient.self.levelRenderer
            for (i in 0 until chunks.length()) {
                val chunk = McLevel.level.chunkSource.storage.chunks.get(i)
                if (chunk == null || chunk.isEmpty) continue

                for ((index, section) in chunk.sections.withIndex()) {
                    if (section == null || section.hasOnlyAir()) continue
                    renderer.setSectionDirty(
                        chunk.pos.x,
                        chunk.level.getSectionYFromSectionIndex(index),
                        chunk.pos.z,
                    )
                }
            }
        }
    }

    override fun apply(
        elements: List<Pair<Identifier, TimespanDefinition>>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ) {
        timespans.clear()
        timespans.putAll(repoTimespans)
        timespans.putAll(elements.toMap())
    }

    @JvmStatic
    fun getLoadedTimespans(): Map<Identifier, TimespanDefinition> = timespans

    init {
        McClient.registerClientReloadListener(Catharsis.id("timespans"), this)
    }
}
