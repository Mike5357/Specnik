package com.thenodemc.specnik.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.thenodemc.specnik.Specnik;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;

public class SpecnikCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("specnik")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("subcommand", StringArgumentType.word())
                                .suggests((context, builder) -> getSubcommandSuggestions(builder))
                                .executes(context -> {
                                    String subcommand = StringArgumentType.getString(context, "subcommand");
                                    if (subcommand.equalsIgnoreCase("reload")) {
                                        Specnik.getInstance().loadConfig();
                                        context.getSource().sendSuccess(
                                                () -> Component.literal("Config reloaded.").withStyle(ChatFormatting.GREEN),
                                                false
                                        );
                                        return 1;
                                    } else {
                                        context.getSource().sendFailure(
                                                Component.literal("Invalid subcommand. Try: /specnik reload")
                                        );
                                        return 0;
                                    }
                                })
                        )
        );
    }

    private static CompletableFuture<Suggestions> getSubcommandSuggestions(SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(new String[]{"reload"}, builder);
    }
}