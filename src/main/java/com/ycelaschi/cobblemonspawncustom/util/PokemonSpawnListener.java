package com.ycelaschi.cobblemonspawncustom.util;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.api.events.CobblemonEvents;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class PokemonSpawnListener {

    /**
     * Inscreve-se no evento de spawn de Pokémon com segurança de tipo.
     *
     * @param priority A prioridade do evento (ex: Priority.HIGH)
     * @param handler  O que fazer quando o evento ocorrer
     * @return Uma referência à inscrição, que pode ser usada para cancelá-la
     */
    public static ObservableSubscription<SpawnEvent<?>> onPokemonSpawn(
            Priority priority,
            Function1<PokemonEntity, Unit> handler
    ) {
        return CobblemonEvents.ENTITY_SPAWN.subscribe(priority, event -> {
            if (event.getEntity() instanceof PokemonEntity pokemon) {
                return handler.invoke(pokemon);
            }
            return Unit.INSTANCE;
        });
    }
}
