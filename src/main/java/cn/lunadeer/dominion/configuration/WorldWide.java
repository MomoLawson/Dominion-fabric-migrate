package cn.lunadeer.dominion.configuration;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * World-wide dominion settings.
 * Ported from Bukkit to Fabric/SnakeYAML.
 *
 * Manages per-world settings for environment and guest privilege flags.
 */
public class WorldWide {

    private static class WorldConfig {
        private boolean enabled = false;
        // In Fabric, we store flag names -> boolean values since the Flag API is not yet ported.
        private final Map<String, Boolean> guestPrivilegeFlags = new HashMap<>();
        private final Map<String, Boolean> environmentFlags = new HashMap<>();
    }

    private static final Map<String, WorldConfig> worlds = new HashMap<>();

    public static boolean isWorldWideEnabled(String worldName) {
        return worlds.containsKey(worldName) && worlds.get(worldName).enabled;
    }

    public static boolean isWorldWideEnabled(ServerLevel world) {
        return isWorldWideEnabled(world.dimension().location().toString());
    }

    public static @Nullable Map<String, Boolean> getEnvironmentFlagValues(String worldName) {
        if (!worlds.containsKey(worldName)) {
            return null;
        }
        return worlds.get(worldName).environmentFlags;
    }

    public static boolean getEnvFlagValue(String worldName, @NotNull String flagName, boolean defaultValue) {
        if (!worlds.containsKey(worldName)) {
            return defaultValue;
        }
        return worlds.get(worldName).environmentFlags.getOrDefault(flagName, defaultValue);
    }

    public static @Nullable Map<String, Boolean> getGuestPrivilegeFlagValues(String worldName) {
        if (!worlds.containsKey(worldName)) {
            return null;
        }
        return worlds.get(worldName).guestPrivilegeFlags;
    }

    public static boolean getGuestFlagValue(String worldName, @NotNull String flagName, boolean defaultValue) {
        if (!worlds.containsKey(worldName)) {
            return defaultValue;
        }
        return worlds.get(worldName).guestPrivilegeFlags.getOrDefault(flagName, defaultValue);
    }

    /**
     * Load a single world's world-wide configuration from a YAML file.
     */
    @SuppressWarnings("unchecked")
    protected static void loadWorld(File worldWideRootPath, String worldName) throws IOException {
        if (!worlds.containsKey(worldName)) {
            worlds.put(worldName, new WorldConfig());
        }

        File worldWideFile = new File(worldWideRootPath, worldName + ".yml");
        Map<String, Object> yamlData = new java.util.LinkedHashMap<>();
        if (worldWideFile.exists()) {
            try {
                yamlData = ConfigurationManager.loadYamlFile(worldWideFile);
            } catch (Exception e) {
                XLogger.error("Failed to load world-wide config for {0}: {1}", worldName, e.getMessage());
            }
        }

        // enabled configuration
        if (!yamlData.containsKey("enabled")) {
            yamlData.put("enabled", false);
        }
        Object enabledVal = yamlData.get("enabled");
        worlds.get(worldName).enabled = (enabledVal instanceof Boolean) ? (Boolean) enabledVal : false;

        // flags configuration - deferred until Flag API is ported
        // When Flag/Flags API is ported, iterate over Flags.getAllFlags() here
        // and read/write flag values from/to yamlData.

        // Save back
        ConfigurationManager.saveYamlFile(worldWideFile, yamlData);
    }

    /**
     * Initialize the world-wide system.
     * Registers a world load handler that loads world-wide settings when a world is loaded.
     *
     * @param rootPath The config directory
     */
    public static void load(File rootPath) throws IOException {
        File worldWideDir = new File(rootPath, "world-wide");
        if (!worldWideDir.exists()) {
            if (!worldWideDir.mkdirs()) {
                throw new RuntimeException("Failed to create world-wide dominion directory: " + worldWideDir.getAbsolutePath());
            }
        }

        // In Fabric, world loading happens via ServerLifecycleEvents.WORLD_LOAD.
        // We'll register a callback to load world-wide settings when a world loads.
        // For now, load all existing world files from the directory.
        File[] files = worldWideDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String worldName = file.getName().replace(".yml", "");
                try {
                    loadWorld(worldWideDir, worldName);
                } catch (IOException e) {
                    XLogger.error("Failed to load world-wide settings for {0}: {1}", worldName, e.getMessage());
                }
            }
        }

        // TODO: Register net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents.LOAD
        // to call loadWorld() when new worlds are loaded at runtime.
    }
}
