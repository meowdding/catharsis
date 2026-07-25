---
title: Geo Models (Bedrock Geometry)
lang: en-US
---

# <Environment/> Geo Models (Bedrock Geometry)

Katharsis supports loading Bedrock Entity Geometry (`.geo.json`). 
This allows resource pack creators to use custom 3D models made in **Blockbench** for custom entities or armor models.

## Creating Your Model in Blockbench

When creating a model, you should use the **Bedrock Entity** format in Blockbench. 
Once your finished your model, export it as a **Bedrock Geometry** file, this will generate a `.geo.json` file.

### Bone Naming Conventions

How you name your bones determines how the model animates in game.

#### For Armor Models

The renderer requires specific bone names to attach to the players body parts. If a bone is not named correctly, it will not render on that respective body part.

Use the following bone names depending on the targeted equipment slot:
- Head Slot: `head`
- Chest Slot: `body`, `right_arm`, `left_arm`
- Legs Slot: `right_leg`, `left_leg`
- Feet Slot: `right_foot`, `left_foot`

See the [Armor Models Documentation](./armor_models.md) on how to override armor pieces.

#### For Entity Overrides

If you are replacing a mob, you should name your bones using the **vanilla bone names** for that specific entity. 
If the bone names match, the model will use the vanilla animations for that entity.

See the [Entity Overrides Documentation](./entity_overrides.md) on how to override entities.


## Current Limitations & Quirks

Katharsis uses a custom renderer to load Bedrock files into Minecraft. 
Because of this, some advanced Bedrock features are ignored or unsupported.

Keep the following limits in mind when creating your models:

1. **UV Mapping Restrictions:**
   - Per-Face UVs: The mod cannot handle per-face UV mapping. Using this feature will break the model. Stick to the default flat Boxed UV arrays.
2. **Ignored Features:**
    - Animations & Molang: Custom animations bundled inside the `.geo.json`, Molang queries, and `binding` properties are ignored.
    - Custom Meshes: Custom Meshes like `poly_mesh` and `texture_meshes` are not supported.
    - Locators: Locator points are ignored.
    - Capes: Geometry cape definitions are unused.
3. **Armor Rendering Scaling:**
    - The armor renderer adjusts to the models pivot and scale.

If you require any specific features or encounter any issues, please ask on our Discord or create a GitHub Issue!
