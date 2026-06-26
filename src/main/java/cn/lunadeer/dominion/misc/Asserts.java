package cn.lunadeer.dominion.misc;
import cn.lunadeer.dominion.api.dtos.*;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.server.level.ServerPlayer;
import java.util.*;
public class Asserts {
    public static class AssertsText extends ConfigurationPart {
        public String noPermission = "No permission."; public String notOwner = "Not owner.";
        public String dominionNotFound = "Not found."; public String alreadyExists = "Already exists.";
        public String tooMany = "Too many."; public String notSubDominion = "Not sub-dominion.";
        public String isSubDominion = "Is sub-dominion.";
    }
    public static void assertDominionOwner(ServerPlayer p, DominionDTO d) throws DominionException { if (d == null || !d.getOwner().equals(p.getUUID())) throw new DominionException(Language.assertsText.notOwner); }
    public static void assertDominionNotNull(DominionDTO d) throws DominionException { if (d == null) throw new DominionException(Language.assertsText.dominionNotFound); }
    public static void assertDominionAdmin(ServerPlayer p, DominionDTO d) throws DominionException { if (d == null) throw new DominionException(Language.assertsText.dominionNotFound); }
    public static void assertCanCreate(ServerPlayer p, CuboidDTO c, UUID w) throws DominionException {}
    public static void assertNotSubDominion(DominionDTO d) throws DominionException {}
    public static void assertSubDominion(DominionDTO d) throws DominionException {}
    public static void assertDominionNameNotExists(String name, UUID owner) throws DominionException { if (cn.lunadeer.dominion.cache.CacheManager.instance.getDominion(name) != null) throw new DominionException("Dominion name already exists"); }
}
