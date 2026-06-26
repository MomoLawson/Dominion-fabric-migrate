package cn.lunadeer.dominion.cache.server;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.utils.XLogger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class PlayerCache {
    private final Map<UUID, PlayerDTO> byUuid = new ConcurrentHashMap<>();
    private final Map<String, UUID> byName = new ConcurrentHashMap<>();
    public void load() {
        try {
            List<? extends PlayerDTO> players = cn.lunadeer.dominion.doos.PlayerDOO.selectAll();
            for (PlayerDTO p : players) { byUuid.put(p.getUuid(), p); byName.put(p.getLastKnownName().toLowerCase(), p.getUuid()); }
            XLogger.info("Loaded {0} players", players.size());
        } catch (Exception e) { XLogger.error("Failed to load players: {0}", e.getMessage()); }
    }
    public PlayerDTO getByUuid(UUID uuid) { return byUuid.get(uuid); }
    public PlayerDTO getByName(String name) { UUID id = byName.get(name.toLowerCase()); return id != null ? byUuid.get(id) : null; }
    public String getNameByUuid(UUID uuid) { PlayerDTO p = byUuid.get(uuid); return p != null ? p.getLastKnownName() : "Unknown"; }
    public List<String> getAllNames() { return new ArrayList<>(byName.keySet()); }
    public int count() { return byUuid.size(); }
}
