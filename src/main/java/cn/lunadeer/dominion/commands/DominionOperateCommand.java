package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.api.events.dominion.modify.DominionReSizeEvent;
import cn.lunadeer.dominion.api.events.dominion.modify.DominionSetMessageEvent;
import cn.lunadeer.dominion.handler.DominionProviderHandler;
import cn.lunadeer.dominion.managers.TeleportManager;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.api.providers.DominionProvider;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

import java.util.Arrays;
import java.util.UUID;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.*;

public class DominionOperateCommand {

    public static class DominionOperateCommandText extends ConfigurationPart {
        public String resizeDescription = "Resize a dominion with a specific size and direction.";
        public String easyExpandDescription = "Expand the dominion size easily based on the player's current location.";
        public String easyContractDescription = "Contract the dominion size easily based on the player's current location.";
        public String deleteDescription = "Delete a dominion. Use 'force' to confirm deletion.";
        public String setMessageDescription = "Set a message for a dominion, either for entering or leaving.";
        public String setTeleportDescription = "Set the teleport location for a dominion.";
        public String renameDescription = "Rename a dominion.";
        public String setMapColorDescription = "Set the map color for a dominion.";
        public String giveDescription = "Give a dominion to a player.";
        public String tpDescription = "Teleport to a dominion.";
        public String switchUiDescription = "Switch the UI type for the dominion commands.";
    }

