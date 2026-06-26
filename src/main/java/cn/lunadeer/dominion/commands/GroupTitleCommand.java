package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.doos.PlayerDOO;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

import static cn.lunadeer.dominion.misc.Asserts.assertDominionOwner;
import static cn.lunadeer.dominion.misc.Converts.*;

public class GroupTitleCommand {

    public static class GroupTitleCommandText extends ConfigurationPart {
        public String groupNotBelonging = "Don't belong to group {0}.";
        public String usingTitleSuccess = "Using title {0} successfully.";
        public String usingTitleFail = "Failed to use title, reason: {0}";
        public String useTitleDescription = "Use a group title or disable current title.";
    }

    /**
     * Uses a title for a player.
     *
     * @param source          the command source
     * @param groupTitleIdStr the ID of the group title as a string. -1 for disable current title.
     */
    public static void useTitle(CommandSourceStack source, String groupTitleIdStr) {
        try {
            ServerPlayer player = source.getPlayer();
            int titleId = toIntegrity(groupTitleIdStr);
            if (titleId == -1) {
                ((PlayerDOO) toPlayerDTO(player.getUUID())).setUsingGroupTitleID(-1);
                return;
            }
            PlayerDTO playerDto = toPlayerDTO(player.getUUID());
            GroupDTO group = toGroupDTO(titleId);
            DominionDTO dominion = toDominionDTO(group.getDomID());
            try {
                assertDominionOwner(player, dominion);
            } catch (Exception e) {
                MemberDTO member = CacheManager.instance.getMember(dominion, player);
                if (member == null) {
                    throw new DominionException(Language.groupTitleCommandText.groupNotBelonging, group.getNamePlain());
                }
                if (!Objects.equals(member.getGroupId(), group.getId())) {
                    throw new DominionException(Language.groupTitleCommandText.groupNotBelonging, group.getNamePlain());
                }
            }
            ((PlayerDOO) playerDto).setUsingGroupTitleID(group.getId());
            Notification.info(player, Language.groupTitleCommandText.usingTitleSuccess, group.getNamePlain());
        } catch (Exception e) {
            sendError(source, Language.groupTitleCommandText.usingTitleFail, e.getMessage());
        }
    }

    // --- Helper ---

    private static void sendError(CommandSourceStack source, String msg, Object... args) {
        try {
            ServerPlayer player = source.getPlayer();
            Notification.error(player, msg, args);
        } catch (Exception ex) {
            Notification.error(source.level().getServer(), msg, args);
        }
    }
}
