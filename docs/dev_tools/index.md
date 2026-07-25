---
title: Dev Tools
lang: en-US
---

# <Environment /> Dev Tools

Katharsis provides a list of developer tools to help resource pack creators and developers.
These can be accessed in game using the `/katharsis dev` and `/katharsis debug` commands.

## Features Overview

### [Commands](./commands.md)
A full list of all dev commands available in the mod.

### [Area Selection](./area_selection.md)
A flood fill tool used to export bounding boxes or block coordinate arrays for regions, and ore veins.

### Regex Tester
Accessible using `/katharsis dev regex`, this opens an ingame GUI that lets you paste raw text components and test Regular Expressions against them.

### <Environment type="hypixel" /> GUI & Slot Debugging
Tools to visualize the defined IDs for GUIs and their slots.
* `/katharsis debug gui` toggles an overlay that displays the currently hovered slot ID and all active GUI definitions.
* `/katharsis debug slots <mode>` renders colored overlays on slots to help identify which slots are matched and which are missing.

### <Environment type="hypixel" /> Entity & Item Generation
Use `/katharsis dev give` to generate mannequins and armor stands with specific armor sets, or custom items with injected NBT data. 
You can also use `/katharsis dev find` to find SkyBlock items using the name or ID.
