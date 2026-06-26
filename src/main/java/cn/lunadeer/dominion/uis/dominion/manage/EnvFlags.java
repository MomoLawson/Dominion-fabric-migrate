package cn.lunadeer.dominion.uis.dominion.manage;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.uis.AbstractUI;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class EnvFlags extends AbstractUI {
    public static void show(CommandSourceStack source, String domId, String page) {
        new EnvFlags().displayByPreference(source, domId, page);
    }

    @Override
    protected void showTUI(ServerPlayer player, String... args) throws Exception {
        int domId = Integer.parseInt(args[0]);
        DominionDTO dominion = CacheManager.instance.getDominion(domId);
        if (dominion == null) return;
        ListView listView = new ListView("Env Flags: " + dominion.getName(), "/dominion set_env", args[1]);
        for (EnvFlag flag : Flags.getEnvFlags()) {
            boolean val = dominion.isEnvFlag(flag.getFlagName());
            listView.addLine(new Line(new FunctionalButton(flag.getDisplayName() + ": " + val) {
                @Override public void function() {
                    dominion.setEnvFlag(flag.getFlagName(), !val);
                    show(player.getCommandSource(), args[0], args[1]);
                }
            }));
        }
        listView.show(player);
    }

    @Override protected void showCUI(ServerPlayer player, String... args) throws Exception { showTUI(player, args); }
    @Override protected void showConsole(CommandSourceStack sender, String... args) throws Exception {}
}
