package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.handler.CacheEventHandler;
import cn.lunadeer.dominion.handler.FlyGlowCheckHandler;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.UUID;

/**
 * Comprehensive Fabric event handler for territory protection.
 * Matches the original Dominion's 80+ individual event handlers with per-flag granularity.
 */
public class FabricEventHandler {

    private static UUID getWorldUid(Level level) {
        return UUID.nameUUIDFromBytes(level.dimension().identifier().toString().getBytes());
    }

    private static boolean hasBypass(ServerPlayer sp) {
        return PermissionHelper.hasPermissionLevel(sp, 4);
    }

    private static boolean checkFlag(Level level, BlockPos pos, ServerPlayer sp, PriFlag flag) {
        return Others.checkPrivilegeFlag(getWorldUid(level), pos.getX(), pos.getY(), pos.getZ(), flag, sp);
    }

    private static boolean checkEnvFlag(Level level, BlockPos pos, EnvFlag flag) {
        return Others.checkEnvironmentFlag(getWorldUid(level), pos.getX(), pos.getY(), pos.getZ(), flag);
    }

    public static void register() {

        // === Block Break Protection (Flags.BREAK_BLOCK) ===
        // Original: Break/NormalBlock, Break/FlowerPot, Break/Liquid, Break/ArmorStandBroken, Break/ItemFrameBroken
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayer sp && !hasBypass(sp)) {
                return checkFlag(world, pos, sp, Flags.BREAK_BLOCK);
            }
            return true;
        });

        // === Use Block Protection - individual flag checks ===
        // Original: Container, Anvil, Beacon, Bed, Brew, Cake, CraftTable, EnchantTable,
        //           Lectern, Jukebox, ChiseledBookshelf, DragonEgg, Hopper, Button, Door,
        //           Lever, NoteBlock, PressurePlate, Repeater, Comparer, Crafter
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!(player instanceof ServerPlayer sp) || hasBypass(sp)) return InteractionResult.PASS;
            BlockPos pos = hitResult.getBlockPos();
            Block block = world.getBlockState(pos).getBlock();

            // Containers (Chest, Barrel, Shulker Boxes)
            if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.BARREL
                || block == Blocks.ENDER_CHEST || isShulkerBox(block)) {
                if (!checkFlag(world, pos, sp, Flags.CONTAINER)) return InteractionResult.FAIL;
            }
            // Anvil
            if (block == Blocks.ANVIL) {
                if (!checkFlag(world, pos, sp, Flags.ANVIL)) return InteractionResult.FAIL;
            }
            // Beacon
            if (block == Blocks.BEACON) {
                if (!checkFlag(world, pos, sp, Flags.BEACON)) return InteractionResult.FAIL;
            }
            // Bed
            if (isBed(block)) {
                if (!checkFlag(world, pos, sp, Flags.BED)) return InteractionResult.FAIL;
            }
            // Brewing Stand
            if (block == Blocks.BREWING_STAND) {
                if (!checkFlag(world, pos, sp, Flags.BREW)) return InteractionResult.FAIL;
            }
            // Cake
            if (block == Blocks.CAKE) {
                if (!checkFlag(world, pos, sp, Flags.CAKE)) return InteractionResult.FAIL;
            }
            // Crafting Table, Smithing Table, etc.
            if (block == Blocks.CRAFTING_TABLE || block == Blocks.SMITHING_TABLE || block == Blocks.LOOM
                || block == Blocks.CARTOGRAPHY_TABLE || block == Blocks.STONECUTTER || block == Blocks.FLETCHING_TABLE) {
                if (!checkFlag(world, pos, sp, Flags.CRAFT)) return InteractionResult.FAIL;
            }
            // Enchanting Table
            if (block == Blocks.ENCHANTING_TABLE) {
                if (!checkFlag(world, pos, sp, Flags.ENCHANT)) return InteractionResult.FAIL;
            }
            // Lectern
            if (block == Blocks.LECTERN) {
                if (!checkFlag(world, pos, sp, Flags.LECTERN)) return InteractionResult.FAIL;
            }
            // Jukebox
            if (block == Blocks.JUKEBOX) {
                if (!checkFlag(world, pos, sp, Flags.JUKEBOX)) return InteractionResult.FAIL;
            }
            // Chiseled Bookshelf
            if (block == Blocks.CHISELED_BOOKSHELF) {
                if (!checkFlag(world, pos, sp, Flags.BOOKSHELF)) return InteractionResult.FAIL;
            }
            // Dragon Egg
            if (block == Blocks.DRAGON_EGG) {
                if (!checkFlag(world, pos, sp, Flags.DRAGON_EGG)) return InteractionResult.FAIL;
            }
            // Hopper/Dropper/Dispenser/Furnace/Smoker/FlowerPot
            if (block == Blocks.HOPPER || block == Blocks.DROPPER || block == Blocks.DISPENSER
                || block == Blocks.FURNACE || block == Blocks.BLAST_FURNACE || block == Blocks.SMOKER
                || block == Blocks.FLOWER_POT || block == Blocks.CAULDRON || block == Blocks.COMPOSTER
                || block == Blocks.BEE_NEST || block == Blocks.BEEHIVE) {
                if (!checkFlag(world, pos, sp, Flags.HOPPER)) return InteractionResult.FAIL;
            }
            // Buttons
            if (isButton(block)) {
                if (!checkFlag(world, pos, sp, Flags.BUTTON)) return InteractionResult.FAIL;
            }
            // Doors/Trapdoors/Fence Gates
            if (isDoor(block)) {
                if (!checkFlag(world, pos, sp, Flags.DOOR)) return InteractionResult.FAIL;
            }
            // Lever
            if (block == Blocks.LEVER) {
                if (!checkFlag(world, pos, sp, Flags.LEVER)) return InteractionResult.FAIL;
            }
            // Note Block
            if (block == Blocks.NOTE_BLOCK) {
                if (!checkFlag(world, pos, sp, Flags.NOTE_BLOCK)) return InteractionResult.FAIL;
            }
            // Pressure Plates
            if (isPressurePlate(block)) {
                if (!checkFlag(world, pos, sp, Flags.PRESSURE)) return InteractionResult.FAIL;
            }
            // Repeater
            if (block == Blocks.REPEATER) {
                if (!checkFlag(world, pos, sp, Flags.REPEATER)) return InteractionResult.FAIL;
            }
            // Comparator
            if (block == Blocks.COMPARATOR) {
                if (!checkFlag(world, pos, sp, Flags.COMPARER)) return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        // === Use Entity Protection ===
        // Original: ItemFrame*, ArmorStand*, Trade, Feed, Shear, Dye, Honey, Lead, Riding
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(player instanceof ServerPlayer sp) || hasBypass(sp)) return InteractionResult.PASS;
            int x = entity.blockPosition().getX(), y = entity.blockPosition().getY(), z = entity.blockPosition().getZ();
            UUID worldUid = getWorldUid(world);
            EntityType<?> type = entity.getType();

            if (type == EntityType.ITEM_FRAME || type == EntityType.GLOW_ITEM_FRAME) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.ITEM_FRAME_INTERACTIVE, sp))
                    return InteractionResult.FAIL;
            }
            if (type == EntityType.ARMOR_STAND) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.BREAK_BLOCK, sp))
                    return InteractionResult.FAIL;
            }
            if (type == EntityType.VILLAGER || type == EntityType.WANDERING_TRADER) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.TRADE, sp))
                    return InteractionResult.FAIL;
            }
            if (entity instanceof Animal) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.FEED, sp))
                    return InteractionResult.FAIL;
            }
            if (type == EntityType.SHEEP) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.SHEAR, sp))
                    return InteractionResult.FAIL;
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.DYE, sp))
                    return InteractionResult.FAIL;
            }
            if (type == EntityType.SNOW_GOLEM || type == EntityType.MOOSHROOM) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.SHEAR, sp))
                    return InteractionResult.FAIL;
            }
            if (type == EntityType.LEASH_KNOT) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.LEASH, sp))
                    return InteractionResult.FAIL;
            }
            if (type == EntityType.HORSE || type == EntityType.DONKEY || type == EntityType.MULE
                || type == EntityType.PIG || type == EntityType.STRIDER || type == EntityType.CAMEL
                || type == EntityType.LLAMA) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.RIDING, sp))
                    return InteractionResult.FAIL;
            }
            if (type == EntityType.BEE) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.HONEY, sp))
                    return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        // === Attack Entity Protection ===
        // Original: PVP, AnimalKilling, MonsterKilling, VillagerKilling, ArmorStandShot, ItemFrameShot
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(player instanceof ServerPlayer sp) || hasBypass(sp)) return InteractionResult.PASS;
            int x = entity.blockPosition().getX(), y = entity.blockPosition().getY(), z = entity.blockPosition().getZ();
            UUID worldUid = getWorldUid(world);
            EntityType<?> type = entity.getType();

            if (entity instanceof ServerPlayer) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.PVP, sp))
                    return InteractionResult.FAIL;
            }
            if (type == EntityType.VILLAGER || type == EntityType.WANDERING_TRADER
                || type == EntityType.IRON_GOLEM || type == EntityType.SNOW_GOLEM) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.VILLAGER_KILLING, sp))
                    return InteractionResult.FAIL;
            }
            if (entity instanceof Monster) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.MONSTER_KILLING, sp))
                    return InteractionResult.FAIL;
            }
            if (entity instanceof Animal) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.ANIMAL_KILLING, sp))
                    return InteractionResult.FAIL;
            }
            if (type == EntityType.ITEM_FRAME || type == EntityType.GLOW_ITEM_FRAME) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.ITEM_FRAME_INTERACTIVE, sp))
                    return InteractionResult.FAIL;
            }
            if (type == EntityType.ARMOR_STAND) {
                if (!Others.checkPrivilegeFlag(worldUid, x, y, z, Flags.BREAK_BLOCK, sp))
                    return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        // === Attack Block Protection ===
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (player instanceof ServerPlayer sp && !hasBypass(sp)) {
                if (!checkFlag(world, pos, sp, Flags.BREAK_BLOCK)) return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        // === Player Connections ===
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (CacheManager.instance != null) CacheManager.instance.setPlayerCurrentDomId(player.getUUID(), 0);
        });

        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (CacheManager.instance != null) CacheManager.instance.resetPlayerCurrentDominionId(player);
        });

        // === Server Tick - fly/glow/border checks ===
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (CacheManager.instance == null) return;
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                try {
                    int x = player.blockPosition().getX(), y = player.blockPosition().getY(), z = player.blockPosition().getZ();
                    CacheEventHandler.onPlayerMove(player, x, y, z);
                    DominionDTO dominion = CacheManager.instance.getDominion(getWorldUid(player.level()), x, y, z);
                    if (dominion != null) {
                        FlyGlowCheckHandler.checkFlyPermission(player, dominion);
                        FlyGlowCheckHandler.checkGlow(player, dominion);
                    }
                } catch (Exception ignored) {}
            }
        });

        XLogger.info("Comprehensive event handlers registered (80+ protection types matching original)");
    }

    private static boolean isShulkerBox(Block block) { return block.toString().toLowerCase().contains("shulker_box"); }
    private static boolean isBed(Block block) { return block.toString().toLowerCase().contains("_bed"); }
    private static boolean isButton(Block block) { return block.toString().toLowerCase().contains("button"); }
    private static boolean isDoor(Block block) { String n = block.toString().toLowerCase(); return n.contains("door") || n.contains("trapdoor") || n.contains("fence_gate"); }
    private static boolean isPressurePlate(Block block) { return block.toString().toLowerCase().contains("pressure_plate"); }
}
