package cn.lunadeer.dominion.utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
public class Notification {
    public static void info(CommandSourceStack s, String m) { s.sendSuccess(() -> Component.literal(m), false); }
    public static void info(CommandSourceStack s, String m, Object... a) { info(s, format(m, a)); }
    public static void error(CommandSourceStack s, String m) { s.sendFailure(Component.literal("§c" + m)); }
    public static void error(CommandSourceStack s, Exception e) { error(s, e.getMessage()); }
    public static void error(CommandSourceStack s, String m, Object... a) { error(s, format(m, a)); }
    public static void sendActionBar(ServerPlayer p, String m) { p.sendSystemMessage(Component.literal(m)); }
    private static String format(String m, Object... a) { for (int i = 0; i < a.length; i++) m = m.replace("{" + i + "}", String.valueOf(a[i])); return m; }
}
