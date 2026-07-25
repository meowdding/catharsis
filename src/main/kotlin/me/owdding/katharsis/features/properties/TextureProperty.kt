package me.owdding.katharsis.features.properties

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.codecs.IncludedCodecs
import me.owdding.katharsis.utils.extensions.base64Texture
import me.owdding.katharsis.utils.types.Base64String
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

object TextureProperty : SelectItemModelProperty<Base64String> {

    val ID = Katharsis.id("texture")
    val TYPE: SelectItemModelProperty.Type<out SelectItemModelProperty<Base64String>, Base64String> = SelectItemModelProperty.Type.create(
        MapCodec.unit { TextureProperty },
        IncludedCodecs.BASE64_STRING_CODEC,
    )

    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, displayContext: ItemDisplayContext): Base64String? {
        return stack.base64Texture
    }

    override fun valueCodec(): Codec<Base64String> = KatharsisCodecs.getCodec<Base64String>()
    override fun type(): SelectItemModelProperty.Type<out SelectItemModelProperty<Base64String>, Base64String> = TYPE
}
