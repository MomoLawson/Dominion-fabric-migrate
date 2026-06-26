package cn.lunadeer.dominion.inputters;

import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.inputter.InputterRunner;
import net.minecraft.commands.CommandSourceStack;

public class SearchPlayerInputter {
    public static class SearchPlayerInputterText extends ConfigurationPart {
        public String button = "SEARCH PLAYER";
        public String hint = "Enter the player name to search.";
    }

    public static void searchOn(CommandSourceStack source) {
        new InputterRunner(source, Language.searchPlayerInputterText.hint) {
            @Override public void run(String input) {
                // Search for player and show results
            }
        };
    }
}
