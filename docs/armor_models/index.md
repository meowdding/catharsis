---
title: Armor Models
lang: en-US
next: # we need to hardcode these bc the "next/previous page" button is broken on index pages
    text: Block Replacements
    link: /block_replacements
prev:
    text: Item Models
    link: /item_models
---

# Armor Models

In Catharsis we support a way to define custom armor models for items.
This allows you to create dynamic textures for armor that can change based on certain properties,

You can define armor models in an armor definition file in `assets/<namespace>/catharsis/armors/<id>.json`.
Similarly to item models you can also declare it for skyblock ids i.e. `assets/skyblock/catharsis/armors/<id>.json`.

Armor models support some of the same model types as item models such as `minecraft:condition` and `minecraft:range_dispatch`
as well as armor specific ones such as `catharsis:texture`.

## Json format

<TreeView>
<span><TypeIcon type="object"/> Root object</span>

- <TypeIcon type="object"/> **model**: Set the **Armor Model** to use.

</TreeView>

### Armor Model

<TreeView>
<span><TypeIcon type="object"/> An <b>Armor Model</b> object</span>

- <TypeIcon type="string"/> **type**: One of `minecraft:condition`, `minecraft:range_dispatch`, or `catharsis:texture`.
- <TypeIcon/> Additional fields depending on the value of type, see the respective model type documentation for more details.

</TreeView>

## Available Armor Model Types

#### **Texture** (`catharsis:texture`)

Render a specific texture for the armor.

<TreeView>
<span><TypeIcon type="object"/> Root <b>texture</b> armor model object</span>

- <TypeIcon type="string"/> **type**: `catharsis:texture`
- <TypeIcon type="string"/> **texture**: The texture to use for the armor. This should be a resource location pointing to a PNG file.

</TreeView>

#### **Condition** (`minecraft:condition`)

Render an armor model based on a boolean property.

<TreeView>
<span><TypeIcon type="object"/> Root <b>condition</b> armor model object</span>

- <TypeIcon type="string"/> **type**: `minecraft:condition`
- <TypeIcon type="string"/> **property**: type of boolean property.
  - <TypeIcon/> You can find a list of all available item model boolean properties on the Minecraft wiki at [Item Model Properties](https://minecraft.wiki/w/Items_model_definition#Boolean_property_types). 
  Additionally, you can find a list of all Catharsis custom item properties in the [Catharsis Conditional Properties](../item_models/conditional_properties) documentation.
- <TypeIcon type="object"/> **on_true**: The **armor model** object when the property is true.
- <TypeIcon type="object"/> **on_false**: The **armor model** object when the property is false.

</TreeView>

#### **Range Dispatch** (`minecraft:range_dispatch`)

Render an armor model based on a numeric property. 
Will select last entry with a threshold less than or equal to the property value.

<TreeView>
<span><TypeIcon type="object"/> Root <b>range_dispatch</b> armor model object</span>

- <TypeIcon type="string"/> **type**: `minecraft:range_dispatch`
- <TypeIcon type="string"/> **property**: type of numeric property.
  - <TypeIcon/> You can find a list of all available item model numeric properties on the Minecraft wiki at [Item Model Properties](https://minecraft.wiki/w/Items_model_definition#Numeric_property_types). 
  Additionally, you can find a list of all Catharsis custom item properties in the [Catharsis Range Properties](../item_models/range_properties) documentation.
- <TypeIcon/> Additional fields depending on the value of **numeric property type**.
- <TypeIcon type="float"/> **scale**: Optional. Will be used to scale the property value before comparing it to thresholds. Default is `1.0`.
- <TypeIcon type="array"/> **entries**:
  - <TypeIcon type="object"/> Entry object
    - <TypeIcon type="float"/> **threshold**: The threshold value for this entry.
    - <TypeIcon type="object"/> **model**: The **armor model** object to use for this threshold.
- <TypeIcon type="object"/> **fallback**: Optional. The **armor model** object to use if no thresholds are met.
  - Will render as a missing texture if fallback is needed but not provided.

</TreeView>
