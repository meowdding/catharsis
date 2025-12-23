<script setup>
import { data } from "../data/repo-areas.data.ts";
</script>
<template>
    <ul>
        <li v-for="a in data">
            <code>{{ a }}</code>
        </li>
    </ul>
</template>
