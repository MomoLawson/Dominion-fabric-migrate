package cn.lunadeer.dominion.utils.scui;

import cn.lunadeer.dominion.utils.XLogger;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.NbtComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages CUI (Chest User Interface) views for Fabric.
 * <p>
 * Ported from Bukkit to Fabric. Handles registration of chest views, click event
 * routing via Fabric networking events, and item tagging with custom NBT for
 * identifying CUI-managed items.
 * <p>
 * In Fabric, inventory click interception is done by overriding the
 * {@link ChestView.CuiAbstractContainerMenu} to handle slot clicks and cancel unwanted actions.
 * This manager tracks active CUI screens and routes click events to the appropriate view.
 */
public class ChestUserInterfaceManager {

    private static ChestUserInterfaceManager instance;

    private final Map<UUID, ChestView> views = new HashMap<>();
    private final Map<UUID, Boolean> cuiScreens = new HashMap<>();

    // NBT tag keys for identifying CUI items
    private static final String CUI_TAG_KEY = "dominion:chest_view";
    private static final String CUI_ID_KEY = "dominion:view_id";

    public static ItemStack PLACE_HOLDER_ITEM;

    /**
     * Returns the singleton instance of the manager.
     *
     * @return the ChestUserInterfaceManager instance
     * @throws IllegalStateException if the manager has not been initialized
     */
    public static ChestUserInterfaceManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ChestUserInterface has not been initialized. " +
                    "Please call ChestUserInterface.init() first.");
        }
        return instance;
    }

    /**
     * Creates and initializes the ChestUserInterfaceManager.
     * Registers server lifecycle events for cleanup and creates the placeholder item.
     */
    public ChestUserInterfaceManager() {
        instance = this;

        // Create placeholder item (gray stained glass pane with empty name)
        PLACE_HOLDER_ITEM = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
        PLACE_HOLDER_ITEM.set(DataComponents.CUSTOM_NAME, Component.literal(" "));
        attachTag(null, PLACE_HOLDER_ITEM);

        // Register player disconnect event to clean up views
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID playerId = handler.player.getUUID();
            if (views.containsKey(playerId)) {
                XLogger.debug("Unregistering ChestView for player: " + handler.player.getName().getString());
                views.remove(playerId);
                cuiScreens.remove(playerId);
            }
        });
    }

    /**
     * Registers a chest view for a player. Only one view per player is tracked.
     *
     * @param view the ChestView to register
     */
    public void registerView(ChestView view) {
        if (view == null || view.getViewId() == null) {
            XLogger.error("Cannot register ChestView with null ID");
            return;
        }
        views.put(view.getViewId(), view);
    }

    /**
     * Gets or creates a basic ChestView for the given player.
     * If the current view is a ChestListView, a new empty ChestView is returned.
     * Otherwise, the existing view is cleared and returned.
     *
     * @param viewOwner the player to get a view for
     * @return a ChestView for the player
     */
    public ChestView getViewOf(ServerPlayer viewOwner) {
        UUID viewId = viewOwner.getUUID();
        if (views.containsKey(viewId)) {
            if (views.get(viewId) instanceof ChestListView) {
                return new ChestView(viewOwner);
            } else {
                return views.get(viewId).clearButtons().clearLayout();
            }
        } else {
            return new ChestView(viewOwner);
        }
    }

    /**
     * Creates a new ChestListView for the given player.
     *
     * @param viewOwner the player to create a list view for
     * @return a new ChestListView
     */
    public ChestListView getListViewOf(ServerPlayer viewOwner) {
        return new ChestListView(viewOwner);
    }

    /**
     * Returns the active ChestView for a player, or null if none is registered.
     *
     * @param playerUUID the player's UUID
     * @return the registered ChestView, or null
     */
    public ChestView getActiveView(UUID playerUUID) {
        return views.get(playerUUID);
    }

    /**
     * Marks a player's screen as a CUI screen, so click events are intercepted.
     *
     * @param playerUUID the player's UUID
     */
    public void markCuiScreen(UUID playerUUID) {
        cuiScreens.put(playerUUID, true);
    }

    /**
     * Unmarks a player's screen as a CUI screen.
     *
     * @param playerUUID the player's UUID
     */
    public void unmarkCuiScreen(UUID playerUUID) {
        cuiScreens.remove(playerUUID);
    }

    /**
     * Checks if a player currently has a CUI screen open.
     *
     * @param playerUUID the player's UUID
     * @return true if the player's current screen is a CUI screen
     */
    public boolean isCuiScreen(UUID playerUUID) {
        return cuiScreens.containsKey(playerUUID) && cuiScreens.get(playerUUID);
    }

    /**
     * Handles a slot click in a CUI screen. Called from the CuiAbstractContainerMenu.
     *
     * @param playerUUID the UUID of the player who clicked
     * @param slot       the slot index that was clicked
     * @param type       the click type (0 = left, 1 = right)
     */
    public void handleSlotClick(UUID playerUUID, int slot, int type) {
        try {
            ChestView view = views.get(playerUUID);
            if (view == null) {
                XLogger.error("ChestView not found for player: " + playerUUID);
                return;
            }
            view.handleClick(slot, type);
        } catch (Exception e) {
            XLogger.error(e);
        }
    }

    /**
     * Tags an ItemStack with custom NBT data to identify it as a CUI item.
     *
     * @param viewId the UUID of the owning view (may be null for placeholder items)
     * @param item   the ItemStack to tag
     * @return the tagged ItemStack
     */
    public static ItemStack attachTag(UUID viewId, ItemStack item) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString(CUI_TAG_KEY, "chest_view");
        if (viewId != null) {
            nbt.putString(CUI_ID_KEY, viewId.toString());
        }
        item.set(DataComponents.CUSTOM_DATA, NbtComponent.of(nbt));
        return item;
    }

    /**
     * Checks if an ItemStack has been tagged as a CUI item.
     *
     * @param item the ItemStack to check
     * @return true if the item has the CUI tag
     */
    public static boolean hasTag(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        NbtComponent customData = item.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return false;
        }
        NbtCompound nbt = customData.copyNbt();
        return nbt.contains(CUI_TAG_KEY);
    }

    /**
     * Extracts the view UUID from a tagged CUI item.
     *
     * @param item the ItemStack to extract the view ID from
     * @return the view UUID, or null if not present
     */
    public static UUID getViewId(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return null;
        }
        NbtComponent customData = item.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }
        NbtCompound nbt = customData.copyNbt();
        if (nbt.contains(CUI_ID_KEY)) {
            String idString = nbt.getString(CUI_ID_KEY);
            if (idString == null || idString.isEmpty()) {
                return null;
            }
            try {
                return UUID.fromString(idString);
            } catch (IllegalArgumentException e) {
                XLogger.debug("Invalid view ID in CUI item: " + idString);
                return null;
            }
        }
        return null;
    }
}
