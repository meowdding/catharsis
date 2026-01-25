import {defineConfig} from 'vitepress'

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


        sidebar: [
            {
                text: "Getting started",
                link: "/getting_started/index",
                items: [
                    {text: "SkyBlock Textures", link: "/getting_started/skyblock_textures"},
                ]
            },
            {
                text: "Item Models",
                link: "/item_models/index",
                items: [
                    {text: "Select properties", link: "/item_models/select_properties"},
                    {text: "Range properties", link: "/item_models/range_properties"},
                    {text: "Conditional properties", link: "/item_models/conditional_properties"},
                ]
            },
            {
                text: "Armor Models",
                link: "/armor_models/index",
            },
            {
                text: "Gui Modifications",
                items: [
                    {text: "Gui Definitions", link: "/guis/definitions"},
                    {text: "Slot Definitions", link: "/guis/slots"},
                ]
            },
            {
                text: "Block replacements",
                link: "/block_replacements/",
                items: [
                    {text: "Conditions", link: "/block_replacements/conditions"},
                    {text: "Virtual Block States", link: "/block_replacements/virtual_block_states"},
                    {text: "Area", link: "/block_replacements/areas"},
                ]
            },
            {
                text: "Miscellaneous",
                items: [
                    {text: "Pack Metadata", link: "/miscellaneous/pack_metadata"},
                    {text: "Data Types", link: "/miscellaneous/data_types"},
                    {text: "Timespan", link: "/miscellaneous/timespans"},
                    {text: ".cats file format", link: "/miscellaneous/cats_file_format"},
                ]
            },
            {
                "text": "Dev Tools",
                "link": "/dev_tools/index",
                "items": [
                    {"text": "Commands", "link": "/dev_tools/commands"},
                    {"text": "Area selection", "link": "/dev_tools/area_selection"},
                ]
            }
        ],

        socialLinks: [
            {icon: 'github', link: 'https://github.com/meowdding/catharsis'}
        ],

        head: [['link', { rel: 'icon', href: '/favicon.png' }]],

        footer: {
            message: 'Tree view icons from the <a href="https://minecraft.wiki" target="_blank">Minecraft.wiki</a>',
        },

        editLink: {
            pattern: 'https://github.com/meowdding/catharsis/edit/main/docs/:path'
        }
    }
})
