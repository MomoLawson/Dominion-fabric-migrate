package cn.lunadeer.dominion.uis;

import cn.lunadeer.dominion.uis.dominion.DominionList;
import cn.lunadeer.dominion.uis.dominion.manage.Info;
import cn.lunadeer.dominion.uis.template.TemplateList;
import cn.lunadeer.dominion.inputters.CreateDominionInputter;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.scui.ChestButton;
import cn.lunadeer.dominion.utils.scui.ChestView;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MainMenu extends AbstractUI {

    public static void show(CommandSourceStack source, String... args) {
        new MainMenu().displayByPreference(source, args);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) {
        ListView listView = new ListView("§6§lDominion Menu", "/dominion menu", "1");

        FunctionalButton createBtn = new FunctionalButton();
        createBtn.setText("§a§lCreate Dominion");
        createBtn.setFunction(p -> CreateDominionInputter.createOn(p.createCommandSourceStack()));
        listView.addLine(new Line().append(createBtn.build()));

        FunctionalButton listBtn = new FunctionalButton();
        listBtn.setText("§e§lMy Dominions");
        listBtn.setFunction(p -> DominionList.show(p.createCommandSourceStack(), "1"));
        listView.addLine(new Line().append(listBtn.build()));

        FunctionalButton templateBtn = new FunctionalButton();
        templateBtn.setText("§b§lTemplates");
        templateBtn.setFunction(p -> TemplateList.show(p.createCommandSourceStack(), "1"));
        listView.addLine(new Line().append(templateBtn.build()));

        FunctionalButton cuiBtn = new FunctionalButton();
        cuiBtn.setText("§7§lSwitch to CUI");
        cuiBtn.setFunction(p -> {
            try {
                cn.lunadeer.dominion.api.dtos.PlayerDTO dto = cn.lunadeer.dominion.cache.CacheManager.instance.getPlayer(p.getUUID());
                if (dto != null) {
                    dto.setUiPreference(cn.lunadeer.dominion.api.dtos.PlayerDTO.UI_TYPE.CUI);
                    showCUI(p, args);
                }
            } catch (Exception e) { Notification.error(p.createCommandSourceStack(), e); }
        });
        listView.addLine(new Line().append(cuiBtn.build()));

        listView.show(player);
    }

    @Override
    protected void showCUI(ServerPlayer player, String... args) {
        ChestView view = new ChestView("§6Dominion Menu", 3);

        view.setButton('A', ChestButton.of(new ItemStack(Items.EMERALD_BLOCK), "§a§lCreate Dominion", () -> {
            CreateDominionInputter.createOn(player.createCommandSourceStack());
        }));
        view.setButton('B', ChestButton.of(new ItemStack(Items.BOOK), "§e§lMy Dominions", () -> {
            DominionList.show(player.createCommandSourceStack(), "1");
        }));
        view.setButton('C', ChestButton.of(new ItemStack(Items.PAPER), "§b§lTemplates", () -> {
            TemplateList.show(player.createCommandSourceStack(), "1");
        }));
        view.setButton('D', ChestButton.of(new ItemStack(Items.COMPASS), "§d§lInfo", () -> {
            Info.show(player.createCommandSourceStack(), "0");
        }));
        view.setButton('E', ChestButton.of(new ItemStack(Items.REDSTONE), "§c§lSwitch to TUI", () -> {
            try {
                cn.lunadeer.dominion.api.dtos.PlayerDTO dto = cn.lunadeer.dominion.cache.CacheManager.instance.getPlayer(player.getUUID());
                if (dto != null) {
                    dto.setUiPreference(cn.lunadeer.dominion.api.dtos.PlayerDTO.UI_TYPE.TUI);
                    showTUI(player, args);
                }
            } catch (Exception e) { Notification.error(player.createCommandSourceStack(), e); }
        }));

        view.setLayout("ABCD#E##############################################");
        view.open(player);
    }

    @Override
    protected void showConsole(CommandSourceStack sender, String... args) {
        Notification.info(sender, "§6=== Dominion Menu ===");
        Notification.info(sender, "§e/dominion create <name> - Create a dominion");
        Notification.info(sender, "§e/dominion menu - Open this menu in-game");
    }
}
