package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.events.PlayerCrossDominionBorderEvent;
import cn.lunadeer.dominion.events.PlayerMoveInDominionEvent;
import cn.lunadeer.dominion.events.PlayerMoveOutDominionEvent;
import cn.lunadeer.dominion.utils.XLogger;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

public class CacheEventHandler {
    public CacheEventHandler() { XLogger.debug("CacheEventHandler registered"); }

    public static void onPlayerMove(ServerPlayer player, int x, int y, int z) {
        UUID playerUuid = player.getUUID();
        UUID worldUid = UUID.nameUUIDFromBytes(player.level().dimension().location().toString().getBytes());
        int currentDomId = CacheManager.instance.getPlayerCurrentDomId(playerUuid);
        DominionDTO newDom = CacheManager.instance.getDominion(worldUid, x, y, z);
        int newDomId = newDom != null ? newDom.getId() : 0;
        if (currentDomId != newDomId) {
            CacheManager.instance.setPlayerCurrentDomId(playerUuid, newDomId);
            if (currentDomId > 0 && newDomId > 0) {
                new PlayerCrossDominionBorderEvent(playerUuid, currentDomId, newDomId);
            } else if (newDomId > 0) {
                new PlayerMoveInDominionEvent(playerUuid, newDomId);
            } else if (currentDomId > 0) {
                new PlayerMoveOutDominionEvent(playerUuid, currentDomId);
            }
        }
    }
}
