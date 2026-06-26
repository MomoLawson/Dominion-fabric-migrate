package cn.lunadeer.dominion.hooks;

import cn.lunadeer.dominion.utils.XLogger;

/**
 * WorldGuard hook - no direct equivalent on Fabric.
 * Stubbed for compatibility.
 */
public class WorldGuardHook {
    public static void initialize() {
        XLogger.info("WorldGuard integration not available on Fabric (stubbed)");
    }

    public static boolean isConflict(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        return false; // No WorldGuard on Fabric
    }
}
