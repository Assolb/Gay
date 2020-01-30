package com.narutocraft.jobs;

import org.bukkit.command.CommandSender;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.NestedCommand;

public class JobsCommands {
	
	@Command(aliases = {"jobs"}, desc = "jobs main command")
	@NestedCommand(value = {Lawyers.class})
	public static void jobs(CommandContext args, CommandSender sender){}
}
