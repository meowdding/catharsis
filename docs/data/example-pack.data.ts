import fs from 'node:fs'
import path from 'node:path'

export default {
    watch: ["../example_pack/**/*"],
    load() {
        const packDir = path.resolve(__dirname, '../example_pack')
        return buildFileTree(packDir)
    }
}

function buildFileTree(dirPath: string, basePath = '') {
    const entries = fs.readdirSync(dirPath, { withFileTypes: true })
    const result = []

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
            const isViewable = ['.json', '.jsonc', '.json5', '.mcmeta'].includes(path.extname(entry.name))

            let content = ""
            if (isViewable) {
                try {
                    content = fs.readFileSync(fullPath, 'utf-8')
                } catch (e) {
                    content = "Unable to read file content."
                }
            }

            result.push({
                id: currentPath,
                name: entry.name,
                isDirectory: false,
                isViewable: isViewable,
                content: content
            })
        }
    }

    return result.sort((a, b) => {
        if (a.isDirectory === b.isDirectory) return a.name.localeCompare(b.name)
        return a.isDirectory ? -1 : 1
    })
}
