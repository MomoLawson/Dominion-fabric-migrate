package cn.lunadeer.dominion.utils.scui.configuration;

import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.configuration.HandleManually;

import java.net.URL;
import java.util.List;

/**
 * Button configuration data class for CUI (Chest User Interface).
 * <p>
 * Ported from Bukkit to Fabric. Material references are stored as string
 * identifiers (e.g. "NETHER_STAR") matching Bukkit Material enum names.
 * The actual conversion to Fabric {@code net.minecraft.item.Items} happens
 * in {@link cn.lunadeer.dominion.utils.scui.ChestButton#build}.
 * <p>
 * Player head textures are stored as a semicolon-delimited string:
 * {@code "PLAYER_HEAD;B64;<base64>"},
 * {@code "PLAYER_HEAD;URL;<skin_url>"},
 * {@code "PLAYER_HEAD;NAME;<player_name>"}.
 */
public class ButtonConfiguration extends ConfigurationPart {

    /**
     * Creates a ButtonConfiguration with a standard material type.
     *
     * @param symbol   the character symbol representing the button in the layout
     * @param material the Bukkit Material name (e.g. "NETHER_STAR", "PAPER")
     * @param name     the display name of the button
     * @param lore     the lore (description lines) of the button
     * @return a new ButtonConfiguration
     */
    @HandleManually
    public static ButtonConfiguration createMaterial(char symbol, String material, String name, List<String> lore) {
        ButtonConfiguration buttonConfig = new ButtonConfiguration();
        buttonConfig.symbol = String.valueOf(symbol);
        buttonConfig.name = name;
        buttonConfig.lore = lore;
        buttonConfig.material = material;
        return buttonConfig;
    }

    /**
     * Creates a ButtonConfiguration with a player head using a base64-encoded texture.
     *
     * @param symbol     the character symbol representing the button in the layout
     * @param textureB64 the base64-encoded profile texture value
     * @param name       the display name of the button
     * @param lore       the lore (description lines) of the button
     * @return a new ButtonConfiguration
     */
    @HandleManually
    public static ButtonConfiguration createHeadByB64(char symbol, String textureB64, String name, List<String> lore) {
        ButtonConfiguration buttonConfig = new ButtonConfiguration();
        buttonConfig.symbol = String.valueOf(symbol);
        buttonConfig.name = name;
        buttonConfig.lore = lore;
        buttonConfig.material = "PLAYER_HEAD;B64;" + textureB64;
        return buttonConfig;
    }

    /**
     * Creates a ButtonConfiguration with a player head by player name.
     *
     * @param symbol     the character symbol representing the button in the layout
     * @param playerName the name of the player whose head texture will be used
     * @param name       the display name of the button
     * @param lore       the lore (description lines) of the button
     * @return a new ButtonConfiguration
     */
    @HandleManually
    public static ButtonConfiguration createHeadByName(char symbol, String playerName, String name, List<String> lore) {
        ButtonConfiguration buttonConfig = new ButtonConfiguration();
        buttonConfig.symbol = String.valueOf(symbol);
        buttonConfig.name = name;
        buttonConfig.lore = lore;
        buttonConfig.material = "PLAYER_HEAD;NAME;" + playerName;
        return buttonConfig;
    }

    /**
     * Creates a ButtonConfiguration with a player head using a skin URL.
     *
     * @param symbol  the character symbol representing the button in the layout
     * @param skinUrl the URL of the skin texture
     * @param name    the display name of the button
     * @param lore    the lore (description lines) of the button
     * @return a new ButtonConfiguration
     */
    @HandleManually
    public static ButtonConfiguration createHeadByUrl(char symbol, URL skinUrl, String name, List<String> lore) {
        ButtonConfiguration buttonConfig = new ButtonConfiguration();
        buttonConfig.symbol = String.valueOf(symbol);
        buttonConfig.name = name;
        buttonConfig.lore = lore;
        buttonConfig.material = "PLAYER_HEAD;URL;" + skinUrl.toString();
        return buttonConfig;
    }

    @HandleManually
    public char getSymbol() {
        return symbol.charAt(0);
    }

    public String symbol;
    public String name;
    public List<String> lore;
    public String material;
}
