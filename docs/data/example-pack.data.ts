import fs from 'node:fs'
import path from 'node:path'

export default {
    watch: ["../example_packs/**/*"],
    load() {
        const packsDir = path.resolve(__dirname, '../example_packs')
        const packs = {}
        const entries = fs.readdirSync(packsDir, { withFileTypes: true })

        for (const entry of entries) {
            if (entry.isDirectory()) {
                packs[entry.name] = buildFileTree(path.join(packsDir, entry.name))
            }
        }
        return packs
    }
}

function buildFileTree(dirPath: string, basePath = '') {
    const entries = fs.readdirSync(dirPath, { withFileTypes: true })
    const result = []

    const viewableExts = ['.json', '.jsonc', '.json5', '.mcmeta', '.txt', '.md']

    for (const entry of entries) {
        const fullPath = path.join(dirPath, entry.name)
        const currentPath = path.join(basePath, entry.name)

        if (entry.isDirectory()) {
            result.push({
                id: currentPath,
                name: entry.name,
                isDirectory: true,
                children: buildFileTree(fullPath, currentPath)
            })
        } else {
            const ext = path.extname(entry.name).toLowerCase()
            const isViewable = viewableExts.includes(ext)

            let content = ""
            let isBase64 = false

            try {
                if (isViewable) {
                    content = fs.readFileSync(fullPath, 'utf-8')
                } else {
                    content = fs.readFileSync(fullPath, 'base64')
                    isBase64 = true
                }
            } catch (e) {
                content = isViewable ? "Unable to read file content." : ""
                console.error(`Failed to read file: ${fullPath}`, e)
            }

            result.push({
                id: currentPath,
                name: entry.name,
                isDirectory: false,
                isViewable: isViewable,
                isBase64: isBase64,
                content: content
            })
        }
    }

    return result.sort((a, b) => {
        if (a.isDirectory === b.isDirectory) return a.name.localeCompare(b.name)
        return a.isDirectory ? -1 : 1
    })
}
