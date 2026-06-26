package cn.lunadeer.dominion.utils.scheduler;

/**
 * Bukkit Spigot task wrapper - stubbed for Fabric.
 * Use FabricTask instead.
 */
public class SpigotTask implements CancellableTask {
    private boolean cancelled = false;

    @Override public void cancel() { cancelled = true; }
    @Override public boolean isCancelled() { return cancelled; }
}
