package cn.lunadeer.dominion.utils.VaultConnect;

import java.util.UUID;

public interface VaultInterface {
    double getBalance(UUID playerUuid);
    boolean withdraw(UUID playerUuid, double amount);
    boolean deposit(UUID playerUuid, double amount);
}
