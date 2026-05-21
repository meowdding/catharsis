---
title: Catharsis Documentation
lang: en-US
---

# Catharsis Documentation

Catharsis provides a vanilla like system for retexturing and remodeling Skyblock items, blocks, and entities.

## Main Features

- **Skyblock Items:** Created for Hypixel Skyblock with native support for Skyblock IDs, but can be used everywhere.
- **Config System:** Uses Fabric Pack Overlays to allow for enabling and disabling texture replacements etc.
- **Vanilla Item Models:** Uses the modern Item Model Definition system as its foundation with custom conditions like location, time, player attributes, or item data types.
- **Custom GUIs:** Define custom layouts and styles for menus.
- **Text Replacements:** Modify item names and lore using regex or composite replacers.
- **Entity Overrides:** Replace standard entities with custom Bedrock-style models and textures.
- **Block Replacements:** Change block texture or sounds based on biome, the block below, and more

## Environment Indication

Certain features are exclusive to either Hypixel SkyBlock or Vanilla Minecraft. These restrictions are indicated by an icon next to the title.

- <Environment type="hypixel"/> indicates that this Feature only works on Hypixel SkyBlock.
- <Environment type="minecraft"/> indicates that this Feature only works in Vanilla Minecraft.
- <Environment type="both"/> indicates that this Feature works in both vanilla Minecraft and on Hypixel SkyBlock.

Unless otherwise specified, these restrictions also apply to any subsection beneath the annotated section.

## Catharsis Texture Packs

Using the [Modrinth](https://modrinth.com/) API, once a day we fetch every Catharsis texture pack to display them on our Website.
<br>You can see all the wonderful texture packs that creators have made using Catharsis [here](https://meowdd.ing/texturepacks).

::: details My Texture Pack Isn't Showing Up!

Make sure to add Catharsis as a dependency on your Modrinth releases:

<img alt="A Modrinth edit version page" src="/modrinth-catharsis.png">

:::
