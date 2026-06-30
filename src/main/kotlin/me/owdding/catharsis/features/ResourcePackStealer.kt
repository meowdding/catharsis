package me.owdding.catharsis.features

import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.types.colors.CatppuccinColors
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import java.util.*

@Module
object ResourcePackStealer {
    var stolenData: ResourcePackSteal? = null

    @Subscription
    private fun onPacketReceived(event: PacketReceivedEvent) {
        if (!LocationAPI.onHypixel) return
        val packet = event.packet as? ClientboundResourcePackPushPacket ?: return

        stolenData = ResourcePackSteal(
            packet.url,
            packet.hash,
            packet.id,
            LocationAPI.onAlpha,
        )
    }

    @Subscription
    private fun onCommand(event: RegisterCommandsEvent) {
        event.register("catharsis packStealer") {
            thenCallback("url") {
                McClient.clipboard = stolenData?.packUrl ?: ""
                Text.of("Copied Pack Url to clipboard", CatppuccinColors.Mocha.base).sendWithPrefix("pack-steal-url")
            }
            callback {
                McClient.clipboard = stolenData.toString()
                Text.of("Copied Pack Data to clipboard", CatppuccinColors.Mocha.base).sendWithPrefix("pack-steal")
            }
        }
    }
}


@GenerateCodec
data class ResourcePackSteal(
    val packUrl: String,
    val packHash: String,
    val packId: UUID,
    val alpha: Boolean,
)
