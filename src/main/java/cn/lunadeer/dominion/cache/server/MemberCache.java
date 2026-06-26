package cn.lunadeer.dominion.cache.server;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.utils.XLogger;
import java.util.*;
public class MemberCache {
    private final Map<Integer, List<MemberDTO>> byDominion = new HashMap<>();
    public void load() {
        try {
            List<? extends MemberDTO> all = cn.lunadeer.dominion.doos.MemberDOO.selectAll();
            for (MemberDTO m : all) byDominion.computeIfAbsent(m.getDomID(), k -> new ArrayList<>()).add(m);
            XLogger.info("Loaded {0} members", all.size());
        } catch (Exception e) { XLogger.error("Failed to load members: {0}", e.getMessage()); }
    }
    public MemberDTO getMember(int domId, java.util.UUID playerUuid) {
        return byDominion.getOrDefault(domId, Collections.<MemberDTO>emptyList()).stream().filter(m -> m.getPlayerUUID() != null && m.getPlayerUUID().equals(playerUuid)).findFirst().orElse(null);
    }
    public int count() { return byDominion.values().stream().mapToInt(List::size).sum(); }
}
