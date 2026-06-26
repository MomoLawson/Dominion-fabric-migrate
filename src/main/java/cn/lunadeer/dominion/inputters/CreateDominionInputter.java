package cn.lunadeer.dominion.inputters;

import cn.lunadeer.dominion.commands.DominionCreateCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.uis.dominion.DominionList;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.inputter.InputterRunner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class CreateDominionInputter {
    public static class CreateDominionInputterText extends ConfigurationPart {
        public String button = "CREATE";
        public String description = "Create a new dominion.";
        public String hint = "A new Dominion will be created around you with the input name.";
    }

    public static void createOn(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return;
        new InputterRunner(source, Language.createDominionInputterText.hint) {
            @Override public void run(String input) {
                DominionCreateCommand.autoCreate(source, input);
            }
        };
    }

    public static FunctionalButton createTuiButtonOn(CommandSourceStack source) {
        return new FunctionalButton(Language.createDominionInputterText.button) {
            @Override public void function() { createOn(source); }
        };
    }
}
