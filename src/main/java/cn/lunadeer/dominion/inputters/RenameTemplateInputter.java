package cn.lunadeer.dominion.inputters;

import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.inputter.InputterRunner;
import net.minecraft.commands.CommandSourceStack;

public class RenameTemplateInputter {
    public static class RenameTemplateInputterText extends ConfigurationPart {
        public String button = "RENAME TEMPLATE";
        public String hint = "Enter the new name for the template.";
    }

    public static void renameOn(CommandSourceStack source, String templateId) {
        new InputterRunner(source, Language.renameTemplateInputterText.hint) {
            @Override public void run(String input) {
                // TemplateCommand.rename(source, templateId, input);
            }
        };
    }
}
