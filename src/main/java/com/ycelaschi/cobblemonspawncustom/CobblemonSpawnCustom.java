package com.ycelaschi.cobblemonspawncustom;

import com.mojang.logging.LogUtils;
import com.ycelaschi.cobblemonspawncustom.config.ConfigLoader;
import com.ycelaschi.cobblemonspawncustom.util.PokemonSpawnListener;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(CobblemonSpawnCustom.MOD_ID)
public class CobblemonSpawnCustom {
    public static final String MOD_ID = "cobblemonspawncustom";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CobblemonSpawnCustom(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
    }

    @net.neoforged.bus.api.SubscribeEvent
    private void registerCommands(RegisterCommandsEvent event) {
        SetAbilityCommand.register(event.getDispatcher());
        // Você pode registrar outros comandos aqui também
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ConfigLoader.loadConfig();
            NeoForge.EVENT_BUS.register(new PokemonSpawnListener());
        });
    }
}
