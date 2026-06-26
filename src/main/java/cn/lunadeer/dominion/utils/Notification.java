package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.Dominion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import static cn.lunadeer.dominion.utils.Misc.formatString;
import static cn.lunadeer.dominion.utils.XLogger.isDebug;

/**
 * Notification utility class.
 * Ported from Bukkit (CommandSender + Adventure) to Fabric (Minecraft Server + Text).
 */
public class Notification {

    public static String prefix = "[Dominion]";

    /**
     * Sends an info message to a player or console.
     * For Fabric, server console messages go to SLF4J, player messages go via Text.
     */
    public static void info(ServerPlayer player, String msg) {
        if (player != null) {
            player.sendMessage(Component.literal(stripColor(prefix + " " + msg)));
        }
    }

    public static void info(ServerPlayer player, String msg, Object... args) {
        info(player, formatString(msg, args));
    }

    /**
     * Console info - logs via XLogger.
     */
    public static void info(MinecraftServer server, String msg) {
        XLogger.info(msg);
    }

    public static void info(MinecraftServer server, String msg, Object... args) {
        info(server, formatString(msg, args));
    }

    public static void warn(ServerPlayer player, String msg) {
        if (player != null) {
            player.sendMessage(Component.literal(stripColor(prefix + " " + msg)));
        }
    }

    public static void warn(ServerPlayer player, String msg, Object... args) {
        warn(player, formatString(msg, args));
    }

    public static void warn(MinecraftServer server, String msg) {
        XLogger.warn(msg);
    }

    public static void warn(MinecraftServer server, String msg, Object... args) {
        warn(server, formatString(msg, args));
    }

    public static void error(ServerPlayer player, String msg) {
        if (player != null) {
            player.sendMessage(Component.literal(stripColor(prefix + " " + msg)));
        }
    }

    public static void error(ServerPlayer player, String msg, Object... args) {
        error(player, formatString(msg, args));
    }

    public static void error(ServerPlayer player, Throwable e) {
        error(player, e.getMessage());
        if (isDebug()) {
            XLogger.error(e);
        }
    }

    public static void error(MinecraftServer server, String msg) {
        XLogger.error(msg);
    }

    public static void error(MinecraftServer server, String msg, Object... args) {
        error(server, formatString(msg, args));
    }

    /**
     * Broadcast a message to all online players.
     */
    public static void all(MinecraftServer server, String msg) {
        XLogger.info(msg);
        for (ServerPlayer player : server.getPlayerList().getPlayerList()) {
            info(player, msg);
        }
    }

    public static void all(MinecraftServer server, String msg, Object... args) {
        all(server, formatString(msg, args));
    }

    /**
     * Send action bar message to player.
     */
    public static void actionBar(ServerPlayer player, String msg) {
        if (player != null) {
            player.sendMessage(Component.literal(stripColor(msg)), true); // true = action bar
        }
    }

    public static void actionBar(ServerPlayer player, String msg, Object... args) {
        actionBar(player, formatString(msg, args));
    }

    /**
     * Strip color codes (&-style) for plain text.
     */
    private static String stripColor(String message) {
        if (message == null) return null;
        return message.replaceAll("&[0-9a-fk-orA-FK-OR]", "");
    }
}
