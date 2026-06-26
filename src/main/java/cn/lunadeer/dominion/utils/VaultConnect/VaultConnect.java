package cn.lunadeer.dominion.utils.VaultConnect;

import cn.lunadeer.dominion.utils.XLogger;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Economy integration for Fabric.
 * Since there's no standard Vault equivalent on Fabric, this provides:
 * 1. A built-in simple economy system (file-based balance tracking)
 * 2. An interface that can be adapted to external economy mods
 */
public class VaultConnect {
    private static VaultInterface economy = null;
    private static boolean enabled = false;
    private static final Map<UUID, Double> balances = new ConcurrentHashMap<>();
    private static Path dataFile;

    public static void initialize() {
        // Try to find an external economy mod
        if (FabricLoader.getInstance().isModLoaded("stonecutter")) {
            XLogger.info("Stonecutter economy detected");
            // Would integrate with Stonecutter API here
        }

        // Use built-in economy system
        economy = new BuiltInEconomy();
        enabled = true;
        loadData();
        XLogger.info("Built-in economy system enabled");
    }

    public static boolean isEnabled() { return enabled; }
    public static VaultInterface getEconomy() { return economy; }

    public static double getBalance(UUID playerUuid) {
        if (!enabled || economy == null) return 0;
        return economy.getBalance(playerUuid);
    }

    public static boolean withdrawPlayer(UUID playerUuid, double amount) {
        if (!enabled || economy == null) return false;
        boolean result = economy.withdraw(playerUuid, amount);
        if (result) saveData();
        return result;
    }

    public static boolean depositPlayer(UUID playerUuid, double amount) {
        if (!enabled || economy == null) return false;
        boolean result = economy.deposit(playerUuid, amount);
        if (result) saveData();
        return result;
    }

    public static String currencyNamePlural() { return "coins"; }
    public static String currencyNameSingular() { return "coin"; }

    /**
     * Set a player's balance directly (admin command).
     */
    public static void setBalance(UUID playerUuid, double amount) {
        balances.put(playerUuid, amount);
        saveData();
    }

    /**
     * Built-in economy implementation with file persistence.
     */
    private static class BuiltInEconomy implements VaultInterface {
        private static final double STARTING_BALANCE = 1000.0;

        @Override
        public double getBalance(UUID playerUuid) {
            return balances.getOrDefault(playerUuid, STARTING_BALANCE);
        }

        @Override
        public boolean withdraw(UUID playerUuid, double amount) {
            double current = getBalance(playerUuid);
            if (current < amount) return false;
            balances.put(playerUuid, current - amount);
            return true;
        }

        @Override
        public boolean deposit(UUID playerUuid, double amount) {
            double current = getBalance(playerUuid);
            balances.put(playerUuid, current + amount);
            return true;
        }
    }

    /**
     * Load balance data from file.
     */
    private static void loadData() {
        try {
            dataFile = FabricLoader.getInstance().getConfigDir().resolve("dominion/economy.dat");
            if (Files.exists(dataFile)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(dataFile.toFile()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 2) {
                            UUID uuid = UUID.fromString(parts[0].trim());
                            double balance = Double.parseDouble(parts[1].trim());
                            balances.put(uuid, balance);
                        }
                    }
                }
                XLogger.info("Loaded {0} player balances", balances.size());
            }
        } catch (Exception e) {
            XLogger.warn("Failed to load economy data: {0}", e.getMessage());
        }
    }

    /**
     * Save balance data to file.
     */
    private static void saveData() {
        try {
            if (dataFile == null) return;
            Files.createDirectories(dataFile.getParent());
            try (PrintWriter writer = new PrintWriter(new FileWriter(dataFile.toFile()))) {
                for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
                    writer.println(entry.getKey().toString() + "," + entry.getValue());
                }
            }
        } catch (Exception e) {
            XLogger.warn("Failed to save economy data: {0}", e.getMessage());
        }
    }
}
