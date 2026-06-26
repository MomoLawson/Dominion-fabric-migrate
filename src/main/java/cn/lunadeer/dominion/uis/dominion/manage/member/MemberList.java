package cn.lunadeer.dominion.uis.dominion.manage.member;

import cn.lunadeer.dominion.uis.AbstractUI;
import cn.lunadeer.dominion.utils.stui.ListView;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class MemberList extends AbstractUI {
    public static void show(CommandSourceStack source, String domId, String page) {
        new MemberList().displayByPreference(source, domId, page);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        ListView listView = new ListView("Members", "/dominion member_list", args[1]);
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
