package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.utils.PermissionHelper;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.misc.Others;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.Misc;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static cn.lunadeer.dominion.Dominion.adminPermission;

/**
 * Teleport manager for dominion teleportation.
 * Ported from Bukkit to Fabric.
 *
 * Handles cooldown, delay, and safe teleportation logic.
 */
public class TeleportManager {

    public static class TeleportManagerText extends ConfigurationPart {
        public String coolingDown = "Please wait for {0} seconds before teleporting again.";
        public String disabled = "Teleportation is disabled for your permission group.";
        public String delay = "Will teleport in {0} seconds, don't move...";
        public String unfinishedCancelled = "Cancelled previous unfinished teleportation.";
        public String cancelMove = "Cancelled teleportation due to movement.";
    }

    private final MinecraftServer server;

    public TeleportManager(MinecraftServer server) {
        this.server = server;
        // Register event listeners for player movement detection
        // In Fabric, this is done via ServerPlayConnectionEvents or tick callbacks
        // TODO: Register movement event handler when event system is ported
    }

    public static Map<UUID, Long> teleportCooldown = new HashMap<>();
    public static Map<UUID, Runnable> teleportDelayTasks = new HashMap<>();

    /**
     * Check if a player is on teleport cooldown.
     */
    public static boolean isCoolingDown(UUID playerUUID, int cooldownSeconds) {
        if (cooldownSeconds <= 0) return false;
        long currentTs = System.currentTimeMillis() / 1000;
        if (teleportCooldown.containsKey(playerUUID)) {
            return teleportCooldown.get(playerUUID) > currentTs;
        }
        return false;
    }

    /**
     * Get remaining cooldown time in seconds.
     */
    public static int getRemainingCooldown(UUID playerUUID) {
        long currentTs = System.currentTimeMillis() / 1000;
        if (teleportCooldown.containsKey(playerUUID)) {
            return (int) Math.max(0, teleportCooldown.get(playerUUID) - currentTs);
        }
        return 0;
    }

    /**
     * Set cooldown for a player.
     */
    public static void setCooldown(UUID playerUUID, int cooldownSeconds) {
        long currentTs = System.currentTimeMillis() / 1000;
        teleportCooldown.put(playerUUID, currentTs + cooldownSeconds);
    }

    /**
     * Teleport a player to a specified location safely.
     * Finds the nearest safe location to avoid suffocation.
     *
     * @param player The player to teleport
     * @param world  The target world
     * @param x      Target X coordinate
     * @param y      Target Y coordinate
     * @param z      Target Z coordinate
     */
    public static void doTeleportSafely(ServerPlayer player, ServerLevel world, double x, double y, double z) {
        // Find a safe Y position (search up from target for air blocks)
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(x, y, z);
        // Simple safe location search - find air above the target
        for (int searchY = (int) y; searchY < world.getMaxBuildHeight(); searchY++) {
            mutablePos.set((int) x, searchY, (int) z);
            if (world.isAir(mutablePos) && world.isAir(mutablePos.up())) {
                // Safe to teleport here
                player.teleport(world, x, searchY, z, player.getYRot(), player.getXRot());
                return;
            }
        }
        // Fallback: teleport to the exact coordinates
        player.teleport(world, x, y, z, player.getYRot(), player.getXRot());
    }

    /**
     * Teleport a player to a specified location with delay and cooldown.
     *
     * @param player   The player to teleport
     * @param world    The target world
     * @param x        Target X coordinate
     * @param y        Target Y coordinate
     * @param z        Target Z coordinate
     */
    public static void teleportWithDelay(ServerPlayer player, ServerLevel world, double x, double y, double z) {
        int delaySec = Configuration.getPlayerLimitation(player.getUUID(), java.util.List.of()).teleportation.delay;
        // TODO: Check admin bypass permission via LuckPerms

        // Cancel existing delayed task
        Runnable existingTask = teleportDelayTasks.remove(player.getUUID());
        if (existingTask != null) {
            Notification.warn(player, Language.teleportManagerText.unfinishedCancelled);
        }

        if (delaySec > 0) {
            Notification.info(player, Language.teleportManagerText.delay, delaySec);
        }

        // Schedule the teleportation
        // In Fabric, we use server tick scheduling
        // For now, execute immediately (delay will be implemented with tick scheduling)
        // TODO: Implement proper delayed teleportation with server tick scheduler
        doTeleportSafely(player, world, x, y, z);
    }

