package me.owdding.catharsis.features.pack.resourceconditions

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.version.VersionPredicate
import net.minecraft.SharedConstants
import net.minecraft.resources.RegistryOps
import net.minecraft.server.packs.PackType
import net.minecraft.util.InclusiveRange
import kotlin.jvm.optionals.getOrNull

@GenerateCodec
data class VersionResourceCondition(
    val type: VersionType = VersionType.MINECRAFT,
    val minecraftPredicate: String?,
    val packFormatRange: InclusiveRange<Int>?,
) : ResourceCondition {
    override fun getType(): ResourceConditionType<*> = TYPE

    override fun test(registryInfoLookup: RegistryOps.RegistryInfoLookup?): Boolean = when (type) {
        VersionType.MINECRAFT -> {
            val predicate = VersionPredicate.parse(minecraftPredicate)
            predicate.test(FabricLoader.getInstance().getModContainer("minecraft").getOrNull()!!.metadata.version)
        }

        VersionType.PACK_FORMAT -> {
            val format = SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES)
            (this.packFormatRange == null || this.packFormatRange.isValueInRange(format.major))
        }
    }

    enum class VersionType {
        MINECRAFT,
        PACK_FORMAT
    }

    companion object {
        val TYPE: ResourceConditionType<VersionResourceCondition> = ResourceConditionType.create(
            Catharsis.id("version"),
            CatharsisCodecs.getMapCodec<VersionResourceCondition>(),
        )
    }
}
