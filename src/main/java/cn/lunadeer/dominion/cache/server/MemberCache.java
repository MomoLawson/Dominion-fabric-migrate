package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.doos.DominionDOO;
import cn.lunadeer.dominion.doos.MemberDOO;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Cache for member data.
 * <p>
 * Ported from Bukkit: Player references replaced with ServerPlayer.
 */
public class MemberCache extends Cache {
    private final Integer serverId;

    private volatile ConcurrentHashMap<Integer, MemberDTO> idMembers;
    private volatile ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> dominionMembersMap;
    private volatile ConcurrentHashMap<UUID, Map<Integer, Integer>> playerDominionMemberMap;
    private volatile ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> groupMembersMap;

    public MemberCache(Integer serverId) {
        this.serverId = serverId;
    }

    public @Nullable MemberDTO getMember(@Nullable DominionDTO dominion, @NotNull ServerPlayer player) {
        return getMember(dominion, player.getUUID());
    }

    public @Nullable MemberDTO getMember(@Nullable DominionDTO dominion, @NotNull UUID player_uuid) {
        if (dominion == null) return null;

        ConcurrentHashMap<UUID, Map<Integer, Integer>> currentPlayerDominionMemberMap = playerDominionMemberMap;
        ConcurrentHashMap<Integer, MemberDTO> currentIdMembers = idMembers;

        if (currentPlayerDominionMemberMap == null || currentIdMembers == null ||
                !currentPlayerDominionMemberMap.containsKey(player_uuid)) {
            return null;
        }

        Map<Integer, Integer> playerMemberMap = currentPlayerDominionMemberMap.get(player_uuid);
        if (playerMemberMap == null) return null;

        Integer member_id = playerMemberMap.get(dominion.getId());
        if (member_id == null) return null;

        return currentIdMembers.get(member_id);
    }

    public List<MemberDTO> getMemberBelongedDominions(@NotNull UUID player) {
        ConcurrentHashMap<UUID, Map<Integer, Integer>> currentPlayerDominionMemberMap = playerDominionMemberMap;
        ConcurrentHashMap<Integer, MemberDTO> currentIdMembers = idMembers;

        if (currentPlayerDominionMemberMap == null || currentIdMembers == null ||
                !currentPlayerDominionMemberMap.containsKey(player)) {
            return new ArrayList<>();
        }

        Map<Integer, Integer> playerMemberMap = currentPlayerDominionMemberMap.get(player);
        if (playerMemberMap == null) return new ArrayList<>();

        Collection<Integer> member_ids = playerMemberMap.values();
        return getMembersByIDs(currentIdMembers, member_ids);
    }

    @NotNull
    private List<MemberDTO> getMembersByIDs(ConcurrentHashMap<Integer, MemberDTO> currentIdMembers, Collection<Integer> member_ids) {
        List<MemberDTO> members = new ArrayList<>();
        for (Integer member_id : member_ids) {
            MemberDTO member = currentIdMembers.get(member_id);
            if (member != null) {
                members.add(member);
            }
        }
        return members;
    }

    public @NotNull List<MemberDTO> getDominionMembers(@NotNull DominionDTO dominion) {
        return getDominionMembers(dominion.getId());
    }

    public @NotNull List<MemberDTO> getDominionMembers(@NotNull Integer dominionId) {
        ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> currentDominionMembersMap = dominionMembersMap;
        return getMembersByIDs(dominionId, currentDominionMembersMap);
    }

    @NotNull
    private List<MemberDTO> getMembersByIDs(@NotNull Integer dominionId,
                                             ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> currentDominionMembersMap) {
        ConcurrentHashMap<Integer, MemberDTO> currentIdMembers = idMembers;

        if (currentDominionMembersMap == null || currentIdMembers == null ||
                !currentDominionMembersMap.containsKey(dominionId)) {
            return new ArrayList<>();
        }

        CopyOnWriteArrayList<Integer> memberIds = currentDominionMembersMap.get(dominionId);
        if (memberIds == null) return new ArrayList<>();

        return getMembersByIDs(currentIdMembers, memberIds);
    }

    public @NotNull List<MemberDTO> getGroupMembers(@NotNull GroupDTO group) {
        return getGroupMembers(group.getId());
    }

    public @NotNull List<MemberDTO> getGroupMembers(@NotNull Integer groupId) {
        ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> currentGroupMembersMap = groupMembersMap;
        return getMembersByIDs(groupId, currentGroupMembersMap);
    }

