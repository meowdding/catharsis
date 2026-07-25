import fs from "node:fs";

export default {
    watch: ["../../repo/guis/**/*.json"],
    load(watchedFiles) {
        return watchedFiles.map((file) => {
            const description = JSON.parse(fs.readFileSync(file, "utf-8"))["_comment"]

            return {
                name: "katharsis:" +file.slice(file.indexOf("repo/guis") + 10, -5),
                description: description
            }
        })
    }
}
