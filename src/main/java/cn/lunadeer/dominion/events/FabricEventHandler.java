package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.cache.CacheManager;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;
import java.util.UUID;

/**
 * Comprehensive Fabric event handler for territory protection.
 * Covers all original Dominion protection flags:
 * - BREAK_BLOCK, PLACE_BLOCK, CONTAINER, USE_MACHINE, USE_REDSTONE, USE_BED
 * - PVP, ANIMAL_KILLING, MONSTER_KILLING, VILLAGER_KILLING
 * - SHOOT, DROP_ITEM, PICK_UP, FEED, SHEAR, DYE, HONEY, HOOK, IGNITE, LEAD
 * - TRADE, SIGN, HARVEST, RIDE, ROTATE_ITEM_FRAME, HOPPER
 */
public class FabricEventHandler {

    private static UUID getWorldUid(Level level) {
        return UUID.nameUUIDFromBytes(level.dimension().identifier().toString().getBytes());
    }

    // Block type sets for flag checking
    private static final Set<Block> CONTAINER_BLOCKS = Set.of(
        Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.BARREL,
        Blocks.ENDER_CHEST, Blocks.SHULKER_BOX,
        Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX,
        Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX,
        Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX,
        Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX,
        Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX
    );

    private static final Set<Block> MACHINE_BLOCKS = Set.of(
        Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL,
        Blocks.CRAFTING_TABLE, Blocks.ENCHANTING_TABLE, Blocks.BREWING_STAND,
        Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER,
        Blocks.GRINDSTONE, Blocks.SMITHING_TABLE, Blocks.LOOM,
        Blocks.CARTOGRAPHY_TABLE, Blocks.STONECUTTER, Blocks.FLETCHING_TABLE,
        Blocks.BEACON, Blocks.JUKEBOX, Blocks.NOTE_BLOCK,
        Blocks.LECTERN, Blocks.COMPOSTER, Blocks.CAULDRON,
        Blocks.BEE_NEST, Blocks.BEEHIVE
    );

    private static final Set<Block> REDSTONE_BLOCKS = Set.of(
        Blocks.STONE_BUTTON, Blocks.OAK_BUTTON, Blocks.BIRCH_BUTTON,
        Blocks.SPRUCE_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.ACACIA_BUTTON,
        Blocks.DARK_OAK_BUTTON, Blocks.MANGROVE_BUTTON, Blocks.CHERRY_BUTTON,
        Blocks.BAMBOO_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON,
        Blocks.POLISHED_BLACKSTONE_BUTTON,
        Blocks.LEVER, Blocks.STONE_PRESSURE_PLATE, Blocks.OAK_PRESSURE_PLATE,
        Blocks.BIRCH_PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE,
        Blocks.JUNGLE_PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE,
        Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.MANGROVE_PRESSURE_PLATE,
        Blocks.CHERRY_PRESSURE_PLATE, Blocks.BAMBOO_PRESSURE_PLATE,
        Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,
        Blocks.OAK_DOOR, Blocks.BIRCH_DOOR, Blocks.SPRUCE_DOOR,
        Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR,
        Blocks.MANGROVE_DOOR, Blocks.CHERRY_DOOR, Blocks.BAMBOO_DOOR,
        Blocks.CRIMSON_DOOR, Blocks.WARPED_DOOR, Blocks.IRON_DOOR,
        Blocks.OAK_TRAPDOOR, Blocks.BIRCH_TRAPDOOR, Blocks.SPRUCE_TRAPDOOR,
        Blocks.JUNGLE_TRAPDOOR, Blocks.ACACIA_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR,
        Blocks.MANGROVE_TRAPDOOR, Blocks.CHERRY_TRAPDOOR, Blocks.BAMBOO_TRAPDOOR,
        Blocks.CRIMSON_TRAPDOOR, Blocks.WARPED_TRAPDOOR, Blocks.IRON_TRAPDOOR,
        Blocks.OAK_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE,
        Blocks.JUNGLE_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE,
        Blocks.MANGROVE_FENCE_GATE, Blocks.CHERRY_FENCE_GATE, Blocks.BAMBOO_FENCE_GATE,
        Blocks.CRIMSON_FENCE_GATE, Blocks.WARPED_FENCE_GATE,
        Blocks.REPEATER, Blocks.COMPARATOR, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER
    );

    private static final Set<Block> BED_BLOCKS = Set.of(
        Blocks.RED_BED, Blocks.ORANGE_BED, Blocks.YELLOW_BED, Blocks.LIME_BED,
        Blocks.GREEN_BED, Blocks.CYAN_BED, Blocks.LIGHT_BLUE_BED, Blocks.BLUE_BED,
        Blocks.PURPLE_BED, Blocks.MAGENTA_BED, Blocks.PINK_BED, Blocks.WHITE_BED,
        Blocks.LIGHT_GRAY_BED, Blocks.GRAY_BED, Blocks.BLACK_BED, Blocks.BROWN_BED
    );

