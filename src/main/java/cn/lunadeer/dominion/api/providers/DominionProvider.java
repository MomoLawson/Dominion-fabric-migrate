package cn.lunadeer.dominion.api.providers;

import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.api.events.dominion.modify.DominionReSizeEvent;
import cn.lunadeer.dominion.api.events.dominion.modify.DominionSetMessageEvent;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class provides the API interface for creating, modifying, and managing dominions.
 * All operations are asynchronous and return CompletableFuture objects for non-blocking execution.
 * <p>
 * The operator parameter of each method defines who triggers the operation.
 *
 * @since 4.6.0
 */
public abstract class DominionProvider {
    protected static DominionProvider instance;

    /**
     * Gets the singleton instance of the DominionProvider.
     *
     * @return the current DominionProvider instance, or null if not initialized
     */
    public static DominionProvider getInstance() {
        return instance;
    }

    /**
     * Creates a new dominion in the specified world with the given parameters.
     *
     * @param operator    the command source performing this operation
     * @param name        the name of the new dominion
     * @param owner       the UUID of the player who will own this dominion
     * @param worldUid    the UUID of the world where the dominion will be created
     * @param cuboid      the 3D area that defines the dominion's boundaries
     * @param parent      the parent dominion, or null if this is a top-level dominion
     * @param skipEconomy whether to skip economy checks and charges for this operation
     * @return a CompletableFuture that resolves to the created DominionDTO
     */
    public abstract CompletableFuture<DominionDTO> createDominion(@NotNull CommandSourceStack operator,
                                                                  @NotNull String name, @NotNull UUID owner,
                                                                  @NotNull UUID worldUid, @NotNull CuboidDTO cuboid,
                                                                  @Nullable DominionDTO parent, boolean skipEconomy);

    /**
     * Resizes an existing dominion by expanding or contracting it in a specified direction.
     *
     * @param operator  the command source performing this operation
     * @param dominion  the dominion to be resized
     * @param type      the type of resize operation (expand or contract)
     * @param direction the direction in which to resize
     * @param size      the number of blocks to resize by
     * @return a CompletableFuture that resolves to the updated DominionDTO
     */
    public abstract CompletableFuture<DominionDTO> resizeDominion(@NotNull CommandSourceStack operator,
                                                                  @NotNull DominionDTO dominion,
                                                                  @NotNull DominionReSizeEvent.TYPE type,
                                                                  @NotNull DominionReSizeEvent.DIRECTION direction,
                                                                  int size);

    /**
     * Deletes a dominion and optionally all of its sub-dominions.
     *
     * @param operator    the command source performing this operation
     * @param dominion    the dominion to be deleted
     * @param skipEconomy whether to skip economy refunds for this operation
     * @param force       whether to force deletion
     * @return a CompletableFuture that resolves to the deleted DominionDTO
     */
    public abstract CompletableFuture<DominionDTO> deleteDominion(@NotNull CommandSourceStack operator,
                                                                  @NotNull DominionDTO dominion,
                                                                  boolean skipEconomy,
                                                                  boolean force);

    /**
     * Deletes a dominion with force enabled by default.
     */
    public CompletableFuture<DominionDTO> deleteDominion(@NotNull CommandSourceStack operator,
                                                         @NotNull DominionDTO dominion,
                                                         boolean skipEconomy) {
        return deleteDominion(operator, dominion, skipEconomy, true);
    }

    /**
     * Renames an existing dominion to a new name.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion to be renamed
     * @param newName  the new name for the dominion
     * @return a CompletableFuture that resolves to the updated DominionDTO
     */
    public abstract CompletableFuture<DominionDTO> renameDominion(@NotNull CommandSourceStack operator,
                                                                  @NotNull DominionDTO dominion,
                                                                  @NotNull String newName);

