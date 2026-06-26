package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.doos.MemberDOO;
import cn.lunadeer.dominion.utils.XLogger;
import net.minecraft.commands.CommandSourceStack;
import java.util.UUID;

public class MemberProviderHandler {
    public MemberProviderHandler() { XLogger.debug("MemberProviderHandler registered"); }

    public void addMember(CommandSourceStack source, DominionDTO dominion, UUID playerUuid) throws Exception {
        MemberDOO.create(dominion.getId(), playerUuid);
    }

    public void removeMember(CommandSourceStack source, DominionDTO dominion, UUID playerUuid) throws Exception {
        MemberDOO.delete(dominion.getId(), playerUuid);
    }

    public void setMemberFlag(CommandSourceStack source, DominionDTO dominion, UUID playerUuid, String flagName, boolean value) throws Exception {
        MemberDTO member = cn.lunadeer.dominion.cache.CacheManager.instance.getMember(dominion.getId(), playerUuid);
        if (member != null) member.setFlag(flagName, value);
    }
}
