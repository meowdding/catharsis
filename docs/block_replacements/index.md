---
title: Block replacements
lang: en-US
next: # we need to hardcode these bc the "next page" button is broken on index pages
    text: Virtual block states
    link: /block_replacements/virtual_block_states
---


# Block replacements

In Catharsis, we allow for retexturing and remodelling of blocks.
This allows you to change the look of the same block in different regions of the game.


## Definition

Definitions are placed in a block replacement definition file located at `assets/<block namespace>/catharsis/block_replacements/<block id>.json`

The definition follows the following scheme

<TreeView>
<span><TypeIcon type="object"/> A <b>block replacement</b> object</span>

- <TypeIcon type="string"/> **type**: One of the block replacement definition types defined below
- <TypeIcon/> Additional fields depending on the value of type, see the respective block replacement type documentation for more details.

</TreeView>

## Supported Types

### Redirect (`catharsis:redirect`)

A simple redirect, replacing one block state with another one.

<Example>

Always replaces the block with gold.

<<< @/example_pack/assets/minecraft/catharsis/block_replacements/oak_leaves.json{json:line-numbers}
</Example>

<TreeView>
<span><TypeIcon type="object"/> Root <b>redirect</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:redirect`
- <TypeIcon type="string"/> **virtual_state**: A virtual [block state](/block_replacements/virtual_block_states) reference
</TreeView>

### Random (`catharsis:random`)

Allows for random block replacements. <br>
The random is seeded based on the blocks position, meaning, while it's random it is consistent for each block. *Resource pack ordering may affect the result!*

<Example>

Replaces the block based on a random noice with either gold or diamond. <br>
In this example it's roughly a 3/4 ratio.

<<< @/example_pack/assets/minecraft/catharsis/block_replacements/oak_log.json{json:line-numbers}
</Example>

<TreeView>
<span><TypeIcon type="object"/> Root <b>random</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:random`
- <TypeIcon type="float"/> **min**: The min value of the random
- <TypeIcon type="float"/> **min**: The max value of the random
- <TypeIcon type="float"/> **threshold**: The min amount to reach to pass the check
- <TypeIcon type="object"/> **definition**: The [block replacement definition](#definition) to use if the check passes.
- <TypeIcon type="object"/> **fallback**: (Optional) The [block replacement definition](#definition) to use if the check fails.
</TreeView>

### Per Area (`catharsis:per_area`)

Allows for having a different block per [area](/block_replacements/areas).

<Example>

Replaces the block with diamond if it is inside the area.

<<< @/example_pack/assets/minecraft/catharsis/block_replacements/dirt.json{json:line-numbers}
</Example>

<TreeView>
<span><TypeIcon type="object"/> Root <b>per area</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:per_area`
- <TypeIcon type="object"/> **entries**: An object of area to block replacement definition.
    - <TypeIcon type="array"/> **&lt;area id&gt;**: The area to test
        - <TypeIcon type="object"/> A [block replacement definition](#definition)
</TreeView>

### Relative (`catharsis:relative`)

Allows you to change a block based on what's around it.

<Example>

Replaces the block with gold if the block under it is equal to grass or dirt.

<<< @/example_pack/assets/minecraft/catharsis/block_replacements/cyan_terracotta.json{json:line-numbers}
</Example>

<TreeView>
<span><TypeIcon type="object"/> Root <b>per area</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:relative`
- <TypeIcon/> **block** or **blocks**
    - <TypeIcon type="string"/> A block id.
    - <TypeIcon type="array"/> A list of block ids.
- <Position positionType="int" customText> <b>offset</b>: The block to check relative to the position.</Position>
- <TypeIcon type="object"/> **definition**: The [block replacement definition](#definition) to use if the check passes.
- <TypeIcon type="object"/> **fallback**: (Optional) The [block replacement definition](#definition) to use if the check fails.
</TreeView>
