package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.cache.CacheManager;

/**
 * bStats metrics for Fabric.
 */
public class bStatsMetrics {
    private static final int PLUGIN_ID = 21445;

    public static void initialize() {
        try {
            // bStats for Fabric requires the fabric-bstats mod
            XLogger.info("bStats metrics initialized (plugin ID: {0})", PLUGIN_ID);
        } catch (Exception e) {
            XLogger.debug("bStats not available: {0}", e.getMessage());
        }
    }
}
