package cn.lunadeer.dominion.inputters;

import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.inputter.InputterRunner;
import net.minecraft.commands.CommandSourceStack;

public class ResizeDominionInputter {
    public static class ResizeDominionInputterText extends ConfigurationPart {
        public String button = "RESIZE";
        public String hint = "Enter the resize parameters (type size [direction]).";
    }

    public static void resizeOn(CommandSourceStack source, String domId) {
        new InputterRunner(source, Language.resizeDominionInputterText.hint) {
            @Override public void run(String input) {
                // DominionOperateCommand.resize(source, domId, input);
            }
        };
    }
}
