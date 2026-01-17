---
title: Tooltip Background
lang: en-US
---

# Custom Tooltip Background

Allows replacing the item tooltip background and frame with a custom texture using Item definitions.

<Example>
A tooltip background replacement example:

<<< @/example_pack/assets/catharsis/tooltip.json{json:line-numbers}
</Example>

Behaves like item texture replacements, but the model is the following instead:

<TreeView>
    <span><TypeIcon type="object"/> Root <b>texture</b> object</span>
    
- <TypeIcon type="string"/> **type**: `catharsis:texture`
- <TypeIcon type="string"/> **background**: The resource location of the texture to use as the tooltip background.
- <TypeIcon type="string"/> **frame**: The resource location of the texture to use as the tooltip frame.
</TreeView>
