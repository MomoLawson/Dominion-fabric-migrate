package cn.lunadeer.dominion.uis.dominion;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.uis.AbstractUI;
import cn.lunadeer.dominion.uis.dominion.manage.Info;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class DominionList extends AbstractUI {

    public static class DominionListTuiText extends ConfigurationPart {
        public String title = "Your Dominions";
    }

    public static void show(CommandSourceStack source, String pageStr) {
        new DominionList().displayByPreference(source, pageStr);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        String page = args.length > 0 ? args[0] : "1";
        List<DominionDTO> dominions = CacheManager.instance.getPlayerOwnDominionDTOs(player.getUUID());
        ListView listView = new ListView("Your Dominions", "/dominion list", page);
        for (DominionDTO d : dominions) {
            listView.addLine(new Line(new FunctionalButton(d.getName()) {
                @Override public void function() { DominionManage.show(player.getCommandSource(), String.valueOf(d.getId())); }
            }));
        }
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
