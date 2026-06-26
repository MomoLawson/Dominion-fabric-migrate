package cn.lunadeer.dominion.api.dtos.flag;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an environment flag in the Dominion system.
 * This class extends the Flag class and provides specific
 * configuration keys for environment-related flags.
 */
public class EnvFlag extends Flag {

    public EnvFlag(@NotNull String flag_name, @NotNull String display_name, @NotNull String description,
                   @NotNull Boolean default_value, @NotNull Boolean enable, @NotNull String material) {
        super(flag_name, display_name, description, default_value, enable, material);
    }

    @Override
    public String getConfigurationDescKey() {
        return "environment." + getFlagName() + ".description";
    }

    @Override
    public String getConfigurationDefaultKey() {
        return "environment." + getFlagName() + ".default";
    }

    @Override
    public String getConfigurationEnableKey() {
        return "environment." + getFlagName() + ".enable";
    }

    @Override
    public String getConfigurationNameKey() {
        return "environment." + getFlagName();
    }

    @Override
    public String getConfigurationMaterialKey() {
        return "environment." + getFlagName() + ".material";
    }
}
