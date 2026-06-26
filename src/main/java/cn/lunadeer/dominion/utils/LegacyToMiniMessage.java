package cn.lunadeer.dominion.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts legacy color codes (&0-&9, &a-&f, etc.) to MiniMessage format.
 */
public class LegacyToMiniMessage {
    private static final Map<Character, String> COLOR_MAP = new HashMap<>();
    static {
        COLOR_MAP.put('0', "<black>"); COLOR_MAP.put('1', "<dark_blue>");
        COLOR_MAP.put('2', "<dark_green>"); COLOR_MAP.put('3', "<dark_aqua>");
        COLOR_MAP.put('4', "<dark_red>"); COLOR_MAP.put('5', "<dark_purple>");
        COLOR_MAP.put('6', "<gold>"); COLOR_MAP.put('7', "<gray>");
        COLOR_MAP.put('8', "<dark_gray>"); COLOR_MAP.put('9', "<blue>");
        COLOR_MAP.put('a', "<green>"); COLOR_MAP.put('b', "<aqua>");
        COLOR_MAP.put('c', "<red>"); COLOR_MAP.put('d', "<light_purple>");
        COLOR_MAP.put('e', "<yellow>"); COLOR_MAP.put('f', "<white>");
        COLOR_MAP.put('k', "<obfuscated>"); COLOR_MAP.put('l', "<bold>");
        COLOR_MAP.put('m', "<strikethrough>"); COLOR_MAP.put('n', "<underlined>");
        COLOR_MAP.put('o', "<italic>"); COLOR_MAP.put('r', "<reset>");
    }

    public static String convert(String legacy) {
        if (legacy == null) return "";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < legacy.length(); i++) {
            char c = legacy.charAt(i);
            if ((c == '&' || c == '§') && i + 1 < legacy.length()) {
                char code = Character.toLowerCase(legacy.charAt(i + 1));
                String mini = COLOR_MAP.get(code);
                if (mini != null) {
                    result.append(mini);
                    i++;
                    continue;
                }
            }
            result.append(c);
        }
        return result.toString();
    }
}
