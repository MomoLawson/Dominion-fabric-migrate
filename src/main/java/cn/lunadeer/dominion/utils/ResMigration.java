package cn.lunadeer.dominion.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ResMigration {
    private static final Map<String, Map<String, Object>> residenceData = new HashMap<>();

    public static void loadData(File dataFile) {}
    public static Map<String, Map<String, Object>> getResidenceData() { return residenceData; }
    public static boolean hasData() { return !residenceData.isEmpty(); }

    public static class ResidenceNode {
        private final String name;
        private final Map<String, Object> data;
        public ResidenceNode(String name, Map<String, Object> data) { this.name = name; this.data = data; }
        public String getName() { return name; }
        public Map<String, Object> getData() { return data; }
    }
}
