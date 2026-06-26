package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;

public class AdministratorCommand {
    public static class AdministratorCommandText extends ConfigurationPart {
        public String reloadSuccess = "Configuration reloaded.";
        public String reloadFailed = "Failed to reload: {0}";
        public String exportSuccess = "Export complete.";
        public String importSuccess = "Import complete.";
        public String updateLanguageSuccess = "Language updated.";
    }

    public static void handleReload(CommandSourceStack source, String type) {
        try {
            cn.lunadeer.dominion.configuration.Configuration.loadConfigurationAndDatabase(null);
            XLogger.info("Configuration reloaded");
        } catch (Exception e) {
            XLogger.error("Reload failed: {0}", e.getMessage());
        }
    }

    public static void handleExport(CommandSourceStack source, String type) {
        XLogger.info("Export not yet implemented");
    }

    public static void handleImport(CommandSourceStack source, boolean confirm) {
        XLogger.info("Import not yet implemented");
    }

    public static void handleUpdateLanguage(CommandSourceStack source, boolean confirm) {
        XLogger.info("Language update not yet implemented");
    }
}
