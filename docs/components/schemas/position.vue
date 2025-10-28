<script setup>
import TypeIcon from "../type-icon.vue";

const props = defineProps({
    positionType: {
        type: "float" | "int" | "both",
        required: true
    },
    customText: {
        type: String,
        required: false
    }
})
</script>
<template>
    <TypeIcon/>
    <template v-if="customText === undefined">
        <span v-if="positionType === 'both'">A position of any precision</span>
        <span v-else-if="positionType === 'int'">A block position of ints</span>
        <span v-else>A 3d position of floats</span>
    </template>
    <span v-else :content="customText"></span>
</template>
