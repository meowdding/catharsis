---
title: Areas
lang: en-US
---

# Areas

Areas are predefined regions that you can run checks with.
This allows you to remodel things based on their location, while we also get the benefit of optimizing it.

You can define areas under `<namespace>/catharsis/areas/<id>.json`

The definition follows the following scheme

<TreeView>
<span><TypeIcon type="object"/> An <b>Area definition</b> object</span>

- <TypeIcon type="string"/> **type**: One of the area definition types defined below
- <TypeIcon/> Additional fields depending on the value of type, see the respective area type documentation for more details.

</TreeView>

## Supported Types

### Simple (`catharsis:simple`)
 The most simple type of area.

<TreeView>
<span><TypeIcon type="object"/> Root <b>simple</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:simple`
- <BoundingBox custom_title><TypeIcon/> <b>box</b>: One single bounding box</BoundingBox>
- <TypeIcon type="array"/> <b>islands</b> (Optional) A list of skyblock islands
    - <Island/>

</TreeView>

### Multiple (`catharsis:multiple`)
Groups multiple boxes into one area.

<TreeView>
<span><TypeIcon type="object"/> Root <b>multiple</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:multiple`
- <TypeIcon type="array"/> <b>boxes</b>: A list of bounding boxes
    - <BoundingBox/>
- <TypeIcon type="array"/> <b>islands</b>: (Optional) A list of skyblock islands
    - <Island/>
- <TypeIcon type="int"/> **min_size**: (Optional, at least 4) The lowest size a branch can reach in the tree, before falling over to a leaf.

</TreeView>

### Always (`catharsis:always`)
Always matches

<TreeView>
<span><TypeIcon type="object"/> Root <b>always</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:always`

</TreeView>

### On Island (`catharsis:on_island`)
Matches while on island

<TreeView>
<span><TypeIcon type="object"/> Root <b>On Island</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:on_island`
- <TypeIcon type="array"/> **islands**: (Optional) A list of skyblock islands
    - <Island/>

</TreeView>

### Per Island (`catharsis:per_island`)
Allows to change the definition based on the current island.

<TreeView>
<span><TypeIcon type="object"/> Root <b>On Island</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:per_island`
- <TypeIcon type="object"/> **entries**: A list of area definitions
    - <TypeIcon type="array"/> **islands**: A list of skyblock islands
        - <Island/>
    - <TypeIcon type="string"/> **type**: One of the [area definition types](#supported-types)
    - <TypeIcon/> Additional fields depending on the value of type, see the respective area type documentation for more details.

</TreeView>

