package cn.lunadeer.dominion.api.dtos.flag;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a privilege flag in the Dominion system.
 * This class extends the Flag class and provides specific
 * configuration keys for privilege-related flags.
 */
public class PriFlag extends Flag {

    public PriFlag(@NotNull String flag_name, @NotNull String display_name, @NotNull String description,
                   @NotNull Boolean default_value, @NotNull Boolean enable, @NotNull String material) {
        super(flag_name, display_name, description, default_value, enable, material);
    }

    @Override
    public String getConfigurationDescKey() {
        return "privilege." + getFlagName() + ".description";
    }

    @Override
    public String getConfigurationDefaultKey() {
        return "privilege." + getFlagName() + ".default";
    }

    @Override
    public String getConfigurationEnableKey() {
        return "privilege." + getFlagName() + ".enable";
    }

    @Override
    public String getConfigurationNameKey() {
        return "privilege." + getFlagName();
    }

    @Override
    public String getConfigurationMaterialKey() {
        return "privilege." + getFlagName() + ".material";
    }
}
