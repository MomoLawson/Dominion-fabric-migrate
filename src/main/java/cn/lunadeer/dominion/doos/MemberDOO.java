package cn.lunadeer.dominion.doos;

import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.storage.repository.MemberRepository;
import cn.lunadeer.dominion.storage.repository.MemberRepository.MemberRow;
import cn.lunadeer.dominion.utils.XLogger;

import java.sql.SQLException;
import java.util.*;

public class MemberDOO implements MemberDTO {
    private Integer id;
    private UUID playerUuid;
    private Integer domId;
    private Integer groupId = -1;
    private Map<PriFlag, Boolean> flags = new HashMap<>();

    public MemberDOO(MemberRow row) {
        this.id = row.id();
        this.playerUuid = row.playerUUID();
        this.domId = row.domID();
        this.groupId = row.groupId();
        this.flags = row.flags() != null ? new HashMap<>(row.flags()) : new HashMap<>();
    }

    public MemberDOO(Integer domId, UUID playerUuid) {
        this.domId = domId;
        this.playerUuid = playerUuid;
    }

    public MemberDOO() {}

    public Integer getId() { return id; }
    public UUID getPlayerUUID() { return playerUuid; }
    public PlayerDTO getPlayer() {
        return cn.lunadeer.dominion.cache.CacheManager.instance != null
            ? cn.lunadeer.dominion.cache.CacheManager.instance.getPlayer(playerUuid) : null;
    }
    public Integer getDomID() { return domId; }
    public Integer getGroupId() { return groupId; }
    public Map<PriFlag, Boolean> getFlagValue() { return flags; }
    public Map<PriFlag, Boolean> getFlagsValue() { return flags; }
    public Boolean getFlagValue(PriFlag flag) { return flags.getOrDefault(flag, false); }
    public MemberDTO setFlagValue(PriFlag flag, Boolean value) {
        flags.put(flag, value);
        if (id != null) try { MemberRepository.updateFlag(id, flag, value); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }

    public static MemberDOO insert(MemberDOO m) {
        try {
            MemberRow row = MemberRepository.insert(m.playerUuid, m.domId, m.flags);
            if (row != null) return new MemberDOO(row);
        } catch (SQLException e) { XLogger.error("Failed to insert member: {0}", e.getMessage()); }
        return null;
    }

    public static List<MemberDOO> selectAll() {
        try { return MemberRepository.select().stream().map(MemberDOO::new).toList(); }
        catch (SQLException e) { XLogger.error("Failed to select members: {0}", e.getMessage()); return new ArrayList<>(); }
    }

    public static List<MemberDOO> selectByDominionId(Integer domId) {
        try { return MemberRepository.selectByDominionId(domId).stream().map(MemberDOO::new).toList(); }
        catch (SQLException e) { return new ArrayList<>(); }
    }

    public static void deleteById(Integer id) {
        try { MemberRepository.deleteById(id); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
    }

    public static void deleteByPlayerUuid(UUID uuid) {
        try { MemberRepository.deleteByPlayerUuid(uuid); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
    }
}
