---
title: Virtual Block States
lang: en-US
---

# Virtual Block State

A virtual block state is a modified [vanilla block state file](https://minecraft.wiki/w/Tutorial:Models#Block_states) placed at `<namespace>/catharsis/virtual_block_states/<id>.json`.

A virtual block state is used in combination with a block replacement to replace all properties of a block, and allowing changes based on the blocks state.

<Example>

Things that happen in this example
- Replaces the targeted block with `diamond_block`
- Replaces bedrock that appeared in the place for the targeted block with `gold_block`
- Changes the `place_sound` to `minecraft:block.chorus_flower.grow`
- Changes the blend mode to `translucent`

<<< @/example_pack/assets/your_name_space/catharsis/virtual_block_states/diamond.json{json:line-numbers}
</Example>

<TreeView>
<span><TypeIcon type="object"/> A <b>Virtual Block State</b> object</span>

- <TypeIcon type="string"/> **blend**: (Optional) Changes the blending mode used for the block. 
    - One of `DEFAULT`, `SOLID`, `CUTOUT_MIPPED`, `CUTOUT`, `TRANSLUCENT`
- <TypeIcon type="object"/> **sounds**: (Optional) Changes the sound that is played for interactions.
  - <SoundEvent customText> <b>hit</b>: (Optional) The sound that is used when hitting the block.</SoundEvent>
  - <SoundEvent customText> <b>break</b>: (Optional) The sound that is used when the block is broken.</SoundEvent>
  - <SoundEvent customText> <b>step</b>: (Optional) The sound that is used when something steps on the block.</SoundEvent>
  - <SoundEvent customText> <b>place</b>: (Optional) The sound that is used when the block is placed.</SoundEvent>
  - <SoundEvent customText> <b>fall</b>: (Optional) The sound that is used when something falls on the block.</SoundEvent>
- <TypeIcon type="object"/> **overrides**: (Optional) A map of block id to vanilla block state definitions.
  - <TypeIcon type="array"/> **&lt;block_id&gt;**: The block id that is replaced e.g. `minecraft:bedrock`.
    - <TypeIcon/> A vanilla block state as defined [here](https://minecraft.wiki/w/Tutorial:Models#Block_states)
- <TypeIcon/> A vanilla block state as defined [here](https://minecraft.wiki/w/Tutorial:Models#Block_states)
</TreeView>
