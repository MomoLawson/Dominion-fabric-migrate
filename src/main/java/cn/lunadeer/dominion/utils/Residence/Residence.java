package cn.lunadeer.dominion.utils.Residence;

import java.io.File;
import java.util.Map;

public class Residence {
    private final String name;
    private final Map<String, Object> data;

    public Residence(String name, Map<String, Object> data) {
        this.name = name;
        this.data = data;
    }

    public String getName() { return name; }
    public Map<String, Object> getData() { return data; }

    public static Map<String, Residence> loadFromFile(File file) {
        return new java.util.HashMap<>();
    }
}
