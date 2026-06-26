package cn.lunadeer.dominion.cache;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import java.util.*;

public class DominionNodeSectored {
    private final Map<UUID, Map<UUID, List<DominionDTO>>> sectors = new HashMap<>();

    public void build(List<DominionDTO> dominions) {
        for (DominionDTO d : dominions) {
            sectors.computeIfAbsent(d.getWorldUid(), k -> new HashMap<>())
                   .computeIfAbsent(d.getWorldUid(), k -> new ArrayList<>())
                   .add(d);
        }
    }

    public DominionDTO getDominion(UUID worldUid, int x, int y, int z) {
        Map<UUID, List<DominionDTO>> worldSectors = sectors.get(worldUid);
        if (worldSectors == null) return null;
        for (List<DominionDTO> sector : worldSectors.values()) {
            for (DominionDTO d : sector) {
                if (x >= d.getX1() && x <= d.getX2() && y >= d.getY1() && y <= d.getY2() && z >= d.getZ1() && z <= d.getZ2()) {
                    return d;
                }
            }
        }
        return null;
    }
}
