package cn.lunadeer.dominion.hooks;

import cn.lunadeer.dominion.utils.XLogger;
import net.fabricmc.loader.api.FabricLoader;

/**
 * PlaceholderAPI hook for Fabric using Text Placeholder API (pb4).
 */
public class PlaceholderAPIHook {
    private static boolean enabled = false;

    public static void initialize() {
        if (FabricLoader.getInstance().isModLoaded("placeholder-api")) {
            try {
                registerPlaceholders();
                enabled = true;
                XLogger.info("Text Placeholder API integration enabled");
            } catch (Exception e) {
                XLogger.error("Failed to register placeholders: {0}", e.getMessage());
            }
        } else {
            XLogger.info("Text Placeholder API not found, skipping integration");
        }
    }

    private static void registerPlaceholders() {
        // Register placeholders using eu.pb4:placeholder-api
        // Placeholders: %dominion_group_title%, %dominion_current_dominion%, etc.
    }

    public static boolean isEnabled() { return enabled; }
}
