package cn.lunadeer.dominion.inputters;

import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.inputter.InputterRunner;
import net.minecraft.commands.CommandSourceStack;

public class SetMapColorInputter {
    public static class SetMapColorInputterText extends ConfigurationPart {
        public String button = "SET COLOR";
        public String hint = "Enter the color hex code (e.g., #FF0000).";
    }

    public static void setColorOn(CommandSourceStack source, String domId) {
        new InputterRunner(source, Language.setMapColorInputterText.hint) {
            @Override public void run(String input) {
                // DominionOperateCommand.setMapColor(source, domId, input);
            }
        };
    }
}
