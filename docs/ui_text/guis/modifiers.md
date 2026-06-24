---
title: GUI Modifiers
lang: en-US
---

# <Environment/> GUI Modifiers

GUI Modifiers allow Texture Packs to modify containers based on a [GUI Definition](./definitions.md).

## Json format

<TreeView>
<span><TypeIcon type="object"/> Root object</span>
<ul>

- <TypeIcon type="object"/> **target**: [A GUI Definition Target](#gui-definition-target)
- <TypeIcon type="boolean"/> **overrideLabels**: (Optional) Disables rendering the Container Title, defaults to `false`.
- <TypeIcon type="boolean"/> **overrideBackground**: (Optional) Disables rendering the Container Background Texture, defaults to `false`.

<Version type="1.0.0-beta.18">

- <TypeIcon type="boolean"/> **disableItemList**: (Optional) Disables the rendering of item list mods, defaults to `false`.

</Version>

- <TypeIcon type="object"/> **bounds**: (Optional) Determines if you clicked outside the container, uses the default container bounds if not specified.
    - <TypeIcon type="int"/> **width**: The width, should be 100 when using the default size.
    - <TypeIcon type="int"/> **height**: The height, should be 100 when using the default size.

<Version type="1.0.0-beta.18">

- <TypeIcon type="array"/> **itemListExclusionZones** (Optional) Specific exclusion zones for item list mods to exclude.
    - <TypeIcon type="object"/> An Exclusion Zone object.
        - <TypeIcon type="int"/> **x**: The x coordinate where the exclusion zone starts.
        - <TypeIcon type="int"/> **y**: The y coordinate where the exclusion zone starts.
        - <TypeIcon type="int"/> **width**: The width of the exclusion zone.
        - <TypeIcon type="int"/> **height**: The height of the exclusion zone.

</Version>

- <TypeIcon type="object"/> **slots**: (Optional) A map of slots to modify.
    - <TypeIcon type="string"/> **&lt;key&gt;**: The id of slot you want to modify
      - <TypeIcon type="object"/> [A slot modifier](#slot-modifier).
- <TypeIcon type="array"/> **elements**: (Optional) A list of [GUI elements](#gui-elements).
- <TypeIcon type="array"/> **widgets**: (Optional) A list of [GUI widgets](#gui-widgets).

</ul>
</TreeView>

## GUI Definition Target

### **Existing GUI Definition** (`catharsis:definition`)

Target to an existing GUI Definition

<TreeView>
<span><TypeIcon type="object"/> Root <b>definition</b> gui modifier target object</span>

- <TypeIcon type="string"/> **type**: `catharsis:definition`
- <TypeIcon type="string"/> **definition**: An existing GUI definition's identifier.

</TreeView>

## Slot Modifier

Modify a slot by moving it, disabling it, etc.

<TreeView>
<span><TypeIcon type="object"/> Root <b>Slot Modifier</b> map value</span>

- <TypeIcon type="boolean"/> **hidden**: (Optional) Hides the slot, defaults to `false`.
- <TypeIcon type="boolean"/> **highlightable**: (Optional) Hides the hover highlight, defaults to `true`.
- <TypeIcon type="object"/> **position**: (Optional) Moves the slot to a new x,y.
    - <TypeIcon type="int"/> **x**: The new x coordinate of the slot.
    - <TypeIcon type="int"/> **y**: The new y coordinate of the slot.
- <TypeIcon type="boolean"/> **clickable**: (Optional) Removes the ability to click a slot, defaults to `true`.

</TreeView>

## GUI Elements

### **Player** (`catharsis:player`)

Render the Player itself on the screen.

<TreeView>
<span><TypeIcon type="object"/> Root <b>GUI element</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:player`
- <TypeIcon type="object"/> **x**: The x coordinate & alignment of the player. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The x coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the player, can be `START`, `CENTER` or `END`.
- <TypeIcon type="object"/> **y**: The y coordinate & alignment of the player. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The y coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the player, can be `START`, `CENTER` or `END`.
- <TypeIcon type="int"/> **width**: The width of the player.
- <TypeIcon type="int"/> **height**: The height of the player.
- <TypeIcon type="string"/> **rotation**: (Optional) The players rotation, will follow the mouse if undefined. **Can only be one of the entries below.**
    - <TypeIcon type="array"/> A list of 4 floats: first, second, and the third component of the imaginary part, and the real part.
    - <TypeIcon type="object"/> An AxisAngle definition.
        - <TypeIcon type="float"/> **angle**: The angle in radians.
        - <TypeIcon type="array"/> **axis**: A list of 4 floats: The value of x, y, and z.

</TreeView>

### **Sprite** (`catharsis:sprite`)

Render a sprite on the screen.

<TreeView>
<span><TypeIcon type="object"/> Root <b>GUI element</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:sprite`
- <TypeIcon type="string"/> **sprite**: The Identifier of the sprite to render.
- <TypeIcon type="string"/> **layer**: (Optional) The layer to render the sprite on, defaults to `BACKGROUND`. Can be `BACKGROUND` & `FOREGROUND`.
- <TypeIcon type="object"/> **x**: (Optional) The x coordinate & alignment of the sprite. Defaults to `0, START`. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The x coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the sprite, can be `START`, `CENTER` or `END`.
- <TypeIcon type="object"/> **y**: (Optional) The y coordinate & alignment of the sprite. Defaults to `0, START`. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The y coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the sprite, can be `START`, `CENTER` or `END`.
- <TypeIcon type="int"/> **width**: (Optional) The width of the sprite, defaults to the container width.
- <TypeIcon type="int"/> **height**: (Optional) The height of the sprite, defaults to the container height.

</TreeView>

### **Text** (`catharsis:text`)

Render text on the screen.

<TreeView>
<span><TypeIcon type="object"/> Root <b>GUI element</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:text`
- <TypeIcon type="object"/> **text**: [A component in JSON format.](https://minecraft.wiki/w/Text_component_format)
- <TypeIcon type="int"/> **color**: (Optional) The color to render the base string with, defaults to [`-12566464`](https://www.hexcolortool.com/#6844f8).
- <TypeIcon type="object"/> **x**: The x coordinate & alignment of the text. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The x coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the text, can be `START`, `CENTER` or `END`.
- <TypeIcon type="object"/> **y**: The y coordinate & alignment of the text. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The y coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the text, can be `START`, `CENTER` or `END`.
- <TypeIcon type="float"/> **alignment**: (Optional) The text alignment based on the coordinates above, defaults to `0`. 0 is left, 0.5 is center, 1 is right.

</TreeView>

## GUI Widget

### Button (`catharsis:button`)

Render a button on the screen.

<TreeView>
<span><TypeIcon type="object"/> Root <b>GUI widget</b> object</span>

- <TypeIcon type="string"/> **normal**: The Identifier of the texture to render.
- <TypeIcon type="string"/> **hovered**: The Identifier of the texture to render when the button is hovered.
- <TypeIcon type="object"/> **interaction**: The interaction when clicked, view [here](#gui-widget-interaction).
- <TypeIcon type="object"/> **tooltip**: The tooltip when hovered, view [here](#gui-widget-tooltip).
- <TypeIcon type="object"/> **x**: The x coordinate & alignment of the button. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The x coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the button, can be `START`, `CENTER` or `END`.
- <TypeIcon type="object"/> **y**: The y coordinate & alignment of the button. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The y coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the button, can be `START`, `CENTER` or `END`.
- <TypeIcon type="int"/> **width**: The width of the button.
- <TypeIcon type="int"/> **height**: The height of the button.

</TreeView>

## GUI Widget Interaction

### **Link** (`catharsis:link`)

Open a link in the default browser.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget interaction</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:link`
- <TypeIcon type="string"/> **url**: A link to the Site.

</TreeView>

### **Slot Clicking** (`catharsis:slot`)

Click a slot on the screen.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget interaction</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:slot`
- <TypeIcon type="int"/> **slot**: The slot to click.

</TreeView>

### **Sending a command** (`catharsis:command`)

Send a command in the chat.

::: warning
This is currently disabled due to security concerns, please join the Discord to provide feedback.
:::

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget interaction</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:command`
- <TypeIcon type="string"/> **command**: The command to send.

</TreeView>

## GUI Widget Tooltip

### **Text** (`catharsis:text`)

Renders a custom defined text.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget tooltip</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:text`
- <TypeIcon type="object"/><TypeIcon type="array"/> **text**: A list of or a single [component in JSON format.](https://minecraft.wiki/w/Text_component_format)

</TreeView>

### **Slot Index** (`catharsis:slot`)

Steals the tooltip from an existing slot.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget tooltip</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:slot`
- <TypeIcon type="int"/> **slot**: The slot to steal the tooltip from.

</TreeView>

### **Widget Interaction** (`catharsis:interaction`)

Steals the tooltip from the defined widget interaction.
Only works if the interaction is [`catharsis:slot`](#slot-clicking-catharsis-slot).

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget tooltip</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:interaction`

</TreeView>
