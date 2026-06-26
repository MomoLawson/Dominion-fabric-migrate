package cn.lunadeer.dominion.uis.dominion.copy;

import cn.lunadeer.dominion.uis.AbstractUI;
import cn.lunadeer.dominion.utils.stui.ListView;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class DominionCopy extends AbstractUI {
    public static void show(CommandSourceStack source, String type, String domId) {
        new DominionCopy().displayByPreference(source, type, domId);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        ListView listView = new ListView("Copy " + args[0], "/dominion copy_" + args[0], "1");
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
