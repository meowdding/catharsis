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
This allows you to create dynamic textures for armors that can change based on certain properties.

You can define armor models in an armor definition file in `assets/<namespace>/catharsis/armors/<id>.json`.
Similarly to item models you can also declare it for skyblock ids i.e. `assets/skyblock/catharsis/armors/<id>.json`.

Armor models support some of the same model types as item models such as `minecraft:condition` and `minecraft:range_dispatch`
as well as armor specific ones such as `catharsis:texture` or `catharsis:model`.

## Json format

<TreeView>
<span><TypeIcon type="object"/> Root object</span>

- <TypeIcon type="object"/> **model**: Set the **Armor Model** to use, as defined below.
- <TypeIcon type="object"/> **part_visibility**: Optional. What body parts should be visible when equipped.
    - <TypeIcon/> Can be any of `head`, `chest`, `left_arm`, `right_arm`, `left_leg`, or `right_leg`.
        - <TypeIcon type="boolean"/> Will hide both the part and the layer on top ie. sleeves or pants layer.
        - <TypeIcon type="object"/> Part Visibility Object
            - <TypeIcon type="boolean"/> **base**: Optional. Whether to show the body part. Defaults to `true`.
            - <TypeIcon type="boolean"/> **overlay**: Optional. Whether to show the part overlay. Defaults to `true`.

</TreeView>

### Armor Model

<TreeView>
<span><TypeIcon type="object"/> An <b>Armor Model</b> object</span>

- <TypeIcon type="string"/> **type**: One of the types below.
- <TypeIcon/> Additional fields depending on the value of type, see the respective model type documentation for more details.

</TreeView>

## Available Armor Model Types

#### **Texture** (`catharsis:texture`)

Render a specific texture for the armor.

<Example>

A simple armor replacement with a texture

<<< @/example_pack/assets/skyblock/catharsis/armors/texture.json{json:line-numbers}
</Example>

<TreeView>
<span><TypeIcon type="object"/> Root <b>texture</b> armor model object</span>

- <TypeIcon type="string"/> **type**: `catharsis:texture`
- <TypeIcon type="array"/> **layers**: The layers of the armor, each layer is rendered on top of the previous one.
    - <TypeIcon type="string"/> The texture to use for the armor. This should be a resource location pointing to a PNG file.
- <TypeIcon type="array"/> **tints**: Optional. The color tints to apply to each layer of the armor.
    - <TypeIcon type="object"/> Tint Object
        - <TypeIcon type="string"/> **type**: The tint source type.
        - <TypeIcon/> You can find information about what tint sources are available on the Minecraft wiki at [Tint Source Types](https://minecraft.wiki/w/Items_model_definition#Tint_sources_types).
</TreeView>

#### **Model** (`catharsis:model`)

Render a specific bedrock armor geometry for the armor.

<Example>

Replaces the armor with a custom model.

<<< @/example_pack/assets/skyblock/catharsis/armors/model.json{json:line-numbers}
</Example>

<TreeView>
<span><TypeIcon type="object"/> Root <b>model</b> armor model object</span>

- <TypeIcon type="string"/> **type**: `catharsis:model`
- <TypeIcon type="string"/> **model**: The geo model to use for the armor. This should be a resource location pointing to a GEO JSON file.
- <TypeIcon type="array"/> **layers**: The layers of the armor, each layer is rendered on top of the previous one.
    - <TypeIcon type="string"/> The texture to use for the armor. This should be a resource location pointing to a PNG file.
- <TypeIcon type="array"/> **tints**: Optional. The color tints to apply to each layer of the armor.
    - <TypeIcon type="object"/> Tint Object
        - <TypeIcon type="string"/> **type**: The tint source type.
        - <TypeIcon/> You can find information about what tint sources are available on the Minecraft wiki at [Tint Source Types](https://minecraft.wiki/w/Items_model_definition#Tint_sources_types).

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

#### **Select** (`minecraft:select`)

Render an armor model based on a select property.

<TreeView>
<span><TypeIcon type="object"/> Root <b>select</b> armor model object</span>

- <TypeIcon type="string"/> **type**: `minecraft:select`
- <TypeIcon type="string"/> **property**: type of the select property.
  - <TypeIcon/> You can find a list of all available item select properties on the Minecraft wiki at [Property Types](https://minecraft.wiki/w/Items_model_definition#Property_types).
  Additionally, you can find a list of all Catharsis select item properties in the [Catharsis Select Properties](../item_models/select_properties) documentation
- <TypeIcon/> Additional fields depending on the value of the **select property type**.
- <TypeIcon type="object"/> **switch**:
  - <TypeIcon type="array"/> **&lt;value&gt;**: 
- <TypeIcon type="object"/> **fallback**: Optional. The **armor model** object to use if no thresholds are met.
  - Will render as a missing texture if fallback is needed but not provided.

</TreeView>

#### **Redirect** (`catharsis:redirect`)

Changes the context of any future models, allowing you to change things based on other item slots.

<TreeView>
<span><TypeIcon type="object"/> Root <b>redirect</b> armor model object</span>

- <TypeIcon type="string"/> **type**: `catharsis:redirect`
- <TypeIcon type="string"/> **slot**: one of `MAINHAND`, `OFFHAND`, `FEET`, `LEGS`, `CHEST`, `HEAD`, `BODY` or `SADDLE`
- <TypeIcon type="object"/> **model**: The **armor model** object to use if no thresholds are met.
  - Will render as a missing texture if fallback is needed but not provided.

</TreeView>
