<script setup>
    import { data } from "../data/misc-items.data"
</script>
<template>
    <ul>
        <li v-for="a in data">
            <code>{{ a }}</code>
        </li>
    </ul>
</template>
