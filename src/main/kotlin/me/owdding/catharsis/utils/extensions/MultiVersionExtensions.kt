package me.owdding.catharsis.utils.extensions

import com.mojang.blaze3d.vertex.PoseStack

//? >= 1.21.9 {
    fun PoseStack.pose(): PoseStack.Pose = this.last()
//?} else {
    /*fun PoseStack.pose() = this
*///?}

//? = 1.21.8
/*fun net.minecraft.world.entity.LivingEntity.asLivingEntity() = this*/

