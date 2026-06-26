package cn.lunadeer.dominion.doos;

import cn.lunadeer.dominion.api.dtos.*;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.storage.repository.GroupRepository;
import cn.lunadeer.dominion.storage.repository.GroupRepository.GroupRow;
import cn.lunadeer.dominion.utils.XLogger;

import java.sql.SQLException;
import java.util.*;

public class GroupDOO implements GroupDTO {
    private Integer id;
    private Integer domId;
    private String namePlain;
    private String nameColored;
    private Map<PriFlag, Boolean> flags = new HashMap<>();

    public GroupDOO(GroupRow row) {
        this.id = row.id();
        this.domId = row.domID();
        this.namePlain = row.namePlain();
        this.nameColored = row.nameColored();
        this.flags = row.flags() != null ? new HashMap<>(row.flags()) : new HashMap<>();
    }

    public GroupDOO(Integer domId, String name) {
        this.domId = domId;
        this.namePlain = name;
        this.nameColored = name;
    }

    public GroupDOO() {}

    public Integer getId() { return id; }
    public Integer getDomID() { return domId; }
    public String getNamePlain() { return namePlain; }
    public String getNameRaw() { return namePlain; }
    public String getName() { return nameColored != null ? nameColored : namePlain; }
    public String getNameColoredBukkit() { return nameColored != null ? nameColored : namePlain; }
    public net.kyori.adventure.text.Component getNameColoredComponent() {
        return net.kyori.adventure.text.Component.text(nameColored != null ? nameColored : namePlain);
    }
    public Map<PriFlag, Boolean> getFlagValue() { return flags; }
    public Map<PriFlag, Boolean> getFlagsValue() { return flags; }
    public Boolean getFlagValue(PriFlag flag) { return flags.getOrDefault(flag, false); }
    public List<MemberDTO> getMembers() { return new ArrayList<>(); }

    public GroupDTO setName(String n) {
        this.namePlain = n;
        if (id != null) try { GroupRepository.updateName(id, n, n); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }

    public GroupDTO setFlagValue(PriFlag flag, Boolean value) {
        flags.put(flag, value);
        if (id != null) try { GroupRepository.updateFlag(id, flag, value); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }

    // === Database CRUD ===
    public static GroupDOO create(String name, DominionDTO dominion) {
        try {
            GroupRow row = GroupRepository.create(dominion.getId(), name, name, new HashMap<>());
            if (row != null) {
                XLogger.info("Created group: {0} for dominion {1}", name, dominion.getName());
                return new GroupDOO(row);
            }
        } catch (SQLException e) { XLogger.error("Failed to create group: {0}", e.getMessage()); }
        return null;
    }

    public static void deleteById(Integer id) {
        try { GroupRepository.deleteById(id); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
    }

    public static List<GroupDOO> selectAll() {
        try { return GroupRepository.select().stream().map(GroupDOO::new).toList(); }
        catch (SQLException e) { return new ArrayList<>(); }
    }

    public static List<GroupDOO> selectByDominionId(Integer domId) {
        try { return GroupRepository.selectByDominionId(domId).stream().map(GroupDOO::new).toList(); }
        catch (SQLException e) { return new ArrayList<>(); }
    }
}
