package me.owdding.katharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.entity.Entity

interface EntityCondition {

    val codec: MapCodec<out EntityCondition>
    val cost: Int get() = 0

    fun matches(entity: Entity): Boolean
    fun optimize(): EntityCondition = this
}

object EntityConditions {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out EntityCondition>>()

    @IncludedCodec
    val CODEC: MapCodec<EntityCondition> = ID_MAPPER.codec(IncludedCodecs.katharsisIdentifier)
        .dispatchMap(EntityCondition::codec) { it }
        .xmap(EntityCondition::optimize) { it }

    init {
        ID_MAPPER.put(Katharsis.id("npc_skin"), KatharsisCodecs.getMapCodec<PlayerEntityConditions.NpcSkin>())
        ID_MAPPER.put(Katharsis.id("player_skin"), KatharsisCodecs.getMapCodec<PlayerEntityConditions.PlayerSkin>())
        ID_MAPPER.put(Katharsis.id("identity"), KatharsisCodecs.getMapCodec<IdentityEntityCondition>())
        ID_MAPPER.put(Katharsis.id("attribute"), KatharsisCodecs.getMapCodec<AttributeEntityCondition>())
        ID_MAPPER.put(Katharsis.id("island"), KatharsisCodecs.getMapCodec<IslandEntityCondition>())
        ID_MAPPER.put(Katharsis.id("equipment_conditional"), KatharsisCodecs.getMapCodec<ConditionalEquipmentEntityCondition>())
        ID_MAPPER.put(Katharsis.id("equipment_select"), SelectEquipmentEntityCondition.CODEC)
        ID_MAPPER.put(Katharsis.id("equipment_range_dispatch"), RangeSelectEquipmentEntityCondition.CODEC)
        ID_MAPPER.put(Katharsis.id("is_baby"), KatharsisCodecs.getMapCodec<BabyEntityCondition>())
        ID_MAPPER.put(Katharsis.id("max_health"), KatharsisCodecs.getMapCodec<MaxHealthEntityCondition>())
        ID_MAPPER.put(Katharsis.id("nbt_number"), KatharsisCodecs.getMapCodec<NbtNumberEntityCondition>())
        ID_MAPPER.put(Katharsis.id("any"), KatharsisCodecs.getMapCodec<AnyEntityCondition>())
        ID_MAPPER.put(Katharsis.id("all"), KatharsisCodecs.getMapCodec<AllEntityCondition>())
        ID_MAPPER.put(Katharsis.id("has_passenger"), HasPassengerEntityCondition.codec)
        ID_MAPPER.put(Katharsis.id("passenger"), KatharsisCodecs.getMapCodec<PassengerEntityCondition>())
        ID_MAPPER.put(Katharsis.id("position"), KatharsisCodecs.getMapCodec<PositionEntityCondition>())
    }
}
