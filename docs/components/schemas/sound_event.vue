<script setup>
import TypeIcon from "../type-icon.vue";
import TreeView from "../tree-view.vue";

const props = defineProps({
    customText: {
        type: Boolean,
        required: false
    }
})
</script>
<template>
    <TreeView collapsable>
        <template #title>
            <TypeIcon/>
            <span>&nbsp;</span>
            <template v-if="!customText">
                <span>A sound event!</span>
            </template>
            <span v-else><slot/></span>
        </template>
        <ul>
            <li>
                <TypeIcon type="string"/>
                The identifier of the sound to play. You can find a list of sounds <a href="https://minecraft.wiki/w/Sounds.json#Java_Edition_values">here</a>
            </li>
            <li>
                <TypeIcon type="object"/>
                An object describing the sound to play.
                <ul>
                    <li>
                        <TypeIcon type="string"/> <b>sound</b>: The identifier of the sound to play. You can find a list of sounds <a href="https://minecraft.wiki/w/Sounds.json#Java_Edition_values">here</a>
                    </li>
                    <li>
                        <TypeIcon type="float"/> <b>range</b>
                    </li>
                </ul>
            </li>
        </ul>
    </TreeView>
</template>
