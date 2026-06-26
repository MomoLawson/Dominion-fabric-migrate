package cn.lunadeer.dominion.uis.dominion.manage.group;

import cn.lunadeer.dominion.uis.AbstractUI;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class GroupManage extends AbstractUI {
    public static void show(CommandSourceStack source, String domId, String groupId) {
        new GroupManage().displayByPreference(source, domId, groupId);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        ListView listView = new ListView("Group Manage", "/dominion group_manage", "1");
        listView.addLine(new Line(new FunctionalButton("Flags") {
            @Override public void function() { GroupFlags.show(player.getCommandSource(), args[0], args[1], "1"); }
        }));
        listView.addLine(new Line(new FunctionalButton("Add Member") {
            @Override public void function() { SelectMember.show(player.getCommandSource(), args[0], args[1], "1"); }
        }));
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
