package me.owdding.catharsis

import com.google.gson.JsonObject
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.catharsis.events.BootstrapConditionalPropertiesEvent
import me.owdding.catharsis.events.BootstrapItemModelsEvent
import me.owdding.catharsis.events.BootstrapItemTintSourceEvent
import me.owdding.catharsis.events.BootstrapNumericPropertiesEvent
import me.owdding.catharsis.events.BootstrapSelectPropertiesEvent
import me.owdding.catharsis.events.FinishRepoLoadEvent
import me.owdding.catharsis.events.StartRepoLoadEvent
import me.owdding.catharsis.features.imc.ImcHandler
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.generated.CatharsisModules
import me.owdding.catharsis.generated.CatharsisPreLoadModules
import me.owdding.catharsis.repo.CatharsisRemoteRepo
import me.owdding.catharsis.repo.CatharsisRemoteRepo.REPO_BRANCH_PROPERTY
import me.owdding.catharsis.utils.CatharsisDevUtils
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.extensions.sendWithPrefixIf
import me.owdding.catharsis.utils.types.colors.CatharsisColors
import me.owdding.catharsis.utils.types.colors.CatppuccinColors
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
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.suggest
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.url
import java.util.concurrent.CompletableFuture
import kotlin.io.path.readText
import kotlin.time.Instant

@Module
object Catharsis : ClientModInitializer, CatharsisLogger by CatharsisLogger.autoResolve() {
    const val DISCORD = "https://meowdd.ing/discord"
    val buildInfo: BuildInfo by lazy {
        val self = FabricLoader.getInstance().getModContainer(MOD_ID).get()
        self.findPath("catharsis.json").get().readText().readJson<JsonObject>().toDataOrThrow(CatharsisCodecs.getCodec())
    }

    init {
        if (FabricLoader.getInstance().isModLoaded(MOD_ID)) {
            CatharsisPreLoadModules.init { SkyBlockAPI.eventBus.register(it) }
        }
    }

