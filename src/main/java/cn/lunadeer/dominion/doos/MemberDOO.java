package cn.lunadeer.dominion.doos;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import java.util.*;
public class MemberDOO implements MemberDTO {
    private Integer id; private UUID playerUuid; private Integer domId; private Integer groupId = 0;
    public Integer getId() { return id; }
    public UUID getPlayerUUID() { return playerUuid; }
    public PlayerDTO getPlayer() { return cn.lunadeer.dominion.cache.CacheManager.instance.getPlayer(playerUuid); }
    public Integer getDomID() { return domId; }
    public Integer getGroupId() { return groupId; }
    public Map<PriFlag, Boolean> getFlagValue() { return new HashMap<>(); }
    public Boolean getFlagValue(PriFlag flag) { return false; }
    public Map<PriFlag, Boolean> getFlagsValue() { return new HashMap<>(); }
    public MemberDTO setFlagValue(PriFlag flag, Boolean value) { return this; }
    public static MemberDOO insert(MemberDOO m) { return m; }
    public static List<MemberDOO> selectAll() { return new ArrayList<>(); }
    public static void deleteById(Integer id) {}
    public static void deleteByPlayerUuid(UUID uuid) {}
}
