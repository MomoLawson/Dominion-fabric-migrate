package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.managers.DatabaseBackupManager;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.utils.McaRecord;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static cn.lunadeer.dominion.Dominion.adminPermission;
import static cn.lunadeer.dominion.misc.Converts.toWorld;

public class AdministratorCommand {

    public static class AdministratorCommandText extends ConfigurationPart {
        public String reloadCacheButton = "RELOAD CACHE";
        public String reloadCacheDescription = "Reload the cache (dont do this frequently).";
        public String reloadConfigButton = "RELOAD CONFIG";
        public String reloadConfigDescription = "Reload the configuration.";
        public String reloadDescription = "Reload cache, configuration, or both.";
        public String exportDescription = "Export data (MCA list or database).";
        public String importDescription = "Import database data.";
        public String updateLanguageDescription = "Regenerate language files from plugin internal language files.";
        public String updateLanguageConfirm = "This will overwrite your current language files, please confirm by adding 'confirm' at the end of the command.";

        public String reloadingDominionCache = "Reloading dominion cache...";
        public String reloadedDominionCache = "Reload dominion cache success!";
        public String reloadingMemberCache = "Reloading member privilege cache...";
        public String reloadedMemberCache = "Reload member privilege cache success!";
        public String reloadingGroupCache = "Reloading group cache...";
        public String reloadedGroupCache = "Reload group cache success!";

        public String reloadingConfig = "Reloading configuration...";

        public String exportingMcaList = "Exporting MCA list...";
        public String createMcaFolderFailed = "Failed to create mca list folder.";
        public String writingMcaList = "Writing list of world {0}...";
        public String createMcaFileFailed = "Failed to create mca list file {0}.";
        public String exportMCAListFailed = "Failed to export MCA list of world {0}, reason: {1}.";
        public String exportedMCAList = "Exported MCA list to {0} successfully.";

        public String importHint = "Import database is only for migration or restore-backup, don't use it to merge two databases.";
        public String importInfo = "If current database is not empty, it will throw some errors and may cause data loss or corruption.";
        public String importConfirm = "Please confirm the import operation by adding 'confirm' at the end of the command.";
    }

    private enum RELOAD_TYPE {
        CONFIG,
        CACHE,
        ALL
    }

    /**
     * Handles the reload command with a type parameter.
     *
     * @param source  the command source
     * @param typeStr the reload type: "config", "cache", or "all"
     */
    public static void handleReload(CommandSourceStack source, String typeStr) {
        RELOAD_TYPE type;
        try {
            type = RELOAD_TYPE.valueOf(typeStr.toUpperCase());
        } catch (Exception e) {
            type = RELOAD_TYPE.ALL;
        }
        switch (type) {
            case CONFIG:
                reloadConfig(source);
                break;
            case CACHE:
                reloadCache(source);
                break;
            default:
                reloadConfig(source);
                reloadCache(source);
                break;
        }
    }

    /**
     * Reloads the Dominion cache (dominions, members, groups).
     *
     * @param source the command source
     */
    public static void reloadCache(CommandSourceStack source) {
        sendInfo(source, Language.administratorCommandText.reloadingDominionCache);
        CacheManager.instance.getCache().getDominionCache().load();
        sendInfo(source, Language.administratorCommandText.reloadedDominionCache);

        sendInfo(source, Language.administratorCommandText.reloadingMemberCache);
        CacheManager.instance.getCache().getMemberCache().load();
        sendInfo(source, Language.administratorCommandText.reloadedMemberCache);

        sendInfo(source, Language.administratorCommandText.reloadingGroupCache);
        CacheManager.instance.getCache().getGroupCache().load();
        sendInfo(source, Language.administratorCommandText.reloadedGroupCache);
    }

    /**
     * Reloads the configuration.
     *
     * @param source the command source
     */
    public static void reloadConfig(CommandSourceStack source) {
        try {
            sendInfo(source, Language.administratorCommandText.reloadingConfig);
            ServerPlayer player = null;
            try {
                player = source.getPlayer();
            } catch (Exception ignored) {
            }
            Configuration.loadConfigurationAndDatabase(player);
        } catch (Exception e) {
            sendError(source, e.getMessage());
        }
    }

    /**
     * Handles export command.
     *
     * @param source  the command source
     * @param typeStr "mca" or "db"
     */
    public static void handleExport(CommandSourceStack source, String typeStr) {
        if (typeStr.toUpperCase().startsWith("M")) {
            exportMCA(source);
        } else {
            DatabaseBackupManager.exportTables(source.level().getServer());
        }
    }

