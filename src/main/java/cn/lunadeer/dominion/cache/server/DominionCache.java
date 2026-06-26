package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.cache.DominionNode;
import cn.lunadeer.dominion.cache.DominionNodeSectored;
import cn.lunadeer.dominion.doos.DominionDOO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Cache for dominion data, indexed by ID, name, owner, and spatial location.
 * <p>
 * Ported from Bukkit: Location-based lookup replaced with (UUID worldUid, int x, int z).
 */
public class DominionCache extends Cache {
    private final Integer serverId;

    private volatile ConcurrentHashMap<Integer, DominionDTO> idDominions;
    private volatile ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> dominionChildrenMap;
    private volatile ConcurrentHashMap<String, Integer> dominionNameToId;
    private volatile ConcurrentHashMap<UUID, CopyOnWriteArrayList<Integer>> playerOwnDominions;
    private volatile ConcurrentHashMap<UUID, CopyOnWriteArrayList<DominionNode>> playerDominionNodes;
    private volatile ConcurrentHashMap<Integer, DominionNode> dominionNodeMap;

    private final DominionNodeSectored dominionNodeSectored = new DominionNodeSectored();

    public DominionCache(Integer serverId) {
        this.serverId = serverId;
    }

    public @Nullable DominionDTO getDominion(@NotNull Integer id) {
        ConcurrentHashMap<Integer, DominionDTO> currentIdDominions = idDominions;
        return currentIdDominions != null ? currentIdDominions.get(id) : null;
    }

    public @Nullable DominionDTO getDominion(String name) {
        ConcurrentHashMap<String, Integer> currentDominionNameToId = dominionNameToId;
        if (currentDominionNameToId == null) return null;

        Integer id = currentDominionNameToId.get(name);
        try {
            if (id == null) return DominionDOO.select(name);
        } catch (Exception e) {
            return null;
        }
        return getDominion(id);
    }

    /**
     * Retrieves a DominionDTO by world UUID and block coordinates.
     *
     * @param worldUid the UUID of the world
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return the DominionDTO at the given location, or null if not found
     */
    public @Nullable DominionDTO getDominion(@NotNull UUID worldUid, int x, int y, int z) {
        return dominionNodeSectored.getDominionByLocation(worldUid, x, y, z);
    }

    public @NotNull CopyOnWriteArrayList<DominionNode> getPlayerDominionNodes(UUID player) {
        ConcurrentHashMap<UUID, CopyOnWriteArrayList<DominionNode>> currentPlayerDominionNodes = playerDominionNodes;
        return currentPlayerDominionNodes != null ? currentPlayerDominionNodes.getOrDefault(player, new CopyOnWriteArrayList<>()) : new CopyOnWriteArrayList<>();
    }

    public @NotNull List<DominionNode> getAllDominionNodes() {
        ConcurrentHashMap<Integer, DominionNode> currentDominionNodeMap = dominionNodeMap;
        return currentDominionNodeMap != null ? new ArrayList<>(currentDominionNodeMap.values()) : new ArrayList<>();
    }

