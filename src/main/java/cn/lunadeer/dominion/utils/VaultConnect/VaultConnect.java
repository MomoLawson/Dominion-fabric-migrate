package cn.lunadeer.dominion.utils.VaultConnect;

import cn.lunadeer.dominion.utils.XLogger;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Economy integration for Fabric.
 * Since there's no standard Vault equivalent on Fabric, this provides
 * a simple interface that can be adapted to various economy mods.
 */
public class VaultConnect {
    private static VaultInterface economy = null;
    private static boolean enabled = false;

    public static void initialize() {
        // Fabric has no standard economy API like Vault
        // Server operators can implement VaultInterface for their economy mod
        enabled = false;
        XLogger.info("Economy integration disabled (no Vault equivalent on Fabric)");
    }

    public static boolean isEnabled() { return enabled; }
    public static VaultInterface getEconomy() { return economy; }

    public static double getBalance(java.util.UUID playerUuid) {
        if (!enabled || economy == null) return 0;
        return economy.getBalance(playerUuid);
    }

    public static boolean withdrawPlayer(java.util.UUID playerUuid, double amount) {
        if (!enabled || economy == null) return false;
        return economy.withdraw(playerUuid, amount);
    }

    public static boolean depositPlayer(java.util.UUID playerUuid, double amount) {
        if (!enabled || economy == null) return false;
        return economy.deposit(playerUuid, amount);
    }

    public static String currencyNamePlural() { return "coins"; }
    public static String currencyNameSingular() { return "coin"; }
}
