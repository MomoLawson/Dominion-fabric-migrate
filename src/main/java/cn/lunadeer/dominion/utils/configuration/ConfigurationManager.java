package cn.lunadeer.dominion.utils.configuration;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Utility class for loading and saving configuration files.
 * <p>
 * This class uses reflection to read and write configuration files.
 * Capable of reading and writing nested configuration parts.
 * <p>
 * Ported from Bukkit YamlConfiguration to SnakeYAML for Fabric.
 */
public class ConfigurationManager {

    /**
     * Load the configuration file.
     *
     * @param clazz The configuration file class. The class should extend {@link ConfigurationFile}.
     * @param file  The file to load.
     * @throws Exception If failed to load the file.
     */
    public static ConfigurationFile load(Class<? extends ConfigurationFile> clazz, File file) throws Exception {
        if (!file.exists()) {
            return saveDefault(clazz, file);
        }
        Map<String, Object> yamlData = loadYamlFile(file);
        ConfigurationFile instance = readConfigurationFile(yamlData, clazz);
        instance.save(file);
        return instance;
    }

    /**
     * Load the configuration file and update the version field if needed.
     *
     * @param clazz            The configuration file class. The class should extend {@link ConfigurationFile}.
     * @param file             The file to load.
     * @param versionFieldName The name of the version field.
     * @throws Exception If failed to load the file.
     */
    public static ConfigurationFile load(Class<? extends ConfigurationFile> clazz, File file, String versionFieldName) throws Exception {
        Field versionField = clazz.getField(versionFieldName);
        int currentVersion = versionField.getInt(null);
        ConfigurationFile instance = load(clazz, file);
        if (versionField.getInt(null) != currentVersion) {
            File backup = new File(file.getParentFile(), file.getName() + ".bak");
            if (backup.exists() && !backup.delete()) {
                throw new Exception("Failed to delete the backup configuration file.");
            }
            if (!file.renameTo(backup)) {
                throw new Exception("Failed to backup the configuration file.");
            }
            clazz.getField(versionFieldName).set(null, currentVersion);
            return saveDefault(clazz, file);
        }
        return instance;
    }

    /**
     * Save the configuration file with default values.
     *
     * @param clazz The configuration file class. The class should extend {@link ConfigurationFile}.
     * @param file  The file to save.
     * @throws Exception If failed to save the file.
     */
    public static ConfigurationFile saveDefault(Class<? extends ConfigurationFile> clazz, File file) throws Exception {
        createIfNotExist(file);
        Map<String, Object> yamlData = writeConfigurationFile(clazz);
        saveYamlFile(file, yamlData);
        return load(clazz, file);
    }

