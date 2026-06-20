<script setup>
import { ref, computed } from 'vue'
import { data } from '../data/example-pack.data.js'
import Example from './example.vue'
import JSZip from 'jszip'

const packNames = Object.keys(data)
const selectedPack = ref(packNames[0])

const currentPackData = computed(() => {
    return data[selectedPack.value] || []
})

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

function addFilesToZip(zip, items, currentPath = '') {
    for (const item of items) {
        if (item.isDirectory) {
            const folder = zip.folder(item.name)
            addFilesToZip(folder, item.children, currentPath)
        } else {
            if (item.content) {
                zip.file(item.name, item.content, { base64: item.isBase64 })
            }
        }
    }
}

async function downloadZip() {
    const zip = new JSZip()
    addFilesToZip(zip, currentPackData.value)

    const content = await zip.generateAsync({ type: 'blob' })
    const url = URL.createObjectURL(content)

    const a = document.createElement('a')
    a.href = url
    a.download = `${selectedPack.value}.zip`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
}
</script>

<template>
    <v-sheet class="py-2" color="transparent">
        <div class="d-flex align-center mb-4 gap-4">
            <v-select
                v-model="selectedPack"
                :items="packNames"
                label="Select Example Pack"
                variant="outlined"
                density="compact"
                hide-details
                class="flex-grow-1"
            ></v-select>
            <v-btn
                color="primary"
                prepend-icon="mdi-download"
                @click="downloadZip"
            >
                Download Pack
            </v-btn>
        </div>

        <v-treeview :items="currentPackData" item-value="id" item-children="children" density="compact" open-on-click>
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

.gap-4 {
    gap: 16px;
}
</style>
