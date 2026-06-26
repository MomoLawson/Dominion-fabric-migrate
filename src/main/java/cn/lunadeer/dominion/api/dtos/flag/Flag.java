package cn.lunadeer.dominion.api.dtos.flag;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a flag in the Dominion system.
 * This abstract class provides the basic structure and methods
 * for flags, including their name, display name, description,
 * default value, and enable status.
 * <p>
 * Ported from Bukkit: Material replaced with String (Minecraft item ID).
 */
public abstract class Flag {

    private final String flag_name;
    private String display_name;
    private String description;
    private Boolean default_value;
    private Boolean enable;
    private String material; // Minecraft item ID string, e.g. "minecraft:cow_spawn_egg"

    public Flag(@NotNull String flag_name, @NotNull String display_name, @NotNull String description,
                @NotNull Boolean default_value, @NotNull Boolean enable, @NotNull String material) {
        this.flag_name = flag_name;
        this.display_name = display_name;
        this.description = description;
        this.default_value = default_value;
        this.enable = enable;
        this.material = material;
    }

    public @NotNull String getFlagName() {
        return flag_name;
    }

    public @NotNull String getDisplayName() {
        return display_name;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public @NotNull Boolean getDefaultValue() {
        return default_value;
    }

    public @NotNull Boolean getEnable() {
        return enable;
    }

    /**
     * Returns the material ID string used by this flag in chest user interfaces.
     *
     * @return the material ID string (e.g. "minecraft:chest")
     */
    public @NotNull String getMaterial() {
        return material;
    }

    public void setDisplayName(String displayName) {
        this.display_name = displayName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDefaultValue(Boolean defaultValue) {
        this.default_value = defaultValue;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDisplayNameKey() {
        return "flags." + flag_name + ".display-name";
    }

    public String getDescriptionKey() {
        return "flags." + flag_name + ".description";
    }

    public abstract String getConfigurationDescKey();

    public abstract String getConfigurationDefaultKey();

    public abstract String getConfigurationEnableKey();

    public abstract String getConfigurationNameKey();

    public abstract String getConfigurationMaterialKey();
}