    /**
     * Load YAML data from a file using SnakeYAML.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadYamlFile(File file) throws Exception {
        Yaml yaml = new Yaml();
        try (InputStream is = new FileInputStream(file)) {
            Object data = yaml.load(is);
            if (data instanceof Map) {
                return (Map<String, Object>) data;
            }
            return new LinkedHashMap<>();
        }
    }

    /**
     * Save YAML data to a file using SnakeYAML.
     */
    public static void saveYamlFile(File file, Map<String, Object> data) throws Exception {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setWidth(250);
        Yaml yaml = new Yaml(options);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            yaml.dump(data, writer);
        }
    }

    private static Map<String, Object> writeConfigurationFile(Class<? extends ConfigurationFile> clazz) throws Exception {
        Map<String, Object> yamlData = new LinkedHashMap<>();
        ConfigurationFile instance = clazz.getConstructor().newInstance();
        writeConfigurationPart(yamlData, instance, null);
        return yamlData;
    }

    /**
     * Write a ConfigurationPart's fields into a YAML map structure.
     */
    public static void writeConfigurationPart(Map<String, Object> yaml, ConfigurationPart obj, String prefix) throws Exception {
        writeConfigurationPart(yaml, obj, prefix, false);
    }

    /**
     * Write a ConfigurationPart's fields into a YAML map structure.
     */
    public static void writeConfigurationPart(Map<String, Object> yaml, ConfigurationPart obj, String prefix, boolean ignoreComment) throws Exception {
        for (Field field : obj.getClass().getFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(HandleManually.class)) {
                continue;
            }
            String key = camelToKebab(field.getName());
            if (prefix != null && !prefix.isEmpty()) {
                key = prefix + "." + key;
            }
            if (ConfigurationPart.class.isAssignableFrom(field.getType())) {
                // Ensure a sub-map exists for nested parts
                ensurePath(yaml, key);
                writeConfigurationPart(yaml, (ConfigurationPart) field.get(obj), key);
            } else {
                setInMap(yaml, key, field.get(obj));
            }
        }
    }

    /**
     * Read a ConfigurationPart from a YAML map structure.
     */
    public static ConfigurationPart readConfigurationPart(Map<String, Object> yamlData, ConfigurationPart obj, String prefix) throws Exception {
        for (Field field : obj.getClass().getFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(HandleManually.class)) {
                continue;
            }
            String key = camelToKebab(field.getName());
            if (prefix != null && !prefix.isEmpty()) {
                key = prefix + "." + key;
            }
            boolean missingKey = !containsInMap(yamlData, key);
            if (missingKey) {
                ensurePath(yamlData, key);
            }
            if (ConfigurationPart.class.isAssignableFrom(field.getType())) {
                ConfigurationPart nested = (ConfigurationPart) field.get(obj);
                if (nested == null) {
                    nested = (ConfigurationPart) field.getType().getConstructor().newInstance();
                    field.set(obj, nested);
                }
                readConfigurationPart(yamlData, nested, key);
                field.set(obj, nested);
            } else {
                if (!missingKey) {
                    Object value = getFromMap(yamlData, key);
                    if (value != null) {
                        field.set(obj, convertValue(field.getType(), value));
                    }
                }
            }
        }
        return obj;
    }

    /**
     * Read configuration from a ConfigurationFile (which is also a ConfigurationPart).
     */
    private static ConfigurationFile readConfigurationFile(Map<String, Object> yamlData, Class<? extends ConfigurationFile> clazz) throws Exception {
        ConfigurationFile instance = clazz.getConstructor().newInstance();
        instance.setYamlData(yamlData);
        PrePostProcessInorder processes = getAndSortPrePostProcess(clazz);
        // execute methods with @PreProcess annotation
        for (Method method : processes.preProcessMethods) {
            method.invoke(instance);
        }
        readConfigurationPart(yamlData, instance, null);
        // execute methods with @PostProcess annotation
        for (Method method : processes.postProcessMethods) {
            method.invoke(instance);
        }
        return instance;
    }

    // ---- Map navigation helpers ----

    /**
     * Get a value from a nested map using a dotted key path.
     */
    public static Object getFromMap(Map<String, Object> map, String key) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = map;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (next instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nextMap = (Map<String, Object>) next;
                current = nextMap;
            } else {
                return null;
            }
        }
        return current.get(parts[parts.length - 1]);
    }

    /**
     * Set a value in a nested map using a dotted key path.
     */
    @SuppressWarnings("unchecked")
    public static void setInMap(Map<String, Object> map, String key, Object value) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = map;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (next instanceof Map) {
                current = (Map<String, Object>) next;
            } else {
                Map<String, Object> newMap = new LinkedHashMap<>();
                current.put(parts[i], newMap);
                current = newMap;
            }
        }
        current.put(parts[parts.length - 1], value);
    }

    /**
     * Check if a key exists in a nested map using a dotted key path.
     */
    public static boolean containsInMap(Map<String, Object> map, String key) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = map;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (next instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nextMap = (Map<String, Object>) next;
                current = nextMap;
            } else {
                return false;
            }
        }
        return current.containsKey(parts[parts.length - 1]);
    }

    /**
     * Ensure that all intermediate maps exist for a dotted key path.
     */
    @SuppressWarnings("unchecked")
    private static void ensurePath(Map<String, Object> map, String key) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = map;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (next instanceof Map) {
                current = (Map<String, Object>) next;
            } else {
                Map<String, Object> newMap = new LinkedHashMap<>();
                current.put(parts[i], newMap);
                current = newMap;
            }
        }
    }

    /**
     * Convert a value from SnakeYAML's output to the target field type.
     * SnakeYAML returns Integer for ints, Double for doubles, Boolean for booleans, etc.
     * This handles widening/narrowing conversions.
     */
    private static Object convertValue(Class<?> targetType, Object value) {
        if (value == null) return null;
        if (targetType == int.class || targetType == Integer.class) {
            if (value instanceof Number) return ((Number) value).intValue();
            return Integer.parseInt(value.toString());
        }
        if (targetType == long.class || targetType == Long.class) {
            if (value instanceof Number) return ((Number) value).longValue();
            return Long.parseLong(value.toString());
        }
        if (targetType == double.class || targetType == Double.class) {
            if (value instanceof Number) return ((Number) value).doubleValue();
            return Double.parseDouble(value.toString());
        }
        if (targetType == float.class || targetType == Float.class) {
            if (value instanceof Number) return ((Number) value).floatValue();
            return Float.parseFloat(value.toString());
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            if (value instanceof Boolean) return value;
            return Boolean.parseBoolean(value.toString());
        }
        if (targetType == String.class) {
            return value.toString();
        }
        if (targetType == List.class) {
            if (value instanceof List) return value;
            return new ArrayList<>();
        }
        return value;
    }

    // ---- Utility ----

    /**
     * Converts a camelCase string to kebab-case.
     */
    public static String camelToKebab(String camel) {
        return camel.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase();
    }

    private static void createIfNotExist(File file) throws Exception {
        if (file.exists()) return;
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new Exception("Failed to create %s directory.".formatted(file.getParentFile().getAbsolutePath()));
        if (!file.createNewFile()) throw new Exception("Failed to create %s file.".formatted(file.getAbsolutePath()));
    }

    private static class PrePostProcessInorder {
        public List<Method> preProcessMethods = new ArrayList<>();
        public List<Method> postProcessMethods = new ArrayList<>();
    }

    /**
     * Get methods with @PreProcess and @PostProcess annotations and sort them by priority.
     */
    private static PrePostProcessInorder getAndSortPrePostProcess(Class<? extends ConfigurationFile> clazz) {
        Map<Method, Integer> preProcessMethodsWithPriority = new HashMap<>();
        Map<Method, Integer> postProcessMethodsWithPriority = new HashMap<>();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(PreProcess.class)) {
                preProcessMethodsWithPriority.put(method, method.getAnnotation(PreProcess.class).priority());
            }
            if (method.isAnnotationPresent(PostProcess.class)) {
                postProcessMethodsWithPriority.put(method, method.getAnnotation(PostProcess.class).priority());
            }
        }
        PrePostProcessInorder result = new PrePostProcessInorder();
        result.preProcessMethods = new ArrayList<>(preProcessMethodsWithPriority.keySet());
        result.preProcessMethods.sort(Comparator.comparingInt(preProcessMethodsWithPriority::get));
        result.postProcessMethods = new ArrayList<>(postProcessMethodsWithPriority.keySet());
        result.postProcessMethods.sort(Comparator.comparingInt(postProcessMethodsWithPriority::get));
        return result;
    }
}
