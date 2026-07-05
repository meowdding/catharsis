import {h, reactive} from 'vue'
import type {Theme} from 'vitepress'
import DefaultTheme from 'vitepress/theme'
import './style.css'

import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import {createVuetify} from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import {data as modVersionData} from "../../data/mod-version.data"

import {CustomComponents} from "../../components/mod";

const vuetify = createVuetify({
    components,
    directives,
    theme: {
        defaultTheme: 'dark'
    }
})

export const store = reactive({
    version: modVersionData
});


export default {
    extends: DefaultTheme,
    Layout: () => {
        return h(DefaultTheme.Layout, null, {})
    },
    enhanceApp({app, router, siteData}) {
        app.use(vuetify)
        router.onAfterRouteChange = (to) => {
            if (typeof window !== 'undefined') {
                let version = window.location.search.substring(1)
                if (version.length !== 0) {
                    store.version = version
                } else if (store.version.length === 0) {
                    store.version = modVersionData
                }

                const modVersions = siteData.value.themeConfig.modVersions;

                if (store.version === "latest") {
                    store.version = modVersions.find((x: any) => x.isLatest)?.version;
                } else if (store.version === "beta") {
                    store.version = modVersions.find((x: any) => x.isBeta)?.version;
                }

                let location = window.location.toString()
                let last = location.lastIndexOf("?")
                window.history.replaceState({}, true, location.substring(0, last <= 0 ? location.length : last) + "?" + store.version)
            }
        }
        Object.entries(CustomComponents).forEach(([key, value]) => {
            app.component(key, value)
        })
    }
} satisfies Theme
