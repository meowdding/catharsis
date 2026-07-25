import {defineConfig} from 'vitepress'
import vuetify from 'vite-plugin-vuetify'
import * as semver from "semver";
import modVersion from "../data/mod-version.data"

const modVersionData = modVersion.load()
export const modVersions = [
    { text: "1.0.0-beta.21" },
    { text: "1.0.0-beta.20" },
    { text: "1.0.0-beta.19" },
    { text: "1.0.0-beta.18" },
    { text: "1.0.0-beta.17" },
    { text: "1.0.0-beta.16" }
].map((x) => {
    const version = x.text;
    let text = x.text;
    let isBeta = false;
    let isLatest = false;

    if (semver.gt(version, modVersionData)) {
        text = `${version} (Beta Release)`;
        isBeta = true;
    } else if (semver.eq(version, modVersionData)) {
        text = `${version} (Latest Release)`;
        isLatest = true;
    }

    return {
        version: version,
        text: text,
        link: "?" + version,
        isBeta,
        isLatest
    };
});

// https://vitepress.dev/reference/site-config
export default defineConfig({
    title: "Katharsis",
    description: "Katharsis Documentation",
    base: "/",

    head: [['link', {rel: 'icon', href: '/favicon.png'}]],

    themeConfig: {
        // Passing ModVersions through here into other modules
        modVersions,

        // https://vitepress.dev/reference/default-theme-config
        nav: [
            {text: 'Home', link: '/'},
            {
                "text": "Mod Version",
                "items": modVersions
            }
        ],

        search: {provider: 'local'},

        sidebar: [
            {
                text: "Introduction",
                collapsed: false,
                items: [
                    {text: "Getting Started", link: "/getting_started/index"},
                    {text: "SkyBlock Textures", link: "/getting_started/skyblock_textures"},
                    {text: "Example Pack", link: "/getting_started/example_pack"}
                ]
            },
            {
                text: "Pack Setup",
                collapsed: false,
                items: [
                    {text: "Katharsis Pack Metadata", link: "/pack_metadata/index"},
                    {text: "Configuration Options", link: "/pack_metadata/config"},
                    {text: "Fabric Overlays", link: "/pack_metadata/overlays"}
                ]
            },
            {
                text: "Models & Visuals",
                collapsed: false,
                items: [
                    {
                        text: "Item Models",
                        collapsed: false,
                        items: [
                            {text: "Overview", link: "/models_visuals/item_models/index"},
                            {text: "Select Properties", link: "/models_visuals/item_models/select_properties"},
                            {text: "Range Properties", link: "/models_visuals/item_models/range_properties"},
                            {text: "Conditional Properties", link: "/models_visuals/item_models/conditional_properties"},
                        ]
                    },
                    {text: "Armor Models", link: "/models_visuals/armor_models"},
                    {
                        text: "Block Replacements",
                        collapsed: false,
                        items: [
                            {text: "Overview", link: "/models_visuals/block_replacements/index"},
                            {text: "Conditions", link: "/models_visuals/block_replacements/conditions"},
                            {text: "Virtual Block States", link: "/models_visuals/block_replacements/virtual_block_states"},
                            {text: "Areas", link: "/models_visuals/block_replacements/areas"},
                        ]
                    },
                    {text: "Entity Overrides", link: "/models_visuals/entity_overrides"},
                    {text: "Bedrock Geometry", link: "/models_visuals/bedrock_geometry"},
                ]
            },
            {
                text: "UI & Text",
                collapsed: false,
                items: [
                    {
                        text: "GUI Modifications",
                        collapsed: false,
                        items: [
                            {text: "GUI Definitions", link: "/ui_text/guis/definitions"},
                            {text: "Slot Definitions", link: "/ui_text/guis/slots"},
                            {text: "GUI Modifiers", link: "/ui_text/guis/modifiers"},
                        ]
                    },
                    {text: "Text Replacements", link: "/ui_text/text_replacements"},
                    {text: "Tooltip Backgrounds", link: "/ui_text/tooltip_background"},
                ]
            },
            {
                text: "Mod Compatibility",
                collapsed: false,
                items: [
                    {text: "Overview", link: "/mod_compatibility/index"},
                    {text: "Imc", link: "/mod_compatibility/imc"},
                    {text: "Other Compats", link: "/mod_compatibility/other_compats"}
                ]
            },
            {
                text: "Miscellaneous",
                collapsed: false,
                items: [
                    {text: "Data Types", link: "/miscellaneous/data_types"},
                    {text: "Timespans", link: "/miscellaneous/timespans"},
                    {text: ".cats File Format", link: "/miscellaneous/cats_file_format"},
                    {text: "Miscellaneous Items", link: "/miscellaneous/misc_items"}
                ]
            },
            {
                text: "Developer Tools",
                collapsed: false,
                items: [
                    {text: "Overview", link: "/dev_tools/index"},
                    {text: "Commands", link: "/dev_tools/commands"},
                    {text: "Area Selection", link: "/dev_tools/area_selection"},
                ]
            }
        ],

        socialLinks: [
            {icon: 'github', link: 'https://github.com/meowdding/katharsis'},
            {icon: 'discord', link: 'https://meowdd.ing/discord'},
            {icon: 'matrix', link: 'https://matrix.to/#/#meowdding:mona.cat'},
        ],

        footer: {
            message: 'Tree view icons from the <a href="https://minecraft.wiki" target="_blank">Minecraft.wiki</a>' +
                '<br/>Not an official Minecraft product. Not approved by or affiliated with Mojang or Microsoft.' +
                '<br/>Not approved by or affiliated with Hypixel.',
        },

        editLink: {
            pattern: 'https://github.com/meowdding/katharsis/tree/development/docs/:path'
        },

        lastUpdated: {
            text: 'Last Updated'
        },
    },
    vite: {
        plugins: [
            vuetify({autoImport: true})
        ],
        ssr: {
            noExternal: ['vuetify']
        }
    }
})
