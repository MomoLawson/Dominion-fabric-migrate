package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.doos.PlayerDOO;
import cn.lunadeer.dominion.api.providers.MemberProvider;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.*;

public class MemberCommand {

    public static class MemberCommandText extends ConfigurationPart {
        public String addMemberDescription = "Add a player as member to a dominion.";
        public String setMemberPrivilegeDescription = "Set privilege flag for a member in a dominion.";
        public String removeMemberDescription = "Remove a member from a dominion.";
    }

    /**
     * Adds a member to a dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion
     * @param playerName   the name of the player to be added
     */
    public static void addMember(CommandSourceStack source, String dominionName, String playerName) {
        try {
            PlayerDTO player = null;
            try {
                player = toPlayerDTO(playerName);
            } catch (Exception e) {
                ServerPlayer bukkitPlayer = Dominion.server.getPlayerList().getPlayer(playerName);
                if (bukkitPlayer != null) player = PlayerDOO.create(bukkitPlayer);
                if (player == null) throw e;
            }
            DominionDTO dominion = toDominionDTO(dominionName);
            MemberProvider.getInstance().addMember(source, dominion, player);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Sets a member's privilege in a dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion
     * @param playerName   the name of the player
     * @param flagName     the name of the privilege flag
     * @param valueStr     the value of the privilege flag
     */
    public static void setMemberPrivilege(CommandSourceStack source, String dominionName, String playerName, String flagName, String valueStr) {
        try {
            PriFlag flag = toPriFlag(flagName);
            boolean value = toBoolean(valueStr);
            DominionDTO dominion = toDominionDTO(dominionName);
            MemberDTO member = toMemberDTO(dominion, playerName);
            MemberProvider.getInstance().setMemberFlag(source, dominion, member, flag, value);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Removes a member from a dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion
     * @param playerName   the name of the player to be removed
     */
    public static void removeMember(CommandSourceStack source, String dominionName, String playerName) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            MemberDTO member = toMemberDTO(dominion, playerName);
            MemberProvider.getInstance().removeMember(source, dominion, member);
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