    /**
     * Exports MCA list for all dominions.
     *
     * @param source the command source
     */
    public static void exportMCA(CommandSourceStack source) {
        try {
            CacheManager.instance.getCache().getMcaWhitelistCache().clear();
            MinecraftServer server = source.level().getServer();
            server.execute(() -> {
                try {
                    sendInfo(source, Language.administratorCommandText.exportingMcaList);
                    Map<String, List<String>> mca_cords = new HashMap<>();
                    List<DominionDTO> doms = CacheManager.instance.getAllDominions();
                    for (DominionDTO dom : doms) {
                        ServerLevel world;
                        try {
                            world = toWorld(dom.getWorldUid());
                        } catch (Exception e) {
                            continue;
                        }
                        String worldName = world.dimension().location().toString();
                        mca_cords.putIfAbsent(worldName, new ArrayList<>());
                        int mca_x1 = convertWorld2Mca(dom.getCuboid().x1()) - 1;
                        int mca_x2 = convertWorld2Mca(dom.getCuboid().x2()) + 1;
                        int mca_z1 = convertWorld2Mca(dom.getCuboid().z1()) - 1;
                        int mca_z2 = convertWorld2Mca(dom.getCuboid().z2()) + 1;
                        for (int x = mca_x1; x <= mca_x2; x++) {
                            for (int z = mca_z1; z <= mca_z2; z++) {
                                String file_name = "r." + x + "." + z + ".mca";
                                if (mca_cords.get(worldName).contains(file_name)) {
                                    continue;
                                }
                                mca_cords.get(worldName).add(file_name);
                                CacheManager.instance.getCache().getMcaWhitelistCache().add(new McaRecord(x, z, worldName));
                            }
                        }
                    }
                    File folder = new File(Dominion.getConfigDir().toFile(), "exported-mca-list");
                    if (!folder.exists()) {
                        if (!folder.mkdirs()) {
                            throw new DominionException(Language.administratorCommandText.createMcaFolderFailed);
                        }
                    }
                    for (String worldName : mca_cords.keySet()) {
                        File file = new File(folder, worldName.replace(":", "_") + ".txt");
                        sendInfo(source, Language.administratorCommandText.writingMcaList, worldName);
                        try {
                            if (file.exists()) {
                                File backup = new File(folder, worldName.replace(":", "_") + ".txt.bak");
                                file.renameTo(backup);
                            }
                            if (!file.createNewFile()) {
                                throw new DominionException(Language.administratorCommandText.createMcaFileFailed, file.getName());
                            }
                            List<String> cords = mca_cords.get(worldName);
                            for (String cord : cords) {
                                Files.write(file.toPath(), (cord + "\n").getBytes(), StandardOpenOption.APPEND);
                            }
                        } catch (Exception e) {
                            sendError(source, Language.administratorCommandText.exportMCAListFailed, worldName, e.getMessage());
                        }
                    }
                    sendInfo(source, Language.administratorCommandText.exportedMCAList, folder.getAbsolutePath());
                } catch (Exception e) {
                    sendError(source, e.getMessage());
                }
            });
        } catch (Exception e) {
            sendError(source, e.getMessage());
        }
    }

    /**
     * Handles import command.
     *
     * @param source  the command source
     * @param confirm whether the user has confirmed the import
     */
    public static void handleImport(CommandSourceStack source, boolean confirm) {
        if (!confirm) {
            sendWarn(source, Language.administratorCommandText.importHint);
            sendWarn(source, Language.administratorCommandText.importInfo);
            sendWarn(source, Language.administratorCommandText.importConfirm);
            return;
        }
        DatabaseBackupManager.importTables(source.level().getServer());
    }

    /**
     * Handles language update command.
     *
     * @param source  the command source
     * @param confirm whether the user has confirmed the update
     */
    public static void handleUpdateLanguage(CommandSourceStack source, boolean confirm) {
        if (!confirm) {
            sendWarn(source, Language.administratorCommandText.updateLanguageConfirm);
            return;
        }
        try {
            Language.updateLanguageFiles(Configuration.language, true);
            Language.loadLanguageFiles(Configuration.language);
        } catch (Exception e) {
            sendError(source, "Failed to update language files: {0}", e.getMessage());
        }
    }

    private static int convertWorld2Mca(int world) {
        return world < 0 ? world / 512 - 1 : world / 512;
    }

    // --- Helper methods to send messages to the source (player or console) ---

    private static void sendInfo(CommandSourceStack source, String msg, Object... args) {
        try {
            ServerPlayer player = source.getPlayer();
            Notification.info(player, msg, args);
        } catch (Exception e) {
            Notification.info(source.level().getServer(), msg, args);
        }
    }

    private static void sendWarn(CommandSourceStack source, String msg, Object... args) {
        try {
            ServerPlayer player = source.getPlayer();
            Notification.warn(player, msg, args);
        } catch (Exception e) {
            Notification.warn(source.level().getServer(), msg, args);
        }
    }

    private static void sendError(CommandSourceStack source, String msg, Object... args) {
        try {
            ServerPlayer player = source.getPlayer();
            Notification.error(player, msg, args);
        } catch (Exception e) {
            Notification.error(source.level().getServer(), msg, args);
        }
    }

    private static void sendError(CommandSourceStack source, Throwable e) {
        try {
            ServerPlayer player = source.getPlayer();
            Notification.error(player, e);
        } catch (Exception ex) {
            Notification.error(source.level().getServer(), e.getMessage());
        }
    }
}
