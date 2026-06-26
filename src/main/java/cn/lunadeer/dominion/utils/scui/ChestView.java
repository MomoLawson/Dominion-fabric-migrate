package cn.lunadeer.dominion.utils.scui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

/**
 * Fabric Chest UI - opens a chest inventory GUI for the player.
 * Uses SimpleContainer + ChestMenu to create interactive chest interfaces.
 */
public class ChestView {
    private final String title;
    private final int rows;
    private final Map<Character, ChestButton> buttonMap = new HashMap<>();
    private String layout = "";
    private SimpleContainer container;

    public ChestView(String title, int rows) {
        this.title = title;
        this.rows = Math.min(Math.max(rows, 1), 6);
    }

    public ChestView setLayout(String layout) { this.layout = layout; return this; }
    public ChestView setButton(char key, ChestButton button) { buttonMap.put(key, button); return this; }

    /**
     * Open the chest GUI for a player.
     */
    public void open(ServerPlayer player) {
        int size = rows * 9;
        container = new SimpleContainer(size);

        // Place buttons according to layout
        if (!layout.isEmpty()) {
            for (int i = 0; i < layout.length() && i < size; i++) {
                char key = layout.charAt(i);
                if (key != ' ' && key != '#' && buttonMap.containsKey(key)) {
                    ChestButton btn = buttonMap.get(key);
                    container.setItem(i, btn.getItem());
                } else if (key == '#') {
                    // Filler item
                    container.setItem(i, createFiller());
                }
            }
        } else {
            // Auto-layout: place buttons sequentially
            int slot = 0;
            for (ChestButton btn : buttonMap.values()) {
                if (slot < size) {
                    container.setItem(slot++, btn.getItem());
                }
            }
        }

        // Open the menu for the player
        Component menuTitle = Component.literal(title);
        player.openMenu(new SimpleMenuProvider(
            (syncId, playerInventory, p) -> {
                if (rows <= 3) {
                    return new ChestMenu(MenuType.GENERIC_9x3, syncId, playerInventory, container, rows);
                } else if (rows == 4) {
                    return new ChestMenu(MenuType.GENERIC_9x4, syncId, playerInventory, container, rows);
                } else if (rows == 5) {
                    return new ChestMenu(MenuType.GENERIC_9x5, syncId, playerInventory, container, rows);
                } else {
                    return new ChestMenu(MenuType.GENERIC_9x6, syncId, playerInventory, container, rows);
                }
            },
            menuTitle
        ));

        // Register this view for click handling
        ChestUserInterfaceManager.getInstance().registerView(player.getUUID(), this);
    }

    /**
     * Handle a click on a slot.
     */
    public void onClick(ServerPlayer player, int slot) {
        if (container == null) return;
        ItemStack clicked = container.getItem(slot);
        if (clicked.isEmpty()) return;

        // Find the button that matches this slot
        for (ChestButton btn : buttonMap.values()) {
            if (btn.getItem() != null && ItemStack.matches(btn.getItem(), clicked)) {
                btn.click();
                break;
            }
        }
    }

    public void refresh() {
        if (container == null) return;
        if (!layout.isEmpty()) {
            for (int i = 0; i < layout.length() && i < container.getContainerSize(); i++) {
                char key = layout.charAt(i);
                if (key != ' ' && key != '#' && buttonMap.containsKey(key)) {
                    container.setItem(i, buttonMap.get(key).getItem());
                }
            }
        }
    }

    private static ItemStack createFiller() {
        ItemStack filler = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
        filler.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal(" "));
        return filler;
    }

    public int getRows() { return rows; }
    public String getTitle() { return title; }
    public Map<Character, ChestButton> getButtonMap() { return buttonMap; }
}
