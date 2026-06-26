package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.utils.XLogger;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Hooks manager for external plugin/mod integrations.
 * Ported from Bukkit to Fabric.
 *
 * In Fabric, we check for mod presence instead of Bukkit plugins.
 * Integrations include:
 * - PlaceholderAPI -> eu.pb4:placeholder-api (Fabric equivalent)
 * - WorldGuard -> No direct Fabric equivalent, may use custom region protection
 */
public class HooksManager {

    private static boolean placeholderApiEnabled = false;

    public HooksManager() {
        // Check if placeholder-api mod is present
        placeholderApiEnabled = FabricLoader.getInstance().isModLoaded("placeholder-api");
        if (placeholderApiEnabled) {
            XLogger.info("Placeholder API detected, enabling placeholder support.");
        }
    }

    /**
     * Set placeholder for the message.
     *
     * @param playerUUID The player UUID for placeholder context
     * @param message    The message with placeholders
     * @return The message with placeholders replaced
     */
    public static String setPlaceholder(java.util.UUID playerUUID, String message) {
        if (placeholderApiEnabled) {
            // TODO: Implement placeholder resolution using eu.pb4:placeholder-api
            // When the placeholder system is ported, resolve placeholders here.
            return message;
        }
        return message;
    }

    /**
     * Check if PlaceholderAPI is available.
     */
    public static boolean isPlaceholderApiEnabled() {
        return placeholderApiEnabled;
    }

    /**
     * Check if there's a region conflict (WorldGuard equivalent).
     * In Fabric, this may be handled by custom region protection mods.
     *
     * @param worldName The world name
     * @param x1        Min X coordinate
     * @param y1        Min Y coordinate
     * @param z1        Min Z coordinate
     * @param x2        Max X coordinate
     * @param y2        Max Y coordinate
     * @param z2        Max Z coordinate
     * @return true if there's a conflict
     */
    public static boolean isConflictWithWorldGuard(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        // WorldGuard is Bukkit-only. In Fabric, equivalent protection
        // would be provided by mods like "Regions" or similar.
        // This is a no-op for now.
        return false;
    }
}
