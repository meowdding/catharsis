---
title: Entity Overrides
lang: en-US
---

# <Environment/> Defining Entity Overrides

Catharsis allows you to override entity models and textures using Bedrock entity geometry (`.geo.json`) and specific targeting conditions.

Example of an entity definition and its corresponding model and texture mapping.

**`{your_namespace}:catharsis/entity_definitions/{entity_name}.json`**

```json
{
    "target": {
        "type": "..."
    },
    "type": "minecraft:..."
}
```

**`{your_namespace}:catharsis/entities/{entity_name}.json`** (Single Variant)

```json
{
    "texture": "{your_namespace}:textures/{texture_path}.png",
    "model": "{your_namespace}:models/{model_path}.geo.json"
}
```

<Version type="1.0.0-beta.17">

**`{your_namespace}:catharsis/entities/{entity_name}.json`** (Weighted Randomization)

```json
{
    "variants": [
        {
          "weight": 5,
          "texture": "{your_namespace}:textures/mob1.png",
          "model": "{your_namespace}:models/mob.geo.json"
        },
        {
          "weight": 1,
          "texture": "{your_namespace}:textures/mob2.png",
          "model": "{your_namespace}:models/mob.geo.json"
        }
    ]
}
```
</Version>

## Entity Definition Explanation

To override an entity, you need to provide two JSON files. The first handles the targeting and conditions, while the second links the actual model and texture assets.

<TreeView>
  <span><TypeIcon type="object"/> An <b>Entity Definition</b> object (`catharsis/entity_definitions/{entity_name}.json`)</span>

- <TypeIcon type="object"/> **target**: The condition logic required to match the entity.
  - <TypeIcon type="string"/> **type**: The condition type (e.g., `identity`, `is_baby`, `all`, etc.).
  - *(Additional keys depend on the condition type used. See Condition Types below.)*
- <TypeIcon type="string"/> **type**: The vanilla entity registry name to target (e.g., `minecraft:zombie`).

</TreeView>

<br/>

<Version type="1.0.0-beta.17">
<TreeView>
  <span><TypeIcon type="object"/> An <b>Entity Asset Mapping</b> object (`catharsis/entities/{entity_name}.json`)</span>

- <TypeIcon type="array"/><TypeIcon type="object"/> Either a list of variants or a single variant can be provided.
  - <TypeIcon type="array"/> **variants**: A list of variations to apply to the entity to support randomizing models and textures. Selecting is based on the entity's UUID.
    - <TypeIcon type="object"/> **variant**: A single entity model/texture variant.
      - <TypeIcon type="number"/> **weight**: (Optional) The chance this variant is selected relative to other variants. Defaults to `1`.
      - <TypeIcon type="string"/> **texture**: The path to your custom texture image.
      - <TypeIcon type="string"/> **emissive_texture**: (Optional) The path to an emissive texture image, if your model uses emissive textures. Defaults to none.
      - <TypeIcon type="string"/> **model**: The path to your Bedrock entity geometry file.
      - <TypeIcon type="boolean"/> **translucent**: (Optional) If the model has translucent textures. Defaults to `false`.
  - <TypeIcon type="object"/> The singular variant.
    - <TypeIcon type="string"/> **texture**: The path to your custom texture image.
    - <TypeIcon type="string"/> **emissive_texture**: (Optional) The path to an emissive texture image, if your model uses emissive textures. Defaults to none.
    - <TypeIcon type="string"/> **model**: The path to your Bedrock entity geometry file.
    - <TypeIcon type="boolean"/> **translucent**: (Optional) If the model has translucent textures. Defaults to `false`.

</TreeView>
</Version>

> **Note:** Ensure you place the actual `.png` texture and `.geo.json` model in the specific paths you declare. If you use vanilla names for the bones in your model, the entity
> will automatically use vanilla animations. If a bone has the wrong name, the mod will send an error to the log.

## Condition Types

There are multiple different definitions you can use inside the `target` object to precisely match entities.

### All / Any (`all`, `any`)

Used to evaluate multiple conditions in combination with each other. `all` requires every condition in the list to be met, while `any` matches if at least one condition is true.

<TreeView>

- <TypeIcon type="string"/> **type**: Either `all` or `any`.
- <TypeIcon type="array"/> **conditions**: A list of condition objects.

</TreeView>

### Skin (`npc_skin`, `player_skin`)

Allows you to access state about a player entity.

<TreeView>

- <TypeIcon type="string"/> **type**: Either `npc_skin` or `player_skin`.
- <TypeIcon type="string"/> **skin**: A reference to a skin URL. The entity is only matched if it uses this skin *exactly*.

</TreeView>

### Identity (`identity`)

Allows you to access state about regular entities based on their core identifiers.

<TreeView>

- <TypeIcon type="string"/> **type**: `identity`
- <TypeIcon type="string"/> **uuid**: (Optional) Matches the entity's exact UUID. *Note: Skyblock mostly uses random UUIDs.*
- <TypeIcon type="string"/> **name**: (Optional) Matches the entity's custom name.

</TreeView>

### Attribute (`attribute`)

