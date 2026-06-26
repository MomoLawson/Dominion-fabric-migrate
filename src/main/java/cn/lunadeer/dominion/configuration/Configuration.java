package cn.lunadeer.dominion.configuration;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.storage.DatabaseManager;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

import static cn.lunadeer.dominion.configuration.Language.loadLanguageFiles;

/**
 * Main configuration file for Dominion.
 * Ported from Bukkit to Fabric/SnakeYAML.
 */
@Headers({
        "This is the configuration file of Dominion.",
        "For more information, please refer to the documentation.",
        "https://dominion.lunadeer.cn/en/notes/doc/owner/config-ref/overview/",
        "",
        "If you want to control player's privilege, please refer to the limitations configuration.",
})
public class Configuration extends ConfigurationFile {

    public static class ConfigurationText extends ConfigurationPart {
        public String loadingLanguage = "Loading language {0}...";
        public String loadLanguageFail = "Failed to load language {0} reason: {1}, using default en_us.";
        public String loadLanguageSuccess = "Successfully loaded language {0}.";

        public String loadingFlag = "Loading flag configuration...";
        public String loadFlagSuccess = "Successfully loaded flag configuration.";

        public String loadingLimitations = "Loading limitation configuration...";
        public String savingDefaultLimitation = "Because no limitation file found, saving default limitation file.";
        public String saveLimitationFail = "Failed to save limitation file: {0}";
        public String loadingLimitation = "Loading limitation file: {0}...";
        public String loadLimitationFail = "Failed to load limitation file: {0} reason: {1}";
        public String loadLimitations = "Successfully loaded {0} limitations: {1}.";

        public String loadConfiguration = "Successfully loaded configuration.";
        public String debugEnabled = "Debug mode enabled.";
        public String prepareDatabase = "Preparing database...";
        public String databaseConnected = "Database connected successfully.";

        public String multiServerSqlite = "Database with type sqlite is not supported in multi-server mode, disabled multi-server mode.";
        public String serverIdInvalid = "Server id must be positive integer (> 0), disabled multi-server mode.";
    }

