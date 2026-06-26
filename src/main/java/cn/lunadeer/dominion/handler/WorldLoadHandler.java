package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.utils.XLogger;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Handles world load events, running registered callbacks when worlds are loaded.
 * Ported from Bukkit WorldLoadEvent to Fabric ServerLifecycleEvents.
 */
public class WorldLoadHandler {

    private static WorldLoadHandler instance;

    public static WorldLoadHandler getInstance() {
        if (instance == null) {
            instance = new WorldLoadHandler();
        }
        return instance;
    }

    private final List<String> loadedWorlds = new ArrayList<>();
    private final List<Consumer<ServerLevel>> runners = new ArrayList<>();

    public WorldLoadHandler() {
        // Register for server started event to run on already-loaded worlds
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
    }

    private void onServerStarted(MinecraftServer server) {
        // Delay to let worlds finish loading, then run callbacks on all loaded worlds
        server.execute(() -> {
            for (ServerLevel world : server.getAllLevels()) {
                String worldKey = world.dimension().identifier().toString();
                if (loadedWorlds.contains(worldKey)) continue;
                loadedWorlds.add(worldKey);
                for (Consumer<ServerLevel> runner : runners) {
                    try {
                        runner.accept(world);
                    } catch (Exception e) {
                        XLogger.error(e);
                    }
                }
            }
        });
    }

    /**
     * Registers a callback to run when worlds are loaded.
     * Note: In Fabric, world loading is handled via ServerLifecycleEvents.
     * Additional world loads (dimension creation) would need Mixin hooks.
     */
    public void addRunner(Consumer<ServerLevel> runner) {
        runners.add(runner);
        // If server is already started, run on all current worlds
        if (Dominion.server != null) {
            for (ServerLevel world : Dominion.server.getAllLevels()) {
                String worldKey = world.dimension().identifier().toString();
                if (!loadedWorlds.contains(worldKey)) {
                    loadedWorlds.add(worldKey);
                }
                try {
                    runner.accept(world);
                } catch (Exception e) {
                    XLogger.error(e);
                }
            }
        }
    }
}
