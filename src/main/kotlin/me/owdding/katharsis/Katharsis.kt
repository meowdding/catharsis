package me.owdding.katharsis

import com.google.gson.JsonObject
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.katharsis.events.BootstrapConditionalPropertiesEvent
import me.owdding.katharsis.events.BootstrapItemModelsEvent
import me.owdding.katharsis.events.BootstrapItemTintSourceEvent
import me.owdding.katharsis.events.BootstrapNumericPropertiesEvent
import me.owdding.katharsis.events.BootstrapSelectPropertiesEvent
import me.owdding.katharsis.events.FinishRepoLoadEvent
import me.owdding.katharsis.events.StartRepoLoadEvent
import me.owdding.katharsis.features.imc.ImcHandler
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.generated.KatharsisModules
import me.owdding.katharsis.generated.KatharsisPreLoadModules
import me.owdding.katharsis.repo.KatharsisRemoteRepo
import me.owdding.katharsis.repo.KatharsisRemoteRepo.REPO_BRANCH_PROPERTY
import me.owdding.katharsis.utils.KatharsisDevUtils
import me.owdding.katharsis.utils.KatharsisLogger
import me.owdding.katharsis.utils.extensions.sendWithPrefix
import me.owdding.katharsis.utils.extensions.sendWithPrefixIf
import me.owdding.katharsis.utils.types.colors.KatharsisColors
import me.owdding.katharsis.utils.types.colors.CatppuccinColors
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.AutoCollect
import me.owdding.ktmodules.Module
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.color.item.ItemTintSources
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.PreparableReloadListener
import org.intellij.lang.annotations.Pattern
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.url
import java.util.concurrent.CompletableFuture
import kotlin.io.path.readText
import kotlin.time.Instant

@Module
object Katharsis : ClientModInitializer, KatharsisLogger by KatharsisLogger.autoResolve() {
    const val DISCORD = "https://meowdd.ing/discord"
    val buildInfo: BuildInfo by lazy {
        val self = FabricLoader.getInstance().getModContainer(MOD_ID).get()
        self.findPath("katharsis.json").get().readText().readJson<JsonObject>().toDataOrThrow(KatharsisCodecs.getCodec())
    }

    init {
        if (FabricLoader.getInstance().isModLoaded(MOD_ID)) {
            KatharsisPreLoadModules.init { SkyBlockAPI.eventBus.register(it) }
        }
    }

    const val MOD_ID = "katharsis"
    override fun onInitializeClient() {
        info("Katharsis client initialized!")
        KatharsisModules.init { SkyBlockAPI.eventBus.register(it) }

        BootstrapConditionalPropertiesEvent(ConditionalItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapNumericPropertiesEvent(RangeSelectItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapSelectPropertiesEvent(SelectItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapItemModelsEvent(ItemModels.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapItemTintSourceEvent(ItemTintSources.ID_MAPPER::put).post(SkyBlockAPI.eventBus)

        loadRepo()
        ImcHandler.setup()
    }

    fun loadRepo() {
        val branch = KatharsisDevUtils.properties[REPO_BRANCH_PROPERTY] ?: buildInfo.branch.replace("/", "-")
        info("Loading repo on branch $branch")
        Text.of("Loading repo on branch $branch").sendWithPrefixIf { McLevel.hasLevel }
        CompletableFuture.runAsync {
            StartRepoLoadEvent.post(SkyBlockAPI.eventBus)
            KatharsisRemoteRepo.initialize(branch) {
                McClient.runOrNextTick {
                    try {
                        FinishRepoLoadEvent.post(SkyBlockAPI.eventBus)
                    } catch (throwable: Throwable) {
                        error("Failed to finish repo loading!", throwable)
                    }
                    info("Finished loading repo!")
                    Text.of("Finished loading repo!").sendWithPrefixIf { McLevel.hasLevel }
                }
            }
        }.exceptionally { throwable ->
            error("Failed to load remote repo!", throwable)
            Text.of("Failed to load repo! Please report this on the Discord").sendWithPrefixIf { McLevel.hasLevel }
            null
        }
    }

    @Subscription
    fun registerCommand(context: RegisterCommandsEvent) {
        context.register("katharsis") {
            callback {
                Text.of("Trying to access the config? Run \'") {
                    color = KatharsisColors.skyblue
                    append("/katharsis config <>", KatharsisColors.slateblue)
                    append("' instead.")
                }.sendWithPrefix()
            }

            thenCallback("catpack") {
                McClient.openUri("https://meowdd.ing/catpack")
                Text.of("Opening Catsquash Website...", CatppuccinColors.Mocha.pink).sendWithPrefix()
            }

            then("docs") {
                thenCallback("dev") {
                    McClient.openUri("https://katharsis.meowdd.ing?dev")
                    Text.of("Opening Katharsis Development Docs...", CatppuccinColors.Mocha.pink).sendWithPrefix()
                }
                callback {
                    McClient.openUri("https://katharsis.meowdd.ing?latest")
                    Text.of("Opening Katharsis Docs...", CatppuccinColors.Mocha.pink).sendWithPrefix()
                }
            }

            thenCallback("discord") {
                Text.of("Join the Meowdding Discord!").apply {
                    this.url = DISCORD
                    this.color = CatppuccinColors.Mocha.pink
                    this.hover = Text.of(DISCORD).withColor(TextColor.GRAY)
                }.sendWithPrefix()
            }

            then("repo") {
                thenCallback("reload") {
                    KatharsisRemoteRepo.uninitialize()
                    loadRepo()
                }
                thenCallback("branch branch", StringArgumentType.greedyString()) {
                    val branch = argument<String>("branch")
                    KatharsisDevUtils.properties[REPO_BRANCH_PROPERTY] = branch
                    KatharsisDevUtils.saveProperties()
                    Text.of("Set repo branch to $branch").sendWithPrefix()
                }
                thenCallback("branch reset") {
                    KatharsisDevUtils.properties.remove(REPO_BRANCH_PROPERTY)
                    KatharsisDevUtils.saveProperties()
                    Text.of("Reset repo branch!").sendWithPrefix()
                }
            }
        }
    }

    fun id(@Pattern("[a-z_0-9\\/.-]+") path: String): Identifier = Identifiers.of(MOD_ID, path)
    fun mc(@Pattern("[a-z_0-9\\/.-]+") path: String): Identifier = Identifiers.of(path)
    fun sbapi(@Pattern("[a-z_0-9\\/.-]+") path: String): Identifier = Identifiers.of(SkyBlockAPI.MOD_ID, path)
    fun registerClientReloadListener(id: Identifier, replacements: PreparableReloadListener) {
        if (System.getProperties().containsKey("katharsis.skip-listeners")) return
        McClient.registerClientReloadListener(id, replacements)
    }

    @GenerateCodec
    data class BuildInfo(
        val ref: String,
        val branch: String,
        val timestamp: Instant,
    ) {
        val isMain = ref == "main"
    }

}

@AutoCollect("PreLoadModules")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class PreLoadModule
