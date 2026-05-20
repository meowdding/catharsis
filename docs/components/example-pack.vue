<script setup>
import { data } from '../data/example-pack.data.js'
import Example from './example.vue'

function getIcon(item, isOpen) {
    if (item.isDirectory) return isOpen ? 'mdi-folder-open' : 'mdi-folder'

    const ext = item.name.split('.').pop()?.toLowerCase()
    const icons = {
        json: 'mdi-code-json',
        mcmeta: 'mdi-code-json',
        png: 'mdi-file-image',
        txt: 'mdi-file-document-outline',
        md: 'mdi-language-markdown'
    }

    return icons[ext] ?? 'mdi-file-outline'
}
</script>

<template>
    <v-sheet class="py-2" color="transparent">
        <v-treeview :items="data" item-value="id" item-children="children" density="compact" open-on-click>
            <template v-slot:prepend="{ item, isOpen }">
                <v-icon :icon="getIcon(item, isOpen)"></v-icon>
            </template>

            <template v-slot:title="{ item }">
                <Example v-if="!item.isDirectory && item.isViewable" :title="item.name">
                    <template #trigger="{ open }">
                        <span class="clickable-file" @click.stop="open">{{ item.name }}</span>
                    </template>

                    <pre class="code-block"><code>{{ item.content }}</code></pre>
                </Example>

                <span v-else>{{ item.name }}</span>
            </template>
        </v-treeview>
    </v-sheet>
</template>

<style scoped>
.clickable-file {
    cursor: pointer;
    transition: color 0.2s ease;
}

.clickable-file:hover {
    color: var(--vp-c-brand-1);
    text-decoration: underline;
}

.code-block {
    margin: 0;
    padding: 1rem;
    overflow-x: auto;
    background-color: var(--vp-code-bg);
    border-radius: 8px;
}
</style>
