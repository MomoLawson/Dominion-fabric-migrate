package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.utils.XLogger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import java.util.Map;
import java.util.UUID;

public class SelectPointEventsHandler {
    public static final int DEFAULT_GLOW_COLOR = 0x00FF00;

    public SelectPointEventsHandler() { XLogger.debug("SelectPointEventsHandler registered"); }

    public static void handlePointSelect(ServerPlayer player, BlockPos pos) {
        UUID playerUuid = player.getUUID();
        Map<Integer, int[]> points = Dominion.pointsSelect.computeIfAbsent(playerUuid, k -> new java.util.HashMap<>());
        int idx = points.containsKey(0) ? 1 : 0;
        points.put(idx, new int[]{pos.getX(), pos.getY(), pos.getZ()});
    }

    public static void handleInfoTool(ServerPlayer player, ServerLevel level, BlockPos pos) {
        DominionDTO dominion = CacheManager.instance.getDominion(UUID.nameUUIDFromBytes(level.dimension().identifier().toString().getBytes()), pos.getX(), pos.getY(), pos.getZ());
        if (dominion != null) XLogger.info("Dominion: {0}", dominion.getName());
    }
}
