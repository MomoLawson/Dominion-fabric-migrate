package cn.lunadeer.dominion.utils.scui;

import cn.lunadeer.dominion.utils.ColorParser;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.XLogger;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.lunadeer.dominion.managers.HooksManager.setPlaceholder;
import static cn.lunadeer.dominion.utils.scui.ChestUserInterfaceManager.attachTag;
import static cn.lunadeer.dominion.utils.scui.ChestUserInterfaceManager.hasTag;

/**
 * Main chest view container for the CUI (Chest User Interface) system.
 * <p>
 * Ported from Bukkit to Fabric. Uses {@link ChestMenu}
 * and {@link SimpleContainer} for the 9xN chest grid. The player opens the
 * screen via {@link ServerPlayer#openAbstractContainerScreen(MenuProvider)}.
 * <p>
 * Layout strings map characters to inventory slots. Buttons are placed at
 * positions where the layout contains their symbol character.
 */
public class ChestView {

    private final ServerPlayer viewOwner;
    private String title;
    protected final Map<Integer, ChestButton> buttons = new HashMap<>();
    private String layout = "";

    /**
     * Creates a new ChestView for the given player.
     *
     * @param viewOwner the player who will view this chest
     */
    public ChestView(ServerPlayer viewOwner) {
        this.viewOwner = viewOwner;
        this.title = "Default Title";
        ChestUserInterfaceManager.getInstance().registerView(this);
    }

    /**
     * Sets the layout of the chest view.
     * <p>
     * The layout is a list of strings, each representing a row in the chest GUI.
     * Each string must have exactly 9 characters, and the list can have up to 6 rows.
     *
     * @param layout a list of strings representing the layout rows
     * @return the current ChestView instance for method chaining
     * @throws IllegalArgumentException if the layout is empty
     */
    public ChestView setLayout(List<String> layout) {
        StringBuilder builder = new StringBuilder();
        if (layout.isEmpty()) {
            throw new IllegalArgumentException("Layout cannot be empty.");
        }
        for (String row : layout) {
            builder.append(row);
        }
        return setLayout(builder.toString());
    }

    /**
     * Sets the layout of the chest view using an array of strings.
     *
     * @param layout an array of strings representing the layout rows
     * @return the current ChestView instance for method chaining
     */
    public ChestView setLayout(String... layout) {
        return setLayout(String.join("", layout));
    }

    /**
     * Sets the layout of the chest view using a single string.
     * <p>
     * The layout string must have a length that is a multiple of 9 (each 9 characters represent a row),
     * and can have up to 6 rows (maximum 54 characters).
     *
     * @param layout a string representing the layout rows
     * @return the current ChestView instance for method chaining
     * @throws IllegalArgumentException if the layout is empty, not a multiple of 9, or has more than 6 rows
     */
    public ChestView setLayout(String layout) {
        if (layout.isEmpty()) {
            throw new IllegalArgumentException("Layout cannot be empty.");
        }
        if (layout.length() % 9 != 0) {
            throw new IllegalArgumentException("Layout must be a multiple of 9 characters.");
        }
        if (layout.length() / 9 > 6) {
            throw new IllegalArgumentException("Layout cannot have more than 6 rows.");
        }
        this.layout = layout;
        return this;
    }

