package cn.lunadeer.dominion.api.events;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.api.events.dominion.modify.DominionReSizeEvent;
import cn.lunadeer.dominion.api.events.dominion.modify.DominionSetMessageEvent;
import cn.lunadeer.dominion.utils.McaRecord;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Central registry for all Fabric-compatible Dominion event callbacks.
 * <p>
 * Each callback is a functional interface registered as a Fabric Event, allowing
 * other mods to listen for Dominion events without depending on Bukkit.
 */
public final class FabricEventBus {

    // ========== Player movement events ==========

    /** Fired when a player moves into a dominion. */
    public interface PlayerMoveInDominionCallback {
        Event<PlayerMoveInDominionCallback> EVENT = EventFactory.createArrayBacked(
                PlayerMoveInDominionCallback.class,
                listeners -> (player, dominion) -> {
                    for (PlayerMoveInDominionCallback listener : listeners) {
                        listener.onPlayerMoveInDominion(player, dominion);
                    }
                }
        );
        void onPlayerMoveInDominion(@NotNull ServerPlayer player, @NotNull DominionDTO dominion);
    }

    /** Fired when a player moves out of a dominion. dominion may be null if deleted. */
    public interface PlayerMoveOutDominionCallback {
        Event<PlayerMoveOutDominionCallback> EVENT = EventFactory.createArrayBacked(
                PlayerMoveOutDominionCallback.class,
                listeners -> (player, dominion) -> {
                    for (PlayerMoveOutDominionCallback listener : listeners) {
                        listener.onPlayerMoveOutDominion(player, dominion);
                    }
                }
        );
        void onPlayerMoveOutDominion(@NotNull ServerPlayer player, @Nullable DominionDTO dominion);
    }

    /** Fired when a player crosses a dominion border. from/to may be null. */
    public interface PlayerCrossDominionBorderCallback {
        Event<PlayerCrossDominionBorderCallback> EVENT = EventFactory.createArrayBacked(
                PlayerCrossDominionBorderCallback.class,
                listeners -> (player, from, to) -> {
                    for (PlayerCrossDominionBorderCallback listener : listeners) {
                        listener.onPlayerCrossDominionBorder(player, from, to);
                    }
                }
        );
        void onPlayerCrossDominionBorder(@NotNull ServerPlayer player,
                                          @Nullable DominionDTO from, @Nullable DominionDTO to);
    }

    // ========== Dominion lifecycle events ==========

