package com.narutocraft.donate;

import java.io.IOException;

import org.bukkit.command.CommandSender;

import com.narutocraft.stats.ConfigPlayer;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;

public class NestedDonateCommand {

	@Command(aliases = {"add"}, desc = "give donate player", usage = "<player> <donate> - give donate player <player>", min = 2)
	@CommandPermissions(value = {"com.narutocraft.admin"})
	public static void add(CommandContext args, CommandSender sender)
	{
		String player = args.getString(0);
		ConfigPlayer config = new ConfigPlayer(player);
		
		try 
		{
			config.addDonate(args.getString(1));
		} 
		catch (IOException e) 
		{	
			e.printStackTrace();
		}
	}
}
