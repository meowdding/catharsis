---
title: Area Selection
lang: en-US
---

# <Environment /> Area Selection

The area selection tool provides a flood fill used to create bounding boxes for block structures, like custom ore veins or other structures.

### Setup and Filtering

The flood fill uses a whitelist of allowed blocks, where only blocks added to this filter list will be included when it searches for connected areas.

- Add a block: `/catharsis dev area_select add block <id>`
- Remove a block: `/catharsis dev area_select remove block <id>`
- View the current list: `/catharsis dev area_select list`
- Clear all blocks: `/catharsis dev area_select clear`

### Dispatching a Selection

Look at the block you want the flood fill to start from and run the following command. 
The `range` is the maximum distance from the origin the search will traverse (defaults to 100).

> `/catharsis dev area_select run [flags] [range]`

:::tip
Dispatching a selection with a range higher than 100-200 blocks may cause performance loss and will take a while.
If you want to abort the flood fill, you can run `/catharsis dev area_select cancel`.
:::

### Flags

You can customize the behavior and export formats using flags:

#### State Flags (only one allowed):
> - `-e` / `--exposed`: A block must be connected to air on at least one side.
> - `-c` / `--covered`: A block must not be exposed to air at all.

####  Export Flags (only one allowed):
> - `-j` / `--json`: Sets the export format to JSON (default).
> - `-r` / `--raw`: Sets the export format to raw coordinate strings.

#### Misc Flags:
> - `-d` / `--diagonal`: Includes diagonals in the flood fill.
> - `-o` / `--outline`: Directly outlines the resulting region bounds once it's finished.
> - `-a` / `--auto_dispatch`: Tries to dispatch a flood fill selection for every block within the radius (square).
> - `-t` / `--max_y <y>`: Sets the maximum Y-level.
> - `-b` / `--min_y <y>`: Sets the minimum Y-level.

### Output

When a selection finishes, Catharsis will send a clickable message in chat.

- [area]: Copies the bounding box for the region to your clipboard.
- [blocks]: Copies an array of every individual block coordinate found in the selection.
- [outline]: Toggles an in world render of the specific blocks captured.
- [highlight]: Toggles an in world bounding box around the entire region.
