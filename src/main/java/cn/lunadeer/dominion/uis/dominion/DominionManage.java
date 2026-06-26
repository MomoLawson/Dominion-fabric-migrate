package cn.lunadeer.dominion.uis.dominion;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.uis.AbstractUI;
import cn.lunadeer.dominion.uis.dominion.manage.*;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class DominionManage extends AbstractUI {

    public static class DominionManageTuiText extends ConfigurationPart {
        public String title = "Manage: {0}";
    }

    public static void show(CommandSourceStack source, String domIdStr) {
        new DominionManage().displayByPreference(source, domIdStr);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        int domId = Integer.parseInt(args[0]);
        DominionDTO dominion = CacheManager.instance.getDominion(domId);
        if (dominion == null) return;
        ListView listView = new ListView("Manage: " + dominion.getName(), "/dominion manage", "1");
        listView.addLine(new Line(new FunctionalButton("Info") {
            @Override public void function() { Info.show(player.getCommandSource(), args[0]); }
        }));
        listView.addLine(new Line(new FunctionalButton("Environment Flags") {
            @Override public void function() { EnvFlags.show(player.getCommandSource(), args[0], "1"); }
        }));
        listView.addLine(new Line(new FunctionalButton("Guest Flags") {
            @Override public void function() { GuestFlags.show(player.getCommandSource(), args[0], "1"); }
        }));
        listView.addLine(new Line(new FunctionalButton("Members") {
            @Override public void function() { cn.lunadeer.dominion.uis.dominion.manage.member.MemberList.show(player.getCommandSource(), args[0], "1"); }
        }));
        listView.addLine(new Line(new FunctionalButton("Groups") {
            @Override public void function() { cn.lunadeer.dominion.uis.dominion.manage.group.GroupList.show(player.getCommandSource(), args[0], "1"); }
        }));
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
