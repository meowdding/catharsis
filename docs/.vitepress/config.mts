import {defineConfig} from 'vitepress'
import vuetify from 'vite-plugin-vuetify'

// https://vitepress.dev/reference/site-config
export default defineConfig({
    title: "Catharsis",
    description: "Catharsis Documentation",
    base: "/",
    themeConfig: {
        // https://vitepress.dev/reference/default-theme-config
        nav: [
            {text: 'Home', link: '/'},
        ],

        search: {provider: 'local'},

        sidebar: [
            {
                text: "Introduction",
                items: [
                    {text: "Getting Started", link: "/getting_started/index"},
                    {text: "SkyBlock Textures", link: "/getting_started/skyblock_textures"},
                    {text: "Example Pack", link: "/getting_started/example_pack"}
                ]
            },
            {
                text: "Pack Setup",
                items: [
                    {text: "Catharsis Pack Metadata", link: "/pack_metadata/index"},
                    {text: "Configuration Options", link: "/pack_metadata/config"},
                    {text: "Fabric Overlays", link: "/pack_metadata/overlays"}
                ]
            },
            {
                text: "Models & Visuals",
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
                ]
            },
            {
                text: "UI & Text",
                items: [
                    {
                        text: "GUI Modifications",
                        collapsed: false,
                        items: [
                            {text: "GUI Definitions", link: "/ui_text/guis/definitions"},
                            {text: "Slot Definitions", link: "/ui_text/guis/slots"},
                        ]
                   },
                    {text: "Text Replacements", link: "/ui_text/text_replacements"},
                    {text: "Tooltip Backgrounds", link: "/ui_text/tooltip_background"},
                ]
            },
            {
                text: "Miscellaneous",
                items: [
                    {text: "Data Types", link: "/miscellaneous/data_types"},
                    {text: "Timespans", link: "/miscellaneous/timespans"},
                    {text: ".cats File Format", link: "/miscellaneous/cats_file_format"},
                ]
            },
            {
                text: "Developer Tools",
                items: [
                    {text: "Overview", link: "/dev_tools/index"},
                    {text: "Commands", link: "/dev_tools/commands"},
                    {text: "Area Selection", link: "/dev_tools/area_selection"},
                ]
           }
        ],

        socialLinks: [
            {icon: 'github', link: 'https://github.com/meowdding/catharsis'}
        ],

        head: [['link', {rel: 'icon', href: '/favicon.png'}]],

        footer: {
            message: 'Tree view icons from the <a href="https://minecraft.wiki" target="_blank">Minecraft.wiki</a>' +
                '<br/>Not an official Minecraft product. Not approved by or affiliated with Mojang or Microsoft.' +
                '<br/>Not approved or affiliated with Hypixel.',
        },

        editLink: {
            pattern: 'https://github.com/meowdding/catharsis/tree/development/docs/:path'
        }
    },
    vite: {
        plugins: [
            vuetify({ autoImport: true })
        ],
        ssr: {
            noExternal: ['vuetify']
        }
    }
})
