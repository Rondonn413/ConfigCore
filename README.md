# ConfigCore - Technical Documentation

# Install
[![](https://jitpack.io/v/Joseplay1012/ConfigCore.svg)](https://jitpack.io/#Joseplay1012/ConfigCore)

## Overview

The `AbstractConfig` class defines the foundation for managing
configuration files in Bukkit/Spigot-based plugins.\
Its goal is to provide an abstract layer that unifies: - Loading and
saving YAML files. - Internal cache for fast value access. - Strong
typing system using `enum` as keys. - Automatic processing of formatted
messages.

This architecture eliminates repetitive code across different
configurations and allows the creation of robust, safe, and consistent
configurations throughout the plugin.

------------------------------------------------------------------------

## Core Structure

### 1. Configuration File

-   The storage location for data, typically in YAML format.
-   Examples: `config.yml`, `messages.yml`.

### 2. Enum Keys

-   Each configuration key is represented by an `enum` implementing the
    `ConfigKey` interface.
-   Each key defines:
    -   Path in the YAML (`path`).
    -   Value type (`ValueType`).
    -   Default value (`defaultValue`).

### 3. Internal Cache

-   Loaded values are stored in a `Map<Enum<?>, Object>`.
-   Avoids repeated file access.
-   Allows fast retrieval with utility methods (`getString`, `getInt`,
    etc.).

------------------------------------------------------------------------

## Class Components

### ConfigKey Interface

Standardizes access to configuration keys:

``` java
public interface ConfigKey {
    String getPath();         // YAML path
    ValueType getType();      // Value type
    Object getDefaultValue(); // Default value
}
```

### ValueType Enum

Defines supported configuration value types:

-   `STRING`
-   `STRING_LIST`
-   `INT`
-   `INT_LIST`
-   `BOOLEAN`
-   `DOUBLE`

Each type is mapped to its corresponding Java class.

------------------------------------------------------------------------

## Practical Usage

### Example Enum Keys

``` java
public enum MyKeys implements ConfigKey {
    ENABLE_FEATURE("my-section.enable-feature", ValueType.BOOLEAN, true),
    MAX_PLAYERS("my-section.max-players", ValueType.INT, 10),
    WELCOME_MESSAGE("my-section.welcome", ValueType.STRING, "&aWelcome!");

    private final String path;
    private final ValueType type;
    private final Object defaultValue;

    MyKeys(String path, ValueType type, Object defaultValue) {
        this.path = path;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getPath() { return path; }
    @Override
    public ValueType getType() { return type; }
    @Override
    public Object getDefaultValue() { return defaultValue; }
}
```

### Concrete Class

``` java
public class MyConfig extends AbstractConfig {

    public MyConfig(File file) {
        super(file);
        loadValues(MyKeys.class, "my-section");
    }

    @Override
    protected Object getDefaultValue(Enum<?> key) {
        return ((MyKeys) key).getDefaultValue();
    }

    @Override
    protected ConfigKey getConfigKey(Enum<?> key) {
        return (MyKeys) key;
    }
}
```

### Corresponding YAML Example

``` yaml
my-section:
  enable-feature: true
  max-players: 15
  welcome: "&bWelcome to the server!"
```

### Plugin Usage Example

``` java
MyConfig config = new MyConfig(new File(plugin.getDataFolder(), "config.yml"));

boolean enabled = config.getBoolean(MyConfig.MyKeys.ENABLE_FEATURE);
int maxPlayers = config.getInt(MyConfig.MyKeys.MAX_PLAYERS);
String welcomeMessage = config.getString(MyConfig.MyKeys.WELCOME_MESSAGE);

config.setValue(MyConfig.MyKeys.MAX_PLAYERS, 20); // Update and save to file
```

------------------------------------------------------------------------

## Message Processing

All values of type `STRING` or `STRING_LIST` go through the
`processMessage` method, which uses `GradientMessage` for formatted
message processing.\
This allows gradient-colored messages to be declared directly in YAML.

------------------------------------------------------------------------

## Execution Flow

1.  The plugin instantiates the concrete class extending
    `AbstractConfig`.
2.  The `loadValues` method loads enum-defined keys into the cache.
3.  The developer accesses values using typed methods (`getString`,
    `getInt`, etc.).
4.  Invalid or missing YAML values are automatically replaced with the
    default value defined in the enum.
5.  Updates are performed via `setValue`, which updates both cache and
    file.

------------------------------------------------------------------------

## Benefits

-   Centralized configuration logic.
-   Elimination of code duplication.
-   Strongly typed consistency in value access.
-   Native support for gradient messages.
-   Easy maintenance and expansion.
