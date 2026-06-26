package cn.lunadeer.dominion.api;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.api.providers.DominionProvider;
import cn.lunadeer.dominion.api.providers.GroupProvider;
import cn.lunadeer.dominion.api.providers.MemberProvider;
import cn.lunadeer.dominion.utils.McaRecord;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Interface representing the Dominion API.
 * <p>
 * This interface provides methods to interact with the Dominion mod.
 * <p>
 * Use the {@link #getInstance()} method to retrieve the singleton instance of the DominionAPI.
 */
public abstract class DominionAPI {

    protected static DominionAPI instance;

    /**
     * Retrieves the singleton instance of the DominionAPI.
     *
     * @return the singleton instance of the DominionAPI
     */
    public static DominionAPI getInstance() {
        return instance;
    }

    /**
     * Retrieves a PlayerDTO by the player's name.
     *
     * @param name the name of the player
     * @return the PlayerDTO associated with the given name, or null if not found
     */
    public abstract @Nullable PlayerDTO getPlayer(String name);

    /**
     * Retrieves a PlayerDTO by the player's UUID.
     *
     * @param player the UUID of the player
     * @return the PlayerDTO associated with the given UUID, or null if not found
     */
    public abstract @Nullable PlayerDTO getPlayer(@NotNull UUID player);

    /**
     * Retrieves the name of a player by their UUID.
     *
     * @param uuid the UUID of the player
     * @return the name of the player associated with the given UUID
     */
    public abstract @NotNull String getPlayerName(@NotNull UUID uuid);

    /**
     * Retrieves all DominionDTO objects.
     *
     * @return a list of all DominionDTO objects
     */
    public abstract List<DominionDTO> getAllDominions();

    /**
     * Retrieves all DominionDTO objects owned by a specific player.
     *
     * @param player the UUID of the player
     * @return a list of DominionDTO objects owned by the specified player
     */
    public abstract List<DominionDTO> getAllDominionsOfPlayer(@NotNull UUID player);

    /**
     * Retrieves the child dominions of a given parent dominion.
     *
     * @param parent the parent DominionDTO whose children are to be retrieved
     * @return a list of child DominionDTO objects
     */
    public abstract List<DominionDTO> getChildrenDominionOf(DominionDTO parent);

    /**
     * Retrieves a DominionDTO by its ID.
     *
     * @param id the ID of the dominion to retrieve
     * @return the DominionDTO associated with the given ID
     */
    public abstract @Nullable DominionDTO getDominion(Integer id);

    /**
     * Retrieves a DominionDTO by its name.
     *
     * @param name the name of the dominion to retrieve
     * @return the DominionDTO associated with the given name
     */
    public abstract @Nullable DominionDTO getDominion(String name);

    /**
     * Retrieves a DominionDTO by world UUID and block coordinates.
     *
     * @param worldUid the UUID of the world
     * @param x        the x coordinate
     * @param y        the y coordinate
     * @param z        the z coordinate
     * @return the DominionDTO at the given location, or null if not found
     */
    public abstract @Nullable DominionDTO getDominion(UUID worldUid, int x, int y, int z);

    /**
     * Retrieves the dominions owned by a player.
     *
     * @param player the UUID of the player
     * @return a list of DominionDTO objects representing the dominions owned by the player
     */
    public abstract List<DominionDTO> getPlayerOwnDominionDTOs(UUID player);

    /**
     * Retrieves the dominions where a player is an admin.
     *
     * @param player the UUID of the player
     * @return a list of DominionDTO objects representing the dominions where the player is an admin
     */
    public abstract List<DominionDTO> getPlayerAdminDominionDTOs(UUID player);

    /**
     * Retrieves a MemberDTO by the player's UUID.
     *
     * @param dominion the DominionDTO to retrieve the member from
     * @param player   the ServerPlayer representing the player
     * @return the MemberDTO associated with the given player, or null if not found
     */
    public abstract @Nullable MemberDTO getMember(@Nullable DominionDTO dominion, @NotNull ServerPlayer player);

    /**
     * Retrieves a MemberDTO by the player's UUID.
     *
     * @param dominion the DominionDTO to retrieve the member from
     * @param player   the UUID of the player
     * @return the MemberDTO associated with the given player, or null if not found
     */
    public abstract @Nullable MemberDTO getMember(@Nullable DominionDTO dominion, @NotNull UUID player);

    /**
     * Retrieves a GroupDTO by the member's group ID.
     *
     * @param member the MemberDTO whose group ID is to be used for retrieval
     * @return the GroupDTO associated with the given member's group ID, or null if not found
     */
    public abstract @Nullable GroupDTO getGroup(MemberDTO member);

    /**
     * Retrieves a GroupDTO by its ID.
     *
     * @param id the ID of the group to retrieve
     * @return the GroupDTO associated with the given ID, or null if not found
     */
    public abstract @Nullable GroupDTO getGroup(Integer id);

    /**
     * Retrieves the current dominion of a player.
     *
     * @param player the ServerPlayer representing the player
     * @return the DominionDTO associated with the player's current location, or null if not found
     */
    public abstract @Nullable DominionDTO getPlayerCurrentDominion(@NotNull ServerPlayer player);

    /**
     * Resets the current dominion ID for a player.
     *
     * @param player the ServerPlayer representing the player
     */
    public abstract void resetPlayerCurrentDominionId(@NotNull ServerPlayer player);

    /**
     * Retrieves the total count of dominions.
     *
     * @return the total count of dominions
     */
    public abstract Integer dominionCount();

    /**
     * Retrieves the total count of groups.
     *
     * @return the total count of groups
     */
    public abstract Integer groupCount();

    /**
     * Retrieves the total count of members.
     *
     * @return the total count of members
     */
    public abstract Integer memberCount();

    /**
     * Checks if a player has a specific privilege flag at the given location.
     *
     * @param worldUid the UUID of the world
     * @param x        the x coordinate
     * @param y        the y coordinate
     * @param z        the z coordinate
     * @param flag     the privilege flag to check
     * @param player   the player whose privileges are being checked
     * @return true if the player has the privilege flag at the location, false otherwise
     */
    public abstract boolean checkPrivilegeFlag(@NotNull UUID worldUid, int x, int y, int z,
                                                @NotNull PriFlag flag, @NotNull ServerPlayer player);

    /**
     * Checks if a player has a specific privilege flag for the given dominion.
     *
     * @param dom    the DominionDTO to check the privilege flag in, or null if not applicable
     * @param flag   the privilege flag to check
     * @param player the player whose privileges are being checked
     * @return true if the player has the privilege flag in the dominion, false otherwise
     */
    public abstract boolean checkPrivilegeFlag(@Nullable DominionDTO dom, @NotNull PriFlag flag,
                                                @NotNull ServerPlayer player);

    /**
     * Checks if a player has a specific privilege flag at the given location without triggering messages or events.
     *
     * @param worldUid the UUID of the world
     * @param x        the x coordinate
     * @param y        the y coordinate
     * @param z        the z coordinate
     * @param flag     the privilege flag to check
     * @param player   the player whose privileges are being checked
     * @return true if the player has the privilege flag at the location, false otherwise
     */
    public abstract boolean checkPrivilegeFlagSilence(@NotNull UUID worldUid, int x, int y, int z,
                                                      @NotNull PriFlag flag, @NotNull ServerPlayer player);

    /**
     * Checks if a player has a specific privilege flag for the given dominion without triggering messages or events.
     *
     * @param dom    the DominionDTO to check the privilege flag in, or null if not applicable
     * @param flag   the privilege flag to check
     * @param player the player whose privileges are being checked
     * @return true if the player has the privilege flag in the dominion, false otherwise
     */
    public abstract boolean checkPrivilegeFlagSilence(@Nullable DominionDTO dom, @NotNull PriFlag flag,
                                                      @NotNull ServerPlayer player);

    /**
     * Checks if the specified environment flag is set at the given location.
     *
     * @param worldUid the UUID of the world
     * @param x        the x coordinate
     * @param y        the y coordinate
     * @param z        the z coordinate
     * @param flag     the environment flag to check
     * @return true if the environment flag is set at the location, false otherwise
     */
    public abstract boolean checkEnvironmentFlag(@NotNull UUID worldUid, int x, int y, int z,
                                                  @NotNull EnvFlag flag);

    /**
     * Checks if the specified environment flag is set for the given dominion.
     *
     * @param dom  the DominionDTO to check for the environment flag, or null if not applicable
     * @param flag the environment flag to check
     * @return true if the environment flag is set for the dominion, false otherwise
     */
    public abstract boolean checkEnvironmentFlag(@Nullable DominionDTO dom, @NotNull EnvFlag flag);

    /**
     * Retrieves the DominionProvider instance.
     *
     * @return the singleton instance of DominionProvider
     */
    public static DominionProvider getDominionProvider() {
        return DominionProvider.getInstance();
    }

    /**
     * Retrieves the GroupProvider instance.
     *
     * @return the singleton instance of GroupProvider
     */
    public static GroupProvider getGroupProvider() {
        return GroupProvider.getInstance();
    }

    /**
     * Retrieves the MemberProvider instance.
     *
     * @return the singleton instance of MemberProvider
     */
    public static MemberProvider getMemberProvider() {
        return MemberProvider.getInstance();
    }

    /**
     * Reloads the dominion cache.
     */
    public abstract void reloadCache();

    /**
     * Reloads the dominion configuration.
     */
    public abstract void reloadConfig() throws Exception;

    /**
     * Retrieves the whitelist of MCA initiative.
     *
     * @return a list of McaRecord objects representing the MCA whitelist coordinates
     */
    public abstract List<McaRecord> getMcaWhiteListInitiative();

    /**
     * Retrieves the whitelist of MCA passive from cache.
     *
     * @return a list of McaRecord objects representing the MCA passive whitelist coordinates, or an empty list if not in cache
     */
    public abstract List<McaRecord> getMcaWhiteListPassive();
}
