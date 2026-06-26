package cn.lunadeer.dominion.utils.VaultConnect;

import java.util.UUID;

public class Vault implements VaultInterface {
    @Override public double getBalance(UUID playerUuid) { return 0; }
    @Override public boolean withdraw(UUID playerUuid, double amount) { return false; }
    @Override public boolean deposit(UUID playerUuid, double amount) { return false; }
}
