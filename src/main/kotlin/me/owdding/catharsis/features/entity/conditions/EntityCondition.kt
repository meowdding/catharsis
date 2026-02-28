package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.codecs.IncludedCodecs
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
    val CODEC: MapCodec<EntityCondition> = ID_MAPPER.codec(IncludedCodecs.catharsisIdentifier)
        .dispatchMap(EntityCondition::codec) { it }
        .xmap(EntityCondition::optimize) { it }

    init {
        ID_MAPPER.put(Catharsis.id("npc_skin"), CatharsisCodecs.getMapCodec<PlayerEntityConditions.NpcSkin>())
        ID_MAPPER.put(Catharsis.id("player_skin"), CatharsisCodecs.getMapCodec<PlayerEntityConditions.PlayerSkin>())
        ID_MAPPER.put(Catharsis.id("identity"), CatharsisCodecs.getMapCodec<IdentityEntityCondition>())
        ID_MAPPER.put(Catharsis.id("attribute"), CatharsisCodecs.getMapCodec<AttributeEntityCondition>())
        ID_MAPPER.put(Catharsis.id("island"), CatharsisCodecs.getMapCodec<IslandEntityCondition>())
        ID_MAPPER.put(Catharsis.id("equipment"), CatharsisCodecs.getMapCodec<EquipmentEntityCondition>())
        ID_MAPPER.put(Catharsis.id("any"), CatharsisCodecs.getMapCodec<AnyEntityCondition>())
        ID_MAPPER.put(Catharsis.id("all"), CatharsisCodecs.getMapCodec<AllEntityCondition>())
    }
}
