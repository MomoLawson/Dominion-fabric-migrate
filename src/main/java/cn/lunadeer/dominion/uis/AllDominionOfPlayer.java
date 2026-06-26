package cn.lunadeer.dominion.uis;

import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class AllDominionOfPlayer extends AbstractUI {

    public static class AllDominionOfPlayerTuiText extends ConfigurationPart {
        public String title = "Dominions of {0}";
    }

    public static void show(CommandSourceStack source, String playerName, String pageStr) {
        new AllDominionOfPlayer().displayByPreference(source, playerName, pageStr);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        String targetPlayer = args.length > 0 ? args[0] : "";
        ListView listView = new ListView("Dominions of " + targetPlayer, "/dominion all_of", args.length > 1 ? args[1] : "1");
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
