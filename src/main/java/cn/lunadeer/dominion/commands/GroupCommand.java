package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.api.providers.GroupProvider;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.*;

public class GroupCommand {

    public static class GroupCommandText extends ConfigurationPart {
        public String createGroupDescription = "Create a new group in a dominion.";
        public String deleteGroupDescription = "Delete a group from a dominion.";
        public String renameGroupDescription = "Rename a group in a dominion.";
        public String setGroupFlagDescription = "Set privilege flag for a group.";
        public String addMemberDescription = "Add a member to a group.";
        public String removeMemberDescription = "Remove a member from a group.";
    }

    /**
     * Creates a new group in a dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion
     * @param groupName    the name of the new group
     */
    public static void createGroup(CommandSourceStack source, String dominionName, String groupName) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            GroupProvider.getInstance().createGroup(source, dominion, groupName);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Deletes a group from a dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion
     * @param groupName    the name of the group to delete
     */
    public static void deleteGroup(CommandSourceStack source, String dominionName, String groupName) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            GroupDTO group = toGroupDTO(dominion, groupName);
            GroupProvider.getInstance().deleteGroup(source, dominion, group);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Renames a group in a dominion.
     *
     * @param source         the command source
     * @param dominionName   the name of the dominion
     * @param oldGroupName   the current name of the group
     * @param newGroupName   the new name for the group
     */
    public static void renameGroup(CommandSourceStack source, String dominionName, String oldGroupName, String newGroupName) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            GroupDTO group = toGroupDTO(dominion, oldGroupName);
            GroupProvider.getInstance().renameGroup(source, dominion, group, newGroupName);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Sets a privilege flag for a group.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion
     * @param groupName    the name of the group
     * @param flagName     the name of the privilege flag
     * @param valueStr     the value to set
     */
    public static void setGroupFlag(CommandSourceStack source, String dominionName, String groupName, String flagName, String valueStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            GroupDTO group = toGroupDTO(dominion, groupName);
            PriFlag flag = toPriFlag(flagName);
            boolean value = toBoolean(valueStr);
            GroupProvider.getInstance().setGroupFlag(source, dominion, group, flag, value);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Adds a member to a group.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion
     * @param groupName    the name of the group
     * @param playerName   the name of the player to add
     */
    public static void addMember(CommandSourceStack source, String dominionName, String groupName, String playerName) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            MemberDTO member = toMemberDTO(dominion, playerName);
            GroupDTO group = toGroupDTO(dominion, groupName);
            GroupProvider.getInstance().addMember(source, dominion, group, member);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Removes a member from a group.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion
     * @param groupName    the name of the group
     * @param playerName   the name of the player to remove
     */
    public static void removeMember(CommandSourceStack source, String dominionName, String groupName, String playerName) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            GroupDTO group = toGroupDTO(dominion, groupName);
            MemberDTO member = toMemberDTO(dominion, playerName);
            GroupProvider.getInstance().removeMember(source, dominion, group, member);
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
