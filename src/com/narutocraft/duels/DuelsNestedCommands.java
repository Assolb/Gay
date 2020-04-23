package com.narutocraft.duels;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;

public class DuelsNestedCommands {
	
	public static DuelsHandler handler = new DuelsHandler();
	public static HashMap<String, Integer> stadiums = new HashMap<String, Integer>();
	
	private static void setStadiumsCoords() // X1 МЕНЬШЕ X2 БОЛЬШЕ
	{
		
	}

	@Command(aliases = { "call" }, desc = "startin duel", usage = "", min = 1) // описание изменить
	public static void call(CommandContext args, CommandSender sender) 
	{
		if(!(sender instanceof Player)) 
			return;
		
		Player player = (Player) sender;
		
		if(Bukkit.getPlayer(args.getString(0)) == null) 
		{
			player.sendMessage(ChatColor.RED + "[ERROR] Данный игрок не в сети");
		}
	
		if(handler.isNickInPending(args.getString(0)) == true)
		{
			player.sendMessage(ChatColor.RED + "[ERROR] Игрок, которого вы вызвали на дуэль, еще не ответил на другой вызов");
			return;
		}
				
		Location location = player.getLocation();
		
		int stadiumsCount;
		int x = (int) Math.round(location.getX());
		int y = (int) Math.round(location.getY());
		int z = (int) Math.round(location.getZ()); 
		
		String village;
		int stadiumNumber;
		
		if(x > 123 && x < 123 && z < 123 && z > 123) // coords KONOHA
		{
			village = "konoha";
			stadiumsCount = 4;
		}
		else if(x > 123 && x < 123 && z < 123 && z > 123) // coords KIRI	
		{
			village = "kiri";
			stadiumsCount = 4;
		}
		else if(x > 123 && x < 123 && z < 123 && z > 123) // coords KUMO
		{
			village = "kumo";
			stadiumsCount = 4;
		}
		else if(x > 123 && x < 123 && z < 123 && z > 123) // coords SUNA	
		{
			village = "suna";
			stadiumsCount = 4;
		}
		else if(x > 123 && x < 123 && z < 123 && z > 123) // coords IWA
		{
			village = "iwa";
			stadiumsCount = 4;
		}
		else 
		{
			player.sendMessage(ChatColor.RED + "[INFO] Вы не находитесь в деревне с стадионами для поединков");
			return;
		}
		
		for(int i = 0; i < stadiumsCount; i++) // 
		{
			if(stadiums.get(village + "_" + i + "x") < x && stadiums.get("x" + (i + 1)) > x && stadiums.get("z") + i < z && stadiums.get("z") + (i + 1) > z) 
			{
				stadiumNumber = i;
				if(handler.isBusyStadium(village, stadiumNumber))
				{
					player.sendMessage(ChatColor.RED + "[ERROR] На стадионе уже проходит поединок");
					return;
				}
				
				handler.duel(player.getName(), args.getString(0), village, stadiumNumber);
			}
		}
	}
		
	@Command(aliases = { "accept" }, desc = "startin duel", usage = "", min = 1) // описание изменить
	public static void accept(CommandContext args, CommandSender sender) 
	{
		
	}
	
	@Command(aliases = { "reject" }, desc = "startin duel", usage = "", min = 1) // описание изменить
	public static void reject(CommandContext args, CommandSender sender) 
	{
		String nick = ((Player) sender).getName();
		if(handler.isNickInPending(nick))
			handler.removePending(nick);
	}
}
