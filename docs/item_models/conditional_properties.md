---
title: Item
lang: en-US
---

# Conditional Item Properties

This page lists all custom conditional item properties that are added ontop of the vanilla ones.

### `catharsis:datatype`

> Returns item specific data like, rarity, reforge, fuel...
> #### Additional fields
> - `data_type`, defines the data type to use. [all supported types](data_types)

### `catharsis:all`

> Returns true if all of the given conditions are met.
> #### Additional fields
> - `conditions`, an array of other item conditions to check.

### `catharsis:any`

> Returns true if any of the given conditions are met.
> #### Additional fields
> - `conditions`, an array of other item conditions to check.

### `catharsis:in_area`

> Returns true if the player is in the specified area.
> #### Additional fields
> - `area` The id of the area as defined [here](/block_replacements/areas)
