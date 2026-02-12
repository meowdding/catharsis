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
- <TypeIcon type="string"/> **texture**: The resource location of the texture to use as the tooltip background. In the vanilla Tooltip Style format, can be found at https://minecraft.wiki/w/Data_component_format#tooltip_style

</TreeView>

#### **Condition** (`minecraft:condition`)

Render an tooltip model based on a boolean property.

<TreeView>
<span><TypeIcon type="object"/> Root <b>condition</b> tooltip model object</span>

- <TypeIcon type="string"/> **type**: `minecraft:condition`
- <TypeIcon type="string"/> **property**: type of boolean property.
  - <TypeIcon/> You can find a list of all available item model boolean properties on the Minecraft wiki at [Item Model Properties](https://minecraft.wiki/w/Items_model_definition#Boolean_property_types). 
  Additionally, you can find a list of all Catharsis custom item properties in the [Catharsis Conditional Properties](../item_models/conditional_properties) documentation.
- <TypeIcon type="object"/> **on_true**: The **tooltip model** object when the property is true.
- <TypeIcon type="object"/> **on_false**: The **tooltip model** object when the property is false.

</TreeView>

#### **Range Dispatch** (`minecraft:range_dispatch`)

Render an tooltip model based on a numeric property.
Will select last entry with a threshold less than or equal to the property value.

<TreeView>
<span><TypeIcon type="object"/> Root <b>range_dispatch</b> tooltip model object</span>

- <TypeIcon type="string"/> **type**: `minecraft:range_dispatch`
- <TypeIcon type="string"/> **property**: type of numeric property.
  - <TypeIcon/> You can find a list of all available item model numeric properties on the Minecraft wiki at [Item Model Properties](https://minecraft.wiki/w/Items_model_definition#Numeric_property_types). 
  Additionally, you can find a list of all Catharsis custom item properties in the [Catharsis Range Properties](../item_models/range_properties) documentation.
- <TypeIcon/> Additional fields depending on the value of **numeric property type**.
- <TypeIcon type="float"/> **scale**: Optional. Will be used to scale the property value before comparing it to thresholds. Default is `1.0`.
- <TypeIcon type="array"/> **entries**:
  - <TypeIcon type="object"/> Entry object
    - <TypeIcon type="float"/> **threshold**: The threshold value for this entry.
    - <TypeIcon type="object"/> **model**: The **tooltip model** object to use for this threshold.
- <TypeIcon type="object"/> **fallback**: Optional. The **tooltip model** object to use if no thresholds are met.
  - Will render as a missing texture if fallback is needed but not provided.

</TreeView>

#### **Select** (`minecraft:select`)

Render a tooltip background based on a select property.

<TreeView>
<span><TypeIcon type="object"/> Root <b>select</b> background model object</span>

- <TypeIcon type="string"/> **type**: `minecraft:select`
- <TypeIcon type="string"/> **property**: type of the select property.
  - <TypeIcon/> You can find a list of all available item select properties on the Minecraft wiki at [Property Types](https://minecraft.wiki/w/Items_model_definition#Property_types).
  Additionally, you can find a list of all Catharsis select item properties in the [Catharsis Select Properties](../item_models/select_properties) documentation
- <TypeIcon/> Additional fields depending on the value of the **select property type**.
- <TypeIcon type="object"/> **switch**:
  - <TypeIcon type="array"/> **&lt;value&gt;**: 
- <TypeIcon type="object"/> **fallback**: Optional. The **background model** object to use if no thresholds are met.
  - Will render as a missing texture if fallback is needed but not provided.

</TreeView>
