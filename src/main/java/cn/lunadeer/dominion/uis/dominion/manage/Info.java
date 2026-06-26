package cn.lunadeer.dominion.uis.dominion.manage;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.uis.AbstractUI;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class Info extends AbstractUI {
    public static void show(CommandSourceStack source, String domId) {
        new Info().displayByPreference(source, domId);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        int domId = Integer.parseInt(args[0]);
        DominionDTO d = CacheManager.instance.getDominion(domId);
        if (d == null) return;
        ListView listView = new ListView("Info: " + d.getName(), "/dominion info", "1");
        listView.addLine(new Line(new FunctionalButton("Name: " + d.getName())));
        listView.addLine(new Line(new FunctionalButton("Owner: " + d.getOwnerName())));
        listView.addLine(new Line(new FunctionalButton("World: " + d.getWorldUid())));
        listView.addLine(new Line(new FunctionalButton("Size: " + (d.getX2()-d.getX1()) + "x" + (d.getZ2()-d.getZ1()))));
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
