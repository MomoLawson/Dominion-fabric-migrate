package cn.lunadeer.dominion.uis;

import cn.lunadeer.dominion.utils.stui.ListView;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class MigrateList extends AbstractUI {

    public static void show(CommandSourceStack source, String pageStr) {
        new MigrateList().displayByPreference(source, pageStr);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        ListView listView = new ListView("Residence Migration", "/dominion migrate", args.length > 0 ? args[0] : "1");
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
