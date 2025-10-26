---
title: Block replacements
lang: en-US
---

# Areas

Areas are predefined regions that you can run checks with.
This allows you to remodel things based on their location, while we also get the benefit of optimizing it.

You can define areas under `<namespace>/catharsis/areas/<id>.json`

The definition follows the following scheme

```json
{
    "type": <type>
}
```

## Supported Types

### `basic`
> The most basic definition of an area
> #### Additional fields
> - `boxes` Either a list of [bounding boxes](/misc/schemas#bounding-box) or just one bounding box
> - `islands` (Optional) Either a lost of [skyblock island](/misc/schemas#skyblock-island)
> - `min_size` Int (optional, at least 4) the lowest size a branch can reach in the tree, before falling over to a leaf.

### `per_island`
> Allows to change the definition based on the current island.
> 
> #### Addition fields
> - `entries` A list of area entries, they must also include an `islands` field as described in the basic entry.


