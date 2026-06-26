package cn.lunadeer.dominion.uis;

import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class TitleList extends AbstractUI {

    public static void show(CommandSourceStack source, String pageStr) {
        new TitleList().displayByPreference(source, pageStr);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        ListView listView = new ListView("Group Titles", "/dominion title_use", args.length > 0 ? args[0] : "1");
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
