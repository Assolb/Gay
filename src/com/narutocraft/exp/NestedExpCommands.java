package com.narutocraft.exp;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.narutocraft.stats.ConfigPlayer;
import com.narutocraft.util.SenderType;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;

public class NestedExpCommands {

	@Command(aliases = {"add"}, desc = "give exp to player", usage = "<player> <exp> - give <exp> to <player>", min = 2)
	@CommandPermissions(value = {"com.narutocraft.admin"})
	public static void add(CommandContext args, CommandSender sender)
	{
		SenderType type = null;
		
		if(sender instanceof Player) type = SenderType.PLAYER; 
		else if(sender instanceof ConsoleCommandSender) type = SenderType.CONSOLE;
		else type = SenderType.OTHER;
		
		Player p = null;
		if(type == SenderType.PLAYER) p = (Player) sender;
		
		ConfigPlayer config = new ConfigPlayer(args.getString(0));
		if(!config.isExists())
		{
			if(type == SenderType.OTHER)
			{
				return;
			}
			
			else if(type == SenderType.CONSOLE)
			{
				Bukkit.getLogger().severe("Player " + args.getString(0) + " doesn`t exists");
				return;
			}
			
			else 
			{
				p.sendMessage(ChatColor.RED + "[ERROR] Игрок " + ChatColor.GOLD + args.getString(0) + ChatColor.RED + " не существует");
				return;
			}
		}
		
		try 
		{
			if(Bukkit.getPlayer(args.getString(0)) == null)
			{
				if(type == SenderType.CONSOLE)
				{
					Bukkit.getLogger().severe("Player " + args.getString(0) + " is not online");
					return;
				}
				
				if(type == SenderType.PLAYER)
				{
					p.sendMessage(ChatColor.RED + "[ERROR] Игрок " + ChatColor.GOLD + args.getString(0) + ChatColor.RED + " не в сети");
					return;
				}
				return;
			}
			
			try {
				config.addExp(args.getInteger(1));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(type == SenderType.OTHER)
			{
				return;
			}
			
		    if(type == SenderType.CONSOLE)
			{
				Bukkit.getLogger().info("Player " + args.getString(0) + " successfully got exp");
			}
			
			if(type == SenderType.PLAYER)
			{
				p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "Игрок " + ChatColor.GOLD + args.getString(0) + ChatColor.GREEN + " успешно получил очки опыта");
			}
			
			Bukkit.getPlayer(args.getString(0)).sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + (type == SenderType.OTHER ? "NPC " : "Администратор ") + ChatColor.GOLD + (type == SenderType.PLAYER ? p.getName() : "CONSOLE") + "выдал вам " + ChatColor.GOLD + args.getInteger(1) + ChatColor.GREEN + " очков опыта");
			
		} 
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
		}
	}
	
	@Command(aliases = {"info"}, desc = "check info about exp of player", usage = "<player> - check info about exp of <player>", min = 1)
	@CommandPermissions(value = {"com.narutocraft.admin"})
	public static void info(CommandContext args, CommandSender sender)
	{
		SenderType type = null;
		
		if(sender instanceof Player) type = SenderType.PLAYER; 
		else if(sender instanceof ConsoleCommandSender) type = SenderType.CONSOLE;
		else type = SenderType.OTHER;
		
		Player p = null;
		if(type == SenderType.PLAYER) p = (Player) sender;
		
		ConfigPlayer config = new ConfigPlayer(args.getString(0));
		if(!config.isExists())
		{
			if(type == SenderType.OTHER)
			{
				return;
			}
			
			else if(type == SenderType.CONSOLE)
			{
				Bukkit.getLogger().severe("Player " + args.getString(0) + " doesn`t exists");
				return;
			}
			
			else 
			{
				p.sendMessage(ChatColor.RED + "[ERROR] Игрок " + ChatColor.GOLD + args.getString(0) + ChatColor.RED + " не существует");
				return;
			}
		}
		
		if(type == SenderType.CONSOLE)
		{
			Bukkit.getLogger().info(args.getString(0) + ": " + config.getExp() + " EXP");
			return;
		}
		
		if(type == SenderType.PLAYER){
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.RESET + args.getString(0) + ": " + ChatColor.GREEN + "LVL: " + ChatColor.GOLD + config.getLvl() + ChatColor.RESET + ", " + ChatColor.GREEN + "EXP: " + ChatColor.GOLD + config.getExp());
			return;
		}
		
	}
}
