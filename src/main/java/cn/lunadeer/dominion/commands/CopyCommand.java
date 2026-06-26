package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.api.providers.GroupProvider;
import cn.lunadeer.dominion.api.providers.MemberProvider;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toDominionDTO;

public class CopyCommand {

    public static class CopyCommandText extends ConfigurationPart {
        public String copyEnvSuccess = "Copied environment flag from {0} to {1} success.";
        public String copyGuestSuccess = "Copied guest privilege flag from {0} to {1} success.";
        public String copyMemberSuccess = "Copied members from {0} to {1} success.";
        public String copyGroupSuccess = "Copied groups from {0} to {1} success.";
        public String copyEnvironmentDescription = "Copy environment flags from one dominion to another.";
        public String copyGuestDescription = "Copy guest privilege flags from one dominion to another.";
        public String copyMemberDescription = "Copy members from one dominion to another.";
        public String copyGroupDescription = "Copy groups from one dominion to another.";
    }

    /**
     * Copies environment flags from one dominion to another.
     *
     * @param source the command source
     * @param from   the source dominion name
     * @param to     the target dominion name
     */
    public static void copyEnvironment(CommandSourceStack source, String from, String to) {
        try {
            DominionDTO fromDominion = toDominionDTO(from);
            DominionDTO toDominion = toDominionDTO(to);
            for (EnvFlag flag : fromDominion.getEnvironmentFlagValue().keySet()) {
                if (toDominion.getEnvFlagValue(flag) == fromDominion.getEnvFlagValue(flag)) continue;
                toDominion.setEnvFlagValue(flag, fromDominion.getEnvFlagValue(flag));
            }
            sendInfo(source, Language.copyCommandText.copyEnvSuccess, fromDominion.getName(), toDominion.getName());
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Copies guest privilege flags from one dominion to another.
     *
     * @param source the command source
     * @param from   the source dominion name
     * @param to     the target dominion name
     */
    public static void copyGuest(CommandSourceStack source, String from, String to) {
        try {
            DominionDTO fromDominion = toDominionDTO(from);
            DominionDTO toDominion = toDominionDTO(to);
            for (PriFlag flag : fromDominion.getGuestPrivilegeFlagValue().keySet()) {
                if (toDominion.getGuestFlagValue(flag) == fromDominion.getGuestFlagValue(flag)) continue;
                toDominion.setGuestFlagValue(flag, fromDominion.getGuestFlagValue(flag));
            }
            sendInfo(source, Language.copyCommandText.copyGuestSuccess, fromDominion.getName(), toDominion.getName());
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Copies members from one dominion to another.
     *
     * @param source the command source
     * @param from   the source dominion name
     * @param to     the target dominion name
     */
    public static void copyMember(CommandSourceStack source, String from, String to) {
        try {
            DominionDTO fromDominion = toDominionDTO(from);
            DominionDTO toDominion = toDominionDTO(to);
            for (MemberDTO member : fromDominion.getMembers()) {
                try {
                    MemberDTO toMember = CacheManager.instance.getMember(toDominion, member.getPlayerUUID());
                    if (toMember == null) {
                        toMember = MemberProvider.getInstance().addMember(source, toDominion, member.getPlayer()).get();
                        if (toMember == null) continue;
                    }
                    for (PriFlag flag : member.getFlagsValue().keySet()) {
                        if (toMember.getFlagValue(flag) == member.getFlagValue(flag)) continue;
                        MemberProvider.getInstance().setMemberFlag(source,
                                toDominion,
                                toMember,
                                flag,
                                member.getFlagValue(flag));
                    }
                } catch (Exception e) {
                    Notification.warn(getPlayerOrNull(source), e.getMessage());
                }
            }
            sendInfo(source, Language.copyCommandText.copyMemberSuccess, fromDominion.getName(), toDominion.getName());
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Copies groups (and members) from one dominion to another.
     *
     * @param source the command source
     * @param from   the source dominion name
     * @param to     the target dominion name
     */
    public static void copyGroup(CommandSourceStack source, String from, String to) {
        try {
            copyMember(source, from, to); // copy members first
            DominionDTO fromDominion = toDominionDTO(from);
            DominionDTO toDominion = toDominionDTO(to);
            for (GroupDTO group : fromDominion.getGroups()) {
                try {
                    GroupDTO toGroup = toDominion.getGroups().stream()
                            .filter(g -> g.getNamePlain().equals(group.getNamePlain()))
                            .findFirst()
                            .orElse(null);
                    if (toGroup == null) {
                        GroupDTO groupCreated = GroupProvider.getInstance().createGroup(source, toDominion, group.getNameRaw()).get();
                        if (groupCreated == null) continue;
                        toGroup = groupCreated;
                    }
                    for (PriFlag flag : group.getFlagsValue().keySet()) {
                        if (toGroup.getFlagValue(flag) == group.getFlagValue(flag)) continue;
                        GroupProvider.getInstance().setGroupFlag(source,
                                toDominion,
                                toGroup,
                                flag,
                                group.getFlagValue(flag));
                    }
                    for (MemberDTO fromMember : fromDominion.getMembers()) {
                        MemberDTO toMember = CacheManager.instance.getMember(toDominion, fromMember.getPlayerUUID());
                        if (toMember == null) continue;
                        if (toMember.getGroupId().equals(toGroup.getId())) continue;
                        GroupProvider.getInstance().addMember(source, toDominion, toGroup, toMember);
                    }
                } catch (Exception e) {
                    Notification.warn(getPlayerOrNull(source), e.getMessage());
                }
            }
            sendInfo(source, Language.copyCommandText.copyGroupSuccess, fromDominion.getName(), toDominion.getName());
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    // --- Helper ---

    private static ServerPlayer getPlayerOrNull(CommandSourceStack source) {
        try {
            return source.getPlayer();
        } catch (Exception e) {
            return null;
        }
    }

    private static void sendInfo(CommandSourceStack source, String msg, Object... args) {
        try {
            ServerPlayer player = source.getPlayer();
            Notification.info(player, msg, args);
        } catch (Exception e) {
            Notification.info(source.level().getServer(), msg, args);
        }
    }

    private static void sendError(CommandSourceStack source, Throwable e) {
        try {
            ServerPlayer player = source.getPlayer();
            Notification.error(player, e);
        } catch (Exception ex) {
            Notification.error(source.level().getServer(), e.getMessage());
        }
    }
}
