package cn.lunadeer.dominion.utils.holograme;

import cn.lunadeer.dominion.nms.FakeEntity;
import java.util.*;

public class HoloManager {
    private static final Map<UUID, List<FakeEntity>> holograms = new HashMap<>();

    public static void addHologram(UUID playerUuid, FakeEntity entity) {
        holograms.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(entity);
    }

    public static void clearHolograms(UUID playerUuid) {
        List<FakeEntity> entities = holograms.remove(playerUuid);
        if (entities != null) entities.clear();
    }

    public static void clearAll() { holograms.clear(); }
}
