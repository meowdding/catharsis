package me.owdding.catharsis.mixins.commands;

import com.mojang.brigadier.tree.CommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(CommandNode.class)
public interface CommandNodeAccessor<S> {

    @Accessor("children")
    Map<String, CommandNode<S>> catharis$getChildren();

    @Accessor("literals")
    Map<String, CommandNode<S>> catharis$getLiteals();

}
