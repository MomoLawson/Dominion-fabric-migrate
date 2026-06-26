package cn.lunadeer.dominion.cache.server;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.cache.DominionNodeSectored;
import cn.lunadeer.dominion.utils.XLogger;
import java.util.*;
public class DominionCache {
    private final Map<Integer, DominionDTO> dominions = new HashMap<>();
    private final DominionNodeSectored sectors = new DominionNodeSectored();
    public void load() {
        try {
            var all = cn.lunadeer.dominion.doos.DominionDOO.selectAll();
            for (DominionDTO d : all) dominions.put(d.getId(), d);
            sectors.build(new ArrayList<>(all));
            XLogger.info("Loaded {0} dominions", all.size());
        } catch (Exception e) { XLogger.error("Failed to load dominions: {0}", e.getMessage()); }
    }
    public DominionDTO getDominion(int id) { return dominions.get(id); }
    public DominionDTO getDominion(String name) { return dominions.values().stream().filter(d -> d.getName().equals(name)).findFirst().orElse(null); }
    public DominionDTO getDominion(java.util.UUID worldUid, int x, int y, int z) { return sectors.getDominion(worldUid, x, y, z); }
    public Collection<DominionDTO> getAll() { return dominions.values(); }
    public int count() { return dominions.size(); }
}
