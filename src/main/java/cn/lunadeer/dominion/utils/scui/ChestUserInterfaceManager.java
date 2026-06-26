package cn.lunadeer.dominion.utils.scui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages open Chest UI views per player.
 * Tracks which players have CUI open and their active views for click handling.
 */
public class ChestUserInterfaceManager {
    private static ChestUserInterfaceManager instance;
    private final Map<UUID, ChestView> activeViews = new ConcurrentHashMap<>();

    public ChestUserInterfaceManager() { instance = this; }
    public static ChestUserInterfaceManager getInstance() { return instance; }

    public boolean isCuiScreen(UUID uuid) { return activeViews.containsKey(uuid); }

    public void registerView(UUID uuid, ChestView view) { activeViews.put(uuid, view); }

    public ChestView getView(UUID uuid) { return activeViews.get(uuid); }

    public void closeScreen(UUID uuid) { activeViews.remove(uuid); }

    /**
     * Handle a slot click for a player's active CUI view.
     */
    public void handleClick(UUID playerUuid, int slot) {
        ChestView view = activeViews.get(playerUuid);
        if (view != null) {
            // Find the player and delegate to the view
            // This will be called from a Mixin or event handler
        }
    }
}
