import fs from "node:fs";

export default {
    load() {
        let version = null

        fs.readFileSync("../gradle.properties", "utf-8").toString().split(/[\n\r]+/).map((x) => {
            let first = x.indexOf('=')
            return {key: x.substring(0, first),value: x.substring(first + 1)}
        }).forEach((x) => {
            if (x.key === "version") {
                version = x.value.toString()
            }
        })

        return version;
    }
}
