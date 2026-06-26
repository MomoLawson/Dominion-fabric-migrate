package cn.lunadeer.dominion;

import cn.lunadeer.dominion.utils.XLogger;

/**
 * API implementation for Dominion.
 * Provides the public API interface for other mods to interact with Dominion.
 */
public class DominionInterface {
    private static DominionInterface instance;

    public DominionInterface() {
        instance = this;
        XLogger.info("Dominion API interface initialized");
    }

    public static DominionInterface getInstance() {
        return instance;
    }
}
