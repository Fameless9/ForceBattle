package net.fameless.core.config;

import net.fameless.core.caption.Caption;
import net.fameless.core.util.PluginPaths;
import net.fameless.core.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PluginConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("BungeeAFK/" + PluginConfig.class.getSimpleName());
    public static Yaml YAML;
    private static YamlConfig config;

    public static void init() {
        LOGGER.info("Loading configuration...");
        File configFile = PluginPaths.getConfigFile();

        if (!configFile.exists()) {
            ResourceUtil.extractResourceIfMissing("config.yml", configFile);
        }

        String yamlContent;
        try {
            yamlContent = new String(Files.readAllBytes(Paths.get(configFile.toURI())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        YAML = new Yaml();
        config = new YamlConfig(YAML.load(yamlContent));
    }

    public static void reload() {
        init();
    }

    public static void reloadAll() {
        LOGGER.info("Reloading all configurations...");
        PluginConfig.reload();
        Caption.loadDefaultLanguages();
    }

    public static void shutdown() {
        saveNow();
    }

    public static void saveNow() {
        File configFile = PluginPaths.getConfigFile();

        String fileContent = YAML.dump(config.data());
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(fileContent);
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("Failed to write configuration file: {}", configFile.getAbsolutePath(), e);
        }
    }

    public static YamlConfig get() {
        return config;
    }

}
