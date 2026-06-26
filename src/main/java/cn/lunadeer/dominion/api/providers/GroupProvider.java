package cn.lunadeer.dominion.api.providers;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides the API interface for creating, modifying, and managing groups within dominions.
 * All operations are asynchronous and return CompletableFuture objects for non-blocking execution.
 * <p>
 * The operator parameter of each method defines who triggers the operation.
 *
 * @since 4.6.0
 */
public abstract class GroupProvider {
    protected static GroupProvider instance;

    /**
     * Gets the singleton instance of the GroupProvider.
     *
     * @return the current GroupProvider instance, or null if not initialized
     */
    public static GroupProvider getInstance() {
        return instance;
    }

    /**
     * Sets a privilege flag for a specific group within a dominion.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion containing the group
     * @param group    the group whose flag will be updated
     * @param flag     the specific privilege flag to modify
     * @param newValue the new value for the flag
     * @return a CompletableFuture that resolves to the updated GroupDTO
     */
    public abstract CompletableFuture<GroupDTO> setGroupFlag(@NotNull CommandSourceStack operator,
                                                             @NotNull DominionDTO dominion,
                                                             @NotNull GroupDTO group,
                                                             @NotNull PriFlag flag,
                                                             boolean newValue);

    /**
     * Creates a new group within the specified dominion.
     *
     * @param operator  the command source performing this operation
     * @param dominion  the dominion where the group will be created
     * @param groupName the name of the new group
     * @return a CompletableFuture that resolves to the created GroupDTO
     */
    public abstract CompletableFuture<GroupDTO> createGroup(@NotNull CommandSourceStack operator,
                                                            @NotNull DominionDTO dominion,
                                                            @NotNull String groupName);

    /**
     * Deletes an existing group from the specified dominion.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion containing the group to be deleted
     * @param group    the group to be deleted
     * @return a CompletableFuture that resolves to the deleted GroupDTO
     */
    public abstract CompletableFuture<GroupDTO> deleteGroup(@NotNull CommandSourceStack operator,
                                                            @NotNull DominionDTO dominion,
                                                            @NotNull GroupDTO group);

    /**
     * Renames an existing group within the specified dominion.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion containing the group to be renamed
     * @param group    the group to be renamed
     * @param newName  the new name for the group
     * @return a CompletableFuture that resolves to the updated GroupDTO
     */
    public abstract CompletableFuture<GroupDTO> renameGroup(@NotNull CommandSourceStack operator,
                                                            @NotNull DominionDTO dominion,
                                                            @NotNull GroupDTO group,
                                                            @NotNull String newName);

    /**
     * Adds a member to the specified group within a dominion.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion containing the group
     * @param group    the group to which the member will be added
     * @param member   the member to be added to the group
     * @return a CompletableFuture that resolves to the updated MemberDTO
     */
    public abstract CompletableFuture<MemberDTO> addMember(@NotNull CommandSourceStack operator,
                                                           @NotNull DominionDTO dominion,
                                                           @NotNull GroupDTO group,
                                                           @NotNull MemberDTO member);

    /**
     * Removes a member from the specified group within a dominion.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion containing the group
     * @param group    the group from which the member will be removed
     * @param member   the member to be removed from the group
     * @return a CompletableFuture that resolves to the updated MemberDTO
     */
    public abstract CompletableFuture<MemberDTO> removeMember(@NotNull CommandSourceStack operator,
                                                              @NotNull DominionDTO dominion,
                                                              @NotNull GroupDTO group,
                                                              @NotNull MemberDTO member);
}
