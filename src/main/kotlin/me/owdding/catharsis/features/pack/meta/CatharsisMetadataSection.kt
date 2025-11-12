package me.owdding.catharsis.features.pack.meta

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.pack.config.PackConfigHandler
import me.owdding.catharsis.features.pack.config.PackConfigOption
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.impl.util.version.VersionParser
import net.fabricmc.loader.impl.util.version.VersionPredicateParser
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.metadata.MetadataSectionType
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.jvm.optionals.getOrNull

@GenerateCodec
data class CatharsisMetadataSection(
    val id: String,
    val version: String,
    @FieldName("update_url") val updateUrl: String?,
    val dependencies: Map<String, String> = emptyMap(),
    val config: List<PackConfigOption> = emptyList(),
) {

    val incompatibilities: List<Pair<String, ModContainer?>> = dependencies.mapNotNull { (mod, range) ->
        runCatching {
            val predicate = VersionPredicateParser.parse(range)
            val modContainer = FabricLoader.getInstance().getModContainer(mod).getOrNull()

            when {
                modContainer == null -> mod to null
                predicate.test(modContainer.metadata.version) -> null
                else -> mod to modContainer
            }
        }.getOrNull()
    }

    val incompatibleTooltip: List<Component> = run {
        if (incompatibilities.isEmpty()) {
            emptyList()
        } else {
            val missing = incompatibilities.mapNotNull { (id, container) ->
                if (container == null) id else null
            }
            val conflicting = incompatibilities.mapNotNull { (id, container) ->
                container?.metadata?.let { "${it.name} (Current: ${it.version}, Required: ${dependencies[id]})" }
            }

            buildList {
                if (missing.isNotEmpty()) add(Text.translatable("pack.catharsis.incompatible.tooltip.missing"))
                for (mod in missing) add(Text.of(" - $mod"))

                if (conflicting.isNotEmpty()) {
                    if (missing.isNotEmpty()) add(Component.empty())
                    add(Text.translatable("pack.catharsis.incompatible.tooltip.conflicting"))
                    for (mod in conflicting) add(Text.of(" - $mod"))
                }
            }
        }
    }

    private var _isUpdateAvailable: Boolean? = null
    val isUpdateAvailable: Boolean
        get() {
            if (updateUrl == null) return false
            if (_isUpdateAvailable == null) {
                val info = PackUpdateChecker.getUpdateInfo(updateUrl) ?: return false
                val latestVersion = runCatching { VersionParser.parse(info.versions[McClient.version], false) }.getOrNull()
                val currentVersion = runCatching { VersionParser.parse(version, false) }.getOrNull()
                _isUpdateAvailable = latestVersion != null && currentVersion != null && latestVersion > currentVersion
            }
            return _isUpdateAvailable!!
        }

    init {
        this.updateUrl?.let(PackUpdateChecker::requestUpdateInfo)
        val default = PackConfigHandler.getConfig(this.id).default
        default.asMap().clear()
        for (option in this.config) {
            val id = option.id ?: continue
            default.add(id, option.asJson)
        }
    }

    companion object {

        @JvmField
        val TYPE = MetadataSectionType(Catharsis.id("pack/v1").toString(), CatharsisCodecs.getCodec<CatharsisMetadataSection>())
    }
}
