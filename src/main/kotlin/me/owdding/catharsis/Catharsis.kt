package me.owdding.catharsis

import com.google.gson.JsonObject
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.catharsis.events.BootstrapConditionalPropertiesEvent
import me.owdding.catharsis.events.BootstrapItemModelsEvent
import me.owdding.catharsis.events.BootstrapNumericPropertiesEvent
import me.owdding.catharsis.events.BootstrapSelectPropertiesEvent
import me.owdding.catharsis.events.FinishRepoLoadEvent
import me.owdding.catharsis.events.StartRepoLoadEvent
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.generated.CatharsisModules
import me.owdding.catharsis.repo.CatharsisRemoteRepo
import me.owdding.catharsis.repo.CatharsisRemoteRepo.REPO_BRANCH_PROPERTY
import me.owdding.catharsis.utils.CatharsisDevUtils
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.extensions.sendWithPrefixIf
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties
import net.minecraft.resources.Identifier
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
import java.util.concurrent.CompletableFuture
import kotlin.io.path.readText
import kotlin.time.Instant

@Module
object Catharsis : ClientModInitializer, CatharsisLogger by CatharsisLogger.autoResolve() {
    val self = FabricLoader.getInstance().getModContainer(MOD_ID).get()
    val buildInfo: BuildInfo by lazy {
        self.findPath("catharsis.json").get().readText().readJson<JsonObject>().toDataOrThrow(CatharsisCodecs.getCodec())
    }

    const val MOD_ID = "catharsis"
    override fun onInitializeClient() {
        info("Catharsis client initialized!")
        CatharsisModules.init { SkyBlockAPI.eventBus.register(it) }

        BootstrapConditionalPropertiesEvent(ConditionalItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapNumericPropertiesEvent(RangeSelectItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapSelectPropertiesEvent(SelectItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapItemModelsEvent(ItemModels.ID_MAPPER::put).post(SkyBlockAPI.eventBus)

        loadRepo()
    }

    fun loadRepo(notify: Boolean = CatharsisDevUtils.getBoolean("repo_notify")) {
        val branch = CatharsisDevUtils.properties[REPO_BRANCH_PROPERTY] ?: buildInfo.branch.replace("/", "-")
        if (notify) {
            info("Loading repo on branch $branch")
            Text.of("Loading repo on branch $branch").sendWithPrefixIf { McLevel.hasLevel }
        }
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
        }
    }

    @Subscription
    fun registerCommand(context: RegisterCommandsEvent) {
        context.register("catharsis repo") {
            thenCallback("reload") {
                CatharsisRemoteRepo.uninitialize()
                loadRepo(true)
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
        }
    }

    fun id(@Pattern("[a-z_0-9\\/.-]+") path: String): Identifier = Identifiers.of(MOD_ID, path)
    fun mc(@Pattern("[a-z_0-9\\/.-]+") path: String): Identifier = Identifiers.of(path)
    fun sbapi(@Pattern("[a-z_0-9\\/.-]+") path: String): Identifier = Identifiers.of(SkyBlockAPI.MOD_ID, path)

    @GenerateCodec
    data class BuildInfo(
        val ref: String,
        val branch: String,
        val timestamp: Instant,
    ) {
        val isStable = ref == "stable"
    }
}
