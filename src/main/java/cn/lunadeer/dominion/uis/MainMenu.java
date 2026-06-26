package cn.lunadeer.dominion.uis;

import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class MainMenu extends AbstractUI {

    public static class MenuTuiText extends ConfigurationPart {
        public String title = "Dominion Menu";
        public String button = "MENU";
    }

    public static void show(CommandSourceStack source, String pageStr) {
        new MainMenu().displayByPreference(source, pageStr);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        ListView listView = new ListView(Language.menuTuiText.title, "/dominion menu", args.length > 0 ? args[0] : "1");
        listView.addLine(new Line(new FunctionalButton("Create Dominion") {
            @Override public void function() { /* CreateDominionInputter.createOn(player) */ }
        }));
        listView.addLine(new Line(new FunctionalButton("My Dominions") {
            @Override public void function() { DominionList.show(player.getCommandSource(), "1"); }
        }));
        listView.addLine(new Line(new FunctionalButton("Templates") {
            @Override public void function() { /* TemplateList.show() */ }
        }));
        listView.show(player);
    }

    @Override
    protected void showCUI(ServerPlayer player, String... args) throws Exception {
        showTUI(player, args); // Fallback to TUI for now
    }

    @Override
    protected void showConsole(CommandSourceStack sender, String... args) throws Exception {
        Notification.info(sender, "Dominion Menu - Use in-game for full experience");
    }
}
