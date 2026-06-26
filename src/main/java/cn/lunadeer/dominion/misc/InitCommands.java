package cn.lunadeer.dominion.misc;

import cn.lunadeer.dominion.commands.*;
import cn.lunadeer.dominion.utils.XLogger;

/**
 * Initializes all command registrations for Fabric.
 * Commands are registered via Brigadier in CommandManager.
 */
public class InitCommands {
    public static void initialize() {
        XLogger.info("Initializing Dominion commands...");
        // Commands are registered via CommandRegistrationCallback in CommandManager
    }
}