    /**
     * Sets the title of the chest view.
     *
     * @param title the new title for the chest view
     * @return the current ChestView instance for method chaining
     * @throws IllegalArgumentException if the title is null or empty
     */
    public ChestView setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        this.title = title;
        return this;
    }

    /**
     * Clears all buttons from the chest view.
     *
     * @return the current ChestView instance for method chaining
     */
    public ChestView clearButtons() {
        buttons.clear();
        return this;
    }

    /**
     * Clears the layout of the chest view.
     *
     * @return the current ChestView instance for method chaining
     */
    public ChestView clearLayout() {
        layout = "";
        return this;
    }

    /**
     * Sets a button in the chest view by searching for all occurrences of the given
     * symbol in the layout.
     *
     * @param symbol the character symbol to search for in the layout
     * @param button the ChestButton to set at each found slot
     * @return the current ChestView instance for method chaining
     * @throws IllegalStateException if the layout has not been set
     */
    public ChestView setButton(char symbol, ChestButton button) {
        if (layout.isEmpty()) {
            throw new IllegalStateException("Layout must be set before adding buttons with symbols.");
        }
        for (int i = 0; i < layout.length(); i++) {
            if (layout.charAt(i) == symbol) {
                setButton(i, button);
            }
        }
        return this;
    }

    /**
     * Sets a button at the specified slot index in the chest view.
     *
     * @param slot   the slot index (0-53)
     * @param button the ChestButton to set
     * @return the current ChestView instance for method chaining
     * @throws IllegalArgumentException if the slot is out of bounds
     */
    public ChestView setButton(int slot, ChestButton button) {
        if (slot < 0 || slot >= 54) {
            throw new IllegalArgumentException("Slot must be between 0 and 53.");
        }
        buttons.put(slot, button);
        return this;
    }

    /**
     * Sets a button at the specified row and column in the chest view.
     *
     * @param row    row index (0-5)
     * @param column column index (0-8)
     * @param button the ChestButton to set
     * @return the current ChestView instance for method chaining
     */
    public ChestView setButton(int row, int column, ChestButton button) {
        if (row < 0 || row >= 6 || column < 0 || column >= 9) {
            throw new IllegalArgumentException("Row must be between 0 and 5 and column must be between 0 and 8.");
        }
        return setButton(row * 9 + column, button);
    }

    /**
     * Sets multiple buttons in the chest view.
     *
     * @param buttons a map of slot indices to ChestButton instances
     * @return the current ChestView instance for method chaining
     */
    public ChestView setButtons(Map<Integer, ChestButton> buttons) {
        for (Map.Entry<Integer, ChestButton> entry : buttons.entrySet()) {
            setButton(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Removes the button at the specified slot.
     *
     * @param slot the slot index to clear
     * @return the current ChestView instance for method chaining
     */
    public ChestView removeButton(int slot) {
        buttons.remove(slot);
        return this;
    }

    /**
     * Opens the chest view for the player. If the current inventory already has the
     * custom CUI tag and the same size, it refreshes the view; otherwise, creates
     * a new screen handler.
     */
    public void open() {
        AbstractContainerMenu currentHandler = viewOwner.currentAbstractContainerMenu;
        if (currentHandler != null
                && currentHandler instanceof ChestMenu containerHandler
                && containerHandler.getInventory() instanceof SimpleContainer
                && ChestUserInterfaceManager.getInstance().isCuiScreen(viewOwner.getUUID())
                && containerHandler.getRows() * 9 == (layout.isEmpty() ? 54 : layout.length())) {
            refresh(currentHandler);
        } else {
            create();
        }
    }

    /**
     * Closes the chest view for the player if it is currently a CUI screen.
     */
    public void close() {
        if (ChestUserInterfaceManager.getInstance().isCuiScreen(viewOwner.getUUID())) {
            viewOwner.closeAbstractContainerScreen();
        }
    }

    /**
     * Creates a new screen handler and opens it for the player.
     */
    protected void create() {
        int rows = layout.isEmpty() ? 6 : layout.length() / 9;
        SimpleContainer inventory = new SimpleContainer(rows * 9);

        // Create the screen handler factory
        MenuProvider factory = new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("");
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                return new CuiAbstractContainerMenu(syncId, playerInventory, inventory, rows);
            }
        };

        viewOwner.openAbstractContainerScreen(factory);

        // Refresh the newly opened screen
        AbstractContainerMenu handler = viewOwner.currentAbstractContainerMenu;
        if (handler instanceof ChestMenu) {
            ChestUserInterfaceManager.getInstance().markCuiScreen(viewOwner.getUUID());
            refresh(handler);
        } else {
            throw new IllegalStateException("Failed to open inventory for player: " + viewOwner.getName().getString());
        }
    }

    /**
     * Refreshes the contents of the chest view.
     *
     * @param handler the screen handler to refresh
     */
    protected void refresh(AbstractContainerMenu handler) {
        try {
            // Resolve the title with placeholders and colors
            String resolvedTitle = setPlaceholder(viewOwner.getUUID(), title);
            resolvedTitle = ColorParser.getFormattedType(resolvedTitle);
            // Note: Changing title after opening requires sending a new inventory packet,
            // which is complex in Fabric. Title is set at creation time via factory.
            // For title updates on refresh, we would need to close and reopen or use packets.

            if (handler instanceof ChestMenu containerHandler) {
                Inventory inventory = containerHandler.getInventory();
                int totalSlots = layout.isEmpty() ? 54 : layout.length();

                // Place buttons
                for (Map.Entry<Integer, ChestButton> entry : buttons.entrySet()) {
                    int slot = entry.getKey();
                    ChestButton button = entry.getValue();
                    if (button != null) {
                        ItemStack item = attachTag(viewOwner.getUUID(), button.build(viewOwner.getUUID()));
                        inventory.setStack(slot, item);
                    } else {
                        inventory.setStack(slot, ItemStack.EMPTY);
                    }
                }

                // Fill empty slots with placeholder items
                for (int i = 0; i < totalSlots; i++) {
                    if (inventory.getStack(i).isEmpty()) {
                        inventory.setStack(i, ChestUserInterfaceManager.PLACE_HOLDER_ITEM.copy());
                    }
                }
            }
        } catch (Exception e) {
            XLogger.error(e);
            viewOwner.closeAbstractContainerScreen();
            Notification.error(viewOwner, e);
        }
    }

    /**
     * Returns the UUID of the view owner (used as the view identifier).
     *
     * @return the player's UUID
     */
    public UUID getViewId() {
        return viewOwner.getUUID();
    }

    /**
     * Returns the player who owns this view.
     *
     * @return the ServerPlayer
     */
    public ServerPlayer getViewOwner() {
        return viewOwner;
    }

    /**
     * Returns the title of this view.
     *
     * @return the title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the layout string of this view.
     *
     * @return the layout string
     */
    public String getLayout() {
        return layout;
    }

    /**
     * Handles a click event on this view. Delegates to the button at the given slot.
     *
     * @param slot the slot that was clicked
     * @param type the click type (0 = left click, 1 = right click, etc.)
     */
    public void handleClick(int slot, int type) {
        ChestButton button = buttons.get(slot);
        if (button != null) {
            button.onClick(slot);
        } else {
            viewOwner.sendMessage(Component.literal("§cNo action assigned to this slot."));
        }
    }

    /**
     * Custom AbstractContainerMenu subclass for CUI screens. Extends
     * {@link ChestMenu} to handle click events
     * and prevent players from taking items out of the chest.
     */
    public static class CuiAbstractContainerMenu extends ChestMenu {

        private final int rows;

        public CuiAbstractContainerMenu(int syncId, Inventory playerInventory, Inventory inventory, int rows) {
            super(rows <= 3 ? MenuType.GENERIC_9X3 : rows <= 4 ? MenuType.GENERIC_9X4
                    : rows <= 5 ? MenuType.GENERIC_9X5 : MenuType.GENERIC_9X6,
                    syncId, playerInventory, inventory, rows);
            this.rows = rows;
        }

        @Override
        public ItemStack quickMove(Player player, int slot) {
            // Prevent shift-clicking items out of the CUI
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canUse(Player player) {
            return true;
        }
    }
}
