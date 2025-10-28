---
title: Virtual Block States
lang: en-US
---

# Virtual Block State

A virtual block state is a modified [vanilla block state file](https://minecraft.wiki/w/Tutorial:Models#Block_states) placed at `<namespace>/catharsis/virtual_block_states/<id>.json`.

A virtual block state is used in combination with a block replacement to replace all properties of a block, and allowing changes based on the blocks state.

<TreeView>
<span><TypeIcon type="object"/> A <b>Virtual Block State</b> object</span>

- <TypeIcon type="string"/> **blend**: (Optional) Changes the blending mode used for the block. One of `DEFAULT`, `SOLID`, `CUTOUT_MIPPED`, `CUTOUT`, `TRANSLUCENT`
- <TypeIcon/> A vanilla block state as defined [here](https://minecraft.wiki/w/Tutorial:Models#Block_states)
</TreeView>
