package com.narutocraft.exams;

import org.bukkit.command.CommandSender;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.NestedCommand;

public class ExamsCommands {

	@Command(aliases = {"exams"}, desc = "exams main command")
	@NestedCommand(value = {ExamsNestedCommands.class})
	public static void exams(CommandContext args, CommandSender sender){}
}
