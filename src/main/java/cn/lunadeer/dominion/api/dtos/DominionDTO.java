package cn.lunadeer.dominion.api.dtos;

import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Interface representing a Dominion Data Transfer Object (DTO).
 */
public interface DominionDTO {
    /**
     * Gets the ID of the dominion.
     *
     * @return the ID of the dominion
     */
    @NotNull Integer getId();

    /**
     * Gets the UUID of the dominion owner.
     *
     * @return the UUID of the dominion owner
     */
    @NotNull UUID getOwner();

    /**
     * Gets the DTO of the dominion owner.
     *
     * @return the DTO of the dominion owner
     */
    @NotNull PlayerDTO getOwnerDTO();

    /**
     * Sets the owner of the dominion. Returns the dominion object if successful, otherwise returns null.
     *
     * @param owner the UUID of the dominion owner
     * @return the dominion object
     * @throws SQLException if a database access error occurs
     */
    @NotNull DominionDTO setOwner(UUID owner) throws SQLException;

    /**
     * Sets the owner of the dominion. Returns the dominion object if successful, otherwise returns null.
     *
     * @param owner the dominion owner as a ServerPlayer
     * @return the dominion object
     * @throws SQLException if a database access error occurs
     */
    @NotNull DominionDTO setOwner(ServerPlayer owner) throws SQLException;

    /**
     * Gets the name of the dominion.
     *
     * @return the name of the dominion
     */
    @NotNull String getName();

    /**
     * Sets the name of the dominion. Returns the dominion object if successful, otherwise returns null.
     *
     * @param name the name of the dominion
     * @return the dominion object
     * @throws SQLException if a database access error occurs
     */
    @NotNull DominionDTO setName(String name) throws SQLException;

    /**
     * Gets the UUID of the world where the dominion is located.
     * This method guarantees a non-null UUID, but does not guarantee the existence of the world.
     * To check if the world exists, use the server's world manager.
     *
     * @return the UUID of the world where the dominion is located
     */
    @NotNull UUID getWorldUid();

    /**
     * Gets the cuboid of the dominion.
     *
     * @return the cuboid of the dominion
     */
    @NotNull CuboidDTO getCuboid();

    /**
     * Sets the cuboid of the dominion. Returns the dominion object if successful, otherwise returns null.
     *
     * @param cuboid the cuboid of the dominion
     * @return the dominion object
     * @throws SQLException if a database access error occurs
     */
    @NotNull DominionDTO setCuboid(@NotNull CuboidDTO cuboid) throws SQLException;

    /**
     * Gets the ID of the parent dominion.
     *
     * @return the ID of the parent dominion, or -1 if there is no parent dominion
     */
    @NotNull Integer getParentDomId();

    /**
     * Gets the welcome message of the dominion.
     *
     * @return the welcome message of the dominion
     */
    @NotNull String getJoinMessage();

    /**
     * Sets the welcome message of the dominion. Returns the dominion object if successful, otherwise returns null.
     *
     * @param joinMessage the welcome message of the dominion
     * @return the dominion object
     * @throws SQLException if a database access error occurs
     */
    @NotNull DominionDTO setJoinMessage(String joinMessage) throws SQLException;

    /**
     * Gets the leave message of the dominion.
     *
     * @return the leave message of the dominion
     */
    @NotNull String getLeaveMessage();

    /**
     * Sets the leave message of the dominion. Returns the dominion object if successful, otherwise returns null.
     *
     * @param leaveMessage the leave message of the dominion
     * @return the dominion object
     * @throws SQLException if a database access error occurs
     */
    @NotNull DominionDTO setLeaveMessage(String leaveMessage) throws SQLException;

    /**
     * Gets all environment flag values of the dominion.
     *
     * @return the environment flag values of the dominion
     */
    @NotNull Map<EnvFlag, Boolean> getEnvironmentFlagValue();

    /**
     * Gets the value of a specific environment flag of the dominion.
     *
     * @param flag the environment flag
     * @return the value of the environment flag
     */
    boolean getEnvFlagValue(@NotNull EnvFlag flag);

    /**
     * Gets all guest privilege flag values of the dominion.
     *
     * @return the guest privilege flag values of the dominion
     */
    @NotNull Map<PriFlag, Boolean> getGuestPrivilegeFlagValue();

    /**
     * Gets the value of a specific guest privilege flag of the dominion.
     *
     * @param flag the guest privilege flag
     * @return the value of the guest privilege flag
     */
    boolean getGuestFlagValue(@NotNull PriFlag flag);

    /**
     * Sets the value of a specific environment flag of the dominion. Returns the dominion object if successful, otherwise returns null.
     *
     * @param flag  the flag
     * @param value the value of the flag
     * @return the dominion object
     * @throws SQLException if a database access error occurs
     */
    @NotNull DominionDTO setEnvFlagValue(@NotNull EnvFlag flag, @NotNull Boolean value) throws SQLException;

