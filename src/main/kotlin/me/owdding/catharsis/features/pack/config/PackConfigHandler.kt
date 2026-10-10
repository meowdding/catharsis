package me.owdding.catharsis.features.pack.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.pack.meta.CatharsisMetadataSection
import me.owdding.catharsis.mixins.commands.CommandNodeAccessor
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.types.colors.CatppuccinColors
import me.owdding.catharsis.utils.types.suggestion.IterableSuggestionProvider
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.minecraft.util.GsonHelper
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.misc.CommandBuilder
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import kotlin.io.path.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.time.isDistantPast

data class PackConfig(
    val packId: String,
    val default: JsonObject = JsonObject(),
    val current: JsonObject = JsonObject(),
) {

    fun set(id: String, value: JsonElement) {
        current.add(id, value)
    }

    fun get(id: String): JsonElement? {
        return current.get(id) ?: default.get(id)
    }

    fun options(): List<PackConfigOption>? = PackConfigHandler.catharsisPackOptions[packId]?.takeUnless(List<PackConfigOption>::isEmpty)
}

@Module
object PackConfigHandler : ResourceManagerReloadListener {

    private const val SAVE_PATH = "catharsis/pack_configs.json"

    private val logger = CatharsisLogger.named("PackConfigHandler")
    private val path = McClient.config.resolve(SAVE_PATH)
    private val configs = mutableMapOf<String, PackConfig>()
    private var saveRequestedAt = Instant.DISTANT_PAST

    var catharsisPackOptions: Map<String, List<PackConfigOption>?> = emptyMap()
        private set
    // map of command -> pack id
    private var catharsisPackCommands: Map<String, String> = emptyMap()

    // we need to smuggle the latest fabric command dispatcher so that we can edit it at will later
    private var smuggledFabricDispatcher: CommandDispatcher<FabricClientCommandSource>? = null

    init {
        logger.runCatching("Loading pack configurations") {
            if (path.notExists()) {
                path.parent?.createDirectories()
                path.createFile()
                logger.info("No existing config found")
                return@runCatching
            }
            val json = GsonHelper.parse(path.readText().ifBlank { "{}" })
            for ((key, value) in json.entrySet()) {
                configs[key] = PackConfig(key, JsonObject(), value.asJsonObject)
            }
        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            smuggledFabricDispatcher = dispatcher
            registerCommands()
        }

        Catharsis.registerClientReloadListener(Catharsis.id("packconfig_handler"), this)
    }

    fun getConfig(packId: String): PackConfig {
        return configs.getOrPut(packId) { PackConfig(packId) }
    }

    fun save() {
        this.saveRequestedAt = currentInstant()
    }

    @JvmStatic
    fun updateDefaults(id: String, options: List<PackConfigOption>) {
        val config = getConfig(id).default
        config.asMap().clear()
        for (option in options) {
            option.addToDefault(config)
        }
    }

    @JvmStatic
    fun isLoaded(id: String): Boolean {
        return catharsisPackOptions.containsKey(id)
    }

    @Subscription(TickEvent::class)
    @TimePassed("10s")
    private fun onTick() {
        if (!saveRequestedAt.isDistantPast && saveRequestedAt.since() >= 10.seconds) {
            val output = JsonObject()
            for ((key, value) in configs) {
                output.add(key, value.current.deepCopy())
            }

            Scheduling.async {
                logger.debug("Saving pack configurations to $SAVE_PATH")
                path.writeText(output.toString())
                saveRequestedAt = Instant.DISTANT_PAST
            }
        }
    }

    // Everything added here will be accessible via "/catharsis config <id>" and "/config_command" for the packs that register it
    private fun CommandBuilder<*>.registerConfigCommand(
        idGetter: CommandContext<*>.() -> String,
    ) {
        callback {
            val id = idGetter()
            openPackConfigScreen(id)
        }
        then("search", StringArgumentType.string()) {
            callback {
                val id = idGetter()
                val search = argument<String>("search")
                openPackConfigScreen(id, search)
            }
        }
    }

