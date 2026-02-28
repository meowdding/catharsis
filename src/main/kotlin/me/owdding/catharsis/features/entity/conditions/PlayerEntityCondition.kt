package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldNames
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.entity.ClientAvatarEntity
import net.minecraft.core.ClientAsset
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import tech.thatgravyboat.skyblockapi.utils.extentions.isRealPlayer


sealed interface PlayerEntityConditions : EntityCondition {
    @GenerateCodec
    data class NpcSkin(
        @FieldNames("skins", "skin") @Compact val skins: Set<String>
    ) : PlayerEntityConditions {

        override val codec: MapCodec<out EntityCondition> = CatharsisCodecs.getMapCodec<NpcSkin>()

        override fun matches(entity: Entity): Boolean {
            if (entity !is ClientAvatarEntity) return false
            if (entity is Player && entity.isRealPlayer()) return false

            val skin = getSkin(entity) ?: return false

            return skin in skins
        }
    }

    @GenerateCodec
    data class PlayerSkin(
        @FieldNames("skins", "skin") @Compact val skins: Set<String>
    ) : PlayerEntityConditions {

        override val codec: MapCodec<out EntityCondition> = CatharsisCodecs.getMapCodec<PlayerSkin>()

        override fun matches(entity: Entity): Boolean {
            if (entity !is ClientAvatarEntity) return false
            if (entity !is Player || !entity.isRealPlayer()) return false

            val skin = getSkin(entity) ?: return false

            return skin in skins
        }
    }

    fun getSkin(entity: ClientAvatarEntity): String? {
        //? if > 1.21.8 {
        val bodySkin = entity.skin.body
        if (bodySkin !is ClientAsset.DownloadedTexture) return null
        val skinUrl = bodySkin.url
        //?} else {
        /*val skinUrl = entity.skin.textureUrl
        *///?}
        return skinUrl
    }
}
