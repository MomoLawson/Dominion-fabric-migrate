package cn.lunadeer.dominion.uis.dominion.manage.member;

import cn.lunadeer.dominion.uis.AbstractUI;
import cn.lunadeer.dominion.utils.stui.ListView;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class MemberFlags extends AbstractUI {
    public static void show(CommandSourceStack source, String domId, String memberUuid, String page) {
        new MemberFlags().displayByPreference(source, domId, memberUuid, page);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        ListView listView = new ListView("Member Flags", "/dominion member_set_pri", args[2]);
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
