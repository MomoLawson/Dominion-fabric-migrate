package cn.lunadeer.dominion.uis;

import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class AllDominion extends AbstractUI {

    public static class AllDominionTuiText extends ConfigurationPart {
        public String title = "All Dominions";
    }

    public static void show(CommandSourceStack source, String pageStr) {
        new AllDominion().displayByPreference(source, pageStr);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        ListView listView = new ListView("All Dominions", "/dominion all", args.length > 0 ? args[0] : "1");
        CacheManager.instance.getAllDominions().forEach(d -> {
            listView.addLine(new Line(new FunctionalButton(d.getName()) {
                @Override public void function() { DominionManage.show(player.getCommandSource(), String.valueOf(d.getId())); }
            }));
        });
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
