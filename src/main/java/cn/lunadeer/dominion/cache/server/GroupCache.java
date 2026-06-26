package cn.lunadeer.dominion.cache.server;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.utils.XLogger;
import java.util.*;
public class GroupCache {
    private final Map<Integer, GroupDTO> groups = new HashMap<>();
    public void load() {
        try {
            var all = cn.lunadeer.dominion.doos.GroupDOO.selectAll();
            for (GroupDTO g : all) groups.put(g.getId(), g);
            XLogger.info("Loaded {0} groups", all.size());
        } catch (Exception e) { XLogger.error("Failed to load groups: {0}", e.getMessage()); }
    }
    public GroupDTO getGroup(int id) { return groups.get(id); }
    public int count() { return groups.size(); }
}