    @HandleManually
    public static void loadFlagConfiguration() throws IOException {
        XLogger.info(Language.configurationText.loadingFlag);
        File yamlFile = new File(Dominion.getConfigDir().toFile(), "flags.yml");

        Map<String, Object> yamlData = new LinkedHashMap<>();
        if (yamlFile.exists()) {
            Yaml yaml = new Yaml();
            try (InputStream is = new FileInputStream(yamlFile)) {
                Object data = yaml.load(is);
                if (data instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> loaded = (Map<String, Object>) data;
                    yamlData = loaded;
                }
            }
        }

        // Note: Flag loading is deferred until the flag system is ported.
        // When the Flag/Flags API classes are ported, this method will iterate
        // over Flags.getAllFlags() and read/write flag configuration from yamlData.

        // Save the yaml data back
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setWidth(250);
        Yaml yamlOut = new Yaml(options);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(yamlFile), StandardCharsets.UTF_8)) {
            yamlOut.dump(yamlData, writer);
        }
        XLogger.info(Language.configurationText.loadFlagSuccess);
    }

    @Comments("Do not modify this value.")
    public static int version = 3;  // When you change the configuration, increment this value.

    @Comments("The settings of the database.")
    public static Database database = new Database();

    public static class Database extends ConfigurationPart {
        @Comments("Supported types: sqlite, mysql, mariadb, pgsql")
        public String type = "sqlite";

        @Comments("The host of the database.")
        public String host = "localhost";

        @Comments("The port of the database.")
        public String port = "3306";

        @Comments("The database name.")
        public String database = "dominion";

        @Comments("The username of the database.")
        public String username = "dominion";

        @Comments("The password of the database.")
        public String password = "dominion";

        @Comments({"The connection pool size of the database.",})
        public int connectionPoolSize = 10;
    }

    @Comments({
            "The settings of the multi server.",
            "If you have multiple servers proxied by BungeeCord, you can configure and enable this.",
            "Player can manage/teleport across multi-servers.",
            "Database with type sqlite is not supported in multi-server mode.",
            "For migration of existing data, please refer to the documentation.",
            "https://dominion.lunadeer.cn/notes/doc/owner/other/multi-server/"
    })
    public static MultiServer multiServer = new MultiServer();

    public static class MultiServer extends ConfigurationPart {
        @Comments("Enable multi server mode.")
        public boolean enable = false;
        @Comments({
                "The name of this server.",
                "This should be the same as configured in BC (Velocity)."
        })
        public String serverName = "server";
        @Comments({
                "The id of this server, must be unique among all servers.",
                "Must be positive integer. > 0",
                "DO NOT CHANGE THIS AFTER THERE ARE DATA IN THE DATABASE."
        })
        public int serverId = 1;
    }

    @Comments("Language of the plugin, see others in the languages folder.")
    public static String language = "en_us";

    @Comments("Radius of the auto create dominion. -1 to disable.")
    public static int autoCreateRadius = 10;

    @Comments({
            "If player don't login for this days, his dominion will be auto cleaned.",
            "Set to -1 to disable."
    })
    public static int autoCleanAfterDays = 180;

    @Comments("Prevent player from creating dominion around the spawn point.")
    public static int serverSpawnProtectionRadius = 10;

    @Comments("Minimum distance between two dominions.")
    public static int minimumDominionDistance = 0;

    @Comments("Tool used to select position for creating dominion.")
    public static String selectTool = "ARROW";

    @Comments("Tool used to show information of clicked dominion.")
    public static String infoTool = "STRING";

    @Comments({
            "The settings of the player default using UI. (For bedrock player, will always be CUI.)",
            "If set to CUI or TUI, player can not change their UI type.",
            "BY_PLAYER: Decide by player themselves.",
            "CUI: Chest GUI.",
            "TUI: Text GUI."
    })
    public static String defaultUiType = "BY_PLAYER";

    @Comments("The settings of the player message.")
    public static PluginMessage pluginMessage = new PluginMessage();

    public static class PluginMessage extends ConfigurationPart {
        @Comments({
                "The default message when player enter/leave dominion.",
                "Variables: {OWNER} - owner of the dominion, {DOM} - name of the dominion."
        })
        public String defaultEnterMessage = "&3{OWNER}: Welcome to {DOM}!";
        public String defaultLeaveMessage = "&3{OWNER}: Leaving {DOM}...";
        @Comments({
                "Where to show the message.",
                "Supported types: BOSS_BAR, ACTION_BAR, TITLE, SUBTITLE, CHAT"
        })
        public String noPermissionDisplayPlace = "ACTION_BAR";
        public String enterLeaveDisplayPlace = "ACTION_BAR";
    }

    @Comments("The settings of the border display effect.")
    public static BorderDisplay borderDisplay = new BorderDisplay();

    public static class BorderDisplay extends ConfigurationPart {
        @Comments({
                "Block material used for area border display (when creating/editing dominions).",
                "Must be a valid Minecraft block ID, e.g. white_stained_glass, light_blue_stained_glass, glass, ice, etc."
        })
        public String borderBlockMaterial = "white_stained_glass";

        @Comments({
                "Particle type used for border crossing effect (when player crosses dominion border).",
                "Must be a valid Minecraft particle type, e.g. end_rod, cloud, snowflake, bubble, dripping_water, etc."
        })
        public String crossingParticleType = "end_rod";

        @Comments({
                "Particle count used for border crossing effect (when player crosses dominion border).",
                "Higher values may increase bandwidth pressure.",
                "Recommended not to exceed 60."
        })
        public int crossingParticleCount = 30;
    }

    @Comments("Whether the player can migrate residence data to dominion.")
    public static boolean residenceMigration = false;

    @Comments("Whether the player have dominion.admin permission can bypass the dominion limitation.")
    public static boolean adminBypass = true;

    @Comments({
            "The settings of the group title.",
            "Player can use their group name as title in tab list."
    })
    public static GroupTitle groupTitle = new GroupTitle();

    public static class GroupTitle extends ConfigurationPart {
        public boolean enable = false;
        public String prefix = "[";
        public String suffix = "]";
    }

    @Comments("The settings of the external link.")
    public static ExternalLinks externalLinks = new ExternalLinks();

    public static class ExternalLinks extends ConfigurationPart {
        public String commandHelp = "";
        public String documentation = "https://dominion.lunadeer.cn/notes/doc/player/";
    }

    @Comments("Player with these permission nodes won't be affected by dominion's fly limitation.")
    public static List<String> flyPermissionNodes = List.of("essentials.fly", "cmi.command.fly", "domfly.use");

    @Comments("Check for updates by internet.")
    public static boolean checkUpdate = true;

    @Comments("Debug mode, if report bugs turn this on.")
    public static boolean debug = false;

    @Comments("Performance recorder, don't open this unless you are debugging.")
    public static boolean timer = false;

    @PostProcess
    public static void checkConfigurationParams() {
        if (database.type.equalsIgnoreCase("sqlite") && multiServer.enable) {
            XLogger.error(Language.configurationText.multiServerSqlite);
            multiServer.enable = false;
        }

        if (multiServer.serverId <= 0) {
            XLogger.error(Language.configurationText.serverIdInvalid);
            multiServer.enable = false;
        }

        if (autoCreateRadius < 0 && autoCreateRadius != -1) {
            autoCreateRadius = -1;
        }

        if (autoCleanAfterDays < 0 && autoCleanAfterDays != -1) {
            autoCleanAfterDays = -1;
        }

        // Material validation removed for Fabric port - will be validated
        // when the block/item system is integrated.

        try {
            // Validate display place enums
            if (!isValidDisplayPlace(pluginMessage.noPermissionDisplayPlace)) {
                XLogger.warn("Invalid no permission display place: {0}", pluginMessage.noPermissionDisplayPlace);
                pluginMessage.noPermissionDisplayPlace = "ACTION_BAR";
            }
            if (!isValidDisplayPlace(pluginMessage.enterLeaveDisplayPlace)) {
                XLogger.warn("Invalid enter leave display place: {0}", pluginMessage.enterLeaveDisplayPlace);
                pluginMessage.enterLeaveDisplayPlace = "ACTION_BAR";
            }
        } catch (Exception e) {
            XLogger.warn("Error validating display places: {0}", e.getMessage());
        }

        // Validate UI type
        try {
            // Valid UI types: BY_PLAYER, CUI, TUI
            String upper = defaultUiType.toUpperCase();
            if (!upper.equals("BY_PLAYER") && !upper.equals("CUI") && !upper.equals("TUI")) {
                XLogger.warn("Invalid default UI type: {0}", defaultUiType);
                defaultUiType = "BY_PLAYER";
            }
        } catch (Exception e) {
            XLogger.warn("Error validating default UI type: {0}", e.getMessage());
            defaultUiType = "BY_PLAYER";
        }
    }

    private static boolean isValidDisplayPlace(String place) {
        if (place == null) return false;
        String upper = place.toUpperCase();
        return upper.equals("BOSS_BAR") || upper.equals("ACTION_BAR") ||
                upper.equals("TITLE") || upper.equals("SUBTITLE") || upper.equals("CHAT");
    }

    @HandleManually
    public static Map<String, Limitation> limitations = new HashMap<>();

    @PostProcess
    public static void loadLimitations() {
        XLogger.info(Language.configurationText.loadingLimitations);
        File configDir = Dominion.getConfigDir().toFile();
        File folder = new File(configDir, "limitations");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        if (files.length == 0) {
            try {
                XLogger.info(Language.configurationText.savingDefaultLimitation);
                ConfigurationManager.saveDefault(Limitation.class, new File(folder, "default.yml"));
                limitations.put("default", new Limitation());
            } catch (Exception e) {
                XLogger.warn(Language.configurationText.saveLimitationFail, e.getMessage());
            }
            return;
        }
        for (File file : files) {
            if (!file.getName().endsWith(".yml")) continue;
            try {
                XLogger.info(Language.configurationText.loadingLimitation, file.getName());
                ConfigurationFile limitationFile = ConfigurationManager.load(Limitation.class, file, "version");
                Limitation limitation = (Limitation) limitationFile;
                limitations.put(file.getName().replace(".yml", ""), limitation);
            } catch (Exception e) {
                XLogger.warn(Language.configurationText.loadLimitationFail, file.getName(), e.getMessage());
            }
        }
        if (!limitations.containsKey("default")) {
            try {
                ConfigurationManager.saveDefault(Limitation.class, new File(folder, "default.yml"));
                limitations.put("default", new Limitation());
            } catch (Exception e) {
                XLogger.warn(Language.configurationText.saveLimitationFail, e.getMessage());
            }
        }
        XLogger.info(Language.configurationText.loadLimitations, limitations.size(), String.join(", ", limitations.keySet()));

        // Note: Permission registration for limitation groups is deferred.
        // In Fabric, permissions are handled via LuckPerms API.
        // Permission nodes like "dominion.limitation.<group>" and "group.<group>"
        // should be registered when the LuckPerms integration is implemented.
    }

    @PostProcess
    public static void setDebug() {
        XLogger.setDebug(debug);
        if (debug) {
            XLogger.warn(Language.configurationText.debugEnabled);
        }
    }

    /**
     * Gets the limitation for a player based on their permissions.
     * In Fabric, this will need to check LuckPerms permissions instead of Bukkit.
     *
     * @param playerUUID the player UUID whose limitation is to be retrieved, or null for default
     * @param groupNames the permission group names the player belongs to
     * @return the limitation for the player, or the default limitation
     */
    public static @NotNull Limitation getPlayerLimitation(@Nullable UUID playerUUID, @NotNull List<String> groupNames) {
        if (playerUUID == null || groupNames.isEmpty()) {
            return limitations.get("default");
        }
        List<Limitation> playerLimitations = new ArrayList<>();
        for (String group : limitations.keySet()) {
            if (group.equals("default")) {
                continue;
            }
            if (groupNames.contains(group) || groupNames.contains("dominion.limitation." + group)) {
                playerLimitations.add(limitations.get(group));
            }
        }
        if (playerLimitations.isEmpty()) {
            return limitations.get("default");
        } else {
            playerLimitations.sort(Comparator.comparingInt(o -> o.priority));
            return playerLimitations.get(playerLimitations.size() - 1);
        }
    }

    /**
     * Get the config directory File for Dominion.
     */
    public static File getDataFolder() {
        return Dominion.getConfigDir().toFile();
    }

    public static void loadConfigurationAndDatabase(Object sender) throws Exception {
        File configDir = Dominion.getConfigDir().toFile();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        // configuration
        ConfigurationManager.load(Configuration.class, new File(configDir, "config.yml"), "version");
        XLogger.info(Language.configurationText.loadConfiguration);

        // language
        loadLanguageFiles(Configuration.language);

        // flag
        loadFlagConfiguration();

        // world-wide
        WorldWide.load(configDir);

        // database
        XLogger.info(Language.configurationText.prepareDatabase);
        if (DatabaseManager.instance == null) {
            new DatabaseManager(configDir,
                    Configuration.database.type,
                    Configuration.database.host,
                    Configuration.database.port,
                    Configuration.database.database,
                    Configuration.database.username,
                    Configuration.database.password
            );
        } else {
            DatabaseManager.instance.set(configDir,
                    Configuration.database.type,
                    Configuration.database.host,
                    Configuration.database.port,
                    Configuration.database.database,
                    Configuration.database.username,
                    Configuration.database.password,
                    Configuration.database.connectionPoolSize
            );
        }
        DatabaseManager.instance.reconnect();
        XLogger.info(Language.configurationText.databaseConnected);
        DatabaseManager.instance.migrate();
    }
}