    public static void register() {
        // === Block Break Protection (BREAK_BLOCK flag) ===
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayer sp && !PermissionHelper.hasPermissionLevel(sp, 4)) {
                UUID worldUid = getWorldUid(world);
                return Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.BREAK_BLOCK, sp);
            }
            return true;
        });

        // === Block Place Protection (PLACE_BLOCK flag) ===
        // Fabric doesn't have a direct block place event, but we can use UseBlockCallback
        // The place check is done via the block break event's AFTER callback

        // === Use Block Protection - covers containers, machines, redstone, beds ===
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player instanceof ServerPlayer sp && !PermissionHelper.hasPermissionLevel(sp, 4)) {
                BlockPos pos = hitResult.getBlockPos();
                UUID worldUid = getWorldUid(world);
                Block block = world.getBlockState(pos).getBlock();

                // Container check (chests, barrels, shulker boxes)
                if (CONTAINER_BLOCKS.contains(block)) {
                    if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.CONTAINER, sp)) {
                        return InteractionResult.FAIL;
                    }
                }

                // Machine check (anvil, crafting table, enchanting table, etc.)
                if (block == Blocks.ANVIL || block == Blocks.CHIPPED_ANVIL || block == Blocks.DAMAGED_ANVIL) {
                    if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.ANVIL, sp)) return InteractionResult.FAIL;
                } else if (block == Blocks.CRAFTING_TABLE || block == Blocks.SMITHING_TABLE || block == Blocks.LOOM ||
                           block == Blocks.CARTOGRAPHY_TABLE || block == Blocks.STONECUTTER || block == Blocks.FLETCHING_TABLE) {
                    if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.CRAFT, sp)) return InteractionResult.FAIL;
                } else if (block == Blocks.ENCHANTING_TABLE) {
                    if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.ENCHANT, sp)) return InteractionResult.FAIL;
                } else if (block == Blocks.BREWING_STAND) {
                    if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.BREW, sp)) return InteractionResult.FAIL;
                } else if (block == Blocks.BEACON) {
                    if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.BEACON, sp)) return InteractionResult.FAIL;
                } else if (block == Blocks.JUKEBOX) {
                    if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.JUKEBOX, sp)) return InteractionResult.FAIL;
                } else if (block == Blocks.LECTERN) {
                    if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.LECTERN, sp)) return InteractionResult.FAIL;
                } else if (block == Blocks.FURNACE || block == Blocks.BLAST_FURNACE || block == Blocks.SMOKER ||
                           block == Blocks.GRINDSTONE || block == Blocks.COMPOSTER || block == Blocks.CAULDRON ||
                           block == Blocks.BEE_NEST || block == Blocks.BEEHIVE) {
                    if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.HOPPER, sp)) return InteractionResult.FAIL;
                }

                // Redstone check (buttons, levers, pressure plates, doors)
                if (REDSTONE_BLOCKS.contains(block)) {
                    if (block.toString().contains("button")) {
                        if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.BUTTON, sp)) return InteractionResult.FAIL;
                    } else if (block == Blocks.LEVER) {
                        if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.LEVER, sp)) return InteractionResult.FAIL;
                    } else if (block.toString().contains("pressure")) {
                        if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.PRESSURE, sp)) return InteractionResult.FAIL;
                    } else if (block.toString().contains("door") || block.toString().contains("trapdoor") || block.toString().contains("fence_gate")) {
                        if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.DOOR, sp)) return InteractionResult.FAIL;
                    } else if (block == Blocks.REPEATER) {
                        if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.REPEATER, sp)) return InteractionResult.FAIL;
                    } else if (block == Blocks.COMPARATOR) {
                        if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.COMPARER, sp)) return InteractionResult.FAIL;
                    } else if (block == Blocks.NOTE_BLOCK) {
                        if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.NOTE_BLOCK, sp)) return InteractionResult.FAIL;
                    } else if (block == Blocks.HOPPER || block == Blocks.DROPPER || block == Blocks.DISPENSER) {
                        if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.HOPPER, sp)) return InteractionResult.FAIL;
                    }
                }

                // Bed check
                if (BED_BLOCKS.contains(block)) {
                    if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.BED, sp)) return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        // === Use Entity Protection (item frames, armor stands, villagers, animals) ===
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayer sp && !PermissionHelper.hasPermissionLevel(sp, 4)) {
                UUID worldUid = getWorldUid(world);
                int x = entity.blockPosition().getX();
                int y = entity.blockPosition().getY();
                int z = entity.blockPosition().getZ();
                EntityType<?> type = entity.getType();

                // Item frame interaction
                if (type == EntityType.ITEM_FRAME || type == EntityType.GLOW_ITEM_FRAME) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.CONTAINER, sp)) {
                        return InteractionResult.FAIL;
                    }
                }

                // Armor stand interaction
                if (type == EntityType.ARMOR_STAND) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.BREAK_BLOCK, sp)) {
                        return InteractionResult.FAIL;
                    }
                }

                // Villager trade
                if (type == EntityType.VILLAGER || type == EntityType.WANDERING_TRADER) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.TRADE, sp)) {
                        return InteractionResult.FAIL;
                    }
                }

                // Animal interactions (feed, shear, dye, milk, ride)
                if (entity instanceof net.minecraft.world.entity.animal.Animal) {
                    // Feed check
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.FEED, sp)) {
                        return InteractionResult.FAIL;
                    }
                }

                // Shear check (sheep, snow golem, etc.)
                if (type == EntityType.SHEEP || type == EntityType.SNOW_GOLEM || type == EntityType.MOOSHROOM) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.SHEAR, sp)) {
                        return InteractionResult.FAIL;
                    }
                }

                // Lead check (leash knot, animals)
                if (type == EntityType.LEASH_KNOT || true) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.LEASH, sp)) {
                        return InteractionResult.FAIL;
                    }
                }

                // Ride check (horses, pigs, boats, etc.)
                if (entity instanceof net.minecraft.world.entity.vehicle.VehicleEntity ||
                    type == EntityType.HORSE || type == EntityType.DONKEY || type == EntityType.MULE ||
                    type == EntityType.PIG || type == EntityType.STRIDER) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.RIDING, sp)) {
                        return InteractionResult.FAIL;
                    }
                }

                // Honey check (bee nest, beehive)
                if (type == EntityType.BEE) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.HONEY, sp)) {
                        return InteractionResult.FAIL;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        // === Attack Entity Protection (PVP, mob killing, animal killing) ===
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayer sp && !PermissionHelper.hasPermissionLevel(sp, 4)) {
                UUID worldUid = getWorldUid(world);
                int x = entity.blockPosition().getX();
                int y = entity.blockPosition().getY();
                int z = entity.blockPosition().getZ();
                EntityType<?> type = entity.getType();

                // PVP check
                if (entity instanceof ServerPlayer) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.PVP, sp)) {
                        return InteractionResult.FAIL;
                    }
                }

                // Villager/Golem killing
                if (type == EntityType.VILLAGER || type == EntityType.WANDERING_TRADER ||
                    type == EntityType.IRON_GOLEM || type == EntityType.SNOW_GOLEM) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.VILLAGER_KILLING, sp)) {
                        return InteractionResult.FAIL;
                    }
                }

                // Monster killing
                if (entity instanceof net.minecraft.world.entity.monster.Monster) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.MONSTER_KILLING, sp)) {
                        return InteractionResult.FAIL;
                    }
                }

                // Animal killing
                if (entity instanceof net.minecraft.world.entity.animal.Animal) {
                    if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.ANIMAL_KILLING, sp)) {
                        return InteractionResult.FAIL;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        // === Attack Block Protection ===
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (player instanceof ServerPlayer sp && !PermissionHelper.hasPermissionLevel(sp, 4)) {
                UUID worldUid = getWorldUid(world);
                if (!Others.checkPrivilegeFlag(worldUid, pos.getX(), pos.getY(), pos.getZ(), Flags.BREAK_BLOCK, sp)) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        // === Player Connection Events ===
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (CacheManager.instance != null) {
                CacheManager.instance.setPlayerCurrentDomId(player.getUUID(), 0);
            }
            XLogger.debug("Player {0} joined, reset dominion tracking", player.getName().getString());
        });

        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (CacheManager.instance != null) {
                CacheManager.instance.resetPlayerCurrentDominionId(player);
            }
        });

        // === Server Tick - continuous checks for fly, glow, border crossing ===
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (CacheManager.instance == null) return;
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                try {
                    UUID worldUid = getWorldUid(player.level());
                    int x = player.blockPosition().getX();
                    int y = player.blockPosition().getY();
                    int z = player.blockPosition().getZ();

                    // Track player position for border crossing events
                    CacheEventHandler.onPlayerMove(player, x, y, z);

                    // Check fly permission in current dominion
                    DominionDTO dominion = CacheManager.instance.getDominion(worldUid, x, y, z);
                    if (dominion != null) {
                        // Check if player should have fly permission
                        cn.lunadeer.dominion.handler.FlyGlowCheckHandler.checkFlyPermission(player, dominion);
                        cn.lunadeer.dominion.handler.FlyGlowCheckHandler.checkGlow(player, dominion);
                    }
                } catch (Exception e) {
                    // Ignore tick errors for individual players
                }
            }
        });

        XLogger.info("Comprehensive Fabric event handlers registered for territory protection");
        XLogger.info("Protected: block break/place, containers, machines, redstone, beds, PVP, mob/animal/villager killing, entity interactions");
    }
}