    /**
     * Sets the value of a specific guest privilege flag of the dominion. Returns the dominion object if successful, otherwise returns null.
     *
     * @param flag  the flag
     * @param value the value of the flag
     * @return the dominion object
     * @throws SQLException if a database access error occurs
     */
    @NotNull DominionDTO setGuestFlagValue(@NotNull PriFlag flag, @NotNull Boolean value) throws SQLException;

    /**
     * Gets the teleport location X of the dominion.
     *
     * @return the teleport location X coordinate
     */
    int getTpLocationX();

    /**
     * Gets the teleport location Y of the dominion.
     *
     * @return the teleport location Y coordinate
     */
    int getTpLocationY();

    /**
     * Gets the teleport location Z of the dominion.
     *
     * @return the teleport location Z coordinate
     */
    int getTpLocationZ();

    /**
     * Sets the teleport location of the dominion. Returns the dominion object if successful, otherwise returns null.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return the dominion object
     * @throws SQLException if a database access error occurs
     */
    @NotNull DominionDTO setTpLocation(int x, int y, int z) throws SQLException;

    /**
     * Sets the owner glow flag of the dominion. Returns the dominion object if successful, otherwise returns null.
     *
     * @param ownerGlow the owner glow flag of the dominion
     * @return the dominion object
     * @throws SQLException if a database access error occurs
     */
    @NotNull DominionDTO setOwnerGlow(Boolean ownerGlow) throws SQLException;

    /**
     * Gets the red component of the dominion's color.
     *
     * @return the red component of the dominion's color
     */
    int getColorR();

    /**
     * Gets the green component of the dominion's color.
     *
     * @return the green component of the dominion's color
     */
    int getColorG();

    /**
     * Gets the blue component of the dominion's color.
     *
     * @return the blue component of the dominion's color
     */
    int getColorB();

    /**
     * Gets the color of the dominion as a string.
     *
     * @return the color of the dominion as a string
     */
    @NotNull String getColor();

    /**
     * Gets the hexadecimal representation of the dominion's color.
     *
     * @return the hexadecimal representation of the dominion's color
     */
    int getColorHex();

    /**
     * Sets the color of the dominion. Returns the dominion object if successful, otherwise returns null.
     *
     * @param r the red component (0-255)
     * @param g the green component (0-255)
     * @param b the blue component (0-255)
     * @return the dominion object
     * @throws SQLException if a database access error occurs
     */
    @NotNull DominionDTO setColor(int r, int g, int b) throws SQLException;

    /**
     * Gets all groups of the dominion.
     *
     * @return the list of groups
     */
    List<GroupDTO> getGroups();

    /**
     * Gets all members of the dominion.
     *
     * @return the list of members
     */
    List<MemberDTO> getMembers();

    /**
     * Gets the server ID associated with the dominion.
     *
     * @return the server ID associated with the dominion
     */
    Integer getServerId();

    /**
     * Get the owner glow flag of the dominion.
     *
     * @return the owner glow flag of the dominion
     */
    Boolean getOwnerGlow();

    // Convenience methods for compatibility
    default boolean isEnvFlag(String flagName) {
        for (Map.Entry<EnvFlag, Boolean> entry : getEnvironmentFlagValue().entrySet()) {
            if (entry.getKey().getFlagName().equals(flagName)) return entry.getValue();
        }
        return false;
    }

    default boolean isGuestFlag(String flagName) {
        for (Map.Entry<PriFlag, Boolean> entry : getGuestPrivilegeFlagValue().entrySet()) {
            if (entry.getKey().getFlagName().equals(flagName)) return entry.getValue();
        }
        return false;
    }

    default boolean isOwnerGlow() { return getOwnerGlow() != null && getOwnerGlow(); }

    default String getOwnerName() {
        try { return getOwnerDTO().getLastKnownName(); } catch (Exception e) { return "Unknown"; }
    }

    default int getX1() { return getCuboid().x1(); }
    default int getY1() { return getCuboid().y1(); }
    default int getZ1() { return getCuboid().z1(); }
    default int getX2() { return getCuboid().x2(); }
    default int getY2() { return getCuboid().y2(); }
    default int getZ2() { return getCuboid().z2(); }

    default void setEnvFlag(String flagName, boolean value) {
        for (Map.Entry<EnvFlag, Boolean> entry : getEnvironmentFlagValue().entrySet()) {
            if (entry.getKey().getFlagName().equals(flagName)) {
                try { setEnvFlagValue(entry.getKey(), value); } catch (Exception ignored) {}
                return;
            }
        }
    }

    default void setGuestFlag(String flagName, boolean value) {
        for (Map.Entry<PriFlag, Boolean> entry : getGuestPrivilegeFlagValue().entrySet()) {
            if (entry.getKey().getFlagName().equals(flagName)) {
                try { setGuestFlagValue(entry.getKey(), value); } catch (Exception ignored) {}
                return;
            }
        }
    }
}
