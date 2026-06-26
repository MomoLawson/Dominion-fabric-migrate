package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.api.providers.DominionProvider;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.*;
import static cn.lunadeer.dominion.misc.Others.autoPoints;

public class DominionCreateCommand {

    public static class DominionCreateCommandText extends ConfigurationPart {
        public String createDescription = "Create a new dominion using selected points.";
        public String createSubDescription = "Create a sub-dominion within an existing dominion.";
        public String autoCreateDescription = "Automatically create a dominion around your current position.";
        public String autoCreateSubDescription = "Automatically create a sub-dominion within an existing dominion.";
    }

    /**
     * Creates a new dominion using the player's selected points.
     *
     * @param source       the command source
     * @param dominionName the name for the new dominion
     */
    public static void create(CommandSourceStack source, String dominionName) {
        try {
            ServerPlayer player = source.getPlayer();
            ServerLevel world = player.level();
            int[][] points = getSelectedPoints(player);
            CuboidDTO cuboidDTO = new CuboidDTO(points[0], points[1]);
            String worldUid = world.dimension().location().toString();
            java.util.UUID worldUUID = java.util.UUID.nameUUIDFromBytes(worldUid.getBytes());
            DominionProvider.getInstance().createDominion(
                    source,
                    dominionName,
                    player.getUUID(),
                    worldUUID, cuboidDTO,
                    null, false
            );
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Creates a sub-dominion within an existing dominion using selected points.
     *
     * @param source         the command source
     * @param dominionName   the name for the new sub-dominion
     * @param parentDominion the name of the parent dominion
     */
    public static void createSub(CommandSourceStack source, String dominionName, String parentDominion) {
        try {
            ServerPlayer player = source.getPlayer();
            ServerLevel world = player.level();
            int[][] points = getSelectedPoints(player);
            CuboidDTO cuboidDTO = new CuboidDTO(points[0], points[1]);
            String worldUid = world.dimension().location().toString();
            java.util.UUID worldUUID = java.util.UUID.nameUUIDFromBytes(worldUid.getBytes());
            DominionProvider.getInstance().createDominion(
                    source,
                    dominionName,
                    player.getUUID(),
                    worldUUID, cuboidDTO,
                    toDominionDTO(parentDominion),
                    false
            );
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Automatically creates a dominion around the player's current position.
     *
     * @param source       the command source
     * @param dominionName the name for the new dominion
     */
    public static void autoCreate(CommandSourceStack source, String dominionName) {
        try {
            ServerPlayer player = source.getPlayer();
            ServerLevel world = player.level();
            BlockPos[] points = autoPoints(player);
            int[] pos1 = {points[0].getX(), points[0].getY(), points[0].getZ()};
            int[] pos2 = {points[1].getX(), points[1].getY(), points[1].getZ()};
            CuboidDTO cuboidDTO = new CuboidDTO(pos1, pos2);
            String worldUid = world.dimension().location().toString();
            java.util.UUID worldUUID = java.util.UUID.nameUUIDFromBytes(worldUid.getBytes());
            DominionProvider.getInstance().createDominion(
                    source,
                    dominionName,
                    player.getUUID(),
                    worldUUID, cuboidDTO,
                    null, false
            );
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Automatically creates a sub-dominion within an existing dominion.
     *
     * @param source         the command source
     * @param dominionName   the name for the new sub-dominion
     * @param parentDominion the name of the parent dominion
     */
    public static void autoCreateSub(CommandSourceStack source, String dominionName, String parentDominion) {
        try {
            ServerPlayer player = source.getPlayer();
            ServerLevel world = player.level();
            BlockPos[] points = autoPoints(player);
            int[] pos1 = {points[0].getX(), points[0].getY(), points[0].getZ()};
            int[] pos2 = {points[1].getX(), points[1].getY(), points[1].getZ()};
            CuboidDTO cuboidDTO = new CuboidDTO(pos1, pos2);
            String worldUid = world.dimension().location().toString();
            java.util.UUID worldUUID = java.util.UUID.nameUUIDFromBytes(worldUid.getBytes());
            DominionProvider.getInstance().createDominion(
                    source,
                    dominionName,
                    player.getUUID(),
                    worldUUID, cuboidDTO,
                    toDominionDTO(parentDominion),
                    false
            );
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    // --- Helper ---

    private static void sendError(CommandSourceStack source, Throwable e) {
        try {
            ServerPlayer player = source.getPlayer();
            Notification.error(player, e);
        } catch (Exception ex) {
            Notification.error(source.level().getServer(), e.getMessage());
        }
    }
}
