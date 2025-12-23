export default {
    watch: ["../../repo/areas/**/*.json"],
    load(watchedFiles) {
        return watchedFiles.map((file) => {
            return "catharsis:" + file.slice(file.indexOf("repo/areas") + 11, -5)
        })
    }
}
