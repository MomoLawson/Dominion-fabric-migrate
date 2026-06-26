package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.events.FabricEventBus;
import cn.lunadeer.dominion.utils.XLogger;

/**
 * Logs external flag registrations.
 * Ported from Bukkit Listener to Fabric callback.
 */
public class FlagRegisterHandler {

    public static void register() {
        FabricEventBus.FlagRegisterCallback.EVENT.register((modId, flag, registerAction) -> {
            XLogger.debug("External flag registered: " +
                    flag.getDisplayName() +
                    " (" + flag.getFlagName() + ")"
                    + " - From: " + modId);
            // Execute the actual registration
            registerAction.run();
        });
    }
}
