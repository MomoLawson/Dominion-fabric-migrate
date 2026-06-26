package cn.lunadeer.dominion.doos;

import cn.lunadeer.dominion.api.dtos.*;
import cn.lunadeer.dominion.api.dtos.flag.*;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.storage.repository.DominionRepository;
import cn.lunadeer.dominion.storage.repository.DominionRepository.DominionRow;
import cn.lunadeer.dominion.utils.XLogger;

import java.sql.SQLException;
import java.util.*;

/**
 * Dominion Data Object - implements DominionDTO with database persistence via DominionRepository.
 */
public class DominionDOO implements DominionDTO {
    private Integer id;
    private UUID owner;
    private String name;
    private UUID worldUid;
    private int x1, y1, z1, x2, y2, z2;
    private Integer parentDomId = -1;
    private String joinMessage = "";
    private String leaveMessage = "";
    private int tpX, tpY, tpZ;
    private int colorR = 255, colorG = 255, colorB = 255;
    private Boolean ownerGlow = false;
    private Map<EnvFlag, Boolean> envFlags = new HashMap<>();
    private Map<PriFlag, Boolean> guestFlags = new HashMap<>();

    /** Create from database row */
    public DominionDOO(DominionRow row) {
        this.id = row.id();
        this.owner = row.owner();
        this.name = row.name();
        this.worldUid = row.worldUid();
        this.x1 = row.x1(); this.y1 = row.y1(); this.z1 = row.z1();
        this.x2 = row.x2(); this.y2 = row.y2(); this.z2 = row.z2();
        this.parentDomId = row.parentDomId();
        this.joinMessage = row.joinMessage() != null ? row.joinMessage() : "";
        this.leaveMessage = row.leaveMessage() != null ? row.leaveMessage() : "";
        this.envFlags = row.envFlags() != null ? new HashMap<>(row.envFlags()) : new HashMap<>();
        this.guestFlags = row.guestFlags() != null ? new HashMap<>(row.guestFlags()) : new HashMap<>();
        this.ownerGlow = row.ownerGlow();
        // Parse color
        String colorStr = row.color();
        if (colorStr != null && colorStr.startsWith("#") && colorStr.length() == 7) {
            try {
                this.colorR = Integer.parseInt(colorStr.substring(1, 3), 16);
                this.colorG = Integer.parseInt(colorStr.substring(3, 5), 16);
                this.colorB = Integer.parseInt(colorStr.substring(5, 7), 16);
            } catch (NumberFormatException ignored) {}
        }
        // Parse TP location
        String tpStr = row.tpLocation();
        if (tpStr != null && !tpStr.isEmpty()) {
            String[] parts = tpStr.split(",");
            if (parts.length >= 3) {
                try {
                    this.tpX = Integer.parseInt(parts[0]);
                    this.tpY = Integer.parseInt(parts[1]);
                    this.tpZ = Integer.parseInt(parts[2]);
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    /** Constructor for creating new dominion */
    public DominionDOO(UUID owner, String name, UUID worldUid, CuboidDTO cuboid, Integer parentDomId) {
        this.owner = owner;
        this.name = name;
        this.worldUid = worldUid;
        this.x1 = cuboid.x1(); this.y1 = cuboid.y1(); this.z1 = cuboid.z1();
        this.x2 = cuboid.x2(); this.y2 = cuboid.y2(); this.z2 = cuboid.z2();
        this.parentDomId = parentDomId;
        this.joinMessage = Configuration.pluginMessage != null ? Configuration.pluginMessage.defaultEnterMessage : "Welcome!";
        this.leaveMessage = Configuration.pluginMessage != null ? Configuration.pluginMessage.defaultLeaveMessage : "Leaving.";
        this.tpX = (x1 + x2) / 2; this.tpY = (y1 + y2) / 2; this.tpZ = (z1 + z2) / 2;
    }

    public DominionDOO() {}

    // === Getters ===
    public Integer getId() { return id; }
    public UUID getOwner() { return owner; }
    public PlayerDTO getOwnerDTO() { return cn.lunadeer.dominion.cache.CacheManager.instance != null ? cn.lunadeer.dominion.cache.CacheManager.instance.getPlayer(owner) : null; }
    public String getName() { return name; }
    public UUID getWorldUid() { return worldUid; }
    public CuboidDTO getCuboid() { return new CuboidDTO(x1, y1, z1, x2, y2, z2); }
    public Integer getParentDomId() { return parentDomId; }
    public String getJoinMessage() { return joinMessage; }
    public String getLeaveMessage() { return leaveMessage; }
    public Map<EnvFlag, Boolean> getEnvironmentFlagValue() { return envFlags; }
    public boolean getEnvFlagValue(EnvFlag f) { return envFlags.getOrDefault(f, false); }
    public Map<PriFlag, Boolean> getGuestPrivilegeFlagValue() { return guestFlags; }
    public boolean getGuestFlagValue(PriFlag f) { return guestFlags.getOrDefault(f, false); }
    public int getTpLocationX() { return tpX; }
    public int getTpLocationY() { return tpY; }
    public int getTpLocationZ() { return tpZ; }
    public int getColorR() { return colorR; }
    public int getColorG() { return colorG; }
    public int getColorB() { return colorB; }
    public String getColor() { return String.format("#%02x%02x%02x", colorR, colorG, colorB); }
    public int getColorHex() { return (colorR << 16) | (colorG << 8) | colorB; }
    public List<GroupDTO> getGroups() { return new ArrayList<>(); }
    public List<MemberDTO> getMembers() { return new ArrayList<>(); }
    public Integer getServerId() { return 0; }
    public Boolean getOwnerGlow() { return ownerGlow; }

    // === Setters with database persistence ===
    public DominionDTO setOwner(UUID o) {
        this.owner = o;
        if (id != null) try { DominionRepository.updateOwner(id, o); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }
    public DominionDTO setOwner(net.minecraft.server.level.ServerPlayer p) { return setOwner(p.getUUID()); }
    public DominionDTO setName(String n) {
        this.name = n;
        if (id != null) try { DominionRepository.updateName(id, n); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }
    public DominionDTO setCuboid(CuboidDTO c) {
        x1=c.x1(); y1=c.y1(); z1=c.z1(); x2=c.x2(); y2=c.y2(); z2=c.z2();
        if (id != null) try { DominionRepository.updateCuboid(id, x1, y1, z1, x2, y2, z2); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }
    public DominionDTO setJoinMessage(String m) {
        this.joinMessage = m;
        if (id != null) try { DominionRepository.updateJoinMessage(id, m); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }
    public DominionDTO setLeaveMessage(String m) {
        this.leaveMessage = m;
        if (id != null) try { DominionRepository.updateLeaveMessage(id, m); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }
    public DominionDTO setEnvFlagValue(EnvFlag f, Boolean v) {
        envFlags.put(f, v);
        if (id != null) try { DominionRepository.updateEnvFlag(id, f, v); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }
    public DominionDTO setGuestFlagValue(PriFlag f, Boolean v) {
        guestFlags.put(f, v);
        if (id != null) try { DominionRepository.updateGuestFlag(id, f, v); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }
    public DominionDTO setTpLocation(int x, int y, int z) {
        tpX=x; tpY=y; tpZ=z;
        if (id != null) try { DominionRepository.updateTpLocation(id, x+","+y+","+z); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }
    public DominionDTO setOwnerGlow(Boolean g) {
        this.ownerGlow = g;
        if (id != null) try { DominionRepository.updateOwnerGlow(id, g); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }
    public DominionDTO setColor(int r, int g, int b) {
        colorR=r; colorG=g; colorB=b;
        if (id != null) try { DominionRepository.updateColor(id, getColor()); } catch (SQLException e) { XLogger.error("DB error: {0}", e.getMessage()); }
        return this;
    }

    // === Database CRUD ===

    public static DominionDTO insert(DominionDOO d) {
        try {
            DominionRow row = new DominionRow(null, d.owner, d.name, d.worldUid,
                d.x1, d.y1, d.z1, d.x2, d.y2, d.z2, d.parentDomId,
                d.joinMessage, d.leaveMessage, d.envFlags, d.guestFlags,
                d.tpX+","+d.tpY+","+d.tpZ, String.format("#%02x%02x%02x", d.colorR, d.colorG, d.colorB),
                0, d.ownerGlow);
            DominionRow inserted = DominionRepository.insert(row);
            if (inserted != null) {
                XLogger.info("Inserted dominion: {0} (id={1})", d.name, inserted.id());
                return new DominionDOO(inserted);
            }
        } catch (SQLException e) {
            XLogger.error("Failed to insert dominion: {0}", e.getMessage());
        }
        return null;
    }

    public static void deleteById(Integer id) {
        try {
            DominionRepository.deleteById(id);
            XLogger.info("Deleted dominion id={0}", id);
        } catch (SQLException e) {
            XLogger.error("Failed to delete dominion: {0}", e.getMessage());
        }
    }

    public static List<DominionDOO> selectAll() {
        try {
            return DominionRepository.selectAll(0).stream().map(DominionDOO::new).toList();
        } catch (SQLException e) {
            XLogger.error("Failed to select dominions: {0}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public static DominionDOO selectById(Integer id) {
        try {
            DominionRow row = DominionRepository.select(id);
            return row != null ? new DominionDOO(row) : null;
        } catch (SQLException e) {
            XLogger.error("Failed to select dominion: {0}", e.getMessage());
            return null;
        }
    }

    public static List<DominionDOO> selectByOwner(UUID owner) {
        return selectAll().stream().filter(d -> d.owner != null && d.owner.equals(owner)).toList();
    }

    public static boolean nameExists(String name, UUID owner) {
        try {
            DominionRow row = DominionRepository.select(name);
            return row != null;
        } catch (SQLException e) {
            return false;
        }
    }

    public static int count() { return selectAll().size(); }
}
