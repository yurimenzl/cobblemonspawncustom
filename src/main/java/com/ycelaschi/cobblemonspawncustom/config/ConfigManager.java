package com.ycelaschi.cobblemonspawncustom.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class ConfigManager {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config/cobblemon_spawn_config.json");
    private static final String DEFAULT_CONFIG_PATH = "assets/cobblemonspawncustom/config/default_cobblemon_spawn_config.json";

    public static void loadConfig(ResourceManager resourceManager) {
        try {
            String defaultJson = loadDefaultJson(resourceManager);
            if (!Files.exists(CONFIG_PATH)) {
                createConfigFromJson(defaultJson);
            } else {
                String existingJson = Files.readString(CONFIG_PATH);

                if (!jsonEquals(existingJson, defaultJson)) {
                    System.out.println("Configuração modificada detectada. Substituindo pela configuração padrão.");
                    createConfigFromJson(defaultJson);
                }
            }

            String finalJson = Files.readString(CONFIG_PATH);
            ConfigData config = GSON.fromJson(finalJson, ConfigData.class);
        } catch (IOException e) {
            System.err.println("Erro ao carregar configuração: " + e.getMessage());
        }

    }

    private static String loadDefaultJson(ResourceManager resourceManager) throws IOException {
        ResourceLocation defaultConfigLocation = ResourceLocation.parse("cobblemonspawncustom:config/default_cobblemon_spawn_config.json");
        Resource resource = resourceManager.getResource(defaultConfigLocation).orElseThrow();

        try (InputStream stream = resource.open();
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        }
    }

    private static void createDefaultConfig(ResourceManager resourceManager) throws IOException {
        ResourceLocation defaultConfigLocation = ResourceLocation.parse("cobblemonspawncustom:config/default_cobblemon_spawn_config.json");
        Resource resource = resourceManager.getResource(defaultConfigLocation).orElseThrow();

        String line;
        try (
                InputStream defaultConfigStream = resource.open();
                BufferedReader reader = new BufferedReader(new InputStreamReader(defaultConfigStream));
                FileWriter writer = new FileWriter(CONFIG_PATH.toFile());
        ) {
            while((line = reader.readLine()) != null) {
                writer.write(line + "\n");
            }
        }

    }

    private static void createConfigFromJson(String json) throws IOException {
        Files.createDirectories(CONFIG_PATH.getParent());
        Files.writeString(CONFIG_PATH, json);
    }

    private static boolean jsonEquals(String json1, String json2) {
        try {
            Object obj1 = GSON.fromJson(json1, Object.class);
            Object obj2 = GSON.fromJson(json2, Object.class);
            return obj1.equals(obj2);
        } catch (Exception var4) {
            return false;
        }
    }

    public static class ConfigData {
        public int ivValue;
        public int ivQuantity;
        public double shinyChance;
        public double haChance;
        public List<String> speciesList;
    }
}
