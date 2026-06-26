package cn.lunadeer.dominion.inputters;

import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.inputter.InputterRunner;
import net.minecraft.commands.CommandSourceStack;

public class EditMessageInputter {
    public static class EditMessageInputterText extends ConfigurationPart {
        public String button = "EDIT MESSAGE";
        public String hint = "Enter the new message.";
    }

    public static void editOn(CommandSourceStack source, String domId, String type) {
        new InputterRunner(source, Language.editMessageInputterText.hint) {
            @Override public void run(String input) {
                // DominionOperateCommand.setMessage(source, domId, type, input);
            }
        };
    }
}
