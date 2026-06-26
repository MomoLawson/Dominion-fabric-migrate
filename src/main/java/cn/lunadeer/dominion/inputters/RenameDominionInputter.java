package cn.lunadeer.dominion.inputters;

import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.inputter.InputterRunner;
import net.minecraft.commands.CommandSourceStack;

public class RenameDominionInputter {
    public static class RenameDominionInputterText extends ConfigurationPart {
        public String button = "RENAME";
        public String hint = "Enter the new name for the dominion.";
    }

    public static void renameOn(CommandSourceStack source, String domId) {
        new InputterRunner(source, Language.renameDominionInputterText.hint) {
            @Override public void run(String input) {
                // DominionOperateCommand.rename(source, domId, input);
            }
        };
    }
}
