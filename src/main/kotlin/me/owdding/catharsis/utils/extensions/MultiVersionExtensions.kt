package me.owdding.catharsis.utils.extensions

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.entity.LivingEntity

//? >= 1.21.9 {
    fun PoseStack.pose(): PoseStack.Pose = this.last()
//?} else {
    /*fun PoseStack.pose() = this
*///?}

fun LivingEntity.asLivingEntity() = this

