package net.joseplay.configCore;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for configuration management in the Marriage plugin.
 * Supports multiple data types (strings, lists, ints, etc.) with type-safe access via enums.
 * Provides loading, saving, and validation with defaults from plugin resources.
 */
public abstract class AbstractConfig {
    protected final File file;
    protected FileConfiguration config;
    protected final Map<Enum<?>, Object> values = new HashMap<>();

    /**
     * Constructor. Initializes the config file and loads values.
     * @param file The config file (e.g., config.yml, messages.yml).
     */
    public AbstractConfig(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
        loadConfig();
    }

    /**
     * Loads the configuration, saving defaults if the file doesn't exist.
     */
    public void loadConfig() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
    }

    /**
     * Processes a message string with gradient formatting.
     * @param rawMsg The raw message.
     * @return The processed message or fallback.
     */
    public String processMessage(String rawMsg) {
        if (rawMsg == null || rawMsg.trim().isEmpty()) {
            return "";
        }
        return rawMsg.replace("&", "§");
    }

    /**
     * Saves the configuration to file.
     * @throws IOException on save failure.
     */
    public void saveConfig() throws IOException {
        config.save(file);
    }

    /**
     * Loads values from config into cache. Must be implemented by subclasses.
     */
    protected <E extends Enum<E> & ConfigKey> void loadValues(Class<E> enumClass, String pathConfig) {
        values.clear();
        ConfigurationSection section = config.getConfigurationSection(pathConfig);
        if (section == null) {
            Bukkit.getLogger().warning("No '" + pathConfig + "' section found in " + file.getName() + ". Using defaults.");
            return;
        }

        for (E key : enumClass.getEnumConstants()) {
            String path = key.getPath();
            Object loaded = null;
            boolean valid = false;

            switch (key.getType()) {
                case STRING:
                    if (config.isString(path)) {
                        String rawMsg = config.getString(path);
                        loaded = processMessage(rawMsg);
                        valid = true;
                    }
                    break;
                case STRING_LIST:
                    if (config.isList(path)) {
                        List<String> rawList = config.getStringList(path);
                        loaded = Collections.unmodifiableList(rawList.stream()
                                .map(this::processMessage)
                                .toList());
                        valid = true;
                    }
                    break;
                case INT:
                    if (config.isInt(path)) {
                        loaded = config.getInt(path);
                        valid = true;
                    }
                    break;
                case INT_LIST:
                    if (config.isList(path)) {
                        List<?> rawList = config.getList(path);
                        List<Integer> intList = rawList.stream()
                                .filter(obj -> obj instanceof Number)
                                .map(obj -> ((Number) obj).intValue())
                                .toList();
                        loaded = Collections.unmodifiableList(intList);
                        valid = !intList.isEmpty();
                    }
                    break;
                case BOOLEAN:
                    if (config.isBoolean(path)) {
                        loaded = config.getBoolean(path);
                        valid = true;
                    }
                    break;
                case DOUBLE:
                    if (config.isDouble(path)) {
                        loaded = config.getDouble(path);
                        valid = true;
                    }
                    break;
                default:
                    Bukkit.getLogger().warning("Unknown type for " + path);
            }

            if (!valid) {
                loaded = key.getDefaultValue();
                Bukkit.getLogger().warning("Invalid or missing value for " + path + ". Using default: " + loaded);
            }
            values.put(key, loaded);
        }
    }

    /**
     * Gets a value from cache or default.
     * @param key The enum key (must match ConfigKey type in subclass).
     * @return The value, cast to expected type.
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(Enum<?> key) {
        return (T) values.getOrDefault(key, getDefaultValue(key));
    }

    /**
     * Gets a value from cache or default.
     * @param key The enum key (must match ConfigKey type in subclass).
     * @return The value, cast to expected type.
     */
    @SuppressWarnings("unchecked")
    public <P extends Enum & ConfigKey, C> C getValue(P key, ValueType type) {
        if (key.getType() != type) throw new IllegalArgumentException("Key " + key + " is of type " + key.getType() + ", expected " + type);
        C value = (C) values.getOrDefault(key, key.getDefaultValue());
        return value;
    }

    /**
     * Gets a value {@link String} from {@link ConfigKey}
     */
    @SuppressWarnings("unchecked")
    public <P extends Enum & ConfigKey> String getString(P key){
        return getValue(key, ValueType.STRING);
    }

    /**
     * Gets a value {@link List<String>} from {@link ConfigKey}
     */
    @SuppressWarnings("unchecked")
    public <P extends Enum & ConfigKey> List<String> getStringList(P key){
        return getValue(key, ValueType.STRING_LIST);
    }

    /**
     * Gets a value {@link Integer} from {@link ConfigKey}
     */
    @SuppressWarnings("unchecked")
    public <P extends Enum & ConfigKey> int getInt(P key){
        return getValue(key, ValueType.INT);
    }

    /**
     * Gets a value {@link List<Integer>} from {@link ConfigKey}
     */
    @SuppressWarnings("unchecked")
    public <P extends Enum & ConfigKey> List<Integer> getIntList(P key){
        return getValue(key, ValueType.INT_LIST);
    }

    /**
     * Gets a value {@link Boolean} from {@link ConfigKey}
     */
    @SuppressWarnings("unchecked")
    public <P extends Enum & ConfigKey> boolean getBoolean(P key){
        return getValue(key, ValueType.BOOLEAN);
    }

    /**
     * Gets a value {@link Double} from {@link ConfigKey}
     */
    @SuppressWarnings("unchecked")
    public <P extends Enum & ConfigKey> String getDouble(P key){
        return getValue(key, ValueType.DOUBLE);
    }

    /**
     * Sets a value in config and cache, then saves.
     * @param key The enum key.
     * @param value The value to set.
     */
    public boolean setValue(Enum<?> key, Object value) {
        ConfigKey configKey = getConfigKey(key);
        config.set(configKey.getPath(), value);
        values.put(key, value);
        try {
            saveConfig();
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to save " + file.getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the default value for a key. Must be implemented by subclasses.
     */
    protected abstract Object getDefaultValue(Enum<?> key);

    /**
     * Converts enum key to ConfigKey. Must be implemented by subclasses.
     */
    protected abstract ConfigKey getConfigKey(Enum<?> key);

    /**
     * Interface for configuration keys, defining path, type, and default value.
     */
    public interface ConfigKey {
        String getPath();
        ValueType getType();
        Object getDefaultValue();
    }

    /**
     * Enum for value types supported in configuration.
     */
    public enum ValueType {
        STRING(String.class), STRING_LIST(List.class), INT(Integer.class), INT_LIST(List.class), BOOLEAN(Boolean.class), DOUBLE(Double.class);

        private final Class<?> aClass;

        ValueType(Class<?> aClass){
            this.aClass = aClass;
        }

        public Class<?> getaClass() {
            return aClass;
        }
    }
}