---
title: Armor Models
lang: en-US
next: # we need to hardcode these bc the "next page" button is broken on index pages
    text: Texture Armor Model
    link: /armor_models/texture_model
---

# Armor Models

In Catharsis we support a way to define custom armor models for items.
This allows you to create dynamic textures for armor that can change based on certain properties,

You can define armor models in an armor definition file in `assets/<namespace>/catharsis/armors/<id>.json`.
Similarly to item models you can also declare it for skyblock ids ie. `assets/skyblock/catharsis/armors/<id>.json`.

Armor models support some of the same model types as item models such as `minecraft:condition` and `minecraft:range_dispatch`
as well as armor specific ones such as `catharsis:texture`.

You can see more details on how to use these models on the sidebar.
