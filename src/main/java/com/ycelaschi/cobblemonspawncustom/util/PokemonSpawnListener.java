package com.ycelaschi.cobblemonspawncustom.util;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.ycelaschi.cobblemonspawncustom.config.ConfigLoader;
import com.ycelaschi.cobblemonspawncustom.config.SpeciesConfig;

import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.HiddenAbilityProperty;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;

import java.util.*;

public class PokemonSpawnListener {

    private static final Logger LOGGER = LoggerFactory.getLogger("CobblemonSpawnCustom");

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof PokemonEntity pokemonEntity)) return;

        if (!ConfigLoader.isLoaded()) {
            LOGGER.warn("Config ainda não carregada quando Pokémon spawnou.");
            return;
        }

        Pokemon originalPokemon = pokemonEntity.getPokemon();
        String speciesName = originalPokemon.getSpecies().getName().toLowerCase();
        SpeciesConfig config = ConfigLoader.getSpeciesConfig(speciesName);

        if (config != null) {
            int ivValue = config.ivValue;
            int ivQuantity = config.ivQuantity;
            double shinyChance = config.shinyChance;
            double haChance = config.haChance;

            List<Stats> allStats = Arrays.asList(
                    Stats.HP, Stats.ATTACK, Stats.DEFENCE,
                    Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE, Stats.SPEED
            );

            Collections.shuffle(allStats);
            List<Stats> selectedStats = allStats.subList(0, ivQuantity);
            List<Stats> remainingStats = allStats.subList(ivQuantity, allStats.size());

            for (Stats stat : selectedStats) {
                originalPokemon.setIV(stat, ivValue);
                LOGGER.info("[CobblemonSpawnCustom] Set {} IV {} for: {}", stat, ivValue, speciesName);
            }

            for (Stats stat : remainingStats) {
                int randomIv = new Random().nextInt(32);
                originalPokemon.setIV(stat, randomIv);
                LOGGER.info("[CobblemonSpawnCustom] Set {} IV {} for: {}", stat, randomIv, speciesName);
            }

            if (new Random().nextDouble() < shinyChance) {
                originalPokemon.setShiny(true);
                LOGGER.info("[CobblemonSpawnCustom] Set Shiny for: {}", speciesName);
            }

            if (new Random().nextDouble() < haChance) {
                new HiddenAbilityProperty(true).apply(originalPokemon);
                LOGGER.info("[CobblemonSpawnCustom] Set Hidden Ability for: {}", speciesName);
            }

            pokemonEntity.setPokemon(originalPokemon);
        }
    }
}
