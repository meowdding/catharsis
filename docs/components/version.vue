<script setup>
import { store } from "../.vitepress/theme/index";
import * as semver from "semver";

const props = defineProps({
    type: {
        type: String,
        required: true
    }
})

const matches = () => {
    return semver.lte(props.type, store.version, false)
}

</script>
<template>
    <slot v-if="matches()"/>
</template>