    /**
     * Teleports a player to a dominion's teleport location.
     *
     * @param player   The player to be teleported.
     * @param dominion The dominion to which the player will be teleported.
     */
    public static void teleportToDominion(ServerPlayer player, DominionDTO dominion) {
        // Check privilege
        if (!Configuration.getPlayerLimitation(player.getUUID(), java.util.List.of()).teleportation.enable &&
                !(PermissionHelper.hasPermissionLevel(player, 4) && Configuration.adminBypass)) {
            Notification.warn(player, Language.teleportManagerText.disabled);
            return;
        }
        // If tp flag is disabled, return too
        if (!Flags.TELEPORT.getEnable()) {
            Notification.warn(player, Language.teleportManagerText.disabled);
            return;
        }
        if (!Others.checkPrivilegeFlagSilence(dominion, Flags.TELEPORT, player)) {
            return;
        }
        boolean needCooldown = Configuration.getPlayerLimitation(player.getUUID(), java.util.List.of()).teleportation.cooldown > 0;
        int delaySec = Configuration.getPlayerLimitation(player.getUUID(), java.util.List.of()).teleportation.delay;
        if (PermissionHelper.hasPermissionLevel(player, 4) && Configuration.adminBypass) {
            needCooldown = false;
            delaySec = 0;
        }
        // Cooldown
        if (needCooldown) {
            int currentTs = (int) (System.currentTimeMillis() / 1000);
            if (teleportCooldown.containsKey(player.getUUID())) {
                if (teleportCooldown.get(player.getUUID()) > currentTs) {
                    Notification.warn(player, Language.teleportManagerText.coolingDown, teleportCooldown.get(player.getUUID()) - currentTs);
                    return;
                }
            }
        }
        // Cancel existing delayed task
        Runnable existingTask = teleportDelayTasks.remove(player.getUUID());
        if (existingTask != null) {
            Notification.warn(player, Language.teleportManagerText.unfinishedCancelled);
        }
        // Get teleport location
        int[] tpLoc = dominion.getTpLocation();
        if (tpLoc == null || tpLoc.length < 3) {
            // Fall back to dominion center
            tpLoc = new int[]{
                    (dominion.getCuboid().x1() + dominion.getCuboid().x2()) / 2,
                    dominion.getCuboid().y1(),
                    (dominion.getCuboid().z1() + dominion.getCuboid().z2()) / 2
            };
        }
        ServerLevel world = dominion.level();
        if (world == null) {
            // Try to resolve world from worldUid
            try {
                String worldUid = dominion.getWorldUid().toString();
                net.minecraft.util.Identifier worldId = net.minecraft.util.Identifier.tryParse(worldUid);
                if (worldId != null) {
                    var worldKey = net.minecraft.registry.ResourceKey.of(net.minecraft.registry.Registries.WORLD, worldId);
                    world = player.level().getServer().getLevel(worldKey);
                }
            } catch (Exception ignored) {
            }
            if (world == null) {
                Notification.warn(player, "Cannot find the world for this dominion.");
                return;
            }
        }
        if (delaySec > 0) {
            Notification.info(player, Language.teleportManagerText.delay, delaySec);
        }
        // For now, teleport immediately (delay scheduling TODO)
        doTeleportSafely(player, world, tpLoc[0], tpLoc[1], tpLoc[2]);
        // Set cooldown
        if (needCooldown) {
            setCooldown(player.getUUID(), Configuration.getPlayerLimitation(player.getUUID(), java.util.List.of()).teleportation.cooldown);
        }
    }
}