    const val MOD_ID = "catharsis"
    override fun onInitializeClient() {
        info("Catharsis client initialized!")
        CatharsisModules.init { SkyBlockAPI.eventBus.register(it) }

        BootstrapConditionalPropertiesEvent(ConditionalItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapNumericPropertiesEvent(RangeSelectItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapSelectPropertiesEvent(SelectItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapItemModelsEvent(ItemModels.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapItemTintSourceEvent(ItemTintSources.ID_MAPPER::put).post(SkyBlockAPI.eventBus)

        loadRepo()
        ImcHandler.setup()
    }

    fun loadRepo() {
        val branch = CatharsisDevUtils.properties[REPO_BRANCH_PROPERTY] ?: buildInfo.branch.replace("/", "-")
        info("Loading repo on branch $branch")
        Text.of("Loading repo on branch $branch").sendWithPrefixIf { McLevel.hasLevel }
        CompletableFuture.runAsync {
            StartRepoLoadEvent.post(SkyBlockAPI.eventBus)
            CatharsisRemoteRepo.initialize(branch) {
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

            CompletableFuture.runAsync {
                while (!McLevel.hasLevel) {
                    Thread.sleep(5000)
                }
                McClient.runNextTick {
                    Text.of("Failed to load repo! ") {
                        color = TextColor.RED

                        append("Click here for a way to potentially fix it. ") {
                            color = CatppuccinColors.Mocha.red
                            suggest = "catharsis repo failed"
                            hover = Text.of("Click here!")
                        }
                        append("If that doesn't work or doesn't apply to you, report it on the Discord.") {
                            url = DISCORD
                            color = CatppuccinColors.Mocha.pink
                            hover = Text.of(DISCORD).withColor(TextColor.GRAY)
                        }
                    }
                }
            }
            null
        }
    }

    @Subscription
    fun registerCommand(context: RegisterCommandsEvent) {
        context.register("catharsis") {
            callback {
                Text.of("Trying to access the config? Run \'") {
                    color = CatharsisColors.skyblue
                    append("/catharsis config <>", CatharsisColors.slateblue)
                    append("' instead.")
                }.sendWithPrefix()
            }

            thenCallback("catpack") {
                McClient.openUri("https://meowdd.ing/catpack")
                Text.of("Opening Catsquash Website...", CatppuccinColors.Mocha.pink).sendWithPrefix()
            }

            then("docs") {
                thenCallback("dev") {
                    McClient.openUri("https://catharsis.meowdd.ing?dev")
                    Text.of("Opening Catharsis Development Docs...", CatppuccinColors.Mocha.pink).sendWithPrefix()
                }
                callback {
                    McClient.openUri("https://catharsis.meowdd.ing?latest")
                    Text.of("Opening Catharsis Docs...", CatppuccinColors.Mocha.pink).sendWithPrefix()
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
                    CatharsisRemoteRepo.uninitialize()
                    loadRepo()
                }
                thenCallback("branch branch", StringArgumentType.greedyString()) {
                    val branch = argument<String>("branch")
                    CatharsisDevUtils.properties[REPO_BRANCH_PROPERTY] = branch
                    CatharsisDevUtils.saveProperties()
                    Text.of("Set repo branch to $branch").sendWithPrefix()
                }
                thenCallback("branch reset") {
                    CatharsisDevUtils.properties.remove(REPO_BRANCH_PROPERTY)
                    CatharsisDevUtils.saveProperties()
                    Text.of("Reset repo branch!").sendWithPrefix()
                }
                thenCallback("failed") {
                    Text.multiline(
                        Text.of("--------------------------------------------------", TextColor.GRAY),
                        Text.of("!!! XFINITY ISP/INTERNET CENSORSHIP !!!") {
                            color = TextColor.RED
                            bold = true
                        },
                        Text.of("If you live in Russia or any country that (partially) blocks the internet, whitelist these URLs in your VPN."),
                        Text.of(""),
                        Text.of("If your ISP is xFinity, whitelist these URLs in the Advanced Security feature (Or just disable it)."),
                        Text.of(""),
                        Text.of("URLs:") { bold = true },
                        Text.of(""),
                        Text.of("> skyblock-repo.pages.dev/") {
                            hover = Text.of("Click to copy https://skyblock-repo.pages.dev/")
                            onClick {
                                McClient.clipboard = "https://skyblock-repo.pages.dev/"
                                Text.of("Copied https://skyblock-repo.pages.dev/ to clipboard!", TextColor.GREEN).send()
                            }
                        },
                        Text.of(""),
                        Text.of("> skyblock-api-repo.thatgravyboat.tech/") {
                            hover = Text.of("Click to copy https://skyblock-api-repo.thatgravyboat.tech/")
                            onClick {
                                McClient.clipboard = "https://skyblock-api-repo.thatgravyboat.tech/"
                                Text.of("Copied https://skyblock-api-repo.thatgravyboat.tech/ to clipboard!", TextColor.GREEN).send()
                            }
                        },
                        Text.of(""),
                        Text.of("> catharsis.repo.meowdd.ing/") {
                            hover = Text.of("Click to copy https://catharsis.repo.meowdd.ing/")
                            onClick {
                                McClient.clipboard = "https://catharsis.repo.meowdd.ing/"
                                Text.of("Copied https://catharsis.repo.meowdd.ing/ to clipboard!", TextColor.GREEN).send()
                            }
                        },
                        Text.of(""),
                        Text.of("Click them to copy the full URLs!", TextColor.GOLD),
                        Text.of("--------------------------------------------------", TextColor.GRAY),
                    ).send()
                }
            }
        }
    }

    fun id(@Pattern("[a-z_0-9\\/.-]+") path: String): Identifier = Identifiers.of(MOD_ID, path)
    fun mc(@Pattern("[a-z_0-9\\/.-]+") path: String): Identifier = Identifiers.of(path)
    fun sbapi(@Pattern("[a-z_0-9\\/.-]+") path: String): Identifier = Identifiers.of(SkyBlockAPI.MOD_ID, path)
    fun registerClientReloadListener(id: Identifier, replacements: PreparableReloadListener) {
        if (System.getProperties().containsKey("catharsis.skip-listeners")) return
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
