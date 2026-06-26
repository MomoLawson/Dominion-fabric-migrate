package cn.lunadeer.dominion.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Miscellaneous utility methods.
 * Ported from Bukkit to Fabric.
 */
public class Misc {

    /**
     * Formats a string by replacing placeholders with the provided arguments.
     * Each placeholder in the format `{index}` within the string is replaced
     * with the corresponding argument from the `args` array.
     *
     * @param str  the string containing placeholders to format
     * @param args the arguments to replace placeholders in the string
     * @return the formatted string
     */
    public static String formatString(String str, Object... args) {
        String formatStr = str;
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                args[i] = "[null]";
            }
            formatStr = formatStr.replace("{" + i + "}", args[i].toString());
        }
        return formatStr;
    }

    /**
     * Formats a list of strings by replacing placeholders with the provided arguments.
     *
     * @param list the list of strings to format
     * @param args the arguments to replace placeholders in the strings
     * @return a new list of formatted strings
     */
    public static List<String> formatStringList(List<String> list, Object... args) {
        List<String> result = new ArrayList<>();
        for (String s : list) {
            result.add(formatString(s, args));
        }
        return result;
    }
}