    @Override
    void loadExecution() throws Exception {
        ConcurrentHashMap<Integer, MemberDTO> tempIdMembers = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> tempDominionMembersMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<UUID, Map<Integer, Integer>> tempPlayerDominionMemberMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> tempGroupMembersMap = new ConcurrentHashMap<>();

        List<MemberDOO> allMembers = MemberDOO.select();
        for (MemberDOO member : allMembers) {
            PlayerDTO player = CacheManager.instance.getPlayer(member.getPlayerUUID());
            if (player == null) {
                DominionDOO.deleteByPlayerUuid(member.getPlayerUUID());
                MemberDOO.deleteByPlayerUuid(member.getPlayerUUID());
                continue;
            }
            DominionDTO dominion = CacheManager.instance.getDominion(member.getDomID());
            if (dominion == null || !Objects.equals(dominion.getServerId(), serverId)) continue;

            tempIdMembers.put(member.getId(), member);
            tempDominionMembersMap.computeIfAbsent(member.getDomID(), k -> new CopyOnWriteArrayList<>())
                    .add(member.getId());
            tempPlayerDominionMemberMap.computeIfAbsent(member.getPlayerUUID(), k -> new ConcurrentHashMap<>())
                    .put(member.getDomID(), member.getId());
            if (member.getGroupId() != -1) {
                tempGroupMembersMap.computeIfAbsent(member.getGroupId(), k -> new CopyOnWriteArrayList<>())
                        .add(member.getId());
            }
        }

        synchronized (this) {
            idMembers = tempIdMembers;
            dominionMembersMap = tempDominionMembersMap;
            playerDominionMemberMap = tempPlayerDominionMemberMap;
            groupMembersMap = tempGroupMembersMap;
        }
    }

    @Override
    void loadExecution(Integer idToLoad) throws Exception {
        MemberDTO member = MemberDOO.select(idToLoad);
        if (member == null) {
            synchronized (this) {
                if (idMembers != null) {
                    MemberDTO removed = idMembers.remove(idToLoad);
                    if (removed != null) {
                        removeFromMappings(removed);
                    }
                }
            }
            return;
        }

        if (idMembers == null || dominionMembersMap == null ||
                playerDominionMemberMap == null || groupMembersMap == null) {
            loadExecution();
            return;
        }

        synchronized (this) {
            MemberDTO old = idMembers.put(member.getId(), member);
            if (old != null) {
                removeFromMappings(old);
            }
            addToMappings(member);
        }
    }

    @Override
    void deleteExecution(Integer idToDelete) throws Exception {
        if (idMembers == null) {
            return;
        }

        synchronized (this) {
            MemberDTO member = idMembers.remove(idToDelete);
            if (member != null) {
                removeFromMappings(member);
            }
        }
    }

    private void removeFromMappings(MemberDTO member) {
        if (dominionMembersMap != null) {
            CopyOnWriteArrayList<Integer> dominionMembers = dominionMembersMap.get(member.getDomID());
            if (dominionMembers != null) {
                dominionMembers.remove(member.getId());
                if (dominionMembers.isEmpty()) {
                    dominionMembersMap.remove(member.getDomID());
                }
            }
        }

        if (playerDominionMemberMap != null) {
            Map<Integer, Integer> playerMemberMap = playerDominionMemberMap.get(member.getPlayerUUID());
            if (playerMemberMap != null) {
                playerMemberMap.remove(member.getDomID());
                if (playerMemberMap.isEmpty()) {
                    playerDominionMemberMap.remove(member.getPlayerUUID());
                }
            }
        }

        if (member.getGroupId() != -1 && groupMembersMap != null) {
            CopyOnWriteArrayList<Integer> groupMembers = groupMembersMap.get(member.getGroupId());
            if (groupMembers != null) {
                groupMembers.remove(member.getId());
                if (groupMembers.isEmpty()) {
                    groupMembersMap.remove(member.getGroupId());
                }
            }
        }
    }

    private void addToMappings(MemberDTO member) {
        if (dominionMembersMap != null) {
            dominionMembersMap.computeIfAbsent(member.getDomID(), k -> new CopyOnWriteArrayList<>())
                    .addIfAbsent(member.getId());
        }

        if (playerDominionMemberMap != null) {
            playerDominionMemberMap.computeIfAbsent(member.getPlayerUUID(), k -> new ConcurrentHashMap<>())
                    .put(member.getDomID(), member.getId());
        }

        if (member.getGroupId() != -1 && groupMembersMap != null) {
            groupMembersMap.computeIfAbsent(member.getGroupId(), k -> new CopyOnWriteArrayList<>())
                    .addIfAbsent(member.getId());
        }
    }

    public Integer count() {
        ConcurrentHashMap<Integer, MemberDTO> currentIdMembers = idMembers;
        return currentIdMembers != null ? currentIdMembers.size() : 0;
    }
}
