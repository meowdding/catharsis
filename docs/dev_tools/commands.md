---
title: Commands
lang: en-US
---

# <Environment /> Commands

### `/catharsis repo`

#### `/catharsis repo reload`
Reloads the remote repository data to apply any upstream changes.

#### `/catharsis repo branch <branch>`
Overrides the remote repository branch and saves it for future boots.

#### `/catharsis repo branch reset`
Resets the repository branch back to the default branch specified in the mods build info.

### `/catharsis dev toggle <feature> [mode]`

#### Gui Definition (`catharsis:gui`)
Toggles an overlay that displays the currently hovered slot ID and all active GUI definitions.

#### Gui Slot Definition (`catharsis:slots`) <NONE|MISSING|PRESENT>
Highlights slots in the current GUI based on the selected mode to help identify which slots have definitions or are missing definitions.

### Entity Definition (`catharsis:entity`)
Adds a text above the head of entities that have an Entity ID attached.

### Mouse Position (`catharsis:mouse_pos`)
Toggles an overlay that displays the current (relative to the top left of the gui) mouse position.

### <Environment type="hypixel" /> `/catharsis dev hand_id`

Prints the id used to modify the item into chat. Also adds a copy button.

### <Environment type="hypixel" />`/catharsis dev hand_data_types`

Adds a copy button in chat to copy all the data types ids with their values for the item in your hand in the chat. Hover to view what condition you can match the data type with.

### `/catharsis dev areas`

#### `/catharsis dev areas toggle <id>`

Toggles rendering of a debug view of the bounding box/octree associated with the area.

#### `/catharsis dev areas disable_all`

Disables all active debug renderers.

### `/catharsis dev area_select`

#### `/catharsis dev area_select <add|remove> <id>`

Adds/Removes the specified block to/from the allow list.

#### `/catharsis dev area_select list`

Prints out the current allow list.

#### `/catharsis dev area_select run {flags} {range}`

Dispatches the area selection with the current allow list, the range is the max distance from the origin.

Usage: `/catharsis dev area_select run [-d] [-e|-c] [-j|-r] [-a] [-o] [-t <y>] [-b <y>] [range]`

#### Flags

State Flags (only one allowed)

> - `-e`/`--exposed` A block must be connected to air on at least one side.
> - `-c`/`--covered` A block must not be exposed to air at all.

Data Flags (only one allowed)

> - `-j`/`--json` Sets the export format to json (default)
> - `-r`/`--raw` Set the export format to raw.

Misc Flags
> - `-d`/`--diagonal` Includes diagonals in the flood fill.
> - `-o`/`--outline` Directly outlines the result.
> - `-a`/`--auto_dispatch` Tries to dispatch a flood fill select for every block in the radius (square).
> - `-t`/`--max_y` `<y>` Sets the maximum Y level for the fill.
> - `-b`/`--min_y` `<y>` Sets the minimum Y level for the fill.

#### `/catharsis dev area_select clear`

Clears all entries from the allow list.

#### `/catharsis dev area_select cancel`

Cancels the currently running area selection job.

### `/catharsis dev give`

#### `/catharsis dev give armorstand <query>`

Gives an armorstand with the armor set of the specified SkyBlock item.

#### `/catharsis dev give mannequin [flags] <query>`

Gives a mannequin with the armor set of the specified SkyBlock item.

Usage: `/catharsis dev give mannequin [-d|-c|-s|-f|-e] [-i] [-l] <query>`

#### Flags

Pose Flags (only one allowed)

> - `-d`/`--standing` (default)
> - `-c`/`--crouching`
> - `-s`/`--swimming`
> - `-f`/`--fall_flying`
> - `-e`/`--sleeping`

Misc flags

> - `-i`/`--immovalbe` Makes the mannequin immovable.
> - `-l`/`--left_handed` Makes the primary hand the left hand.

#### `/catharsis dev give item id|name <query>`

Gives an item by its SkyBlock ID or name. (Also supports giving items directly from JSON parsed in the clipboard when `item` is executed with no further arguments).

### `/catharsis dev find ids|names [flags] <query>`

A utility command to find items, by default uses a contains search that ignores the cases and limits it to 100 items.

Usage: `/catharsis dev find <ids|names> [-r|-c|-s|-e] [-l <amount>|-a] [-m] [-g] [-d <nbt>] [-p] <query>`

#### Flags

Filtering flags (only one allowed)

> - `-c`/`--contains` uses contains (default).
> - `-r`/`--regex` uses regex search.
> - `-s`/`--starts_with` checks if the start is equal.
> - `-e`/`--ends_with` checks if the ending is equal.

Limiting flags (only one allowed)

> - `-l`/`--limit` `<amount>` Limits the items to find (default is 100).
> - `-a`/`--all` Takes all items that match the search.

Misc flags

> - `-m`/`--match_case` Enables case-sensitive matching.
> - `-g`/`--give` Automatically gives you all items that match the search.
> - `-d`/`--custom_data` `<nbt>` Merges the provided custom NBT data tag into the generated items.
> - `-p`/`--place_in_world` Places the resulting items directly into physical shulker boxes in the world (requires Singleplayer).

### `/catharsis dev regex`

Opens an interactive Regex Tester GUI. 
This allows you to type raw text component JSON in one pane, type a regex pattern in the other, and see how the pattern matches and highlights the text output on the screen.

### <Environment type="hypixel"/> `/catharsis dev untextured_ids [type]`

Copies a list of SkyBlock Ids all active packs have not retextured to the clipboard.
Types can be ´ALL´, ´ITEMS´, ´ATTRIBUTES´, ´ENCHANTMENTS´, ´RUNES´, ´PETS´, and ´POTIONS´, or `ALL` if nothing specified.
