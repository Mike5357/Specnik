package com.thenodemc.specnik.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.command.PixelCommand;
import com.thenodemc.specnik.Specnik;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class SpecnikCommand extends PixelCommand {

    private static final ArrayList<String> TAB_COMPLETIONS = Lists.newArrayList("reload");

    public SpecnikCommand(CommandDispatcher<CommandSource> dispatcher) {
        super(dispatcher, "specnik",TextFormatting.RED + "/specnik reload",2);
    }

    @Override
    public void execute(CommandSource sender, String[] args) throws CommandException {
        if (args.length == 0) {
            CommandChatHandler.sendChat(sender, TextFormatting.RED + getUsage(sender));
        } else if (args[0].equalsIgnoreCase("reload")) {
            Specnik.instance.loadConfig();
            CommandChatHandler.sendChat(sender, TextFormatting.GREEN + "Config reloaded.");
        } else {
            CommandChatHandler.sendChat(sender, TextFormatting.RED + "Invalid syntax. "+ getUsage(sender));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args, BlockPos pos) {
        return TAB_COMPLETIONS;
    }

}