    public @NotNull List<DominionDTO> getChildrenOf(Integer id) {
        ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> currentDominionChildrenMap = dominionChildrenMap;
        if (currentDominionChildrenMap != null && currentDominionChildrenMap.containsKey(id)) {
            return currentDominionChildrenMap.get(id).stream().map(this::getDominion).filter(Objects::nonNull).toList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<String> getAllDominionNames() {
        ConcurrentHashMap<String, Integer> currentDominionNameToId = dominionNameToId;
        return currentDominionNameToId != null ? new ArrayList<>(currentDominionNameToId.keySet()) : new ArrayList<>();
    }

    public CopyOnWriteArrayList<DominionDTO> getPlayerOwnDominionDTOs(UUID player) {
        ConcurrentHashMap<UUID, CopyOnWriteArrayList<Integer>> currentPlayerOwnDominions = playerOwnDominions;
        if (currentPlayerOwnDominions == null) return new CopyOnWriteArrayList<>();

        CopyOnWriteArrayList<Integer> dominionIds = currentPlayerOwnDominions.getOrDefault(player, new CopyOnWriteArrayList<>());
        CopyOnWriteArrayList<DominionDTO> dominions = new CopyOnWriteArrayList<>();
        for (Integer id : dominionIds) {
            DominionDTO dominion = getDominion(id);
            if (dominion != null) {
                dominions.add(dominion);
            }
        }
        return dominions;
    }

    public CopyOnWriteArrayList<DominionDTO> getPlayerOwnTopLevelDominionDTOs(UUID player) {
        CopyOnWriteArrayList<DominionDTO> allDominions = getPlayerOwnDominionDTOs(player);
        CopyOnWriteArrayList<DominionDTO> topLevelDominions = new CopyOnWriteArrayList<>();
        for (DominionDTO dominion : allDominions) {
            if (dominion.getParentDomId() == -1) {
                topLevelDominions.add(dominion);
            }
        }
        return topLevelDominions;
    }

    public List<DominionDTO> getPlayerAdminDominionDTOs(UUID player) {
        CacheManager cacheManager = CacheManager.instance;
        List<MemberDTO> playerBelongedDominionMembers = cacheManager.getCache().getMemberCache().getMemberBelongedDominions(player);

        List<DominionDTO> dominions = new ArrayList<>(playerBelongedDominionMembers.size());

        for (MemberDTO member : playerBelongedDominionMembers) {
            boolean hasAdminRights;

            if (member.getGroupId() == -1) {
                hasAdminRights = member.getFlagValue(Flags.ADMIN);
            } else {
                GroupDTO group = cacheManager.getGroup(member.getGroupId());
                hasAdminRights = group != null && group.getFlagValue(Flags.ADMIN);
            }

            if (hasAdminRights) {
                DominionDTO dominion = getDominion(member.getDomID());
                if (dominion != null) {
                    dominions.add(dominion);
                }
            }
        }

        return dominions;
    }

    public @NotNull List<DominionDTO> getAllDominions() {
        ConcurrentHashMap<Integer, DominionDTO> currentIdDominions = idDominions;
        return currentIdDominions != null ? new ArrayList<>(currentIdDominions.values()) : new ArrayList<>();
    }

    @Override
    void loadExecution() throws Exception {
        ConcurrentHashMap<Integer, DominionDTO> tempIdDominions = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> tempDominionChildrenMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Integer> tempDominionNameToId = new ConcurrentHashMap<>();
        ConcurrentHashMap<UUID, CopyOnWriteArrayList<Integer>> tempPlayerOwnDominions = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, DominionNode> tempDominionNodeMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<UUID, CopyOnWriteArrayList<DominionNode>> tempPlayerDominionNodes = new ConcurrentHashMap<>();

        CopyOnWriteArrayList<DominionDTO> dominions = new CopyOnWriteArrayList<>(DominionDOO.selectAll(serverId));

        dominions.forEach(dominion -> tempIdDominions.put(dominion.getId(), dominion));

        dominions.forEach(dominion -> {
            tempDominionNameToId.put(dominion.getName(), dominion.getId());
            tempPlayerOwnDominions.computeIfAbsent(dominion.getOwner(), k -> new CopyOnWriteArrayList<>()).add(dominion.getId());
            tempDominionChildrenMap.computeIfAbsent(dominion.getParentDomId(), k -> new CopyOnWriteArrayList<>()).add(dominion.getId());
        });

        CopyOnWriteArrayList<DominionNode> nodeTree = DominionNode.BuildNodeTree(-1, dominions);
        parseNodeTree(tempPlayerDominionNodes, tempDominionNodeMap, nodeTree, tempIdDominions);

        synchronized (this) {
            idDominions = tempIdDominions;
            dominionChildrenMap = tempDominionChildrenMap;
            dominionNameToId = tempDominionNameToId;
            playerOwnDominions = tempPlayerOwnDominions;
            dominionNodeMap = tempDominionNodeMap;
            playerDominionNodes = tempPlayerDominionNodes;
            dominionNodeSectored.buildAsync(nodeTree);
        }
    }

    @Override
    void loadExecution(Integer idToLoad) throws Exception {
        DominionDTO dominion = DominionDOO.select(idToLoad);
        if (dominion == null) {
            return;
        }
        DominionDTO oldData = idDominions.put(dominion.getId(), dominion);
        if (oldData != null) {
            dominionNameToId.entrySet().removeIf(entry -> entry.getValue().equals(oldData.getId()));
            playerOwnDominions.computeIfAbsent(oldData.getOwner(), k -> new CopyOnWriteArrayList<>()).remove(oldData.getId());
            dominionChildrenMap.computeIfAbsent(oldData.getParentDomId(), k -> new CopyOnWriteArrayList<>()).remove(oldData.getId());
        }
        dominionNameToId.put(dominion.getName(), dominion.getId());
        playerOwnDominions.computeIfAbsent(dominion.getOwner(), k -> new CopyOnWriteArrayList<>()).add(dominion.getId());
        dominionChildrenMap.computeIfAbsent(dominion.getParentDomId(), k -> new CopyOnWriteArrayList<>()).add(dominion.getId());
        rebuildTreeAsync();
    }

    @Override
    void deleteExecution(Integer idToDelete) throws Exception {
        DominionDTO dominionToDelete = idDominions.remove(idToDelete);
        if (dominionToDelete == null) {
            return;
        }
        dominionChildrenMap.remove(idToDelete);
        if (dominionChildrenMap.containsKey(dominionToDelete.getParentDomId())) {
            dominionChildrenMap.get(dominionToDelete.getParentDomId()).remove(idToDelete);
        }
        dominionNameToId.entrySet().removeIf(entry -> entry.getValue().equals(idToDelete));
        if (playerOwnDominions.containsKey(dominionToDelete.getOwner())) {
            playerOwnDominions.get(dominionToDelete.getOwner()).remove(idToDelete);
        }
        dominionNodeMap.remove(idToDelete);
        rebuildTreeAsync();
    }

    public void dominionNameUpdate(String oldName, String newName, Integer id) {
        if (dominionNameToId != null) {
            dominionNameToId.remove(oldName);
            dominionNameToId.put(newName, id);
        }
    }

    private CompletableFuture<Void> rebuildTreeAsync() {
        return CompletableFuture.runAsync(() -> {
            synchronized (this) {
                ConcurrentHashMap<UUID, CopyOnWriteArrayList<DominionNode>> tempPlayerDominionNodes = new ConcurrentHashMap<>();
                ConcurrentHashMap<Integer, DominionNode> tempDominionNodeMap = new ConcurrentHashMap<>();

                CopyOnWriteArrayList<DominionNode> nodeTree = DominionNode.BuildNodeTree(-1, new CopyOnWriteArrayList<>(idDominions.values()));
                parseNodeTree(tempPlayerDominionNodes, tempDominionNodeMap, nodeTree, idDominions);

                playerDominionNodes = tempPlayerDominionNodes;
                dominionNodeMap = tempDominionNodeMap;
                dominionNodeSectored.buildAsync(nodeTree);
            }
        });
    }

    private static void parseNodeTree(ConcurrentHashMap<UUID, CopyOnWriteArrayList<DominionNode>> tempPlayerDominionNodes,
                                       ConcurrentHashMap<Integer, DominionNode> tempDominionNodeMap,
                                       CopyOnWriteArrayList<DominionNode> nodeTree,
                                       ConcurrentHashMap<Integer, DominionDTO> idDominions) {
        nodeTree.forEach(node -> {
            Integer dominionId = node.getDominionId();
            DominionDTO dominionDTO = idDominions.get(dominionId);
            if (dominionDTO != null) {
                tempDominionNodeMap.put(dominionId, node);
                tempPlayerDominionNodes.computeIfAbsent(dominionDTO.getOwner(), k -> new CopyOnWriteArrayList<>()).add(node);
            }
        });
    }

    public Integer count() {
        ConcurrentHashMap<Integer, DominionDTO> currentIdDominions = idDominions;
        return currentIdDominions != null ? currentIdDominions.size() : 0;
    }

    /**
     * Retrieves all dominions in a specific world.
     *
     * @param worldUid the UUID of the world
     * @return a list of dominions in the given world
     */
    public List<DominionDTO> getDominionsByWorld(@NotNull UUID worldUid) {
        List<DominionDTO> dominions = new ArrayList<>();
        for (DominionDTO dominion : getAllDominions()) {
            if (dominion.getWorldUid().equals(worldUid)) {
                dominions.add(dominion);
            }
        }
        return dominions;
    }
}
