package com.narutocraft.teams;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;

public class NestedTeamCommands {

	@Command(aliases = {"create"}, desc = "creating a team", usage = "[jonin] <genin1> <genin2> <genin3> - create team", min = 4)
	public static void create(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			String[] string = new String[args.argsLength()];
			for(int i = 0; i < (args.argsLength() > 4 ? 4 : args.argsLength()); i++)
			{
				string[i] = args.getString(i);
				if(TeamsHandler.getPlayerTeam(string[i]) != null)
				{
					Bukkit.getLogger().severe("Error! Player " + string[i] + "are at the other team");
					return;
				}
				
			}
			
			try 
			{
				Team.createTeam(string);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			return;
		}
		
		Player p = (Player)sender;
		
		String[] string = new String[args.argsLength()];
		for(int i = 0; i < (args.argsLength() > 4 ? 4 : args.argsLength()); i++)
		{
			string[i] = args.getString(i);
			if(TeamsHandler.getPlayerTeam(string[i]) != null)
			{
				p.sendMessage(ChatColor.GOLD + "[TeamSystem] " + ChatColor.RED + " Игрок " + ChatColor.GOLD + string[i] + ChatColor.RED + " состоит в другой команде!");
				return;
			}
			
		}
		
		if(!string[0].equalsIgnoreCase(p.getName()))
		{
			p.sendMessage(ChatColor.GOLD + "[TeamSystem] " + ChatColor.RED + " Для формирования своей команды вы должны иметь ранг - " + ChatColor.GOLD + "Джоунин");
			return;
		}
		
		try 
		{
			int id = Team.createTeam(string);
			if(id > 0)
			{
				p.sendMessage(ChatColor.GOLD + "[TeamSystem] " + ChatColor.GREEN + "Команда номер " + ChatColor.GOLD + id + ChatColor.GREEN + " успешно сформирована!");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
