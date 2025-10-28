import {h} from 'vue'
import type {Theme} from 'vitepress'
import DefaultTheme from 'vitepress/theme'
import './style.css'

import TreeView from "../../components/tree-view.vue";
import TypeIcon from "../../components/type-icon.vue";

export default {
  extends: DefaultTheme,
  Layout: () => {
    return h(DefaultTheme.Layout, null, {})
  },
  enhanceApp({ app, router, siteData }) {
    app.component("TreeView", TreeView)
    app.component("TypeIcon", TypeIcon)
  }
} satisfies Theme
