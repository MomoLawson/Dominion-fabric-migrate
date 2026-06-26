package cn.lunadeer.dominion.cache;
import cn.lunadeer.dominion.api.dtos.*;
import cn.lunadeer.dominion.cache.server.*;
import net.minecraft.server.level.ServerPlayer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class CacheManager {
    public static CacheManager instance;
    private ServerCache serverCache;
    private PlayerCache playerCache;
    private final Map<UUID, Integer> playerCurrentDominion = new ConcurrentHashMap<>();
    public CacheManager() { instance = this; }
    public void load() { playerCache = new PlayerCache(); playerCache.load(); serverCache = new ServerCache(); serverCache.load(); }
    public PlayerDTO getPlayer(UUID uuid) { return playerCache.getByUuid(uuid); }
    public PlayerDTO getPlayer(String name) { return playerCache.getByName(name); }
    public String getPlayerName(UUID uuid) { return playerCache.getNameByUuid(uuid); }
    public List<String> getPlayerNames() { return playerCache.getAllNames(); }
    public List<String> getPlayerManageDominionNames(UUID player) { return getPlayerOwnDominionDTOs(player).stream().map(DominionDTO::getName).toList(); }
    public DominionDTO getDominion(int id) { return serverCache.getDominionCache().getDominion(id); }
    public DominionDTO getDominion(String name) { return serverCache.getDominionCache().getDominion(name); }
    public DominionDTO getDominion(UUID worldUid, int x, int y, int z) { return serverCache.getDominionCache().getDominion(worldUid, x, y, z); }
    public List<DominionDTO> getAllDominions() { try { return new ArrayList<>(cn.lunadeer.dominion.doos.DominionDOO.selectAll()); } catch (Exception e) { return Collections.emptyList(); } }
    public List<String> getAllDominionNames() { return getAllDominions().stream().map(DominionDTO::getName).toList(); }
    public List<DominionDTO> getPlayerOwnDominionDTOs(UUID player) { return getAllDominions().stream().filter(d -> d.getOwner().equals(player)).toList(); }
    public List<DominionDTO> getPlayerAdminDominionDTOs(UUID player) { return getPlayerOwnDominionDTOs(player); }
    public MemberDTO getMember(DominionDTO d, ServerPlayer p) { return d != null ? getMember(d, p.getUUID()) : null; }
    public MemberDTO getMember(DominionDTO d, UUID p) { return d != null ? serverCache.getMemberCache().getMember(d.getId(), p) : null; }
    public GroupDTO getGroup(MemberDTO m) { return m != null ? getGroup(m.getGroupId()) : null; }
    public GroupDTO getGroup(Integer id) { return serverCache.getGroupCache().getGroup(id); }
    public DominionDTO getPlayerCurrentDominion(ServerPlayer p) { Integer id = playerCurrentDominion.get(p.getUUID()); return id != null ? getDominion(id) : null; }
    public void resetPlayerCurrentDominionId(ServerPlayer p) { playerCurrentDominion.remove(p.getUUID()); }
    public int getPlayerCurrentDomId(UUID u) { Integer id = playerCurrentDominion.get(u); return id != null ? id : 0; }
    public void setPlayerCurrentDomId(UUID u, int d) { playerCurrentDominion.put(u, d); }
}
