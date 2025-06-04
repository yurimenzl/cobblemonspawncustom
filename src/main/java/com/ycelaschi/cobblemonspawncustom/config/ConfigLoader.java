package com.ycelaschi.cobblemonspawncustom.config;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ConfigLoader {
    private static boolean loaded = false;
    private static final Path CONFIG_PATH = Paths.get("config/cobblemon_spawn_config.json");

    private static final Map<String, SpeciesConfig> speciesConfigMap = new HashMap<>();

    public static void loadConfig() {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            speciesConfigMap.clear();

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                JsonObject group = entry.getValue().getAsJsonObject();
                int ivValue = group.has("iv_value") ? group.get("iv_value").getAsInt() : 31;
                int ivQuantity = group.has("iv_quantity") ? group.get("iv_quantity").getAsInt() : 3;
                double shinyChance = group.has("shiny_chance") ? group.get("shiny_chance").getAsDouble() : 0.00012207;
                double haChance = group.has("ha_chance") ? group.get("ha_chance").getAsDouble() : 0.00012207;

                JsonArray speciesArray = group.getAsJsonArray("species_list");
                for (JsonElement speciesElement : speciesArray) {
                    String speciesName = speciesElement.getAsString().toLowerCase();

                    // Se já existir uma config para essa espécie, avisa
                    if (speciesConfigMap.containsKey(speciesName)) {
                        System.err.println("[CobblemonSpawner] AVISO: Espécie '" + speciesName + "' está duplicada em múltiplos grupos!");
                    }

                    speciesConfigMap.put(speciesName, new SpeciesConfig(ivValue, ivQuantity, shinyChance, haChance));
                }
                loaded = true;
            }

            System.out.println("[CobblemonSpawner] Configuração carregada com sucesso!");
        } catch (IOException e) {
            System.err.println("[CobblemonSpawner] Erro ao carregar configuração: " + e.getMessage());
        }
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static SpeciesConfig getSpeciesConfig(String speciesName) {
        speciesName = speciesName.toLowerCase();

        // 1. Tenta match exato
        if (speciesConfigMap.containsKey(speciesName)) {
            return speciesConfigMap.get(speciesName);
        }

        // 2. Fallback: match por prefixo, mais longo primeiro
        String bestMatch = null;
        for (String key : speciesConfigMap.keySet()) {
            if (speciesName.startsWith(key)) {
                if (bestMatch == null || key.length() > bestMatch.length()) {
                    bestMatch = key;
                }
            }
        }

        return bestMatch != null ? speciesConfigMap.get(bestMatch) : null;
    }
}