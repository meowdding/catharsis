<script setup>
import { data } from "../data/repo-areas.data.ts";
</script>
<template>
    <ul>
        <li v-for="a in data">
            <code>{{ a.name }}</code>
            <span v-if="a.description !== undefined"> - {{ a.description}}</span>
        </li>
    </ul>
</template>
