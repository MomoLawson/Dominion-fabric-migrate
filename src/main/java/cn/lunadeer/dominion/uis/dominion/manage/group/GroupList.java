package cn.lunadeer.dominion.uis.dominion.manage.group;

import cn.lunadeer.dominion.uis.AbstractUI;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class GroupList extends AbstractUI {
    public static void show(CommandSourceStack source, String domId, String page) {
        new GroupList().displayByPreference(source, domId, page);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        ListView listView = new ListView("Groups", "/dominion group_list", args[1]);
        listView.addLine(new Line(new FunctionalButton("Use /dominion group_create <dom> <name> to create a group")));
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
