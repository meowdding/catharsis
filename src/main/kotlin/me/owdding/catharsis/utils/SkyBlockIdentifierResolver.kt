package me.owdding.catharsis.utils

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.imc.ImcHandler.getCatharsisId
import me.owdding.catharsis.features.item.MiscItemModels
import me.owdding.catharsis.features.pack.meta.CatharsisMetadataSection
import me.owdding.ktmodules.Module
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.UNKNOWN
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockAttributesRepo
import tech.thatgravyboat.skyblockapi.utils.extentions.get
import java.util.function.Predicate
import kotlin.text.lowercase

@Module
object SkyBlockIdentifierResolver : ResourceManagerReloadListener {

    private var loadedPackMetadata = emptyList<CatharsisMetadataSection>()

    fun getCustomLocation(item: ItemStack): Identifier? {
        val itemId = item[DataTypes.SKYBLOCK_ID] ?: return null

        if (itemId.isDerived && loadedPackMetadata.any { it.disableDerivedIds }) {
            return null
        }

        return when {
            itemId.isItem -> resolveItem(itemId)
            itemId.isPet -> resolvePet(itemId)
            itemId.isAttribute -> resolveAttribute(itemId)
            itemId.isRune -> resolveRune(itemId)
            itemId.isEnchantment -> resolveEnchantment(itemId)
            itemId.isPotion -> resolvePotion(itemId)
            else -> null
        }
    }

    @JvmStatic
    fun resolveModelId(predicate: Predicate<Identifier>, stack: ItemStack): Identifier? {
        val extraId = stack.getCatharsisId()
        if (extraId != null) {
            return extraId
        }

        val itemId = getCustomLocation(stack)
        if (itemId != null && predicate.test(itemId)) {
            return itemId
        }

        val hypixelId = getHypixelLocation(stack)
        if (hypixelId != null) {
            return hypixelId
        }

        val baseMiscModel = MiscItemModels.getBaseModel(stack)
        if (baseMiscModel != null && predicate.test(baseMiscModel)) {
            return baseMiscModel
        }

        val extraMiscModel = MiscItemModels.getExtraModel(stack)
        if (extraMiscModel != null && predicate.test(extraMiscModel)) {
            return extraMiscModel
        }

        return null
    }

    private fun getHypixelLocation(item: ItemStack): Identifier? {
        val itemId = item[DataTypes.ID]?.lowercase() ?: return null
        return Identifier.tryBuild("skyblock", itemId)
    }

    private fun SkyBlockId.cleanOrNull() = this.cleanId.lowercase().takeUnless { it == UNKNOWN }

    private fun resolveItem(itemId: SkyBlockId): Identifier? {
        val path = itemId.cleanId.lowercase().takeIf { Identifier.isValidPath(it) } ?: return null
        return Identifier.tryBuild("skyblock", path)
    }

    private fun resolvePet(itemId: SkyBlockId): Identifier? {
        val cleanId = itemId.cleanOrNull() ?: return null
        return Identifier.tryBuild("skyblock", "pets/${cleanId.substringBefore(":").lowercase()}")
    }

    private fun resolveEnchantment(itemId: SkyBlockId): Identifier? {
        val cleanId = itemId.cleanOrNull() ?: return null
        return Identifier.tryBuild("skyblock", "enchantments/${cleanId.substringBefore(":").lowercase()}")
    }

    private fun resolveRune(itemId: SkyBlockId): Identifier? {
        val cleanId = itemId.cleanOrNull() ?: return null
        return Identifier.tryBuild("skyblock", "runes/${cleanId.substringBefore(":").lowercase()}")
    }

    private fun resolveAttribute(itemId: SkyBlockId): Identifier? {
        val attributeId = itemId.cleanOrNull() ?: return null
        val data = SkyBlockAttributesRepo.get(attributeId) ?: return null
        return Identifier.tryBuild("skyblock", "attributes/${data.shardId.lowercase()}")
    }

    private fun resolvePotion(itemId: SkyBlockId): Identifier? {
        val potionId = itemId.cleanOrNull() ?: return null
        return Identifier.tryBuild("skyblock", "potions/${potionId.substringBefore(":").lowercase()}")
    }


    override fun onResourceManagerReload(resourceManager: ResourceManager) {
        loadedPackMetadata = resourceManager.listPacks().toList().mapNotNull { pack ->
            pack.getMetadataSection(CatharsisMetadataSection.TYPE) ?: return@mapNotNull null
        }
    }


    init {
        Catharsis.registerClientReloadListener(Catharsis.id("skyblock_id_resolver"), this)
    }
}
