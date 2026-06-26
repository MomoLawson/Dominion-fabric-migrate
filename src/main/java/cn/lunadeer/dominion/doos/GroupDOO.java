package cn.lunadeer.dominion.doos;
import cn.lunadeer.dominion.api.dtos.*;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import java.util.*;
public class GroupDOO implements GroupDTO {
    private Integer id; private Integer domId; private String name, nameColored;
    public Integer getId() { return id; }
    public Integer getDomID() { return domId; }
    public String getNameRaw() { return name; }
    public String getName() { return nameColored != null ? nameColored : name; }
    public String getNameColoredBukkit() { return nameColored != null ? nameColored : name; }
    public String getNamePlain() { return name != null ? name.replaceAll("§[0-9a-fk-or]", "") : ""; }
    public net.kyori.adventure.text.Component getNameColoredComponent() { return net.kyori.adventure.text.Component.text(nameColored != null ? nameColored : name); }
    public GroupDTO setName(String n) throws java.sql.SQLException { this.name = n; return this; }
    public Map<PriFlag, Boolean> getFlagValue() { return new HashMap<>(); }
    public Boolean getFlagValue(PriFlag flag) { return false; }
    public Map<PriFlag, Boolean> getFlagsValue() { return new HashMap<>(); }
    public GroupDTO setFlagValue(PriFlag flag, Boolean value) throws java.sql.SQLException { return this; }
    public List<MemberDTO> getMembers() throws java.sql.SQLException { return Collections.emptyList(); }
    public static GroupDOO create(String name, DominionDTO dom) { return new GroupDOO(); }
    public static void deleteById(Integer id) {}
    public static List<GroupDOO> selectAll() { return new ArrayList<>(); }
}