    /**
     * Adjusts the size of a specified dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion to be resized
     * @param operation    the operation: "expand" or "contract"
     * @param sizeStr      the size value to adjust by
     * @param faceStr      the direction (e.g., "north", "south", "east", "west", "up", "down"), empty for auto
     */
    public static void resize(CommandSourceStack source, String dominionName, String operation, String sizeStr, String faceStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            DominionReSizeEvent.TYPE type = toResizeType(operation);
            int size = toIntegrity(sizeStr);
            DominionReSizeEvent.DIRECTION dir;
            if (faceStr == null || faceStr.isEmpty()) {
                ServerPlayer player = source.getPlayer();
                dir = toDirection(player);
            } else {
                dir = toDirection(faceStr);
            }
            new DominionProviderHandler().resizeDominion(
                    source,
                    dominion,
                    type,
                    dir,
                    size
            );
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Easy expand: expands the dominion based on the player's current location.
     *
     * @param source  the command source
     * @param sizeStr the size value to adjust by
     * @param faceStr the direction (optional)
     */
    public static void easyExpand(CommandSourceStack source, String sizeStr, String faceStr) {
        easyResize(source, DominionReSizeEvent.TYPE.EXPAND.name(), sizeStr, faceStr);
    }

    /**
     * Easy contract: contracts the dominion based on the player's current location.
     *
     * @param source  the command source
     * @param sizeStr the size value to adjust by
     * @param faceStr the direction (optional)
     */
    public static void easyContract(CommandSourceStack source, String sizeStr, String faceStr) {
        easyResize(source, DominionReSizeEvent.TYPE.CONTRACT.name(), sizeStr, faceStr);
    }

    /**
     * Adjusts the size of a dominion based on the player's current location.
     *
     * @param source    the command source
     * @param operation the resize type: "expand" or "contract"
     * @param sizeStr   the size value to adjust by
     * @param faceStr   the direction (optional)
     */
    public static void easyResize(CommandSourceStack source, String operation, String sizeStr, String faceStr) {
        try {
            ServerPlayer player = source.getPlayer();
            BlockPos pos = player.blockPosition();
            UUID worldUid = UUID.nameUUIDFromBytes(
                    player.level().dimension().location().toString().getBytes()
            );
            DominionDTO dominion = CacheManager.instance.getDominion(worldUid, pos.getX(), pos.getY(), pos.getZ());
            if (dominion == null) {
                throw new DominionException(Language.selectPointEventsHandlerText.noDominion, pos.getX(), pos.getY(), pos.getZ());
            }
            DominionReSizeEvent.TYPE type = toResizeType(operation);
            int size = toIntegrity(sizeStr);
            DominionReSizeEvent.DIRECTION dir;
            if (faceStr == null || faceStr.isEmpty()) {
                dir = toDirection(player);
            } else {
                dir = toDirection(faceStr);
            }
            new DominionProviderHandler().resizeDominion(
                    source,
                    dominion,
                    type,
                    dir,
                    size
            );
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Deletes a specified dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion to be deleted
     * @param forceStr     "force" to confirm deletion
     */
    public static void delete(CommandSourceStack source, String dominionName, String forceStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            boolean force = "force".equals(forceStr);
            new DominionProviderHandler().deleteDominion(source, dominion, false, force);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Sets a message for a specified dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion
     * @param typeStr      the type of message: "enter" or "leave"
     * @param msg          the message content
     */
    public static void setMessage(CommandSourceStack source, String dominionName, String typeStr, String msg) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            DominionSetMessageEvent.TYPE type = toMessageType(typeStr);
            DominionProvider.getInstance().setDominionMessage(
                    source,
                    dominion,
                    type,
                    msg
            );
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Sets the teleport location for a dominion to the player's current position.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion
     */
    public static void setTp(CommandSourceStack source, String dominionName) {
        try {
            ServerPlayer player = source.getPlayer();
            DominionDTO dominion = toDominionDTO(dominionName);
            BlockPos pos = player.blockPosition();
            DominionProvider.getInstance().setDominionTpLocation(source, dominion,
                    pos.getX(), pos.getY(), pos.getZ());
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Renames a specified dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion to be renamed
     * @param newName      the new name for the dominion
     */
    public static void rename(CommandSourceStack source, String dominionName, String newName) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            new DominionProviderHandler().renameDominion(source, dominion, newName);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Sets the map color for a dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion
     * @param colorStr     the hex color string (e.g., "0xFF0000")
     */
    public static void setMapColor(CommandSourceStack source, String dominionName, String colorStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            int[] color = toColor(colorStr);
            DominionProvider.getInstance().setDominionMapColor(source, dominion, color[0], color[1], color[2]);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Gives a dominion to another player.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion to give
     * @param playerName   the name of the recipient player
     * @param forceStr     "force" to confirm the transfer
     */
    public static void give(CommandSourceStack source, String dominionName, String playerName, String forceStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            PlayerDTO player = toPlayerDTO(playerName);
            boolean force = "force".equals(forceStr);
            new DominionProviderHandler().transferDominion(source, dominion, player, force);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Teleports the player to a dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion to teleport to
     */
    public static void tp(CommandSourceStack source, String dominionName) {
        try {
            ServerPlayer player = source.getPlayer();
            DominionDTO dominion = toDominionDTO(dominionName);
            TeleportManager.teleportToDominion(player, dominion);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Switches the UI type for the player (TUI vs CUI).
     *
     * @param source    the command source
     * @param uiTypeStr the UI type string (e.g., "TUI" or "CUI"), or empty to toggle
     */
    public static void switchUi(CommandSourceStack source, String uiTypeStr) {
        try {
            ServerPlayer player = source.getPlayer();
            PlayerDTO playerDTO = CacheManager.instance.getPlayer(player.getUUID());
            if (playerDTO == null) {
                throw new DominionException("Player data not found.");
            }
            PlayerDTO.UI_TYPE uiType;
            if (uiTypeStr == null || uiTypeStr.isEmpty()) {
                uiType = playerDTO.getUiPreference() == PlayerDTO.UI_TYPE.TUI ? PlayerDTO.UI_TYPE.CUI : PlayerDTO.UI_TYPE.TUI;
            } else if (!Arrays.stream(PlayerDTO.UI_TYPE.values()).map(Enum::name).toList().contains(uiTypeStr)) {
                throw new DominionException("Invalid UI type: " + uiTypeStr + ". Valid types are: " +
                        Arrays.stream(PlayerDTO.UI_TYPE.values()).map(Enum::name).toList());
            } else {
                uiType = PlayerDTO.UI_TYPE.valueOf(uiTypeStr);
            }
            playerDTO.setUiPreference(uiType);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Shows info about the dominion at the player's current location.
     *
     * @param source the command source
     */
    public static void easyInfo(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayer();
            BlockPos pos = player.blockPosition();
            UUID worldUid = UUID.nameUUIDFromBytes(
                    player.level().dimension().location().toString().getBytes()
            );
            DominionDTO dominion = CacheManager.instance.getDominion(worldUid, pos.getX(), pos.getY(), pos.getZ());
            if (dominion == null) {
                Notification.error(player, Language.selectPointEventsHandlerText.noDominion, pos.getX(), pos.getY(), pos.getZ());
                return;
            }
            Notification.info(player, ">--------------------<");
            Notification.info(player, dominion.getName());
            PlayerDTO owner = CacheManager.instance.getPlayer(dominion.getOwner());
            if (owner == null) {
                Notification.info(player, ">--------------------<");
                return;
            }
            Notification.info(player, "");
            // TODO: Use TextUserInterface.sizeInfoTuiText when TUI is ported
            Notification.info(player, "Owner: {0}", owner.getLastKnownName());
            CuboidDTO cuboid = dominion.getCuboid();
            Notification.info(player, "Size: {0} x {1} x {2}", cuboid.xLength(), cuboid.yLength(), cuboid.zLength());
            Notification.info(player, "Height: {0} to {1}", cuboid.y1(), cuboid.y2());
            Notification.info(player, ">--------------------<");
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Sets the owner glow for a dominion.
     *
     * @param source        the command source
     * @param dominionName  the name of the dominion
     * @param ownerGlowStr  the boolean value as string
     */
    public static void setOwnerGlow(CommandSourceStack source, String dominionName, String ownerGlowStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            boolean ownerGlow = Boolean.parseBoolean(ownerGlowStr);
            new DominionProviderHandler().setDominionOwnerGlow(source, dominion, ownerGlow);
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
