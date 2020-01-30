package com.narutocraft.anbu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.narutocraft.stats.ConfigPlayer;
import com.narutocraft.village.EnumVillages;
import com.narutocraft.village.Village;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;

public class AnbuCommands {

	@Command(aliases = {"task"}, desc = "anbu tasks main command", usage = "<send/accept/decline/list>", min = 1)
	public static void task(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().severe("This command can be used only by Player");
			return;
		}
		
		String type = args.getString(0);
		
		switch(type)
		{
			case "send":
				send(args, sender);
				break;
			case "accept":
				accept(args, sender);
				break;
			case "decline":
				decline(args, sender);
				break;
			case "list":
				list(sender);
				break;
			case "info":
				info(args, sender);
				break;
			default:
				((Player)sender).sendMessage(ChatColor.RED + "example: /anbu task <send/accept/decline> [...]");
				break;
		}
	}
	
	private static void list(CommandSender sender) 
	{
		Player p = (Player) sender;
		
		ConfigPlayer config = new ConfigPlayer(p.getName());
		Village village = EnumVillages.valueOf(config.getVillage()).village;
		Anbu anbu = new Anbu(village);
		
		if(anbu.getRank(p.getName()) == 0)
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не состоите в отрядах Анбу");
			return;
		}
		
		p.sendMessage(ChatColor.GOLD + "[INFO]" + ChatColor.GREEN + " ID запросов: ");
		
		String tasks = "";
		for(String s : anbu.getCurrentTasks())
		{
			tasks += ChatColor.GOLD + s + ChatColor.RESET + ",";
		}
		
		for(String s : anbu.getTasks())
		{
			tasks += ChatColor.RED + s + ChatColor.RESET + ",";
		}
		tasks = tasks.substring(0, tasks.length() - 1);

		p.sendMessage(tasks);
		
		p.sendMessage(ChatColor.GOLD + "[INFO] Для того чтобы узнать подробную информацию о заданиях используйте: ");
		p.sendMessage(ChatColor.GOLD + "/anbu tasks info <ID задания>");
	}
	
	private static void info(CommandContext args, CommandSender sender)
	{
		Player p = (Player) sender;
		
		ConfigPlayer config = new ConfigPlayer(p.getName());
		Village village = EnumVillages.valueOf(config.getVillage()).village;
		Anbu anbu = new Anbu(village);
		
		if(anbu.getRank(p.getName()) == 0)
		{
			if(!village.getKage().equalsIgnoreCase(p.getName()))
			{
				p.sendMessage(ChatColor.RED + "[ERROR] Вы не состоите в отрядах Анбу");
				return;
			}
		}
		
		if(args.argsLength() < 2)
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Пример использования команды " + ChatColor.GOLD + "/anbu task info <task>");
			return;
		}
		
		int id = args.getInteger(1);
		boolean current = false;
		
		if(!anbu.config.getConfigurationSection("village.anbu.current-missions").contains(Integer.toString(id)))
		{
			if(!anbu.config.getConfigurationSection("village.anbu.missions").contains(Integer.toString(id)))
			{
				p.sendMessage(ChatColor.RED + "[ERROR] Запроса с " + ChatColor.GOLD + "ID " + id + ChatColor.RED + " не существует");
				return;
			}
			current = true;
		}
		
		p.sendMessage(ChatColor.GOLD + "[INFO] Информация о запросе: ");
			p.sendMessage(ChatColor.GOLD + "Тип запроса: " + ChatColor.AQUA + (anbu.config.getString("village.anbu." + (current ? "current-missions." : "missions.") + Integer.toString(id) + ".type").equalsIgnoreCase("kill") ? "Убийство игрока" : "Разведка"));
			p.sendMessage(ChatColor.GOLD + "Цель запроса: " + ChatColor.AQUA + (anbu.config.getString("village.anbu." + (current ? "current-missions." : "missions.") + Integer.toString(id) + ".player")));
			p.sendMessage(ChatColor.GOLD + "Награда за выполнение: " + ChatColor.AQUA + (anbu.config.getInt("village.anbu." + (current ? "current-missions." : "missions.") + Integer.toString(id) + ".reward")));
			p.sendMessage(ChatColor.GOLD + "ID: " + ChatColor.AQUA + id);
	}

	private static void decline(CommandContext args, CommandSender sender) 
	{
		Player p = (Player) sender;
		
		ConfigPlayer config = new ConfigPlayer(p.getName());
		Village village = EnumVillages.valueOf(config.getVillage()).village;
		Anbu anbu = new Anbu(village);
		
		boolean current = false;
		
		if(!anbu.getMembers().contains(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не состоите в отрядах Анбу");
			return;
		}
		
		if(anbu.getRank(p.getName()) < 4)
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вашего ранга в отрядах Анбу недостаточно для этого действия");
			return;
		}
			
		if(args.argsLength() < 2)
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Пример использования команды " + ChatColor.GOLD + "/anbu tasks decline <task>");
			return;
		}
		
		if(!anbu.getTasks().contains(Integer.toString(args.getInteger(1))))
		{
			if(!anbu.getCurrentTasks().contains(Integer.toString(args.getInteger(1))))
			{
				p.sendMessage(ChatColor.RED + "[ERROR] Запроса с " + ChatColor.GOLD + "ID " + args.getInteger(1) + ChatColor.RED + " не существует");
				return;
			}	
			current = true;
		}
		
		anbu.config.set("village.anbu." + (current ? "current-missions." : "missions.") + args.getInteger(1), null);
		try 
		{
			anbu.config.save(anbu.file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		p.sendMessage(ChatColor.GOLD + "[INFO] Вы успешно отказались от выполнения задания Каге");
		
		String kage = village.getKage();
		if(Bukkit.getPlayer(kage) == null)
		{
			ConfigPlayer kageConfig = new ConfigPlayer(kage);
			try 
			{
				kageConfig.addStartMessage("anbu-decline-" + args.getInteger(1), ChatColor.GOLD + "[INFO] " + ChatColor.RED + "Анбу вашей деревни отказались от выполнения задания - " + ChatColor.GOLD + "ID" + args.getInteger(1));
				return;
			} 
			catch (NumberFormatException | IOException e) 
			{
				e.printStackTrace();
			}
		}
		Bukkit.getPlayer(kage).sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.RED + "Анбу вашей деревни отказались от выполнения задания - " + ChatColor.GOLD + "ID" + args.getInteger(1));
	}

	private static void accept(CommandContext args, CommandSender sender) 
	{
		Player p = (Player) sender;
		
		ConfigPlayer config = new ConfigPlayer(p.getName());
		Village village = EnumVillages.valueOf(config.getVillage()).village;
		Anbu anbu = new Anbu(village);
		
		if(!anbu.getMembers().contains(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не состоите в отрядах Анбу");
			return;
		}
		
		if(anbu.getRank(p.getName()) < 4)
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вашего ранга в отрядах Анбу недостаточно для этого действия");
			return;
		}
		
		if(args.argsLength() < 2)
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Пример использования команды " + ChatColor.GOLD + "/anbu tasks accept <task>");
			return;
		}
		
		if(!anbu.getTasks().contains(Integer.toString(args.getInteger(1))))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Запроса с " + ChatColor.GOLD + "ID " + args.getInteger(1) + ChatColor.RED + " не существует");
			return;
		}
		
		anbu.config.createSection("village.anbu.current-missions." + args.getInteger(1));
			anbu.config.set("village.anbu.current-missions." + args.getInteger(1) + ".type", "kill");
			anbu.config.set("village.anbu.current-missions." + args.getInteger(1) + ".player", anbu.config.getString("village.anbu.missions." + args.getInteger(1) + ".player"));
			anbu.config.set("village.anbu.current-missions." + args.getInteger(1) + ".reward", anbu.config.getString("village.anbu.missions." + args.getInteger(1) + ".reward"));
		anbu.config.set("village.anbu.missions." + args.getInteger(1), null);
		try 
		{
			anbu.config.save(anbu.file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "Вы успешно приняли на выполнение запрос - " + ChatColor.GOLD + "ID" + args.getInteger(1));
		
	}

	private static void send(CommandContext args, CommandSender sender)
	{
		Player p = (Player) sender;
		
		ConfigPlayer config = new ConfigPlayer(p.getName());
		Village village = EnumVillages.valueOf(config.getVillage()).village;
		Anbu anbu = new Anbu(village);
		
		if(!village.getKage().equalsIgnoreCase(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Только игроки, занимающие должность Каге, могут отправлять запросы анбу");
			return;
		}
	}
}
