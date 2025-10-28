import {h} from 'vue'
import type {Theme} from 'vitepress'
import DefaultTheme from 'vitepress/theme'
import './style.css'

import {CustomComponents} from "../../components/mod";

export default {
  extends: DefaultTheme,
  Layout: () => {
    return h(DefaultTheme.Layout, null, {})
  },
  enhanceApp({ app, router, siteData }) {
      Object.entries(CustomComponents).forEach(([key, value]) => {
          app.component(key, value)
      })
  }
} satisfies Theme
