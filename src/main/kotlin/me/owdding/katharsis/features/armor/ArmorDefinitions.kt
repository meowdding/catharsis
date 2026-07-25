package me.owdding.katharsis.features.armor

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.TypedResourceManager
import me.owdding.ktmodules.Module
import net.minecraft.client.multiplayer.ClientRegistryLayer
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.PlaceholderLookupProvider
import net.minecraft.util.profiling.ProfilerFiller

@Module
object ArmorDefinitions : SimplePreparableReloadListener<Map<Identifier, ArmorDefinition>>() {

    private val logger = Katharsis.featureLogger("ArmorDefinitions")
    val converter: FileToIdConverter = FileToIdConverter.json("katharsis/armors")
    private val gson = GsonBuilder().create()
    val codec = KatharsisCodecs.getCodec<ArmorDefinition.Unbaked>()

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

    @JvmStatic
    fun hasDefinition(id: Identifier?): Boolean {
        return getDefinition(id) != null
    }

    init {
        Katharsis.registerClientReloadListener(Katharsis.id("armor_definitions"), this)
    }
}
