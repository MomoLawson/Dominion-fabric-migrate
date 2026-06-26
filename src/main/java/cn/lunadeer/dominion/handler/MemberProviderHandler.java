package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.doos.MemberDOO;
import cn.lunadeer.dominion.utils.XLogger;
import net.minecraft.commands.CommandSourceStack;
import java.util.UUID;

public class MemberProviderHandler {
    public MemberProviderHandler() { XLogger.debug("MemberProviderHandler registered"); }
}
