package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.doos.GroupDOO;
import cn.lunadeer.dominion.doos.MemberDOO;
import cn.lunadeer.dominion.utils.XLogger;
import net.minecraft.commands.CommandSourceStack;
import java.util.UUID;

public class GroupProviderHandler {
    public GroupProviderHandler() { XLogger.debug("GroupProviderHandler registered"); }

    public void createGroup(CommandSourceStack source, DominionDTO dominion, String name) throws Exception {
        GroupDOO.create(name, dominion);
    }

    public void deleteGroup(CommandSourceStack source, DominionDTO dominion, GroupDTO group) throws Exception {
        GroupDOO.deleteById(group.getId());
    }

    public void renameGroup(CommandSourceStack source, GroupDTO group, String newName) throws Exception {
        group.setName(newName);
    }

    public void setGroupFlag(CommandSourceStack source, GroupDTO group, String flagName, boolean value) throws Exception {
        group.setFlag(flagName, value);
    }

    public void addMemberToGroup(CommandSourceStack source, DominionDTO dominion, GroupDTO group, UUID playerUuid) throws Exception {
        // Add member to group logic
    }

    public void removeMemberFromGroup(CommandSourceStack source, DominionDTO dominion, UUID playerUuid) throws Exception {
        // Remove member from group logic
    }
}
