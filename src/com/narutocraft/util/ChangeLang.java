package com.narutocraft.util;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.narutocraft.stats.ConfigPlayer;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;

public class ChangeLang {

	@Command(aliases = {"changelang", "cl"}, desc = "Changes language from rus to eng and backward", usage = "- change Changes language from rus to eng and backward")
	public static void changeLang(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		Player p = (Player)sender;
		
		ConfigPlayer config = new ConfigPlayer(p.getName());
		config.changeLang();
		
		p.sendMessage(Lang.getLocal("general", "change-successful", p.getName()));		
	}
	
}
