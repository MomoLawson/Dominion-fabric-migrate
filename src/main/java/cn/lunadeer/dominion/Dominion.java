package cn.lunadeer.dominion;

import cn.lunadeer.dominion.commands.InitCommands;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.managers.MultiServerManager;
import cn.lunadeer.dominion.managers.TeleportManager;
import cn.lunadeer.dominion.managers.DatabaseBackupManager;
import cn.lunadeer.dominion.managers.HooksManager;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Dominion implements ModInitializer {

    public static class DominionText extends ConfigurationPart {
        public String loadingConfig = "Loading Configurations...";
        public String pluginEnabled = "Plugin Enabled!";
        public String pluginVersion = "Plugin Version: {0}";
        public String notificationPrefix = "&6[&eDominion&6]&f";
    }

    public static final String MOD_ID = "dominion";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Dominion instance;
    public static MinecraftServer server;

    // Permission node for dominion default use
    public static String defaultPermission = "dominion.default";

    // Permission node for dominion admin (set during config load)
    public static String adminPermission = "dominion.admin";

    // Player point selection map: player UUID -> (point index 0/1 -> [x, y, z, worldUid_hash])
    public static final Map<UUID, Map<Integer, int[]>> pointsSelect = new ConcurrentHashMap<>();

    /**
     * Returns the config directory for Dominion.
     * Uses FabricLoader.getInstance().getConfigDir() / "dominion"
     */
    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    }

    @Override
    public void onInitialize() {
        instance = this;
        new XLogger(LOGGER);

        // ASCII art banner
        XLogger.info("  _____                  _       _");
        XLogger.info(" |  __ \\                (_)     (_)");
        XLogger.info(" | |  | | ___  _ __ ___  _ _ __  _  ___  _ __");
        XLogger.info(" | |  | |/ _ \\| '_ ` _ \\| | '_ \\| |/ _ \\| '_ \\");
        XLogger.info(" | |__| | (_) | | | | | | | | | | | (_) | | | |");
        XLogger.info(" |_____/ \\___/|_| |_| |_|_|_| |_|_|\\___/|_| |_|");

        // Register server lifecycle events
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);

        // Register all Dominion commands with Brigadier
        InitCommands.register();
    }

    private void onServerStarted(MinecraftServer minecraftServer) {
        server = minecraftServer;
        XLogger.info(Language.dominionText.loadingConfig);

        try {
            Configuration.loadConfigurationAndDatabase(null);
        } catch (Exception e) {
            XLogger.error("Failed to load configuration: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Initialize cache
        new cn.lunadeer.dominion.cache.CacheManager();
        cn.lunadeer.dominion.cache.CacheManager.instance.load();

        // Initialize event handlers
        new cn.lunadeer.dominion.events.EventsRegister();

        // Initialize provider
        new cn.lunadeer.dominion.handler.DominionProviderHandler();

        // Initialize PlaceholderAPI integration
        cn.lunadeer.dominion.hooks.PlaceholderAPIHook.initialize();

        // Initialize economy system
        cn.lunadeer.dominion.utils.VaultConnect.VaultConnect.initialize();

        XLogger.info(Language.dominionText.pluginVersion, getModVersion());
        XLogger.info(Language.dominionText.pluginEnabled);
    }

    private void onServerStopping(MinecraftServer minecraftServer) {
        XLogger.info("Dominion shutting down...");
    }

    public static String getModVersion() {
        return FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
    }
}
