package com.ycelaschi.cobblemonspawncustom.provider;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;

public class AbilitySuggestionProvider {
    public static final SuggestionProvider<CommandSourceStack> ABILITIES = (context, builder) -> {
        for (AbilityTemplate ability : Abilities.INSTANCE.all()) {
            builder.suggest(ability.getName().toLowerCase());
        }
        return builder.buildFuture();
    };
}
