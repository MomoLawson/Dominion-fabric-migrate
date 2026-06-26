package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.doos.DominionDOO;
import cn.lunadeer.dominion.doos.PlayerDOO;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.api.providers.DominionProvider;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.UUID;

import static cn.lunadeer.dominion.Dominion.adminPermission;
import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Others.bypassLimit;

public class MigrationCommand {

    public static class MigrationCommandText extends ConfigurationPart {
        public String migrateSuccess = "Migrated residence {0} to dominion successfully.";
        public String migrateFailed = "Failed to migrate residence. Reason: {0}";
        public String missingResidence = "Residence {0} not found.";
        public String notYourResidence = "Residence {0} is not yours.";
        public String migrateDescription = "Migrate a specific residence to dominion.";
        public String migrateAllDescription = "Migrate all residences to dominions.";
        public String notEnabled = "Residence migration is not enabled.";
        public String noData = "No residence data found.";
    }

    /**
     * Handles the migration process for a single residence.
     *
     * @param source  the command source
     * @param resName the name of the residence
     */
    public static void migrate(CommandSourceStack source, String resName) {
        try {
            if (!Configuration.residenceMigration) {
                sendError(source, Language.migrationCommandText.notEnabled);
                return;
            }
            List<ResMigration.ResidenceNode> res_data = CacheManager.instance.getResidenceCache().getResidenceData();
            if (res_data == null) {
                throw new DominionException(Language.migrationCommandText.noData);
            }
            ResMigration.ResidenceNode resNode = res_data.stream().filter(node -> node.name.equals(resName)).findFirst().orElse(null);
            if (resNode == null) {
                throw new DominionException(Language.migrationCommandText.missingResidence, resName);
            }
            try {
                ServerPlayer player = source.getPlayer();
                if (!bypassLimit(player)) {
                    if (!resNode.owner.equals(player.getUUID())) {
                        throw new DominionException(Language.migrationCommandText.notYourResidence, resName);
                    }
                }
            } catch (Exception ignored) {
                // Console source, allow migration
            }
            doMigrateCreate(source, resNode, null);
        } catch (Exception e) {
            sendError(source, Language.migrationCommandText.migrateFailed, e.getMessage());
        }
    }

    /**
     * Migrates all residences to dominions.
     *
     * @param source the command source
     */
    public static void migrateAll(CommandSourceStack source) {
        List<ResMigration.ResidenceNode> res_data = CacheManager.instance.getResidenceCache().getResidenceData();
        if (res_data == null || res_data.isEmpty()) {
            sendError(source, Language.migrationCommandText.noData);
            return;
        }
        int successCount = 0;
        for (ResMigration.ResidenceNode resNode : res_data) {
            try {
                doMigrateCreate(source, resNode, null);
                successCount++;
            } catch (Exception e) {
                sendError(source, Language.migrationCommandText.migrateFailed, e.getMessage());
                XLogger.error(e);
            }
        }
        sendInfo(source, Language.migrationCommandText.migrateSuccess, successCount + "/" + res_data.size());
    }

    /**
     * Performs the actual migration creation process.
     *
     * @param source the command source
     * @param node   the residence node
     * @param parent the parent dominion DTO
     * @throws Exception if an error occurs during migration
     */
    private static void doMigrateCreate(CommandSourceStack source, ResMigration.ResidenceNode node, DominionDTO parent) throws Exception {
        PlayerDTO ownerDTO = PlayerDOO.create(node.owner, node.ownerName);
        int[] pos1 = {node.loc1[0], node.loc1[1], node.loc1[2]};
        int[] pos2 = {node.loc2[0], node.loc2[1], node.loc2[2]};
        CuboidDTO cuboidDTO = new CuboidDTO(pos1, pos2);
        ServerLevel world = node.world;
        String worldUid = world.dimension().location().toString();
        UUID worldUUID = UUID.nameUUIDFromBytes(worldUid.getBytes());
        int renameNumber = 0;
        while (DominionDOO.select(renameNumber == 0 ? node.name : node.name + "_" + renameNumber) != null) {
            renameNumber++;
        }
        DominionDTO dominion = DominionProvider.getInstance().createDominion(
                source,
                renameNumber == 0 ? node.name : node.name + "_" + renameNumber,
                ownerDTO.getUUID(), worldUUID, cuboidDTO, parent, true).get();
        if (dominion != null) {
            sendInfo(source, Language.migrationCommandText.migrateSuccess, node.name);
            postProcessMigration(source, node, dominion);
        }
    }

    /**
     * Post-processes the migration after a dominion is created.
     *
     * @param source          the command source
     * @param node            the residence node containing migration data
     * @param dominionCreated the DominionDTO that was created
     * @throws Exception if an error occurs during child migration
     */
    private static void postProcessMigration(CommandSourceStack source, ResMigration.ResidenceNode node, DominionDTO dominionCreated) throws Exception {
        assert dominionCreated != null : "Migrate Dominion created failed, event.getDominion() is null";
        if (node.tpLoc != null) {
            dominionCreated = dominionCreated.setTpLocation(node.tpLoc);
        }
        if (node.joinMessage != null) {
            dominionCreated = dominionCreated.setJoinMessage(node.joinMessage);
        }
        if (node.leaveMessage != null) {
            dominionCreated = dominionCreated.setLeaveMessage(node.leaveMessage);
        }
        if (node.children != null) {
            for (ResMigration.ResidenceNode child : node.children) {
                doMigrateCreate(source, child, dominionCreated);
            }
        }
    }

    // --- Helper ---

    private static void sendInfo(CommandSourceStack source, String msg, Object... args) {
        try {
            ServerPlayer player = source.getPlayer();
            Notification.info(player, msg, args);
        } catch (Exception e) {
            Notification.info(source.level().getServer(), msg, args);
        }
    }

    private static void sendError(CommandSourceStack source, String msg, Object... args) {
        try {
            ServerPlayer player = source.getPlayer();
            Notification.error(player, msg, args);
        } catch (Exception e) {
            Notification.error(source.level().getServer(), msg, args);
        }
    }
}
