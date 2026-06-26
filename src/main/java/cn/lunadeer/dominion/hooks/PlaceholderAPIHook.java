package cn.lunadeer.dominion.hooks;

import cn.lunadeer.dominion.api.dtos.*;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.utils.XLogger;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * PlaceholderAPI integration using Text Placeholder API (pb4) v3.0.0.
 * Provides placeholder resolution for all original Dominion placeholders.
 */
public class PlaceholderAPIHook {
    private static boolean enabled = false;

    public static void initialize() {
        if (FabricLoader.getInstance().isModLoaded("placeholder-api")) {
            try {
                enabled = true;
                XLogger.info("Text Placeholder API integration enabled");
                XLogger.info("Available placeholders: dominion:current_dominion, dominion:is_member, dominion:member_count, dominion:members, dominion:group_count, dominion:groups");
            } catch (Exception e) {
                XLogger.warn("PlaceholderAPI integration failed: {0}", e.getMessage());
                enabled = false;
            }
        } else {
            XLogger.info("Text Placeholder API not found, skipping integration");
        }
    }

    /**
     * Resolve a placeholder value for a player.
     */
    public static String resolvePlaceholder(ServerPlayer player, String placeholder) {
        if (player == null || CacheManager.instance == null) return "";
        UUID worldUid = getWorldUid(player);
        int x = player.blockPosition().getX(), y = player.blockPosition().getY(), z = player.blockPosition().getZ();

        switch (placeholder) {
            case "current_dominion": {
                DominionDTO d = CacheManager.instance.getDominion(worldUid, x, y, z);
                return d != null ? d.getName() : "Wilderness";
            }
            case "is_member": {
                DominionDTO d = CacheManager.instance.getDominion(worldUid, x, y, z);
                if (d == null) return "false";
                MemberDTO m = CacheManager.instance.getMember(d, player.getUUID());
                return String.valueOf(m != null || d.getOwner().equals(player.getUUID()));
            }
            case "member_count": {
                DominionDTO d = CacheManager.instance.getDominion(worldUid, x, y, z);
                return d != null ? String.valueOf(d.getMembers() != null ? d.getMembers().size() : 0) : "0";
            }
            case "members": {
                DominionDTO d = CacheManager.instance.getDominion(worldUid, x, y, z);
                if (d == null || d.getMembers() == null) return "None";
                StringBuilder sb = new StringBuilder();
                for (MemberDTO m : d.getMembers()) {
                    if (sb.length() > 0) sb.append(", ");
                    PlayerDTO p = CacheManager.instance.getPlayer(m.getPlayerUUID());
                    sb.append(p != null ? p.getLastKnownName() : "?");
                }
                return sb.toString();
            }
            case "group_count": {
                DominionDTO d = CacheManager.instance.getDominion(worldUid, x, y, z);
                return d != null ? String.valueOf(d.getGroups() != null ? d.getGroups().size() : 0) : "0";
            }
            case "groups": {
                DominionDTO d = CacheManager.instance.getDominion(worldUid, x, y, z);
                if (d == null || d.getGroups() == null) return "None";
                StringBuilder sb = new StringBuilder();
                for (GroupDTO g : d.getGroups()) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(g.getNamePlain());
                }
                return sb.toString();
            }
            default: return "";
        }
    }

    private static UUID getWorldUid(ServerPlayer player) {
        return UUID.nameUUIDFromBytes(player.level().dimension().identifier().toString().getBytes());
    }

    public static boolean isEnabled() { return enabled; }
}
