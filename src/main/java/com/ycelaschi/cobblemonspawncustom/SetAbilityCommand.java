package com.ycelaschi.cobblemonspawncustom;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.PlayerExtensionsKt;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ycelaschi.cobblemonspawncustom.provider.AbilitySuggestionProvider;
import kotlin.Unit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class SetAbilityCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("setAbility")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("slot", IntegerArgumentType.integer(0, 5))
                                .then(Commands.argument("ability", StringArgumentType.word())
                                        .suggests(AbilitySuggestionProvider.ABILITIES)
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            int userSlot;

                                            try {
                                                userSlot = IntegerArgumentType.getInteger(ctx, "slot");
                                            } catch (NumberFormatException e) {
                                                ctx.getSource().sendFailure(Component.literal("❌ Slot deve ser um número entre 1 e 6."));
                                                return 0;
                                            }

                                            int slot = userSlot - 1;

                                            if (slot < 0 || slot > 5) {
                                                ctx.getSource().sendFailure(Component.literal("❌ Slot inválido. Use um número entre 0 e 5."));
                                                return 0;
                                            }

                                            String abilityName = StringArgumentType.getString(ctx, "ability").toLowerCase();
                                            PartyStore party = PlayerExtensionsKt.party(player); // Método atualizado

                                            if (party.get(slot) == null) {
                                                ctx.getSource().sendFailure(Component.literal("❌ Nenhum Pokémon encontrado no slot " + slot + "."));
                                                return 0;
                                            }

                                            Pokemon pokemon = party.get(slot);
                                            AbilityTemplate abilityTemplate = Abilities.INSTANCE.getOrException(abilityName); // Método atualizado

                                            if (abilityTemplate == null) {
                                                ctx.getSource().sendFailure(Component.literal("❌ Habilidade '" + abilityName + "' não encontrada."));
                                                return 0;
                                            }

                                            // Aplica a habilidade e fixa
                                            pokemon.updateAbility(new Ability(abilityTemplate, false, Priority.NORMAL));
                                            pokemon.setSpecies(pokemon.getSpecies()); // 🛠️ força recálculo de atributos

                                            // Atualiza na party
                                            party.set(slot, pokemon);

                                            pokemon.getPersistentData().putBoolean("abilityModified", true);

                                            ServerLevel level = player.serverLevel();
                                            Vec3 pos = player.position().add(1, 0, 0); // Spawna ao lado do player

                                            pokemon.sendOut(level, pos, null, entity -> {
                                                return Unit.INSTANCE;
                                            });

                                            ctx.getSource().sendSuccess(() ->
                                                    Component.literal("✅ Habilidade de ")
                                                            .append(Component.literal(pokemon.getDisplayName().getString()).withStyle(style -> style.withBold(true)))
                                                            .append(" foi definida para ")
                                                            .append(Component.literal(abilityName).withStyle(style -> style.withColor(0x00FFAA).withBold(true)))
                                                            .append("."), true);

                                            return 1;
                                        }))));
    }
}