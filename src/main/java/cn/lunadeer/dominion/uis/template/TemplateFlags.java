package cn.lunadeer.dominion.uis.template;
import cn.lunadeer.dominion.uis.AbstractUI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.CommandSourceStack;
public class TemplateFlags extends AbstractUI {
    public static void show(CommandSourceStack source, String... args) { new TemplateFlags().displayByPreference(source, args); }
    protected void showTUI(ServerPlayer player, String... args) {}
    protected void showCUI(ServerPlayer player, String... args) { showTUI(player, args); }
    protected void showConsole(CommandSourceStack sender, String... args) {}
}
