<script setup>
    import { data } from "../data/repo-guis.data"
</script>
<template>
    <ul>
        <li v-for="a in data">
            <code>{{ a.name }}</code>
            <span v-if="a.description !== undefined"> - {{ a.description}}</span>
        </li>
    </ul>
</template>
