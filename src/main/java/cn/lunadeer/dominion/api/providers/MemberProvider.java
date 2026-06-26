package cn.lunadeer.dominion.api.providers;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides the API interface for creating, modifying, and managing members within dominions.
 * All operations are asynchronous and return CompletableFuture objects for non-blocking execution.
 * <p>
 * The operator parameter of each method defines who triggers the operation.
 *
 * @since 4.6.0
 */
public abstract class MemberProvider {
    protected static MemberProvider instance;

    /**
     * Gets the singleton instance of the MemberProvider.
     *
     * @return the current MemberProvider instance, or null if not initialized
     */
    public static MemberProvider getInstance() {
        return instance;
    }

    /**
     * Sets a privilege flag for a specific member within a dominion.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion containing the member
     * @param member   the member whose flag will be updated
     * @param flag     the specific privilege flag to modify
     * @param newValue the new value for the flag
     * @return a CompletableFuture that resolves to the updated MemberDTO
     */
    public abstract CompletableFuture<MemberDTO> setMemberFlag(@NotNull CommandSourceStack operator,
                                                               @NotNull DominionDTO dominion,
                                                               @NotNull MemberDTO member,
                                                               @NotNull PriFlag flag,
                                                               boolean newValue);

    /**
     * Adds a new member to the specified dominion.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion where the player will be added as a member
     * @param player   the player to be added as a member to the dominion
     * @return a CompletableFuture that resolves to the created MemberDTO
     */
    public abstract CompletableFuture<MemberDTO> addMember(@NotNull CommandSourceStack operator,
                                                           @NotNull DominionDTO dominion,
                                                           @NotNull PlayerDTO player);

    /**
     * Removes an existing member from the specified dominion.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion from which the member will be removed
     * @param member   the member to be removed from the dominion
     * @return a CompletableFuture that resolves to the removed MemberDTO
     */
    public abstract CompletableFuture<MemberDTO> removeMember(@NotNull CommandSourceStack operator,
                                                              @NotNull DominionDTO dominion,
                                                              @NotNull MemberDTO member);
}
