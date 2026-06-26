package cn.lunadeer.dominion.inputters;

import cn.lunadeer.dominion.commands.TemplateCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.inputter.InputterRunner;
import net.minecraft.commands.CommandSourceStack;

public class CreateTemplateInputter {
    public static class CreateTemplateInputterText extends ConfigurationPart {
        public String button = "CREATE TEMPLATE";
        public String hint = "Enter the name for the new template.";
    }

    public static void createOn(CommandSourceStack source) {
        new InputterRunner(source, Language.createTemplateInputterText.hint) {
            @Override public void run(String input) {
                TemplateCommand.createTemplate(source, input);
            }
        };
    }
}
