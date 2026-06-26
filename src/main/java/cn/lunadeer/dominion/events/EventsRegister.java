package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.handler.*;
import cn.lunadeer.dominion.utils.XLogger;

/**
 * Registers all event handlers for Fabric.
 * In Fabric, we register callbacks directly instead of using Bukkit's @EventHandler.
 */
public class EventsRegister {

    public EventsRegister() {
        try {
            XLogger.info("Registering Dominion event handlers...");
            new DominionProviderHandler();
            new MemberProviderHandler();
            new GroupProviderHandler();
            new SelectPointEventsHandler();
            new FlyGlowCheckHandler();
            new FlagRegisterHandler();
            new WorldLoadHandler();
            FabricEventHandler.register();
            XLogger.info("All event handlers registered successfully");
        } catch (Exception e) {
            XLogger.error("Failed to register events: {0}", e.getMessage());
        }
    }
}
