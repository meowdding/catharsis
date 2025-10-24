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
                link: "/getting_started",
                items: [
                ]
            },
            {
                text: "Item Models",
                items: [
                    {text: "Select properties", link: "/item_models/select_properties"},
                    {text: "Data Types", link: "/item_models/data_types"},
                ]
            }
        ],

        socialLinks: [
            {icon: 'github', link: 'https://github.com/meowdding/catharsis'}
        ]
    }
})