    /** Fired when a dominion is being created. Return false to cancel. */
    public interface DominionCreateCallback {
        Event<DominionCreateCallback> EVENT = EventFactory.createArrayBacked(
                DominionCreateCallback.class,
                listeners -> (operator, name, owner, worldUid, cuboid, parent, skipEconomyHolder) -> {
                    boolean allow = true;
                    for (DominionCreateCallback listener : listeners) {
                        if (!listener.onDominionCreate(operator, name, owner, worldUid, cuboid, parent, skipEconomyHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onDominionCreate(@NotNull CommandSourceStack operator,
                                 @NotNull String name, @NotNull UUID owner,
                                 @NotNull UUID worldUid, @NotNull cn.lunadeer.dominion.api.dtos.CuboidDTO cuboid,
                                 @Nullable DominionDTO parent,
                                 @NotNull MutableBoolean skipEconomyHolder);
    }

    /** Fired when a dominion is being deleted. Return false to cancel. */
    public interface DominionDeleteCallback {
        Event<DominionDeleteCallback> EVENT = EventFactory.createArrayBacked(
                DominionDeleteCallback.class,
                listeners -> (operator, dominion, skipEconomyHolder, forceHolder) -> {
                    boolean allow = true;
                    for (DominionDeleteCallback listener : listeners) {
                        if (!listener.onDominionDelete(operator, dominion, skipEconomyHolder, forceHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onDominionDelete(@NotNull CommandSourceStack operator,
                                 @NotNull DominionDTO dominion,
                                 @NotNull MutableBoolean skipEconomyHolder,
                                 @NotNull MutableBoolean forceHolder);
    }

    // ========== Dominion modify events ==========

    /** Fired when a dominion is being renamed. Return false to cancel. */
    public interface DominionRenameCallback {
        Event<DominionRenameCallback> EVENT = EventFactory.createArrayBacked(
                DominionRenameCallback.class,
                listeners -> (operator, dominion, oldName, newNameHolder) -> {
                    boolean allow = true;
                    for (DominionRenameCallback listener : listeners) {
                        if (!listener.onDominionRename(operator, dominion, oldName, newNameHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onDominionRename(@NotNull CommandSourceStack operator,
                                 @NotNull DominionDTO dominion,
                                 @NotNull String oldName,
                                 @NotNull MutableString newNameHolder);
    }

    /** Fired when a dominion is being resized. Return false to cancel. */
    public interface DominionReSizeCallback {
        Event<DominionReSizeCallback> EVENT = EventFactory.createArrayBacked(
                DominionReSizeCallback.class,
                listeners -> (operator, dominion, type, direction, sizeHolder, skipEconomyHolder) -> {
                    boolean allow = true;
                    for (DominionReSizeCallback listener : listeners) {
                        if (!listener.onDominionReSize(operator, dominion, type, direction, sizeHolder, skipEconomyHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onDominionReSize(@NotNull CommandSourceStack operator,
                                 @NotNull DominionDTO dominion,
                                 @NotNull DominionReSizeEvent.TYPE type,
                                 @NotNull DominionReSizeEvent.DIRECTION direction,
                                 @NotNull MutableInt sizeHolder,
                                 @NotNull MutableBoolean skipEconomyHolder);
    }

    /** Fired when a dominion is being transferred. Return false to cancel. */
    public interface DominionTransferCallback {
        Event<DominionTransferCallback> EVENT = EventFactory.createArrayBacked(
                DominionTransferCallback.class,
                listeners -> (operator, dominion, newOwnerHolder, forceHolder) -> {
                    boolean allow = true;
                    for (DominionTransferCallback listener : listeners) {
                        if (!listener.onDominionTransfer(operator, dominion, newOwnerHolder, forceHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onDominionTransfer(@NotNull CommandSourceStack operator,
                                   @NotNull DominionDTO dominion,
                                   @NotNull MutableObject<PlayerDTO> newOwnerHolder,
                                   @NotNull MutableBoolean forceHolder);
    }

    /** Fired when a dominion teleport location is being set. Return false to cancel. */
    public interface DominionSetTpLocationCallback {
        Event<DominionSetTpLocationCallback> EVENT = EventFactory.createArrayBacked(
                DominionSetTpLocationCallback.class,
                listeners -> (operator, dominion, xHolder, yHolder, zHolder) -> {
                    boolean allow = true;
                    for (DominionSetTpLocationCallback listener : listeners) {
                        if (!listener.onSetTpLocation(operator, dominion, xHolder, yHolder, zHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onSetTpLocation(@NotNull CommandSourceStack operator,
                                @NotNull DominionDTO dominion,
                                @NotNull MutableInt xHolder, @NotNull MutableInt yHolder, @NotNull MutableInt zHolder);
    }

    /** Fired when a dominion message is being set. Return false to cancel. */
    public interface DominionSetMessageCallback {
        Event<DominionSetMessageCallback> EVENT = EventFactory.createArrayBacked(
                DominionSetMessageCallback.class,
                listeners -> (operator, dominion, type, oldMessage, newMessageHolder) -> {
                    boolean allow = true;
                    for (DominionSetMessageCallback listener : listeners) {
                        if (!listener.onSetMessage(operator, dominion, type, oldMessage, newMessageHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onSetMessage(@NotNull CommandSourceStack operator,
                             @NotNull DominionDTO dominion,
                             @NotNull DominionSetMessageEvent.TYPE type,
                             @NotNull String oldMessage,
                             @NotNull MutableString newMessageHolder);
    }

    /** Fired when a dominion map color is being set. Return false to cancel. */
    public interface DominionSetMapColorCallback {
        Event<DominionSetMapColorCallback> EVENT = EventFactory.createArrayBacked(
                DominionSetMapColorCallback.class,
                listeners -> (operator, dominion, rHolder, gHolder, bHolder) -> {
                    boolean allow = true;
                    for (DominionSetMapColorCallback listener : listeners) {
                        if (!listener.onSetMapColor(operator, dominion, rHolder, gHolder, bHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onSetMapColor(@NotNull CommandSourceStack operator,
                              @NotNull DominionDTO dominion,
                              @NotNull MutableInt rHolder, @NotNull MutableInt gHolder, @NotNull MutableInt bHolder);
    }

    /** Fired when a dominion environment flag is being set. Return false to cancel. */
    public interface DominionSetEnvFlagCallback {
        Event<DominionSetEnvFlagCallback> EVENT = EventFactory.createArrayBacked(
                DominionSetEnvFlagCallback.class,
                listeners -> (operator, dominion, flag, oldValue, newValueHolder) -> {
                    boolean allow = true;
                    for (DominionSetEnvFlagCallback listener : listeners) {
                        if (!listener.onSetEnvFlag(operator, dominion, flag, oldValue, newValueHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onSetEnvFlag(@NotNull CommandSourceStack operator,
                             @NotNull DominionDTO dominion,
                             @NotNull EnvFlag flag,
                             boolean oldValue,
                             @NotNull MutableBoolean newValueHolder);
    }

    /** Fired when a dominion guest flag is being set. Return false to cancel. */
    public interface DominionSetGuestFlagCallback {
        Event<DominionSetGuestFlagCallback> EVENT = EventFactory.createArrayBacked(
                DominionSetGuestFlagCallback.class,
                listeners -> (operator, dominion, flag, oldValue, newValueHolder) -> {
                    boolean allow = true;
                    for (DominionSetGuestFlagCallback listener : listeners) {
                        if (!listener.onSetGuestFlag(operator, dominion, flag, oldValue, newValueHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onSetGuestFlag(@NotNull CommandSourceStack operator,
                               @NotNull DominionDTO dominion,
                               @NotNull PriFlag flag,
                               boolean oldValue,
                               @NotNull MutableBoolean newValueHolder);
    }

    /** Fired when a dominion owner glow flag is being set. Return false to cancel. */
    public interface DominionSetOwnerGlowCallback {
        Event<DominionSetOwnerGlowCallback> EVENT = EventFactory.createArrayBacked(
                DominionSetOwnerGlowCallback.class,
                listeners -> (operator, dominion, oldValue, newValueHolder) -> {
                    boolean allow = true;
                    for (DominionSetOwnerGlowCallback listener : listeners) {
                        if (!listener.onSetOwnerGlow(operator, dominion, oldValue, newValueHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onSetOwnerGlow(@NotNull CommandSourceStack operator,
                               @NotNull DominionDTO dominion,
                               boolean oldValue,
                               @NotNull MutableBoolean newValueHolder);
    }

    // ========== Group events ==========

    public interface GroupCreateCallback {
        Event<GroupCreateCallback> EVENT = EventFactory.createArrayBacked(
                GroupCreateCallback.class,
                listeners -> (operator, dominion, groupNameHolder) -> {
                    boolean allow = true;
                    for (GroupCreateCallback listener : listeners) {
                        if (!listener.onGroupCreate(operator, dominion, groupNameHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onGroupCreate(@NotNull CommandSourceStack operator,
                              @NotNull DominionDTO dominion,
                              @NotNull MutableString groupNameHolder);
    }

    public interface GroupDeleteCallback {
        Event<GroupDeleteCallback> EVENT = EventFactory.createArrayBacked(
                GroupDeleteCallback.class,
                listeners -> (operator, dominion, group) -> {
                    boolean allow = true;
                    for (GroupDeleteCallback listener : listeners) {
                        if (!listener.onGroupDelete(operator, dominion, group)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onGroupDelete(@NotNull CommandSourceStack operator,
                              @NotNull DominionDTO dominion,
                              @NotNull GroupDTO group);
    }

    public interface GroupRenameCallback {
        Event<GroupRenameCallback> EVENT = EventFactory.createArrayBacked(
                GroupRenameCallback.class,
                listeners -> (operator, dominion, group, newNameHolder) -> {
                    boolean allow = true;
                    for (GroupRenameCallback listener : listeners) {
                        if (!listener.onGroupRename(operator, dominion, group, newNameHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onGroupRename(@NotNull CommandSourceStack operator,
                              @NotNull DominionDTO dominion,
                              @NotNull GroupDTO group,
                              @NotNull MutableString newNameHolder);
    }

    public interface GroupSetFlagCallback {
        Event<GroupSetFlagCallback> EVENT = EventFactory.createArrayBacked(
                GroupSetFlagCallback.class,
                listeners -> (operator, dominion, group, flag, oldValue, newValueHolder) -> {
                    boolean allow = true;
                    for (GroupSetFlagCallback listener : listeners) {
                        if (!listener.onGroupSetFlag(operator, dominion, group, flag, oldValue, newValueHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onGroupSetFlag(@NotNull CommandSourceStack operator,
                               @NotNull DominionDTO dominion,
                               @NotNull GroupDTO group,
                               @NotNull PriFlag flag,
                               boolean oldValue,
                               @NotNull MutableBoolean newValueHolder);
    }

    public interface GroupAddMemberCallback {
        Event<GroupAddMemberCallback> EVENT = EventFactory.createArrayBacked(
                GroupAddMemberCallback.class,
                listeners -> (operator, dominion, group, member) -> {
                    boolean allow = true;
                    for (GroupAddMemberCallback listener : listeners) {
                        if (!listener.onGroupAddMember(operator, dominion, group, member)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onGroupAddMember(@NotNull CommandSourceStack operator,
                                 @NotNull DominionDTO dominion,
                                 @NotNull GroupDTO group,
                                 @NotNull MemberDTO member);
    }

    public interface GroupRemoveMemberCallback {
        Event<GroupRemoveMemberCallback> EVENT = EventFactory.createArrayBacked(
                GroupRemoveMemberCallback.class,
                listeners -> (operator, dominion, group, member) -> {
                    boolean allow = true;
                    for (GroupRemoveMemberCallback listener : listeners) {
                        if (!listener.onGroupRemoveMember(operator, dominion, group, member)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onGroupRemoveMember(@NotNull CommandSourceStack operator,
                                    @NotNull DominionDTO dominion,
                                    @NotNull GroupDTO group,
                                    @NotNull MemberDTO member);
    }

    // ========== Member events ==========

    public interface MemberAddedCallback {
        Event<MemberAddedCallback> EVENT = EventFactory.createArrayBacked(
                MemberAddedCallback.class,
                listeners -> (operator, dominion, player) -> {
                    boolean allow = true;
                    for (MemberAddedCallback listener : listeners) {
                        if (!listener.onMemberAdded(operator, dominion, player)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onMemberAdded(@NotNull CommandSourceStack operator,
                              @NotNull DominionDTO dominion,
                              @NotNull PlayerDTO player);
    }

    public interface MemberRemovedCallback {
        Event<MemberRemovedCallback> EVENT = EventFactory.createArrayBacked(
                MemberRemovedCallback.class,
                listeners -> (operator, dominion, member) -> {
                    boolean allow = true;
                    for (MemberRemovedCallback listener : listeners) {
                        if (!listener.onMemberRemoved(operator, dominion, member)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onMemberRemoved(@NotNull CommandSourceStack operator,
                                @NotNull DominionDTO dominion,
                                @NotNull MemberDTO member);
    }

    public interface MemberSetFlagCallback {
        Event<MemberSetFlagCallback> EVENT = EventFactory.createArrayBacked(
                MemberSetFlagCallback.class,
                listeners -> (operator, dominion, member, flag, oldValue, newValueHolder) -> {
                    boolean allow = true;
                    for (MemberSetFlagCallback listener : listeners) {
                        if (!listener.onMemberSetFlag(operator, dominion, member, flag, oldValue, newValueHolder)) {
                            allow = false;
                        }
                    }
                    return allow;
                }
        );
        boolean onMemberSetFlag(@NotNull CommandSourceStack operator,
                                @NotNull DominionDTO dominion,
                                @NotNull MemberDTO member,
                                @NotNull PriFlag flag,
                                boolean oldValue,
                                @NotNull MutableBoolean newValueHolder);
    }

    // ========== Miscellaneous events ==========

    public interface FlagRegisterCallback {
        Event<FlagRegisterCallback> EVENT = EventFactory.createArrayBacked(
                FlagRegisterCallback.class,
                listeners -> (modId, flag, registerAction) -> {
                    for (FlagRegisterCallback listener : listeners) {
                        listener.onFlagRegister(modId, flag, registerAction);
                    }
                }
        );
        void onFlagRegister(@NotNull String modId, @NotNull Flag flag, @NotNull Runnable registerAction);
    }

    public interface ExportMcaListCallback {
        Event<ExportMcaListCallback> EVENT = EventFactory.createArrayBacked(
                ExportMcaListCallback.class,
                listeners -> (list) -> {
                    for (ExportMcaListCallback listener : listeners) {
                        listener.onExportMcaList(list);
                    }
                }
        );
        void onExportMcaList(@NotNull List<McaRecord> list);
    }

    // ========== Mutable holders for event data ==========

    /** A mutable boolean holder, used to allow event listeners to modify values. */
    public static class MutableBoolean {
        private boolean value;
        public MutableBoolean(boolean value) { this.value = value; }
        public boolean get() { return value; }
        public void set(boolean value) { this.value = value; }
    }

    /** A mutable int holder. */
    public static class MutableInt {
        private int value;
        public MutableInt(int value) { this.value = value; }
        public int get() { return value; }
        public void set(int value) { this.value = value; }
    }

    /** A mutable string holder. */
    public static class MutableString {
        private String value;
        public MutableString(String value) { this.value = value; }
        public String get() { return value; }
        public void set(String value) { this.value = value; }
    }

    /** A mutable object holder. */
    public static class MutableObject<T> {
        private T value;
        public MutableObject(T value) { this.value = value; }
        public T get() { return value; }
        public void set(T value) { this.value = value; }
    }

    private FabricEventBus() {}
}
