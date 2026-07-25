---
title: Inter Mod Communication (IMC)
lang: en-US
---

# <Environment /> Inter Mod Communication (IMC)

Katharsis provides Fabric entrypoints that other mods can invoke during initialization.

## Entrypoint Registry

Katharsis exposes three entrypoints under its IMC setup:

| Entrypoint Path                     | Purpose                                                                        | Expected Value Type                                    |
|:------------------------------------|:-------------------------------------------------------------------------------|:-------------------------------------------------------|
| `katharsis:imc/item_id`             | Applies a specific Katharsis ID to an `ItemStack` directly.                    | `java.util.function.BiConsumer<ItemStack, Identifier>` |
| `katharsis:imc/disabled`            | Disables Katharsis retexturing and GUI behaviors for an item stack completely. | `java.util.function.BiConsumer<ItemStack, Boolean>`    |
| `katharsis:imc/hidden_mod_elements` | Checks if a named UI element is hidden by the currently active GUI modifier.   | `java.util.function.Predicate<String>`                 |

## Registering Entrypoints

To link your mods compatibility handlers, register static method handles or consumer implementations inside your `fabric.mod.json` entrypoints block:

```json
"entrypoints": {
  "katharsis:imc/item_id": [
    "your.package.KatharsisSupport::id"
  ],
  "katharsis:imc/disabled": [
    "your.package.KatharsisSupport::disabled"
  ],
  "katharsis:imc/hidden_mod_elements": [
    "your.package.KatharsisSupport::hiddenModElements"
  ]
}
```

## Templates

::: details Kotlin Implementation

```kotlin
package your.package

import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.function.BiConsumer
import java.util.function.Predicate

object KatharsisSupport {

    private var idConsumer: BiConsumer<ItemStack, Identifier> = BiConsumer { _, _ -> }
    private var disabledConsumer: BiConsumer<ItemStack, Boolean> = BiConsumer { _, _ -> }
    private var hiddenModElementsProvider: Predicate<String> = Predicate { false }

    @JvmStatic
    fun id(consumer: BiConsumer<ItemStack, Identifier>) {
        this.idConsumer = consumer
    }

    @JvmStatic
    fun disabled(consumer: BiConsumer<ItemStack, Boolean>) {
        this.disabledConsumer = consumer
    }
    
    @JvmStatic
    fun hiddenModElements(provider: Predicate<String>) {
        this.hiddenModElementsProvider = provider
    }

    fun isModElementHidden(element: String): Boolean {
        return hiddenModElementsProvider.test(element)
    }

    fun ItemStack.disableKatharsisModifications() = apply {
        disabledConsumer.accept(this, true)
    }

    fun ItemStack.withKatharsisId(identifier: Identifier): ItemStack = apply {
        idConsumer.accept(this, identifier)
    }

    fun Item.withKatharsisId(identifier: Identifier): ItemStack = defaultInstance.apply {
        idConsumer.accept(this, identifier)
    }
}
```

:::

::: details Java Implementation

```java
package your.package;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class KatharsisSupport {

    private static BiConsumer<ItemStack, Identifier> idConsumer = (stack, id) -> {};
    private static BiConsumer<ItemStack, Boolean> disabledConsumer = (stack, disabled) -> {};
    private static Predicate<String> hiddenModElementsProvider = (element) -> false;

    public static void id(BiConsumer<ItemStack, Identifier> consumer) {
        idConsumer = consumer;
    }

    public static void disabled(BiConsumer<ItemStack, Boolean> consumer) {
        disabledConsumer = consumer;
    }

    public static void hiddenGuiElements(Predicate<String> provider) {
        hiddenModElementsProvider = provider;
    }
    
    public static boolean isGuiElementHidden(String element) {
        return hiddenModElementsProvider.test(element);
    }

    public static ItemStack disableKatharsisModifications(ItemStack stack) {
        disabledConsumer.accept(stack, true);
        return stack;
    }

    public static ItemStack withKatharsisId(ItemStack stack, Identifier identifier) {
        idConsumer.accept(stack, identifier);
        return stack;
    }

    public static ItemStack withKatharsisId(Item item, Identifier identifier) {
        ItemStack stack = item.getDefaultInstance();
        idConsumer.accept(stack, identifier);
        return stack;
    }
}
```

:::
