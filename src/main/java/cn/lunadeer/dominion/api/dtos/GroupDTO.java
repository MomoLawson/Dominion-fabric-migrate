package cn.lunadeer.dominion.api.dtos;

import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Interface representing a Group Data Transfer Object (DTO).
 */
public interface GroupDTO {
    /**
     * Gets the ID of the group.
     *
     * @return the ID of the group
     */
    @NotNull Integer getId();

    /**
     * Gets the ID of the dominion to which the group belongs.
     *
     * @return the ID of the dominion
     */
    @NotNull Integer getDomID();

    /**
     * Sets the name of the group, which can include color codes.
     * Returns the group object if successful, otherwise returns null.
     *
     * @param name the name of the group
     * @return the group object
     * @throws SQLException if a database access error occurs
     */
    @NotNull GroupDTO setName(@NotNull String name) throws SQLException;

    /**
     * Gets the name of the group in plain text (without color codes).
     * This method should be used in most cases to get the group name.
     *
     * @return the plain text name of the group
     */
    @NotNull String getNamePlain();

    /**
     * Gets the raw name of the group (with color codes).
     *
     * @return the raw name of the group
     */
    @NotNull String getNameRaw();

    /**
     * Gets the name of the group with color codes as a Component type.
     *
     * @return the name of the group with color codes
     */
    @NotNull Component getNameColoredComponent();

    /**
     * Gets the name of the group with color codes as a formatted string.
     *
     * @return the name of the group with color codes
     */
    @NotNull String getNameColoredBukkit();

    /**
     * Gets the value of a specific flag for the group.
     *
     * @param flag the flag
     * @return the value of the flag, or the default value if the flag does not exist
     */
    @NotNull Boolean getFlagValue(@NotNull PriFlag flag);

    /**
     * Gets all flag values for the group.
     *
     * @return a map of flag values
     */
    @NotNull Map<PriFlag, Boolean> getFlagsValue();

    /**
     * Sets the value of a specific flag for the group.
     * Returns the group object if successful, otherwise returns null.
     *
     * @param flag  the flag
     * @param value the value of the flag
     * @return the group object
     * @throws SQLException if a database access error occurs
     */
    @NotNull GroupDTO setFlagValue(@NotNull PriFlag flag, @NotNull Boolean value) throws SQLException;

    /**
     * Gets all members of the group.
     *
     * @return a list of members
     * @throws SQLException if a database access error occurs
     */
    List<MemberDTO> getMembers() throws SQLException;
}
