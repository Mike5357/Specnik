package com.thenodemc.specnik.config;

import info.pixelmon.repack.org.spongepowered.ConfigurationNode;
import info.pixelmon.repack.org.spongepowered.ConfigurationOptions;
import info.pixelmon.repack.org.spongepowered.objectmapping.ObjectMapper;
import info.pixelmon.repack.org.spongepowered.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class SpecnikConfigManager {
    private static final Path CONFIG_PATH = Paths.get("config/Specnik/config.yml");

    public static SpecnikConfig load() {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(CONFIG_PATH)
                .indent(2)
                .defaultOptions(ConfigurationOptions.defaults().header(
                        "Specnik by Mike5357"
                        + System.lineSeparator() +
                        "Need help? Wiki: https://github.com/Mike5357/Specnik/wiki"
                        + System.lineSeparator() +
                        "Found a bug or have a suggestion? Please open an issue at https://github.com/Mike5357/Specnik/issues"
                ))
                .build();

        try {
            ConfigurationNode rootNode = loader.load();
            ObjectMapper<SpecnikConfig> mapper = ObjectMapper.factory().get(SpecnikConfig.class);

            SpecnikConfig config = mapper.load(rootNode);

            mapper.save(config, rootNode);
            loader.save(rootNode);

            return config;

        } catch (IOException e) {
            throw new RuntimeException("Failed to create default Specnik config", e);
        }
    }
}
