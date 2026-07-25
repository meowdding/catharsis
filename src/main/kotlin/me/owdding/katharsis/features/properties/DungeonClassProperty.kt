package me.owdding.katharsis.features.properties

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonAPI
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonClass
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

object DungeonClassProperty : SelectItemModelProperty<DungeonClass> {

    val ID = Katharsis.id("dungeon_class")
    val TYPE: SelectItemModelProperty.Type<out SelectItemModelProperty<DungeonClass>, DungeonClass> = SelectItemModelProperty.Type.create(
        MapCodec.unit { DungeonClassProperty },
        KatharsisCodecs.getCodec()
    )

    override fun get(
        stack: ItemStack,
        level: ClientLevel?,
        entity: LivingEntity?,
        seed: Int,
        displayContext: ItemDisplayContext,
    ): DungeonClass? = DungeonAPI.teammates.find { it.name == entity?.name?.stripped }?.dungeonClass

    override fun valueCodec(): Codec<DungeonClass> = KatharsisCodecs.getCodec()

    override fun type(): SelectItemModelProperty.Type<out SelectItemModelProperty<DungeonClass>, DungeonClass> = TYPE
}
