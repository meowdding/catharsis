import fs from "node:fs";

export default {
    load() {
        const textures = JSON.parse(fs.readFileSync("../repo/misc_items.json", "utf-8"))["textures"];

        return Object.keys(textures);
    }
}
