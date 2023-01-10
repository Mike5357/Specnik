package com.thenodemc.specnik;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.command.PixelCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class SpecnikCommand extends PixelCommand {

    TranslationTextComponent msg;
    public SpecnikCommand(CommandDispatcher<CommandSource> dispatcher) {
        super(dispatcher);
    }

    public String getName() {
        return "specnik";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList("specnik");
    }

    @Override
    public String getUsage(CommandSource icommandsender) {
        return TextFormatting.RED + "/specnik reload";
    }

    public SpecnikCommand(CommandDispatcher<CommandSource> dispatcher, String name, String usage, int permissionLevel) {
        super(dispatcher, name, usage, permissionLevel);
    }

    @Override
    public void execute(CommandSource sender, String[] args) throws CommandException, CommandSyntaxException {
        MinecraftServer server = sender.getServer();
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
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args, BlockPos pos) throws CommandSyntaxException {
        ArrayList<String> s = new ArrayList<>();
        s.add("reload");
        return s;
    }

}
