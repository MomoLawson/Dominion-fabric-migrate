package cn.lunadeer.dominion.uis.dominion.copy;

import cn.lunadeer.dominion.uis.AbstractUI;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class CopyMenu extends AbstractUI {
    public static void show(CommandSourceStack source, String domId) {
        new CopyMenu().displayByPreference(source, domId);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        ListView listView = new ListView("Copy Settings", "/dominion copy", "1");
        listView.addLine(new Line(new FunctionalButton("Copy Env Flags") {
            @Override public void function() { DominionCopy.show(player.getCommandSource(), "env", args[0]); }
        }));
        listView.addLine(new Line(new FunctionalButton("Copy Guest Flags") {
            @Override public void function() { DominionCopy.show(player.getCommandSource(), "guest", args[0]); }
        }));
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
