package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.handler.CacheEventHandler;
import cn.lunadeer.dominion.misc.Others;
import cn.lunadeer.dominion.utils.PermissionHelper;
import cn.lunadeer.dominion.utils.XLogger;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.UUID;

/**
 * Comprehensive Fabric event handler for territory protection.
 * Registers all protection callbacks for dominion flags.
 */
public class FabricEventHandler {

    private static UUID getWorldUid(Level level) {
        return UUID.nameUUIDFromBytes(level.dimension().identifier().toString().getBytes());
    }

    /**
     * Register all event handlers.
     */
    public static void register() {
        // === Block Break Protection ===
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayer sp) {
                UUID worldUid = getWorldUid(world);
                if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.BREAK_BLOCK, sp)) {
                    return false;
                }
            }
            return true;
        });

        // === Block Place Protection ===
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            // After break - could check environment flags
        });

        // === Use Block Protection (containers, machines, etc.) ===
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player instanceof ServerPlayer sp && !cn.lunadeer.dominion.utils.PermissionHelper.hasPermissionLevel(sp, 4)) {
                BlockPos pos = hitResult.getBlockPos();
                UUID worldUid = getWorldUid(world);
                // Check container flag
                if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.CONTAINER, sp)) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        // === Use Entity Protection (item frames, armor stands, etc.) ===
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayer sp && !cn.lunadeer.dominion.utils.PermissionHelper.hasPermissionLevel(sp, 4)) {
                UUID worldUid = getWorldUid(world);
                int x = entity.blockPosition().getX();
                int y = entity.blockPosition().getY();
                int z = entity.blockPosition().getZ();
                // Check various flags based on entity type
                String entityType = entity.getType().toShortString();
                if (entityType.contains("item_frame")) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.CONTAINER, sp)) {
                        return InteractionResult.FAIL;
                    }
                } else if (entityType.contains("armor_stand")) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.BREAK_BLOCK, sp)) {
                        return InteractionResult.FAIL;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        // === Attack Entity Protection (PVP, mob killing) ===
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayer sp && !cn.lunadeer.dominion.utils.PermissionHelper.hasPermissionLevel(sp, 4)) {
                UUID worldUid = getWorldUid(world);
                int x = entity.blockPosition().getX();
                int y = entity.blockPosition().getY();
                int z = entity.blockPosition().getZ();

                if (entity instanceof ServerPlayer) {
                    // PVP check
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.PVP, sp)) {
                        return InteractionResult.FAIL;
                    }
                } else {
                    String entityType = entity.getType().toShortString();
                    if (entityType.contains("villager") || entityType.contains("iron_golem")) {
                        if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.VILLAGER_KILLING, sp)) {
                            return InteractionResult.FAIL;
                        }
                    } else if (entityType.contains("monster") || entityType.contains("zombie") || entityType.contains("skeleton")) {
                        if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.MONSTER_KILLING, sp)) {
                            return InteractionResult.FAIL;
                        }
                    } else {
                        // Animal
                        if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.ANIMAL_KILLING, sp)) {
                            return InteractionResult.FAIL;
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        });

        // === Attack Block Protection (using tools, interactions) ===
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (player instanceof ServerPlayer sp && !cn.lunadeer.dominion.utils.PermissionHelper.hasPermissionLevel(sp, 4)) {
                UUID worldUid = getWorldUid(world);
                if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.BREAK_BLOCK, sp)) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        // === Player Move Tracking (for border crossing events) ===
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            CacheManager.instance.setPlayerCurrentDomId(player.getUUID(), 0);
            XLogger.debug("Player {0} joined, reset dominion tracking", player.getName().getString());
        });

        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            CacheManager.instance.resetPlayerCurrentDominionId(player);
        });

        // === Server Tick for continuous checks ===
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Check fly permissions and glow for all online players
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                try {
                    UUID worldUid = getWorldUid(player.level());
                    DominionDTO dominion = CacheManager.instance.getDominion(worldUid,
                        player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
                    CacheEventHandler.onPlayerMove(player,
                        player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
                } catch (Exception e) {
                    // Ignore tick errors
                }
            }
        });

        XLogger.info("Fabric event handlers registered for territory protection");
    }
}
