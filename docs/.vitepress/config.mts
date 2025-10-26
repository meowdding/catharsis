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
                items: [
                    {text: "Texture Armor Model", link: "/armor_models/texture_model"},
                    {text: "Conditional Armor Model", link: "/armor_models/conditional_model"},
                    {text: "Range Dispatch Armor Model", link: "/armor_models/range_dispatch_model"},
                ]
            },
            {
                text: "TODO NAME", // also move the file
                items: [
                    {text: "Data Types", link: "/item_models/data_types"},
                ]
            },
            {
                "text": "Dev Tools",
                "link": "/dev_tools/index",
                "items": [
                    {"text": "Area selection", "link": "/dev_tools/area_selection"},
                ]
            }
        ],

        socialLinks: [
            {icon: 'github', link: 'https://github.com/meowdding/catharsis'}
        ]
    }
})
