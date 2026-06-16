package me.owdding.catharsis.features.dev

import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.catharsis.features.entity.CustomEntityDefinitions
import me.owdding.catharsis.features.entity.conditions.*
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.types.FloatPredicate
import me.owdding.catharsis.utils.types.colors.CatppuccinColors
import me.owdding.catharsis.utils.types.suggestion.IterableSuggestionProvider
import me.owdding.ktmodules.Module
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.zombie.Zombie
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import java.util.*
import kotlin.random.Random

@Module
object DebugSummonCommand {

    @Subscription
    private fun RegisterCommandsEvent.onRegister() {
        register("catharsis dev summon") {
            thenCallback(
                "id",
                StringArgumentType.greedyString(),
                IterableSuggestionProvider(CustomEntityDefinitions.getAllIds()),
            ) {
                val idString = argument<String>("id")
                val id = Identifier.tryParse(idString)

                if (id == null) {
                    Text.of("Invalid identifier: $idString", CatppuccinColors.Mocha.red).sendWithPrefix("cath-sumon-invalid-id")
                    return@thenCallback
                }

                val definition = CustomEntityDefinitions.getDefinition(id)
                if (definition == null) {
                    Text.of("No entity definition found for $id", CatppuccinColors.Mocha.red).sendWithPrefix("cath-summon-unfound-id")
                    return@thenCallback
                }

                val player = McClient.self.player ?: return@thenCallback
                val level = player.level() as? ClientLevel ?: return@thenCallback

                val entityTag = CompoundTag()
                entityTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(definition.type).toString())
                applyNbtConditions(definition.target, entityTag)

                val entity = EntityType.loadEntityRecursive(entityTag, level, EntitySpawnReason.COMMAND) { entity ->
                    entity.setPos(player.x, player.y, player.z)
                    entity
                }

                if (entity == null) {
                    Text.of("Failed to create entity type ${definition.type}", CatppuccinColors.Mocha.red).sendWithPrefix("cath-summon-entity-not-found")
                    return@thenCallback
                }

                applyEntityProperties(definition.target, entity)

                // Praying this doesn't fail
                entity.uuid = UUID.randomUUID()
                entity.id = -Random.nextInt(1000, 1000000)

                level.addEntity(entity)

                Text.of("Summoned debug entity for ") {
                    append(id.toString(), CatppuccinColors.Mocha.peach)
                }.sendWithPrefix("cath-entity-summoned")
            }
        }
    }

    private fun applyNbtConditions(condition: EntityCondition, tag: CompoundTag) {
        when (condition) {
            is AllEntityCondition -> condition.conditions.forEach { applyNbtConditions(it, tag) }
            is AnyEntityCondition -> condition.conditions.firstOrNull()?.let { applyNbtConditions(it, tag) }
            is NbtNumberEntityCondition -> {
                val value = when (val p = condition.values) {
                    is FloatPredicate.Range -> p.min
                    is FloatPredicate.Set -> p.set.first()
                }
                tag.putFloat(condition.key, value)
            }

            else -> {}
        }
    }

    private fun applyEntityProperties(condition: EntityCondition, entity: Entity) {
        when (condition) {
            is AllEntityCondition -> condition.conditions.forEach { applyEntityProperties(it, entity) }
            is AnyEntityCondition -> condition.conditions.firstOrNull()?.let { applyEntityProperties(it, entity) }
            is IdentityEntityCondition -> {
                // Not handling uuid bc what if 2 entities are spawned, idk..
                if (condition.name != null) {
                    entity.customName = Component.literal(condition.name)
                    entity.isCustomNameVisible = true
                }
            }

            is MaxHealthEntityCondition -> {
                if (entity is LivingEntity) {
                    val hp = when (val predicate = condition.maxHealth) {
                        is FloatPredicate.Range -> predicate.min.toDouble()
                        is FloatPredicate.Set -> predicate.set.first().toDouble()
                    }
                    entity.getAttribute(Attributes.MAX_HEALTH)?.baseValue = hp
                    entity.health = hp.toFloat()
                }
            }

            is AttributeEntityCondition -> {
                if (entity is LivingEntity) {
                    val value = when (val predicate = condition.values) {
                        is FloatPredicate.Range -> predicate.min.toDouble()
                        is FloatPredicate.Set -> predicate.set.first().toDouble()
                    }
                    entity.getAttribute(condition.attribute)?.baseValue = value
                }
            }

            is BabyEntityCondition -> {
                when (entity) {
                    is AgeableMob, is Zombie -> entity.isBaby = condition.isBaby
                }
            }

            is PlayerEntityConditions.NpcSkin -> {
                Text.of("NPC Skin cannot be set on entities without a full profile so this is not implemented.").sendWithPrefix("cath-npc-skin")
            }

            is PlayerEntityConditions.PlayerSkin -> {
                Text.of("Player Skin cannot be set on entities without a full profile so this is not implemented.").sendWithPrefix("cath-player-skin")
            }

            is IslandEntityCondition -> {
                Text.of("Island Property cannot be set on entity directly, use \"/sbapi toggle force_island\" instead", CatppuccinColors.Mocha.maroon).sendWithPrefix("cath-island")
            }

            is EquipmentEntityCondition -> {
                Text.of("Too lazy to implement a Item model Condition to ItemStack parser, manually set the item yourself", CatppuccinColors.Mocha.maroon)
                    .sendWithPrefix("cath-stack")
            }
        }
    }
}
