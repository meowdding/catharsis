<script setup>
import TypeIcon from "../type-icon.vue";

const props = defineProps({
    custom_title: {
        type: Boolean,
        required: false
    }
})
</script>
<template>
    <span><TypeIcon type="string"/> Either the value of <code>mode</code> in <code>/locraw</code> or one of the <a href="https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/location/SkyBlockIsland.kt">enum values</a></span>
</template>
