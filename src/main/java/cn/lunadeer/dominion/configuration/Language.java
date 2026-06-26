package cn.lunadeer.dominion.configuration;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.managers.MultiServerManager;
import cn.lunadeer.dominion.managers.DatabaseBackupManager;
import cn.lunadeer.dominion.managers.TeleportManager;
import cn.lunadeer.dominion.misc.Asserts;
import cn.lunadeer.dominion.misc.Converts;
import cn.lunadeer.dominion.misc.Others;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Language configuration for Dominion.
 * Ported from Bukkit to Fabric.
 *
 * Language files are loaded from mod resources first, then overridden by config dir files.
 */
@Headers({
        "Language file for Dominion plugin",
        "If you want to help translate this file, please refer to:",
        "https://dominion.lunadeer.cn/en/notes/doc/owner/config-ref/languages",
        "for more instructions.",
        "",
        "Most of the text support color codes,",
        "you can use section-sign 0-9 for colors, l for bold, o for italic, n for underline, m for strikethrough, and k for magic.",
        "Also support '&' as an alternative for the section sign.",
})
public class Language extends ConfigurationFile {

    // languages file name list here will be saved to plugin data folder
    @HandleManually
    public enum LanguageCode {
        en_us,
        zh_cn,
        jp_jp,
        zh_tw,
    }

    /**
     * Load language files for the given language code.
     * First copies default language files from mod resources to config dir (if not present),
     * then loads the language configuration from the config dir.
     *
     * @param code the language code to load
     */
    public static void loadLanguageFiles(String code) {
        try {
            Path configDir = Dominion.getConfigDir();
            File languagesFolder = configDir.resolve("languages").toFile();
            File cuiFolder = new File(languagesFolder, "cui");
            File tuiFolder = new File(languagesFolder, "tui");

            // Copy default language files from mod resources to config dir
            for (LanguageCode languageCode : LanguageCode.values()) {
                updateLanguageFiles(languageCode.name(), false);
            }

            XLogger.info(Language.configurationText.loadingLanguage, code);

            // Load main language file
            File mainLangFile = new File(languagesFolder, code + ".yml");
            if (mainLangFile.exists()) {
                ConfigurationManager.load(Language.class, mainLangFile);
            } else {
                XLogger.warn("Language file not found: {0}", mainLangFile.getAbsolutePath());
            }

            // Load CUI language file
            File cuiLangFile = new File(cuiFolder, code + ".yml");
            if (cuiLangFile.exists()) {
                // ConfigurationManager.load(ChestUserInterface.class, cuiLangFile);
                // Deferred until UI classes are ported
            }

            // Load TUI language file
            File tuiLangFile = new File(tuiFolder, code + ".yml");
            if (tuiLangFile.exists()) {
                // ConfigurationManager.load(TextUserInterface.class, tuiLangFile);
                // Deferred until UI classes are ported
            }

            XLogger.info(Language.configurationText.loadLanguageSuccess, code);
        } catch (Exception e) {
            XLogger.error(Language.configurationText.loadLanguageFail, code, e.getMessage());
        }
    }

    /**
     * Copy language file from mod resources to config directory.
     * Only copies if the file does not already exist (unless overwrite is true).
     *
     * In Fabric, mod resources are accessed via FabricLoader.getInstance().getModContainer("dominion").
     */
    public static void updateLanguageFiles(String code, boolean overwrite) {
        Path configDir = Dominion.getConfigDir();
        File languagesFolder = configDir.resolve("languages").toFile();
        if (!languagesFolder.exists()) {
            languagesFolder.mkdirs();
        }
        File cuiFolder = new File(languagesFolder, "cui");
        if (!cuiFolder.exists()) {
            cuiFolder.mkdirs();
        }
        File tuiFolder = new File(languagesFolder, "tui");
        if (!tuiFolder.exists()) {
            tuiFolder.mkdirs();
        }

        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(Dominion.MOD_ID);

        // Copy main language file
        copyResourceToFile(modContainer, "languages/" + code + ".yml",
                new File(languagesFolder, code + ".yml"), overwrite,
                "Failed to save language file for " + code);

        // Copy CUI language file
        copyResourceToFile(modContainer, "languages/cui/" + code + ".yml",
                new File(cuiFolder, code + ".yml"), overwrite,
                "Failed to save CUI language file for " + code);

        // Copy TUI language file
        copyResourceToFile(modContainer, "languages/tui/" + code + ".yml",
                new File(tuiFolder, code + ".yml"), overwrite,
                "Failed to save TUI language file for " + code);
    }

    /**
     * Copy a resource from the mod jar to a file on disk.
     * Only copies if the target file does not exist (unless overwrite is true).
     */
    private static void copyResourceToFile(Optional<ModContainer> modContainer, String resourcePath,
                                           File targetFile, boolean overwrite, String errorMsg) {
        if (!overwrite && targetFile.exists()) return;

        try {
            if (modContainer.isPresent()) {
                Optional<Path> resource = modContainer.get().findPath(resourcePath);
                if (resource.isPresent()) {
                    Files.copy(resource.get(), targetFile.toPath(),
                            overwrite ? java.nio.file.StandardCopyOption.REPLACE_EXISTING : java.nio.file.StandardCopyOption.COPY_ATTRIBUTES);
                    return;
                }
            }
        } catch (Exception e) {
            XLogger.warn("{0}: {1}", errorMsg, e.getMessage());
            XLogger.warn("See https://dominion.lunadeer.cn/en/notes/doc/owner/config-ref/languages , If you want to help us to add this language.");
        }
    }

