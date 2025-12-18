package me.owdding.catharsis.features.armor

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.ktmodules.Module
import net.minecraft.client.multiplayer.ClientRegistryLayer
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.PlaceholderLookupProvider
import net.minecraft.util.profiling.ProfilerFiller
import tech.thatgravyboat.skyblockapi.helpers.McClient

@Module
object ArmorDefinitions : SimplePreparableReloadListener<Map<Identifier, ArmorDefinition>>() {

    private val logger = Catharsis.featureLogger("ArmorDefinitions")
    private val converter = FileToIdConverter.json("catharsis/armors")
    private val gson = GsonBuilder().create()
    private val codec = CatharsisCodecs.getCodec<ArmorDefinition.Unbaked>()

    private val definitions: MutableMap<Identifier, ArmorDefinition> = mutableMapOf()

    override fun prepare(manager: ResourceManager, profiler: ProfilerFiller): Map<Identifier, ArmorDefinition> {
        val registry = ClientRegistryLayer.createRegistryAccess().compositeAccess()
        val resources = TypedResourceManager(manager)

        return converter.listMatchingResources(manager)
            .mapNotNull { (file, resource) ->
                logger.runCatching("Error loading armor definition $file") {
                    resource.openAsReader().use { reader ->
                        val lookup = PlaceholderLookupProvider(registry)
                        val ops = lookup.createSerializationContext(JsonOps.INSTANCE)
                        val swapper = lookup.createSwapper()

                        val id = converter.fileToId(file)
                        val definition = codec.parse(ops, gson.fromJson(reader, JsonElement::class.java)).orThrow.bake(swapper, resources)

                        id to definition
                    }
                }
            }
            .associate { it }
    }

    override fun apply(definitions: Map<Identifier, ArmorDefinition>, manager: ResourceManager, profiler: ProfilerFiller) {
        this.definitions.clear()
        this.definitions.putAll(definitions)
    }

    @JvmStatic
    fun getDefinition(id: Identifier?): ArmorDefinition? {
        return if (id == null) null else definitions[id]
    }

    init {
        McClient.registerClientReloadListener(Catharsis.id("armor_definitions"), this)
    }
}
