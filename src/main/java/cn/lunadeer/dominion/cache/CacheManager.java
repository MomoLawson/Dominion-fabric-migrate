package cn.lunadeer.dominion.cache;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.cache.server.DominionCache;
import cn.lunadeer.dominion.cache.server.PlayerCache;
import cn.lunadeer.dominion.cache.server.ResidenceDataCache;
import cn.lunadeer.dominion.cache.server.ServerCache;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.doos.DominionDOO;
import cn.lunadeer.dominion.doos.PlayerDOO;
import cn.lunadeer.dominion.api.events.FabricEventBus;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.utils.AutoTimer;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.XLogger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static cn.lunadeer.dominion.misc.Others.isInDominion;

/**
 * Manages the cache for the server and other servers.
 * <p>
 * Ported from Bukkit: Location/Player replaced with coordinates+worldUUID / ServerPlayer.
 * Bukkit event registration replaced with Fabric lifecycle event registration.
 */
public class CacheManager {
    private final ServerCache thisServerCache;
    private final ConcurrentHashMap<Integer, ServerCache> otherServerCaches = new ConcurrentHashMap<>();
    private final PlayerCache playerCache;
    private final ResidenceDataCache residenceDataCache = new ResidenceDataCache();

    private final ConcurrentHashMap<UUID, Integer> playerCurrentDominionId = new ConcurrentHashMap<>();

    public static CacheManager instance;

    public static final Long UPDATE_INTERVAL = 1000 * 4L;

    /**
     * Constructs a CacheManager and initializes the server cache.
     */
    public CacheManager() {
        instance = this;

        this.playerCache = new PlayerCache();
        this.playerCache.load();

        this.thisServerCache = new ServerCache(Configuration.multiServer.serverId);
        this.thisServerCache.getDominionCache().load();
        this.thisServerCache.getMemberCache().load();
        this.thisServerCache.getGroupCache().load();
    }

    // ******************************************************************************************************************
    // * Cache Management Methods
    // ******************************************************************************************************************

    public ServerCache getCache() {
        return thisServerCache;
    }

    public Map<Integer, ServerCache> getOtherServerCaches() {
        return otherServerCaches;
    }

    public @Nullable ServerCache getCache(Integer id) {
        if (thisServerCache.getServerId().equals(id)) {
            return thisServerCache;
        }
        return otherServerCaches.get(id);
    }

    public void addServerCache(@NotNull Integer serverId) {
        ServerCache serverCache = new ServerCache(serverId);
        otherServerCaches.put(serverId, serverCache);
    }

    public void reloadCache() {
        thisServerCache.getDominionCache().load();
        thisServerCache.getMemberCache().load();
        thisServerCache.getGroupCache().load();
    }

    public void reloadServerCache(@NotNull Integer serverId) {
        CompletableFuture.runAsync(() -> {
            if (!otherServerCaches.containsKey(serverId)) {
                XLogger.debug("Server cache not found for serverId: {0}", serverId);
                return;
            }
            otherServerCaches.get(serverId).getDominionCache().load();
            otherServerCaches.get(serverId).getMemberCache().load();
            otherServerCaches.get(serverId).getGroupCache().load();
        });
    }

    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    public ResidenceDataCache getResidenceCache() {
        return residenceDataCache;
    }

    // ******************************************************************************************************************
    // * Player Cache Methods
    // ******************************************************************************************************************

    /**
     * Updates the player's name in the cache.
     *
     * @param fabricPlayer the ServerPlayer representing the player
     */
    public void updatePlayerName(@NotNull ServerPlayer fabricPlayer) throws Exception {
        URL skin = null;
        // Skin URL retrieval would go here if needed

        String playerName = fabricPlayer.getName().getString();
        PlayerDTO playerWithSameName = playerCache.getPlayer(playerName);
        if (playerWithSameName != null && !playerWithSameName.getUUID().equals(fabricPlayer.getUUID())) {
            Notification.warn(fabricPlayer, "Another player with the same name \"{0}\"({1}) exists in the Dominion. " +
                            "This may caused by a name change, your player name will be modified to avoid conflicts.",
                    playerName, playerWithSameName.getUUID());
            playerName = playerName + "_" + fabricPlayer.getUUID().toString().substring(0, 8);
        }

        PlayerDTO player = playerCache.getPlayer(fabricPlayer.getUUID());
        if (player == null)
            player = PlayerDOO.create(fabricPlayer.getUUID(), playerName);
        player.updateLastKnownName(playerName, skin);
    }

