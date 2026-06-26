package cn.lunadeer.dominion.utils.scui;

import cn.lunadeer.dominion.utils.ColorParser;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.scui.configuration.ButtonConfiguration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cn.lunadeer.dominion.managers.HooksManager.setPlaceholder;
import static cn.lunadeer.dominion.utils.Misc.formatString;
import static cn.lunadeer.dominion.utils.Misc.formatStringList;

/**
 * Abstract button for the CUI (Chest User Interface) system.
 * <p>
 * Ported from Bukkit to Fabric. Uses {@link net.minecraft.item.ItemStack},
 * {@link net.minecraft.item.Items}, and {@link net.minecraft.text.Text}
 * instead of Bukkit equivalents.
 * <p>
 * For player heads, uses {@link DataComponents#PROFILE} with
 * {@link ResolvableProfile} to set skull textures.
 */
public abstract class ChestButton {

    private final char symbol;
    private final ItemStack item;
    private String displayName;
    private List<String> lore;

    public ChestButton(ButtonConfiguration config) {
        if (config.material.contains(";")) {
            String[] parts = config.material.split(";");
            if (parts.length == 3 && parts[0].equalsIgnoreCase("PLAYER_HEAD")) {
                this.item = new ItemStack(Items.PLAYER_HEAD);
                // Player head textures are resolved asynchronously in the Bukkit version.
                // In Fabric, we can set the profile component directly.
                // The actual texture resolution will be handled lazily when build() is called.
                try {
                    if (parts[1].equalsIgnoreCase("B64")) {
                        ResolvableProfile profile = SkinHelper.createProfileFromB64(parts[2]);
                        if (profile != null) {
                            this.item.set(DataComponents.PROFILE, profile);
                        }
                    } else if (parts[1].equalsIgnoreCase("URL")) {
                        ResolvableProfile profile = SkinHelper.createProfileFromUrl(parts[2]);
                        if (profile != null) {
                            this.item.set(DataComponents.PROFILE, profile);
                        }
                    } else if (parts[1].equalsIgnoreCase("NAME")) {
                        ResolvableProfile profile = SkinHelper.createProfileFromName(parts[2]);
                        if (profile != null) {
                            this.item.set(DataComponents.PROFILE, profile);
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid PLAYER_HEAD texture type: " + parts[1] +
                                ". Expected 'B64', 'URL', or 'NAME'.");
                    }
                } catch (Exception e) {
                    XLogger.debug("Failed to parse texture data: {0} {1}", parts[2], e.getMessage());
                }
            } else {
                throw new IllegalArgumentException("Invalid material type: " + config.material);
            }
        } else {
            this.item = resolveMaterial(config.material);
        }
        this.symbol = config.getSymbol();
        this.displayName = config.name;
        this.lore = new ArrayList<>(config.lore);
    }

    /**
     * Sets the display name of the button with formatted arguments.
     * The arguments are applied to placeholders ({0}, {1}, ...) in the display name.
     *
     * @param args the arguments to format the display name
     * @return the current instance for method chaining
     */
    public ChestButton setDisplayNameArgs(Object... args) {
        this.displayName = formatString(displayName, args);
        return this;
    }

    /**
     * Sets the lore of the button with formatted arguments.
     * Each argument replaces corresponding placeholders in the lore strings.
     *
     * @param args the arguments to format the lore
     * @return the current instance for method chaining
     */
    public ChestButton setLoreArgs(Object... args) {
        this.lore = formatStringList(lore, args);
        return this;
    }

    /**
     * Called when this button is clicked in the chest view.
     *
     * @param slot the slot index that was clicked
     */
    public abstract void onClick(int slot);

    /**
     * Builds the final {@link ItemStack} for this button, applying display name,
     * lore, color codes, and placeholder resolution for the given player.
     *
     * @param viewOwnerUUID the UUID of the player viewing the chest
     * @return the built ItemStack ready to be placed in the inventory
     */
    public ItemStack build(UUID viewOwnerUUID) {
        // Apply display name
        if (displayName != null) {
            String resolved = setPlaceholder(viewOwnerUUID, displayName);
            resolved = ColorParser.getFormattedType(resolved);
            this.item.set(DataComponents.CUSTOM_NAME, Component.literal(resolved));
        }
        // Apply lore
        if (lore != null && !lore.isEmpty()) {
            List<Text> loreTexts = new ArrayList<>();
            for (String line : lore) {
                String resolved = setPlaceholder(viewOwnerUUID, line);
                resolved = ColorParser.getFormattedType(resolved);
                loreTexts.add(Component.literal(resolved));
            }
            this.item.set(DataComponents.LORE, new ItemLore(loreTexts));
        }
        return item;
    }

