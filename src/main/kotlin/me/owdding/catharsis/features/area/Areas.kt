package me.owdding.catharsis.features.area

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.Utils
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.types.suggestion.ResourceLocationSuggestionProvider
import me.owdding.ktmodules.Module
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.core.BlockPos
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object Areas : SimplePreparableReloadListener<List<Pair<ResourceLocation, AreaDefinition>>>() {

    private val enabledDebugRenderers: MutableSet<ResourceLocation> = mutableSetOf()
    private val logger = Catharsis.featureLogger("Areas")
    private val converter = FileToIdConverter.json("catharsis/areas")
    private val gson = GsonBuilder().create()
    private val codec = CatharsisCodecs.getCodec<AreaDefinition>()

    private val areas: MutableMap<ResourceLocation, AreaDefinition> = mutableMapOf()

    @Subscription
    private fun RegisterCommandsEvent.register() {
        register("catharsis areas") {
            thenCallback("render toggle location", ResourceLocationArgument.id(), ResourceLocationSuggestionProvider.create(areas.keys)) {
                val location = argument<ResourceLocation>("location")!!
                if (!areas.containsKey(location)) {
                    Text.of("Unable to find area with location ") {
                        append(location.toString()) {
                            this.color = TextColor.GOLD
                        }
                        append("!")
                    }.sendWithPrefix("catharsis-areas-key-not-found")
                    return@thenCallback
                }

                if (enabledDebugRenderers.contains(location)) {
                    Text.of("Toggled debug rendering for ") {
                        append(location.toString()) {
                            this.color = TextColor.GOLD
                        }
                        append(" off") { this.color = TextColor.RED }
                        append("!")
                    }.sendWithPrefix("catharsis-areas-toggle-$location")
                    enabledDebugRenderers.remove(location)
                } else {
                    Text.of("Toggled debug rendering for ") {
                        append(location.toString()) {
                            this.color = TextColor.GOLD
                        }
                        append(" on") { this.color = TextColor.GREEN }
                        append("!")
                    }.sendWithPrefix("catharsis-areas-toggle-$location")
                    enabledDebugRenderers.add(location)
                }
            }
            thenCallback("render disable_all") {
                enabledDebugRenderers.clear()
                Text.of("Disabled all area debug renderers!").sendWithPrefix()
            }
        }
    }

    override fun prepare(
        manager: ResourceManager,
        profiler: ProfilerFiller,
    ): List<Pair<ResourceLocation, AreaDefinition>> {
        return converter.listMatchingResources(manager).mapNotNull { (id, resource) ->
            val id = converter.fileToId(id)
            logger.runCatching("Error loading area definition $id") {
                resource.openAsReader().use { reader ->
                    id to gson.fromJson(reader, JsonElement::class.java).toDataOrThrow(codec)
                }
            }
        }
    }

    @Subscription
    private fun RenderWorldEvent.AfterTranslucent.renderDebugs() {
        enabledDebugRenderers.mapNotNull { areas[it]?.renderable }.forEach { it.render(this) }
    }

    fun isPlayerInArea(id: ResourceLocation): Boolean = McPlayer.self?.blockPosition()?.let { isInArea(it, id) } == true
    fun isInArea(blockPos: BlockPos, id: ResourceLocation): Boolean = areas[id]?.contains(blockPos) == true

    override fun apply(
        elements: List<Pair<ResourceLocation, AreaDefinition>>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ) {
        areas.clear()
        areas.putAll(elements.toMap())
    }

    @JvmStatic
    fun getLoadedAreas(): Map<ResourceLocation, AreaDefinition> = areas

    init {
        Utils.registerClientReloadListener(Catharsis.id("areas"), this)
    }
}
