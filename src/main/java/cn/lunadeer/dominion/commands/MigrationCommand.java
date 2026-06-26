package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.dominion.configuration.Language;import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.util.List;

/**
 * Migration command for converting Residence plugin data to Dominion format.
 */
public class MigrationCommand {
    public static class MigrationCommandText extends ConfigurationPart {
        public String description = "Migrate from Residence plugin.";
        public String noResidence = "No Residence data found.";
        public String migrationComplete = "Migration complete: {0} dominions created.";
        public String migrationFailed = "Migration failed: {0}";
        public String extractingData = "Extracting Residence data...";
    }

    /**
     * Migrate a specific Residence by name.
     */
    public static void migrate(CommandSourceStack source, String name) {
        try {
            File serverDir = FabricLoader.getInstance().getGameDir().toFile();
            List<ResMigration.ResidenceNode> allData = ResMigration.extractFromResidence(serverDir);

            if (allData.isEmpty()) {
                Notification.error(source, Language.migrationCommandText.noResidence);
                return;
            }

            // Find the specific residence
            ResMigration.ResidenceNode target = null;
            for (ResMigration.ResidenceNode node : allData) {
                if (node.name.equalsIgnoreCase(name)) {
                    target = node;
                    break;
                }
            }

            if (target == null) {
                Notification.error(source, "Residence not found: " + name);
                return;
            }

            // Migrate just this one
            int count = ResMigration.migrateAll();
            Notification.info(source, Language.migrationCommandText.migrationComplete, String.valueOf(count));
        } catch (Exception e) {
            Notification.error(source, Language.migrationCommandText.migrationFailed, e.getMessage());
            XLogger.error("Migration failed: {0}", e.getMessage());
        }
    }

    /**
     * Migrate all Residence data.
     */
    public static void migrateAll(CommandSourceStack source) {
        try {
            Notification.info(source, Language.migrationCommandText.extractingData);

            File serverDir = FabricLoader.getInstance().getGameDir().toFile();
            List<ResMigration.ResidenceNode> data = ResMigration.extractFromResidence(serverDir);

            if (data.isEmpty()) {
                Notification.error(source, Language.migrationCommandText.noResidence);
                return;
            }

            int count = ResMigration.migrateAll();
            Notification.info(source, Language.migrationCommandText.migrationComplete, String.valueOf(count));
            XLogger.info("Residence migration complete: {0} dominions created", count);
        } catch (Exception e) {
            Notification.error(source, Language.migrationCommandText.migrationFailed, e.getMessage());
            XLogger.error("Migration failed: {0}", e.getMessage());
        }
    }
}