    @Subscription
    private fun onCommand(event: RegisterCommandsEvent) {
        event.register("catharsis config") {
            then("id", StringArgumentType.string(), IterableSuggestionProvider(catharsisPackOptions.keys)) {
                registerConfigCommand { argument<String>("id") }
            }
        }
    }

    fun openPackConfigScreen(id: String, search: String = "") {
        val options = getConfig(id).options() ?: run {
            Text.of("No config found for $id").sendWithPrefix()
            return
        }
        McClient.setScreenAsync { PackConfigScreen(McScreen.self, id, options, search) }
    }

    override fun onResourceManagerReload(resourceManager: ResourceManager) {
        // command -> pack id
        val catharsisPackCommands = mutableMapOf<String, String>()
        catharsisPackOptions = resourceManager.listPacks().toList().mapNotNull { pack ->
            val meta = pack.getMetadataSection(CatharsisMetadataSection.TYPE) ?: return@mapNotNull null
            val options = PackConfigOption.fromResource(pack)
            val config = options?.takeUnless(List<PackConfigOption>::isEmpty) ?: meta.config
            if (meta.configCommand != null) catharsisPackCommands[meta.configCommand] = meta.id
            updateDefaults(meta.id, config)
            meta.id to config
        }.toMap()

        val removedCommands = removePreviousCommands()
        // we set this after calling removePreviousCommands so it can know what commands it had before
        this.catharsisPackCommands = catharsisPackCommands.toMap()
        val addedCommands = registerCommands()
        // we need to refresh command completion for the actual changes to get applied
        if ((removedCommands || addedCommands) && McClient.connection?.commands != null) {
            ClientCommands.refreshCommandCompletions()
        }
    }

    // returns true if it removed any command
    private fun removePreviousCommands(): Boolean {
        logger.debug("Removing previous pack config commands")
        val fabricDispatcher = smuggledFabricDispatcher ?: return false
        val commands = catharsisPackCommands.keys
        if (commands.isEmpty()) return false

        val root = fabricDispatcher.root
        val accessor = root as? CommandNodeAccessor<*> ?: return false
        val removedChildren = accessor.`catharis$getChildren`().entries.removeIf { it.key in commands }
        val removedLiteral = accessor.`catharis$getLiteals`().entries.removeIf { it.key in commands }
        return removedChildren || removedLiteral
    }

    // returns true if it added any command
    private fun registerCommands(): Boolean {
        val fabricDispatcher = smuggledFabricDispatcher ?: return false
        val vanillaDispatcher = McClient.connection?.commands ?: return false
        val addedCommands = mutableSetOf<String>()

        context(fabricDispatcher, vanillaDispatcher) {
            catharsisPackCommands.entries.forEach { (command, packId) ->
                val added = tryAddCommand(packId, command)
                if (added) addedCommands.add(command)
            }
        }
        // we update the catharsis pack commands, so if any pack command didn't get added we remove it from here
        // this is so that if any command didn't get registered because hypixel/another mod already registered it,
        // we don't later accidentally delete that command
        catharsisPackCommands = catharsisPackCommands.filter { addedCommands.contains(it.key) }
        return addedCommands.isNotEmpty()
    }

    // returns true if it added the command
    context(fabricDispatcher: CommandDispatcher<FabricClientCommandSource>, vanillaDispatcher: CommandDispatcher<ClientSuggestionProvider>)
    private fun tryAddCommand(packId: String, command: String): Boolean {
        fun requireMessage(text: String) = Text.of("Command ") {
            append(command, CatppuccinColors.Mocha.green)
            append("${text}.")
            color = CatppuccinColors.Mocha.red
        }.sendWithPrefix()

        if (command.any(Char::isWhitespace)) {
            requireMessage("can't have spaces in it")
            return false
        }

        if (vanillaDispatcher.findNode(listOf(command)) != null) {
            requireMessage("is already added by the server")
            return false
        }

        if (fabricDispatcher.findNode(listOf(command)) != null) {
            requireMessage("is already added by another mod")
            return false
        }

        fabricDispatcher.register(
            ClientCommands.literal(command).apply {
                CommandBuilder(this).apply { registerConfigCommand { packId } }
            }
        )
        logger.debug("Added '$command' config command")
        return true
    }
}
