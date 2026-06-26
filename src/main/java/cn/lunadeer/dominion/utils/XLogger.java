package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.Dominion;
import org.slf4j.Logger;

import static cn.lunadeer.dominion.utils.Misc.formatString;

/**
 * Logging utility class.
 * Ported from Bukkit (ConsoleCommandSender-based) to Fabric (SLF4J-based).
 */
public class XLogger {
    public static XLogger instance;

    private final Logger logger;
    private boolean debug = false;

    public XLogger(Logger logger) {
        instance = this;
        this.logger = logger;
    }

    public static XLogger setDebug(boolean debug) {
        instance.debug = debug;
        return instance;
    }

    public static boolean isDebug() {
        return instance.debug;
    }

    public static void info(String message) {
        instance.logger.info("[Dominion] {}", stripColor(message));
    }

    public static void warn(String message) {
        instance.logger.warn("[Dominion] {}", stripColor(message));
    }

    public static void error(String message) {
        instance.logger.error("[Dominion] {}", stripColor(message));
    }

    public static void debug(String message) {
        if (!instance.debug) return;
        instance.logger.debug("[Dominion] {}", stripColor(message));
    }

    public static void info(String message, Object... args) {
        info(formatString(message, args));
    }

    public static void warn(String message, Object... args) {
        warn(formatString(message, args));
    }

    public static void error(String message, Object... args) {
        error(formatString(message, args));
    }

    public static void error(Throwable e) {
        error(e.getMessage());
        if (isDebug()) {
            for (StackTraceElement element : e.getStackTrace()) {
                error("StackTrace | " + element.toString());
            }
        }
    }

    public static void debug(String message, Object... args) {
        debug(formatString(message, args));
    }

    /**
     * Strip color codes (&-style) from a string for plain log output.
     */
    private static String stripColor(String message) {
        if (message == null) return null;
        return message.replaceAll("&[0-9a-fk-orA-FK-OR]", "");
    }
}