Allows access to check entity attributes, such as maximum health or movement speed.

<TreeView>

- <TypeIcon type="string"/> **type**: `attribute`
- <TypeIcon type="string"/> **attribute**: The specific attribute to check.
- <TypeIcon type="any"/> **value** or **values**: Matches one or multiple specific attribute values.

</TreeView>

### NBT Number (`nbt_number`)

Checks if a specific numeric NBT tag on the entity matches a given value, list of values, or a range. 
Works for any number as well as booleans (0 or 1, I think at least).

<TreeView>

- <TypeIcon type="string"/> **type**: `nbt_number`
- <TypeIcon type="string"/> **key**: The NBT key to check.
- <TypeIcon type="any"/> **value** or **values**: Matches one or multiple specific numeric values, or a range.

</TreeView>

<Version type="1.0.0-beta.16">

### Max Health (`max_health`)

Allows you to access the entity's maximum health value.

<TreeView>

- <TypeIcon type="string"/> **type**: `max_health`
- <TypeIcon type="any"/> **max_health**: Matches a specific maximum health value or a range of values.
- <TypeIcon type="boolean"/> **use_modifiers**: (Optional) Use Health Modifiers like mayor perks etc, see a full list [here](https://github.com/meowdding/catharsis/blob/stable/repo/health_modifiers.json).

</TreeView>
</Version>

### <Environment type="hypixel"/> Island (`island`)

Matches if the entity (or player) is located on a specific Skyblock island.

<TreeView>

- <TypeIcon type="string"/> **type**: `island`
- <TypeIcon type="string"/> **island** or **islands**: Matches one or multiple island identifiers.

</TreeView>

### Baby State (`is_baby`)

Matches an entity based on whether it is a baby or an adult.

<TreeView>

- <TypeIcon type="string"/> **type**: `is_baby`
- <TypeIcon type="boolean"/> **is_baby**: The boolean value to compare against (`true` for baby, `false` for adult).

</TreeView>


<Version type="1.0.0-beta.18">

### Equipment Conditional (`equipment_conditional`)

Matches based on a boolean item model property evaluated on the item currently equipped by the entity.

<TreeView>

- <TypeIcon type="string"/> **type**: `equipment_conditional`
- <TypeIcon type="string"/> **slot**: Which equipment slot to check (e.g., `head`, `chest`, `mainhand`).
- <TypeIcon type="object"/> **property**: The conditional item model property to check.

</TreeView>

### Equipment Select (`equipment_select`)

Evaluates a select item model property on the equipped item against a set of values.

<TreeView>

- <TypeIcon type="string"/> **type**: `equipment_select`
- <TypeIcon type="string"/> **slot**: Which equipment slot to check.
- <TypeIcon type="object"/> **property**: The select item model property to check.
- <TypeIcon type="array"/> **cases**: A list of match cases.
  - <TypeIcon type="object"/> **case**
    - <TypeIcon type="array"/> **when**: A list of valid values to match.
    - <TypeIcon type="boolean"/> **result**: The boolean result if matched.
- <TypeIcon type="boolean"/> **fallback**: (Optional) The result if no cases match. Defaults to `false`.

</TreeView>

### Equipment Range Dispatch (`equipment_range_dispatch`)

Evaluates a numeric item model property on the equipped item using threshold entries.

<TreeView>

- <TypeIcon type="string"/> **type**: `equipment_range_dispatch`
- <TypeIcon type="string"/> **slot**: Which equipment slot to check.
- <TypeIcon type="object"/> **property**: The numeric item model property to check.
- <TypeIcon type="number"/> **scale**: (Optional) Multiplier applied to the property's value. Defaults to `1.0`.
- <TypeIcon type="array"/> **entries**: A list of threshold entries.
  - <TypeIcon type="object"/> **entry**
    - <TypeIcon type="number"/> **threshold**: The numeric threshold.
    - <TypeIcon type="boolean"/> **result**: The boolean result applied when the property value is matched.
- <TypeIcon type="boolean"/> **fallback**: (Optional) The result if the value falls outside thresholds or is invalid. Defaults to `false`.

</TreeView>

### Has Passengers (`has_passengers`)

Matches if an entity has passengers.

<TreeView>

- <TypeIcon type="string"/> **type**: `has_passenger`

</TreeView>

### Passenger Conditions (`passengers`)

Runs matches on a specific passenger, or false if none.

<TreeView>

- <TypeIcon type="string"/> **type**: `passenger`
- <TypeIcon type="int"/> **index**: (Optional) The index of the passenger, defaults to `0`. (0 is first, 1 is second, etc.)
- <TypeIcon type="string"/> **entityType**: (Optional) The entity type of the passenger, defaults to `null`. (Null means entity type does not matter)
- <TypeIcon type="object"/> **condition**: The conditions to run on the passenger.

</TreeView>

</Version>

## Model Quirks

When creating your Bedrock `.geo.json` models, please keep the following in mind:

- **Per-face UVs:** The mod **cannot** handle per-face UV mapping. Using this feature will break the model.
- Everything else should work fine out of the box.
