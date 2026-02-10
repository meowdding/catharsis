package me.owdding.catharsis.features.item

import me.owdding.catharsis.events.FinishRepoLoadEvent
import me.owdding.catharsis.events.StartRepoLoadEvent
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.repo.CatharsisRemoteRepo
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.extentions.getTexture
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData

@Module
object MiscItemModels {

    private var cache: MiscItems? = null

    @JvmStatic
    fun getModel(stack: ItemStack): Identifier? {
        val skin = stack.getTexture() ?: return null
        return cache?.textures?.entries?.find { skin in it.value }?.key
    }

    @Subscription
    private fun StartRepoLoadEvent.start() {
        cache = null
    }

    @Subscription
    private fun FinishRepoLoadEvent.finish() {
        cache = CatharsisRemoteRepo.getFileContentAsJson("misc_items.json")?.toData(CatharsisCodecs.getCodec<MiscItems>()) ?: return
    }

    @GenerateCodec
    data class MiscItems(
        val textures: Map<Identifier, @Compact List<String>>,
    )
}
