package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.utils.PermissionHelper;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.utils.XLogger;
import net.minecraft.server.level.ServerPlayer;

public class FlyGlowCheckHandler {
    public FlyGlowCheckHandler() { XLogger.debug("FlyGlowCheckHandler registered"); }

    public static void checkFlyPermission(ServerPlayer player, DominionDTO dominion) {
        if (dominion == null || PermissionHelper.hasPermissionLevel(player, 4)) return;
    }

    public static void checkGlow(ServerPlayer player, DominionDTO dominion) {
        if (dominion == null) return;
        if (dominion.isOwnerGlow() && dominion.getOwner().equals(player.getUUID())) {
            player.setGlowingTag(true);
        }
    }
}
