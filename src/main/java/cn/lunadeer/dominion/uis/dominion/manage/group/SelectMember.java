package cn.lunadeer.dominion.uis.dominion.manage.group;

import cn.lunadeer.dominion.uis.AbstractUI;
import cn.lunadeer.dominion.utils.stui.ListView;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class SelectMember extends AbstractUI {
    public static void show(CommandSourceStack source, String domId, String groupId, String page) {
        new SelectMember().displayByPreference(source, domId, groupId, page);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        ListView listView = new ListView("Select Member", "/dominion group_add_member", args[2]);
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
