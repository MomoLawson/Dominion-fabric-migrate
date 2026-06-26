package cn.lunadeer.dominion.inputters;

import cn.lunadeer.dominion.commands.GroupCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.inputter.InputterRunner;
import net.minecraft.commands.CommandSourceStack;

public class CreateGroupInputter {
    public static class CreateGroupInputterText extends ConfigurationPart {
        public String button = "CREATE GROUP";
        public String hint = "Enter the name for the new group.";
    }

    public static void createOn(CommandSourceStack source, String domId) {
        new InputterRunner(source, Language.createGroupInputterText.hint) {
            @Override public void run(String input) {
                GroupCommand.createGroup(source, domId, input);
            }
        };
    }
}
