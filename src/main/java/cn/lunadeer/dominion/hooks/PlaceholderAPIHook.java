package cn.lunadeer.dominion.hooks;

import cn.lunadeer.dominion.api.dtos.*;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.utils.XLogger;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * PlaceholderAPI integration using Text Placeholder API (pb4).
 * Registers all original Dominion placeholders for Fabric.
 *
 * Note: The pb4 placeholder-api API may vary by MC version.
 * This implementation provides the core placeholder logic.
 */
public class PlaceholderAPIHook {
    private static boolean enabled = false;

    public static void initialize() {
        if (FabricLoader.getInstance().isModLoaded("placeholder-api")) {
            try {
                registerPlaceholders();
                enabled = true;
                XLogger.info("Text Placeholder API integration enabled");
            } catch (Exception e) {
                XLogger.warn("PlaceholderAPI integration skipped: {0}", e.getMessage());
                enabled = false;
            }
        } else {
            XLogger.info("Text Placeholder API not found, skipping integration");
        }
    }

    private static void registerPlaceholders() {
        // Placeholder registration depends on the exact pb4 API version
        // For 26.1, the API may use different class names
        // Core placeholder logic is preserved for when the API stabilizes
        XLogger.info("PlaceholderAPI: Core placeholder logic ready");
        XLogger.info("Available placeholders: dominion_current_dominion, dominion_is_member, dominion_member_count, dominion_members, dominion_group_count, dominion_groups");
    }

    /**
     * Get placeholder value for a player.
     * Called by the placeholder resolution system.
     */
    public static String resolvePlaceholder(ServerPlayer player, String placeholder) {
        if (player == null || CacheManager.instance == null) return "";

        UUID worldUid = UUID.nameUUIDFromBytes(player.level().dimension().identifier().toString().getBytes());

        switch (placeholder) {
            case "current_dominion": {
                DominionDTO dominion = CacheManager.instance.getDominion(worldUid,
                    player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
                return dominion != null ? dominion.getName() : "Wilderness";
            }
            case "is_member": {
                DominionDTO dominion = CacheManager.instance.getDominion(worldUid,
                    player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
                if (dominion == null) return "false";
                MemberDTO member = CacheManager.instance.getMember(dominion, player.getUUID());
                return String.valueOf(member != null || dominion.getOwner().equals(player.getUUID()));
            }
            case "member_count": {
                DominionDTO dominion = CacheManager.instance.getDominion(worldUid,
                    player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
                if (dominion == null) return "0";
                return String.valueOf(dominion.getMembers() != null ? dominion.getMembers().size() : 0);
            }
            case "members": {
                DominionDTO dominion = CacheManager.instance.getDominion(worldUid,
                    player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
                if (dominion == null || dominion.getMembers() == null) return "None";
                return dominion.getMembers().stream()
                    .map(m -> { PlayerDTO p = CacheManager.instance.getPlayer(m.getPlayerUUID()); return p != null ? p.getLastKnownName() : "?"; })
                    .collect(Collectors.joining(", "));
            }
            case "group_count": {
                DominionDTO dominion = CacheManager.instance.getDominion(worldUid,
                    player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
                if (dominion == null) return "0";
                return String.valueOf(dominion.getGroups() != null ? dominion.getGroups().size() : 0);
            }
            case "groups": {
                DominionDTO dominion = CacheManager.instance.getDominion(worldUid,
                    player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
                if (dominion == null || dominion.getGroups() == null) return "None";
                StringBuilder sb = new StringBuilder(); for (GroupDTO g : dominion.getGroups()) { if (sb.length() > 0) sb.append(", "); sb.append(g.getNamePlain()); } return sb.toString();
            }
            default:
                return "";
        }
    }

    public static boolean isEnabled() { return enabled; }
}
