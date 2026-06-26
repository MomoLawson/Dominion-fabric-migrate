package cn.lunadeer.dominion.misc;

import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.configuration.Limitation;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.utils.VaultConnect.VaultConnect;

import java.util.List;
import java.util.UUID;

public class Asserts {

    public static class AssertsText extends ConfigurationPart {
        public String noPermission = "You don't have permission to do that.";
        public String notOwner = "You are not the owner of this dominion.";
        public String notMember = "You are not a member of this dominion.";
        public String dominionNotFound = "Dominion not found.";
        public String playerNotFound = "Player not found.";
        public String alreadyExists = "A dominion with that name already exists.";
        public String tooClose = "This dominion is too close to another dominion.";
        public String tooLarge = "This dominion is too large.";
        public String tooSmall = "This dominion is too small.";
        public String tooMany = "You have too many dominions.";
        public String worldNotFound = "World not found.";
        public String notEnoughMoney = "You don't have enough money.";
        public String cannotCreateHere = "You cannot create a dominion here.";
        public String mustBeOnline = "The player must be online.";
        public String notSubDominion = "This is not a sub-dominion.";
        public String isSubDominion = "This is a sub-dominion.";
        public String depthExceeded = "Maximum sub-dominion depth exceeded.";
    }

    public static void assertDominionOwner(ServerPlayer player, DominionDTO dominion) throws DominionException {
        if (dominion == null) throw new DominionException(Language.assertsText.dominionNotFound);
        if (!dominion.getOwner().equals(player.getUUID())) throw new DominionException(Language.assertsText.notOwner);
    }

    public static void assertDominionNotNull(DominionDTO dominion) throws DominionException {
        if (dominion == null) throw new DominionException(Language.assertsText.dominionNotFound);
    }

    public static void assertDominionNameNotExists(String name, UUID owner) throws DominionException {
        if (CacheManager.instance.getDominion(name) != null) throw new DominionException(Language.assertsText.alreadyExists);
    }

    public static void assertCanCreate(ServerPlayer player, CuboidDTO cuboid, UUID worldUid) throws DominionException {
        Limitation.WorldSettings ws = Configuration.getPlayerLimitation(player.getUUID(), List.of()).getWorldSettings(worldUid.toString());
        if (ws == null) return;
        int amount = CacheManager.instance.getPlayerOwnDominionDTOs(player.getUUID()).size();
        if (ws.amountLimit >= 0 && amount >= ws.amountLimit) throw new DominionException(Language.assertsText.tooMany);
    }

    public static void assertNotSubDominion(DominionDTO dominion) throws DominionException {
        if (dominion.getParentDomId() > 0) throw new DominionException(Language.assertsText.isSubDominion);
    }

    public static void assertSubDominion(DominionDTO dominion) throws DominionException {
        if (dominion.getParentDomId() <= 0) throw new DominionException(Language.assertsText.notSubDominion);
    }

    public static void assertDominionAdmin(ServerPlayer player, DominionDTO dominion) throws DominionException {
        if (dominion == null) throw new DominionException(Language.assertsText.dominionNotFound);
        if (dominion.getOwner().equals(player.getUUID())) return;
        MemberDTO member = CacheManager.instance.getMember(dominion.getId(), player.getUUID());
        if (member == null || member.getGroupId() <= 0) throw new DominionException(Language.assertsText.noPermission);
    }
}