    // Text instances for other systems
    public static Dominion.DominionText dominionText = new Dominion.DominionText();

    public static Configuration.ConfigurationText configurationText = new Configuration.ConfigurationText();
    public static Limitation.LimitationText limitationText = new Limitation.LimitationText();

    public static CommandExceptionText commandExceptionText = new CommandExceptionText();

    public static class CommandExceptionText extends ConfigurationPart {
        public String noPermission = "You do not have permission {0} to do this.";
        public String invalidArguments = "Invalid arguments, usage e.g. {0}.";
    }

    public static InputterText inputterText = new InputterText();

    public static class InputterText extends ConfigurationPart {
        public String onlyPlayer = "TUI inputter can only be used by a player.";
        public String cancel = " [Send 'C' to cancel the inputter.]";
        public String inputterCancelled = "Inputter cancelled.";
    }

    // Additional text instances will be added as more systems are ported:
    // - AbstractUI.ConsoleText / UiCommandsDescription
    // - MultiServerManager.MultiServerManagerText
    // - Asserts / Converts / Others
    // - VaultConnect text
    // - Handler text (DominionProvider, MemberProvider, GroupProvider, SelectPointEvents)
    // - Inputter text instances
    // - Command text instances
    // - DatabaseBackupManager text
    // - TeleportManager text

    public static MultiServerManager.MultiServerManagerText multiServerManagerText = new MultiServerManager.MultiServerManagerText();
    public static DatabaseBackupManager.DatabaseManagerText databaseManagerText = new DatabaseBackupManager.DatabaseManagerText();
    public static TeleportManager.TeleportManagerText teleportManagerText = new TeleportManager.TeleportManagerText();

    // Command text instances
    public static cn.lunadeer.dominion.commands.AdministratorCommand.AdministratorCommandText administratorCommandText = new cn.lunadeer.dominion.commands.AdministratorCommand.AdministratorCommandText();
    public static cn.lunadeer.dominion.commands.DominionCreateCommand.DominionCreateCommandText dominionCreateCommandText = new cn.lunadeer.dominion.commands.DominionCreateCommand.DominionCreateCommandText();
    public static cn.lunadeer.dominion.commands.DominionOperateCommand.DominionOperateCommandText dominionOperateCommandText = new cn.lunadeer.dominion.commands.DominionOperateCommand.DominionOperateCommandText();
    public static cn.lunadeer.dominion.commands.DominionFlagCommand.DominionFlagCommandText dominionFlagCommandText = new cn.lunadeer.dominion.commands.DominionFlagCommand.DominionFlagCommandText();
    public static cn.lunadeer.dominion.commands.MemberCommand.MemberCommandText memberCommandText = new cn.lunadeer.dominion.commands.MemberCommand.MemberCommandText();
    public static cn.lunadeer.dominion.commands.GroupCommand.GroupCommandText groupCommandText = new cn.lunadeer.dominion.commands.GroupCommand.GroupCommandText();
    public static cn.lunadeer.dominion.commands.GroupTitleCommand.GroupTitleCommandText groupTitleCommandText = new cn.lunadeer.dominion.commands.GroupTitleCommand.GroupTitleCommandText();
    public static cn.lunadeer.dominion.commands.TemplateCommand.TemplateCommandText templateCommandText = new cn.lunadeer.dominion.commands.TemplateCommand.TemplateCommandText();
    public static cn.lunadeer.dominion.commands.CopyCommand.CopyCommandText copyCommandText = new cn.lunadeer.dominion.commands.CopyCommand.CopyCommandText();
    public static cn.lunadeer.dominion.commands.MigrationCommand.MigrationCommandText migrationCommandText = new cn.lunadeer.dominion.commands.MigrationCommand.MigrationCommandText();

    // Other ported text instances
    public static Asserts.AssertsText assertsText = new Asserts.AssertsText();
    public static Converts.ConvertsText convertsText = new Converts.ConvertsText();
    public static Others.OthersText othersText = new Others.OthersText();

    public static class SelectPointEventsHandlerText extends ConfigurationPart {
        public String noDominion = "No dominion found at your location ({0}, {1}, {2}).";
    }
    public static SelectPointEventsHandlerText selectPointEventsHandlerText = new SelectPointEventsHandlerText();

    @PostProcess
    public static void setOtherStaticText() {
        // Command exception text
        // InvalidArgumentException.MSG = commandExceptionText.invalidArguments;
        // NoPermissionException.MSG = commandExceptionText.noPermission;

        // Inputter text
        // InputterRunner.ONLY_PLAYER = inputterText.onlyPlayer;
        // InputterRunner.CANCEL = inputterText.cancel;
        // InputterRunner.INPUTTER_CANCELLED = inputterText.inputterCancelled;
    }
}
