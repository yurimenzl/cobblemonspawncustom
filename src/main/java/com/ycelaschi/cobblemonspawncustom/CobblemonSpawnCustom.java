package com.ycelaschi.cobblemonspawncustom;


import com.ycelaschi.cobblemonspawncustom.config.ConfigLoader;
import com.ycelaschi.cobblemonspawncustom.config.SpeciesConfig;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import kotlin.Unit;

import com.cobblemon.mod.common.pokemon.properties.HiddenAbilityProperty;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.ycelaschi.cobblemonspawncustom.util.PokemonSpawnListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mod(CobblemonSpawnCustom.MOD_ID)
public class CobblemonSpawnCustom  {
    public static final String MOD_ID = "cobblemonspawncustom";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CobblemonSpawnCustom(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.register(new ModEvents());
        modEventBus.addListener(this::commonSetup);
    }

    public static void initialize() {
        ConfigLoader.loadConfig();

        PokemonSpawnListener.onPokemonSpawn(Priority.HIGH, (PokemonEntity pokemonEntity) -> {
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
                    LOGGER.info("[Cobblemon Spawn Custom] Set {} IV {} for: {}", stat, ivValue, speciesName);
                }

                for (Stats stat : remainingStats) {
                    Random rand = new Random();
                    int randomIv = rand.nextInt(32); // de 0 a 31
                    originalPokemon.setIV(stat, randomIv);
                    LOGGER.info("[Cobblemon Spawn Custom] Set {} IV {} for: {}", stat, randomIv, speciesName);
                }

                Random randomShiny = new Random();
                double shinyRandom = randomShiny.nextDouble();
                if ( shinyRandom < shinyChance) {
                    originalPokemon.setShiny(true);
                    LOGGER.info("[Cobblemon Spawn Custom] Set Shiny for: {}", speciesName);
                }

                Random randomHa = new Random();
                double haRandom = randomHa.nextDouble();
                if ( haRandom < haChance) {
                    new HiddenAbilityProperty(true).apply(originalPokemon);
                    LOGGER.info("[Cobblemon Spawn Custom] Set Hidden Ability for: {}", speciesName);
                }

                pokemonEntity.setPokemon(originalPokemon);
            }

            return Unit.INSTANCE;
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(CobblemonSpawnCustom::initialize);
    }
}
