import fs from 'node:fs'

export default {
    watch: ["../../repo/timespans/**/*.json"],
    load(watchedFiles) {
        return watchedFiles.map((file) => {
            const description = JSON.parse(fs.readFileSync(file, "utf-8"))["_comment"]

            return {
                name: "catharsis:" +file.slice(file.indexOf("repo/timespans") + 15, -5),
                description: description
            }
        })
    }
}
