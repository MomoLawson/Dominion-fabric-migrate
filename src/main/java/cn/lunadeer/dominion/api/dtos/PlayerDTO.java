package cn.lunadeer.dominion.api.dtos;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Interface representing a Player Data Transfer Object (DTO).
 */
public interface PlayerDTO {
    /**
     * Enum representing the type of user interface preferred by the player.
     * CUI stands for Chest User Interface, and TUI stands for Text User Interface.
     */
    enum UI_TYPE {
        BY_PLAYER, // Determined by player settings
        CUI,       // Chest User Interface
        TUI,       // Text User Interface
    }

    /**
     * Gets the ID of the player.
     *
     * @return the ID of the player
     */
    Integer getId();

    /**
     * Gets the UUID of the player.
     *
     * @return the UUID of the player
     */
    UUID getUuid();

    /**
     * Gets the last known name of the player.
     *
     * @return the last known name of the player
     */
    String getLastKnownName();

    /**
     * Updates the last known name of the player. Returns the updated player object.
     *
     * @param name    the new last known name of the player
     * @param skinUrl the URL of the player's skin, or null
     * @return the updated player object
     */
    PlayerDTO updateLastKnownName(@NotNull String name, @Nullable URL skinUrl) throws SQLException, MalformedURLException;

    /**
     * Gets the ID of the group title the player is using.
     *
     * @return the ID of the group title the player is using
     */
    Integer getUsingGroupTitleID();

    /**
     * Gets the URL of the player's skin.
     *
     * @return the URL of the player's skin
     */
    @NotNull URL getSkinUrl() throws MalformedURLException;

    /**
     * Gets the player's preferred user interface type.
     *
     * @return the preferred user interface type
     */
    @NotNull UI_TYPE getUiPreference();

    /**
     * Sets the player's preferred user interface type.
     *
     * @param uiType the preferred user interface type to set
     * @throws SQLException if an error occurs while updating the database
     */
    void setUiPreference(UI_TYPE uiType) throws SQLException;
}
