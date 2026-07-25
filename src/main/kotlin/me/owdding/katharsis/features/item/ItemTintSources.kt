package me.owdding.katharsis.features.item

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.events.BootstrapItemTintSourceEvent
import me.owdding.katharsis.features.pack.config.PackConfigHandler
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.minecraft.client.color.item.ItemTintSource
import net.minecraft.client.color.item.ItemTintSources
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.extentions.asInt

@GenerateCodec
data class ConfigTintSource(
    val pack: String,
    val id: String,
) : ItemTintSource {

    private val color by lazy { PackConfigHandler.getConfig(pack).get(id).asInt(-1) }

    override fun calculate(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?): Int = this.color
    override fun type(): MapCodec<out ItemTintSource> = KatharsisCodecs.getMapCodec<ConfigTintSource>()
}

@GenerateCodec
data class RedirectedTintSource(
    val slot: EquipmentSlot,
    val source: ItemTintSource,
) : ItemTintSource {

    override fun calculate(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?): Int =
        this.source.calculate(entity?.getItemBySlot(this.slot) ?: ItemStack.EMPTY, level, entity)
    override fun type(): MapCodec<out ItemTintSource> = CODEC

    companion object {

        val CODEC: MapCodec<RedirectedTintSource> = RecordCodecBuilder.mapCodec { it.group(
            EquipmentSlot.CODEC.fieldOf("slot").forGetter(RedirectedTintSource::slot),
            ItemTintSources.CODEC.fieldOf("source").forGetter(RedirectedTintSource::source)
        ).apply(it, ::RedirectedTintSource) }
    }
}

@Module
object ItemTintSources {

    @Subscription
    private fun BootstrapItemTintSourceEvent.onTintSources() {
        this.register(Katharsis.id("config"), KatharsisCodecs.getMapCodec<ConfigTintSource>())
        this.register(Katharsis.id("redirect"), RedirectedTintSource.CODEC)
    }
}