    public @Nullable PlayerDTO getPlayer(String name) {
        return playerCache.getPlayer(name);
    }

    public @Nullable PlayerDTO getPlayer(@NotNull UUID player) {
        return playerCache.getPlayer(player);
    }

    public @NotNull String getPlayerName(@NotNull UUID uuid) {
        return playerCache.getPlayerName(uuid);
    }

    public @NotNull List<String> getPlayerNames() {
        return playerCache.getPlayerNames();
    }

    // ******************************************************************************************************************
    // * Dominion Cache Methods
    // ******************************************************************************************************************

    public List<String> getPlayerManageDominionNames(@NotNull UUID player) {
        List<DominionDTO> dominions = new ArrayList<>(thisServerCache.getDominionCache().getPlayerOwnDominionDTOs(player));
        dominions.addAll(thisServerCache.getDominionCache().getPlayerAdminDominionDTOs(player));
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominions.addAll(serverCache.getDominionCache().getPlayerOwnDominionDTOs(player));
                dominions.addAll(serverCache.getDominionCache().getPlayerAdminDominionDTOs(player));
            }
        }
        List<String> names = new ArrayList<>(dominions.size());
        for (DominionDTO dominion : dominions) {
            names.add(dominion.getName());
        }
        return names;
    }

    public List<String> getAllDominionNames() {
        List<String> names = new ArrayList<>(thisServerCache.getDominionCache().getAllDominionNames());
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                names.addAll(serverCache.getDominionCache().getAllDominionNames());
            }
        }
        return names;
    }

    public List<DominionDTO> getAllDominions() {
        List<DominionDTO> dominions = new ArrayList<>(thisServerCache.getDominionCache().getAllDominions());
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominions.addAll(serverCache.getDominionCache().getAllDominions());
            }
        }
        return dominions;
    }

    public List<DominionDTO> getChildrenDominionOf(DominionDTO parent) {
        List<DominionDTO> dominions = new ArrayList<>(thisServerCache.getDominionCache().getChildrenOf(parent.getId()));
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominions.addAll(serverCache.getDominionCache().getChildrenOf(parent.getId()));
            }
        }
        return dominions;
    }

    public @Nullable DominionDTO getDominion(Integer id) {
        DominionDTO dominion = thisServerCache.getDominionCache().getDominion(id);
        if (dominion != null) {
            return dominion;
        } else if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominion = serverCache.getDominionCache().getDominion(id);
                if (dominion != null) {
                    return dominion;
                }
            }
        }
        try {
            return DominionDOO.select(id);
        } catch (Exception ignore) {
            return null;
        }
    }

    public @Nullable DominionDTO getDominion(String name) {
        DominionDTO dominion = thisServerCache.getDominionCache().getDominion(name);
        if (dominion != null) {
            return dominion;
        } else if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominion = serverCache.getDominionCache().getDominion(name);
                if (dominion != null) {
                    return dominion;
                }
            }
        }
        try {
            return DominionDOO.select(name);
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * Retrieves a dominion by world UUID and block coordinates.
     *
     * @param worldUid the UUID of the world
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return the DominionDTO at the given location, or null if not found
     */
    public @Nullable DominionDTO getDominion(UUID worldUid, int x, int y, int z) {
        return thisServerCache.getDominionCache().getDominion(worldUid, x, y, z);
    }

    public List<DominionDTO> getPlayerOwnDominionDTOs(UUID player) {
        List<DominionDTO> dominions = new ArrayList<>(thisServerCache.getDominionCache().getPlayerOwnDominionDTOs(player));
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominions.addAll(serverCache.getDominionCache().getPlayerOwnDominionDTOs(player));
            }
        }
        return dominions;
    }

    public List<DominionDTO> getPlayerAdminDominionDTOs(UUID player) {
        List<DominionDTO> dominions = new ArrayList<>(thisServerCache.getDominionCache().getPlayerAdminDominionDTOs(player));
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominions.addAll(serverCache.getDominionCache().getPlayerAdminDominionDTOs(player));
            }
        }
        return dominions;
    }

    // ******************************************************************************************************************
    // * Member Cache Methods
    // ******************************************************************************************************************

    public @Nullable MemberDTO getMember(@Nullable DominionDTO dominion, @NotNull ServerPlayer player) {
        return getMember(dominion, player.getUUID());
    }

    public @Nullable MemberDTO getMember(@Nullable DominionDTO dominion, @NotNull UUID player) {
        MemberDTO member = thisServerCache.getMemberCache().getMember(dominion, player);
        if (member != null) {
            return member;
        } else if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                member = serverCache.getMemberCache().getMember(dominion, player);
                if (member != null) {
                    return member;
                }
            }
        }
        return null;
    }

    // ******************************************************************************************************************
    // * Group Cache Methods
    // ******************************************************************************************************************

    public @Nullable GroupDTO getGroup(MemberDTO member) {
        return getGroup(member.getGroupId());
    }

    public @Nullable GroupDTO getGroup(Integer id) {
        if (id == null) return null;
        if (id == -1) return null;
        GroupDTO group = thisServerCache.getGroupCache().getGroup(id);
        if (group != null) {
            return group;
        } else if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                group = serverCache.getGroupCache().getGroup(id);
                if (group != null) {
                    return group;
                }
            }
        }
        return null;
    }

    // ******************************************************************************************************************
    // * Status Check Methods
    // ******************************************************************************************************************

    /**
     * Retrieves the current dominion of a player based on their position.
     * <p>
     * Ported from Bukkit: player.getLocation() replaced with player.blockPosition() / player.getUUID()
     * for world, x, y, z extraction.
     */
    public @Nullable DominionDTO getPlayerCurrentDominion(@NotNull ServerPlayer player) {
        try (AutoTimer ignored = new AutoTimer(Configuration.timer)) {
            UUID playerUuid = player.getUUID();
            UUID worldUid = player.level().dimension().location();
            int px = player.getBlockX();
            int py = player.getBlockY();
            int pz = player.getBlockZ();

            Integer last_in_dom_id = playerCurrentDominionId.get(playerUuid);
            DominionDTO last_dominion = null;
            DominionCache cache = thisServerCache.getDominionCache();
            if (last_in_dom_id != null) {
                last_dominion = cache.getDominion(last_in_dom_id);
            }

            // if player still in the same dominion, and the dominion has no children
            if (isInDominion(last_dominion, worldUid, px, py, pz)
                    && cache.getChildrenOf(last_in_dom_id).isEmpty()) {
                return last_dominion;
            }

            // or get the current dominion
            DominionDTO current_dominion = cache.getDominion(worldUid, px, py, pz);
            int last_dom_id = last_dominion == null ? -1 : last_dominion.getId();
            int current_dom_id = current_dominion == null ? -1 : current_dominion.getId();

            // if last dominion is null, but last dominion id is not null, trigger player move out dominion event
            if (last_dominion == null && last_in_dom_id != null) {
                FabricEventBus.PlayerMoveOutDominionCallback.EVENT.invoker().onPlayerMoveOutDominion(player, null);
            }

            if (last_dom_id == current_dom_id) {
                return last_dominion;
            }

            // trigger player cross dominion border event
            FabricEventBus.PlayerCrossDominionBorderCallback.EVENT.invoker().onPlayerCrossDominionBorder(player, last_dominion, current_dominion);

            if (last_dom_id != -1) {
                FabricEventBus.PlayerMoveOutDominionCallback.EVENT.invoker().onPlayerMoveOutDominion(player, last_dominion);
            }
            if (current_dom_id != -1) {
                FabricEventBus.PlayerMoveInDominionCallback.EVENT.invoker().onPlayerMoveInDominion(player, current_dominion);
            }

            if (current_dominion == null) {
                playerCurrentDominionId.remove(playerUuid);
                return null;
            } else {
                playerCurrentDominionId.put(playerUuid, current_dominion.getId());
                return current_dominion;
            }
        }
    }

    public void resetPlayerCurrentDominionId(@NotNull ServerPlayer player) {
        playerCurrentDominionId.remove(player.getUUID());
    }

    // ******************************************************************************************************************
    // * Miscellaneous Methods
    // ******************************************************************************************************************

    public Integer dominionCount() {
        int count = thisServerCache.getDominionCache().count();
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                count += serverCache.getDominionCache().count();
            }
        }
        return count;
    }

    public Integer groupCount() {
        int count = thisServerCache.getGroupCache().count();
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                count += serverCache.getGroupCache().count();
            }
        }
        return count;
    }

    public Integer memberCount() {
        int count = thisServerCache.getMemberCache().count();
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                count += serverCache.getMemberCache().count();
            }
        }
        return count;
    }
}
