package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;

public class MigrationCommand {
    public static class MigrationCommandText extends ConfigurationPart {
        public String description = "Migrate from Residence plugin.";
        public String noResidence = "No Residence data found.";
        public String migrationComplete = "Migration complete.";
    }

    public static void migrate(CommandSourceStack source, String name) {
        XLogger.info("Migration not yet implemented for Fabric");
    }

    public static void migrateAll(CommandSourceStack source) {
        XLogger.info("Migration not yet implemented for Fabric");
    }
}