    public char getSymbol() {
        return symbol;
    }

    /**
     * Resolves a Bukkit Material name string to a Fabric ItemStack.
     *
     * @param materialName the Bukkit Material enum name (e.g. "NETHER_STAR")
     * @return an ItemStack of the corresponding Fabric item
     * @throws IllegalArgumentException if the material name is unknown
     */
    private static ItemStack resolveMaterial(String materialName) {
        // Common material mappings used by Dominion
        return switch (materialName.toUpperCase()) {
            case "GRAY_STAINED_GLASS_PANE" -> new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
            case "BARRIER" -> new ItemStack(Items.BARRIER);
            case "NETHER_STAR" -> new ItemStack(Items.NETHER_STAR);
            case "PAPER" -> new ItemStack(Items.PAPER);
            case "BOOK" -> new ItemStack(Items.BOOK);
            case "WRITABLE_BOOK" -> new ItemStack(Items.WRITABLE_BOOK);
            case "WRITTEN_BOOK" -> new ItemStack(Items.WRITTEN_BOOK);
            case "MAP" -> new ItemStack(Items.MAP);
            case "CLOCK" -> new ItemStack(Items.CLOCK);
            case "COMPASS" -> new ItemStack(Items.COMPASS);
            case "GOLD_INGOT" -> new ItemStack(Items.GOLD_INGOT);
            case "IRON_INGOT" -> new ItemStack(Items.IRON_INGOT);
            case "DIAMOND" -> new ItemStack(Items.DIAMOND);
            case "EMERALD" -> new ItemStack(Items.EMERALD);
            case "REDSTONE" -> new ItemStack(Items.REDSTONE);
            case "ENDER_PEARL" -> new ItemStack(Items.ENDER_PEARL);
            case "CHEST" -> new ItemStack(Items.CHEST);
            case "ENDER_CHEST" -> new ItemStack(Items.ENDER_CHEST);
            case "ANVIL" -> new ItemStack(Items.ANVIL);
            case "NAME_TAG" -> new ItemStack(Items.NAME_TAG);
            case "ARROW" -> new ItemStack(Items.ARROW);
            case "SPECTRAL_ARROW" -> new ItemStack(Items.SPECTRAL_ARROW);
            case "OAK_SIGN" -> new ItemStack(Items.OAK_SIGN);
            case "OAK_DOOR" -> new ItemStack(Items.OAK_DOOR);
            case "FEATHER" -> new ItemStack(Items.FEATHER);
            case "STICK" -> new ItemStack(Items.STICK);
            case "WHITE_WOOL" -> new ItemStack(Items.WHITE_WOOL);
            case "LIME_WOOL" -> new ItemStack(Items.LIME_WOOL);
            case "RED_WOOL" -> new ItemStack(Items.RED_WOOL);
            case "GREEN_WOOL" -> new ItemStack(Items.GREEN_WOOL);
            case "BLUE_WOOL" -> new ItemStack(Items.BLUE_WOOL);
            case "GOLD_BLOCK" -> new ItemStack(Items.GOLD_BLOCK);
            case "IRON_BLOCK" -> new ItemStack(Items.IRON_BLOCK);
            case "DIAMOND_BLOCK" -> new ItemStack(Items.DIAMOND_BLOCK);
            case "EMERALD_BLOCK" -> new ItemStack(Items.EMERALD_BLOCK);
            case "COMMAND_BLOCK" -> new ItemStack(Items.COMMAND_BLOCK);
            case "BREWING_STAND" -> new ItemStack(Items.BREWING_STAND);
            case "ENCHANTING_TABLE" -> new ItemStack(Items.ENCHANTING_TABLE);
            case "BLAZE_ROD" -> new ItemStack(Items.BLAZE_ROD);
            case "EXPERIENCE_BOTTLE" -> new ItemStack(Items.EXPERIENCE_BOTTLE);
            case "FIREWORK_ROCKET" -> new ItemStack(Items.FIREWORK_ROCKET);
            case "PLAYER_HEAD" -> new ItemStack(Items.PLAYER_HEAD);
            default -> {
                XLogger.warn("Unknown material name: {0}, falling back to BARRIER", materialName);
                yield new ItemStack(Items.BARRIER);
            }
        };
    }
}
