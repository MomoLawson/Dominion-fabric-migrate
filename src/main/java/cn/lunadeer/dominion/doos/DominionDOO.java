package cn.lunadeer.dominion.doos;
import cn.lunadeer.dominion.api.dtos.*;
import cn.lunadeer.dominion.api.dtos.flag.*;
import java.util.*;
public class DominionDOO implements DominionDTO {
    private Integer id; private UUID owner; private String name; private UUID worldUid;
    private int x1, y1, z1, x2, y2, z2; private Integer parentDomId = 0;
    private String joinMessage = "", leaveMessage = ""; private int tpX, tpY, tpZ;
    private int colorR = 255, colorG = 255, colorB = 255; private Boolean ownerGlow = false;
    private Map<EnvFlag, Boolean> envFlags = new HashMap<>();
    private Map<PriFlag, Boolean> guestFlags = new HashMap<>();
    public Integer getId() { return id; }
    public UUID getOwner() { return owner; }
    public PlayerDTO getOwnerDTO() { return cn.lunadeer.dominion.cache.CacheManager.instance.getPlayer(owner); }
    public DominionDTO setOwner(UUID o) { this.owner = o; return this; }
    public DominionDTO setOwner(net.minecraft.server.level.ServerPlayer p) { return setOwner(p.getUUID()); }
    public String getName() { return name; }
    public DominionDTO setName(String n) { this.name = n; return this; }
    public UUID getWorldUid() { return worldUid; }
    public CuboidDTO getCuboid() { return new CuboidDTO(x1, y1, z1, x2, y2, z2); }
    public DominionDTO setCuboid(CuboidDTO c) { x1=c.x1();y1=c.y1();z1=c.z1();x2=c.x2();y2=c.y2();z2=c.z2(); return this; }
    public Integer getParentDomId() { return parentDomId; }
    public String getJoinMessage() { return joinMessage; }
    public DominionDTO setJoinMessage(String m) { this.joinMessage = m; return this; }
    public String getLeaveMessage() { return leaveMessage; }
    public DominionDTO setLeaveMessage(String m) { this.leaveMessage = m; return this; }
    public Map<EnvFlag, Boolean> getEnvironmentFlagValue() { return envFlags; }
    public boolean getEnvFlagValue(EnvFlag f) { return envFlags.getOrDefault(f, false); }
    public Map<PriFlag, Boolean> getGuestPrivilegeFlagValue() { return guestFlags; }
    public boolean getGuestFlagValue(PriFlag f) { return guestFlags.getOrDefault(f, false); }
    public DominionDTO setEnvFlagValue(EnvFlag f, Boolean v) { envFlags.put(f, v); return this; }
    public DominionDTO setGuestFlagValue(PriFlag f, Boolean v) { guestFlags.put(f, v); return this; }
    public int getTpLocationX() { return tpX; }
    public int getTpLocationY() { return tpY; }
    public int getTpLocationZ() { return tpZ; }
    public DominionDTO setTpLocation(int x, int y, int z) { tpX=x;tpY=y;tpZ=z; return this; }
    public DominionDTO setOwnerGlow(Boolean g) { this.ownerGlow = g; return this; }
    public int getColorR() { return colorR; }
    public int getColorG() { return colorG; }
    public int getColorB() { return colorB; }
    public String getColor() { return String.format("#%02x%02x%02x", colorR, colorG, colorB); }
    public int getColorHex() { return (colorR << 16) | (colorG << 8) | colorB; }
    public DominionDTO setColor(int r, int g, int b) { colorR=r;colorG=g;colorB=b; return this; }
    public List<GroupDTO> getGroups() { return Collections.emptyList(); }
    public List<MemberDTO> getMembers() { return Collections.emptyList(); }
    public Integer getServerId() { return 0; }
    public Boolean getOwnerGlow() { return ownerGlow; }
    public static List<DominionDOO> selectAll() { return new ArrayList<>(); }
    public static DominionDTO insert(DominionDOO d) { return d; }
    public static void deleteById(Integer id) {}
}
