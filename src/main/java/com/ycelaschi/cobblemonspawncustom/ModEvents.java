package com.ycelaschi.cobblemonspawncustom;

import com.ycelaschi.cobblemonspawncustom.config.ConfigManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class ModEvents {
    @SubscribeEvent
    public void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Unit>() {
            protected Unit prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
                return Unit.INSTANCE;
            }

            protected void apply(Unit unit, ResourceManager resourceManager, ProfilerFiller profiler) {
                ConfigManager.loadConfig(resourceManager);
            }
        });
    }
}

