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
        if (Files.notExists(CONFIG_PATH)) {
            System.out.println("[CobblemonSpawner] Configuração não encontrada. Criando arquivo padrão...");
            createDefaultConfig();
        }

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

                    if (speciesConfigMap.containsKey(speciesName)) {
                        System.err.println("[CobblemonSpawner] AVISO: Espécie '" + speciesName + "' está duplicada em múltiplos grupos!");
                    }

                    speciesConfigMap.put(speciesName, new SpeciesConfig(ivValue, ivQuantity, shinyChance, haChance));
                }
            }

            loaded = true;
            System.out.println("[CobblemonSpawner] Configuração carregada com sucesso!");
        } catch (IOException e) {
            System.err.println("[CobblemonSpawner] Erro ao carregar configuração: " + e.getMessage());
        }
    }

    private static void createDefaultConfig() {
        JsonObject root = new JsonObject();

        root.add("paradox", createGroup(31, 3, 0.20, 0.25, List.of("ironvaliant")));
        root.add("legendary", createGroup(31, 5, 0.10, 0.15, List.of("celebi")));
        root.add("ultrabeasts", createGroup(31, 4, 0.15, 0.20, List.of("xurkitree")));
        root.add("ditto", createGroup(31, 2, 0.01, 0.01, List.of("ditto")));

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(root, writer);
            }

            System.out.println("[CobblemonSpawner] Arquivo de configuração padrão criado com sucesso.");
        } catch (IOException e) {
            System.err.println("[CobblemonSpawner] Erro ao criar arquivo de configuração: " + e.getMessage());
        }
    }

    private static JsonObject createGroup(int ivValue, int ivQty, double shiny, double ha, List<String> species) {
        JsonObject obj = new JsonObject();
        obj.addProperty("iv_value", ivValue);
        obj.addProperty("iv_quantity", ivQty);
        obj.addProperty("shiny_chance", shiny);
        obj.addProperty("ha_chance", ha);

        JsonArray speciesArray = new JsonArray();
        for (String s : species) {
            speciesArray.add(s);
        }
        obj.add("species_list", speciesArray);

        return obj;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static SpeciesConfig getSpeciesConfig(String speciesName) {
        speciesName = speciesName.toLowerCase();

        if (speciesConfigMap.containsKey(speciesName)) {
            return speciesConfigMap.get(speciesName);
        }

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
