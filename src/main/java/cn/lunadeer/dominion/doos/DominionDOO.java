package cn.lunadeer.dominion.doos;

import cn.lunadeer.dominion.api.dtos.*;
import cn.lunadeer.dominion.api.dtos.flag.*;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.utils.XLogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dominion Data Object - implements DominionDTO with in-memory storage.
 * In production, this would use the database via DominionRepository.
 */
public class DominionDOO implements DominionDTO {
    // In-memory storage for testing
    private static final Map<Integer, DominionDOO> storage = new ConcurrentHashMap<>();
    private static final AtomicInteger idCounter = new AtomicInteger(1);

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

    /**
     * Constructor for creating a new dominion.
     */
    public DominionDOO(UUID owner, String name, UUID worldUid, CuboidDTO cuboid, Integer parentDomId) {
        this.id = idCounter.getAndIncrement();
        this.owner = owner;
        this.name = name;
        this.worldUid = worldUid;
        this.x1 = cuboid.x1();
        this.y1 = cuboid.y1();
        this.z1 = cuboid.z1();
        this.x2 = cuboid.x2();
        this.y2 = cuboid.y2();
        this.z2 = cuboid.z2();
        this.parentDomId = parentDomId;
        this.joinMessage = Configuration.pluginMessage != null ? Configuration.pluginMessage.defaultEnterMessage : "Welcome to {dominion_name}!";
        this.leaveMessage = Configuration.pluginMessage != null ? Configuration.pluginMessage.defaultLeaveMessage : "Leaving {dominion_name}.";
        this.tpX = (x1 + x2) / 2;
        this.tpY = (y1 + y2) / 2;
        this.tpZ = (z1 + z2) / 2;
    }

    /** Default constructor */
    public DominionDOO() {
        this.id = idCounter.getAndIncrement();
    }

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

    // === Setters (return this for chaining) ===
    public DominionDTO setOwner(UUID o) { this.owner = o; return this; }
    public DominionDTO setOwner(net.minecraft.server.level.ServerPlayer p) { return setOwner(p.getUUID()); }
    public DominionDTO setName(String n) { this.name = n; return this; }
    public DominionDTO setCuboid(CuboidDTO c) { x1=c.x1(); y1=c.y1(); z1=c.z1(); x2=c.x2(); y2=c.y2(); z2=c.z2(); return this; }
    public DominionDTO setJoinMessage(String m) { this.joinMessage = m; return this; }
    public DominionDTO setLeaveMessage(String m) { this.leaveMessage = m; return this; }
    public DominionDTO setEnvFlagValue(EnvFlag f, Boolean v) { envFlags.put(f, v); return this; }
    public DominionDTO setGuestFlagValue(PriFlag f, Boolean v) { guestFlags.put(f, v); return this; }
    public DominionDTO setTpLocation(int x, int y, int z) { tpX=x; tpY=y; tpZ=z; return this; }
    public DominionDTO setOwnerGlow(Boolean g) { this.ownerGlow = g; return this; }
    public DominionDTO setColor(int r, int g, int b) { colorR=r; colorG=g; colorB=b; return this; }

    // === Static CRUD operations ===

    /**
     * Insert a new dominion into storage.
     */
    public static DominionDTO insert(DominionDOO d) {
        if (d.id == null) d.id = idCounter.getAndIncrement();
        storage.put(d.id, d);
        XLogger.info("Inserted dominion: {0} (id={1})", d.name, d.id);
        return d;
    }

    /**
     * Delete a dominion by ID.
     */
    public static void deleteById(Integer id) {
        DominionDOO removed = storage.remove(id);
        if (removed != null) {
            XLogger.info("Deleted dominion: {0} (id={1})", removed.name, id);
        }
    }

    /**
     * Select all dominions.
     */
    public static List<DominionDOO> selectAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Select a dominion by ID.
     */
    public static DominionDOO selectById(Integer id) {
        return storage.get(id);
    }

    /**
     * Select dominions by owner UUID.
     */
    public static List<DominionDOO> selectByOwner(UUID owner) {
        return storage.values().stream()
            .filter(d -> d.owner != null && d.owner.equals(owner))
            .toList();
    }

    /**
     * Select dominions by world UID.
     */
    public static List<DominionDOO> selectByWorld(UUID worldUid) {
        return storage.values().stream()
            .filter(d -> d.worldUid != null && d.worldUid.equals(worldUid))
            .toList();
    }

    /**
     * Check if a dominion name exists for a given owner.
     */
    public static boolean nameExists(String name, UUID owner) {
        return storage.values().stream()
            .anyMatch(d -> d.name != null && d.name.equalsIgnoreCase(name) && d.owner != null && d.owner.equals(owner));
    }

    /**
     * Get dominion count.
     */
    public static int count() {
        return storage.size();
    }

    /**
     * Get dominion at a specific location.
     */
    public static DominionDOO getAtLocation(UUID worldUid, int x, int y, int z) {
        // Find the deepest (smallest) dominion containing this point
        DominionDOO best = null;
        long bestVolume = Long.MAX_VALUE;
        for (DominionDOO d : storage.values()) {
            if (d.worldUid != null && d.worldUid.equals(worldUid) &&
                x >= d.x1 && x <= d.x2 && y >= d.y1 && y <= d.y2 && z >= d.z1 && z <= d.z2) {
                long volume = (long)(d.x2 - d.x1) * (d.y2 - d.y1) * (d.z2 - d.z1);
                if (volume < bestVolume) {
                    best = d;
                    bestVolume = volume;
                }
            }
        }
        return best;
    }
}
