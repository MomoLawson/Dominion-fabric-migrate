package cn.lunadeer.dominion.inputters;

import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.inputter.InputterRunner;
import net.minecraft.commands.CommandSourceStack;

public class RenameGroupInputter {
    public static class RenameGroupInputterText extends ConfigurationPart {
        public String button = "RENAME GROUP";
        public String hint = "Enter the new name for the group.";
    }

    public static void renameOn(CommandSourceStack source, String domId, String groupId) {
        new InputterRunner(source, Language.renameGroupInputterText.hint) {
            @Override public void run(String input) {
                // GroupCommand.rename(source, domId, groupId, input);
            }
        };
    }
}