    /**
     * Transfers ownership of a dominion to a new player.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion to be transferred
     * @param newOwner the player who will become the new owner
     * @param force    whether to force the transfer
     * @return a CompletableFuture that resolves to the updated DominionDTO
     */
    public abstract CompletableFuture<DominionDTO> transferDominion(@NotNull CommandSourceStack operator,
                                                                    @NotNull DominionDTO dominion,
                                                                    @NotNull PlayerDTO newOwner,
                                                                    boolean force);

    /**
     * Transfers ownership of a dominion to a new player with force enabled by default.
     */
    public CompletableFuture<DominionDTO> transferDominion(@NotNull CommandSourceStack operator,
                                                           @NotNull DominionDTO dominion,
                                                           @NotNull PlayerDTO newOwner) {
        return transferDominion(operator, dominion, newOwner, true);
    }

    /**
     * Sets the teleport location for a dominion.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion whose teleport location will be updated
     * @param x        the X coordinate of the teleport location
     * @param y        the Y coordinate of the teleport location
     * @param z        the Z coordinate of the teleport location
     * @return a CompletableFuture that resolves to the updated DominionDTO
     */
    public abstract CompletableFuture<DominionDTO> setDominionTpLocation(@NotNull CommandSourceStack operator,
                                                                         @NotNull DominionDTO dominion,
                                                                         int x, int y, int z);

    /**
     * Sets a message for the dominion (enter/leave messages, etc.).
     *
     * @param operator   the command source performing this operation
     * @param dominion   the dominion whose message will be updated
     * @param type       the type of message to set
     * @param newMessage the new message text
     * @return a CompletableFuture that resolves to the updated DominionDTO
     */
    public abstract CompletableFuture<DominionDTO> setDominionMessage(@NotNull CommandSourceStack operator,
                                                                      @NotNull DominionDTO dominion,
                                                                      @NotNull DominionSetMessageEvent.TYPE type,
                                                                      @NotNull String newMessage);

    /**
     * Sets the map display color for the dominion.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion whose map color will be updated
     * @param r        the red component (0-255)
     * @param g        the green component (0-255)
     * @param b        the blue component (0-255)
     * @return a CompletableFuture that resolves to the updated DominionDTO
     */
    public abstract CompletableFuture<DominionDTO> setDominionMapColor(@NotNull CommandSourceStack operator,
                                                                       @NotNull DominionDTO dominion,
                                                                       int r, int g, int b);

    /**
     * Sets an environment flag for the dominion.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion whose environment flag will be updated
     * @param flag     the specific environment flag to modify
     * @param newValue the new value for the flag
     * @return a CompletableFuture that resolves to the updated DominionDTO
     */
    public abstract CompletableFuture<DominionDTO> setDominionEnvFlag(@NotNull CommandSourceStack operator,
                                                                      @NotNull DominionDTO dominion,
                                                                      @NotNull EnvFlag flag,
                                                                      boolean newValue);

    /**
     * Sets a guest privilege flag for the dominion.
     *
     * @param operator the command source performing this operation
     * @param dominion the dominion whose guest flag will be updated
     * @param flag     the specific privilege flag to modify
     * @param newValue the new value for the flag
     * @return a CompletableFuture that resolves to the updated DominionDTO
     */
    public abstract CompletableFuture<DominionDTO> setDominionGuestFlag(@NotNull CommandSourceStack operator,
                                                                        @NotNull DominionDTO dominion,
                                                                        @NotNull PriFlag flag,
                                                                        boolean newValue);

    /**
     * Sets the owner glow flag for the dominion.
     *
     * @param operator   the command source performing this operation
     * @param dominion   the dominion whose owner glow flag will be updated
     * @param ownerGlow  the new value for the owner glow flag
     * @return a CompletableFuture that resolves to the updated DominionDTO
     */
    public abstract CompletableFuture<DominionDTO> setDominionOwnerGlow(@NotNull CommandSourceStack operator,
                                                                        @NotNull DominionDTO dominion,
                                                                        boolean ownerGlow);
}
