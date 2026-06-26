package cn.lunadeer.dominion.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing color codes in strings.
 */
public class ColorParser {

    /**
     * Removes color codes from a string, returning plain text.
     *
     * @param text the string with color codes
     * @return the plain text string
     */
    public static String getPlainText(String text) {
        String[] parts = text.split("&#");
        StringBuilder res = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            String content;
            if (part.length() >= 6 && part.substring(0, 6).matches("^[0-9a-fA-F]{6}$")) {
                content = part.substring(6);
            } else {
                content = part;
            }
            res.append(content);
        }
        return res.toString();
    }

    /**
     * Converts a string with color codes into a Fabric/Minecraft-compatible color string.
     * Uses section sign (§) format for legacy color codes.
     *
     * @param text the string with color codes
     * @return the formatted color string
     */
    public static String getFormattedType(String text) {
        String title = "&f" + text + "&f";
        title = title.replaceAll("&#", "#");
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(title);
        while (matcher.find()) {
            String hexCode = matcher.group();
            StringBuilder builder = new StringBuilder("&x");
            for (char c : hexCode.substring(1).toCharArray()) {
                builder.append('&').append(c);
            }
            title = title.replace(hexCode, builder.toString());
        }
        return translateAlternateColorCodes('&', title);
    }

    /**
     * Translates alternate color codes in a string.
     * Converts '&' prefixed color codes to section sign (§) prefixed codes.
     *
     * @param altColorChar the alternate color code character
     * @param textToTranslate the text to translate
     * @return the translated text
     */
    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}
