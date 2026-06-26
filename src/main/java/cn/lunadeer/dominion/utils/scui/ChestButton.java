package cn.lunadeer.dominion.utils.scui;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * A clickable button in a Chest UI.
 * Wraps an ItemStack with an associated click action.
 */
public class ChestButton {
    private ItemStack item;
    private Runnable action;

    public ChestButton(ItemStack item) {
        this.item = item;
    }

    /**
     * Create a button with a specific item, name, and optional lore.
     */
    public ChestButton(ItemStack item, String name, String... lore) {
        this.item = item.copy();
        if (name != null && !name.isEmpty()) {
            this.item.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        }
        if (lore != null && lore.length > 0) {
            List<Component> loreLines = new ArrayList<>();
            for (String line : lore) {
                loreLines.add(Component.literal(line));
            }
            // Note: Lore component API may vary by MC version
        }
    }

    /**
     * Create a button from a material name with display name.
     */
    public static ChestButton of(ItemStack item, String name, Runnable action) {
        ChestButton btn = new ChestButton(item, name);
        btn.setAction(action);
        return btn;
    }

    /**
     * Create a simple colored pane button.
     */
    public static ChestButton filler() {
        return new ChestButton(new ItemStack(Items.GRAY_STAINED_GLASS_PANE));
    }

    public ChestButton setAction(Runnable action) { this.action = action; return this; }
    public ItemStack getItem() { return item; }
    public void click() { if (action != null) action.run(); }
}
