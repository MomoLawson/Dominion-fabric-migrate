package cn.lunadeer.dominion.uis.dominion.manage;

import cn.lunadeer.dominion.uis.AbstractUI;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class SetSize extends AbstractUI {
    public static void show(CommandSourceStack source, String domId) {
        new SetSize().displayByPreference(source, domId);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        ListView listView = new ListView("Set Size", "/dominion resize", "1");
        listView.addLine(new Line(new FunctionalButton("Use /dominion resize <dom> <type> <size> [dir]")));
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
