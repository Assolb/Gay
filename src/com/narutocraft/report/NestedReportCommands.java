package com.narutocraft.report;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.stats.ConfigPlayer;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;

public class NestedReportCommands {
	
	@Command(aliases = {"list"}, desc = "shows list of reports", usage = "- shows you list of reports", min = 0)
	public static void list(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			NarutoCraft1.logger.severe(ChatColor.RED + "[ERROR] This command can only be used by a player");
			return;
		}
		Player p = (Player)sender;
		
		if(p.hasPermission("com.narutocraft.moderator"))
		{
			File[] files = NarutoCraft1.get().reportHandler.getFolder().listFiles();
			if(files.length == 0)
			{
				p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "Отличная работа! От игроков не поступило ни одной жалобы!");
				return;
			}
			
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "Список жалоб на игроков:");
		
			FileConfiguration config;
			for(File f : files)
			{
				config = YamlConfiguration.loadConfiguration(f);
				if(config.getBoolean("main.ready"))
				p.sendMessage(ChatColor.AQUA + "ID: " + ChatColor.GOLD + config.getInt("main.id") + ChatColor.AQUA + ", Цель жалобы: " + ChatColor.GOLD + config.getString("main.player") + ChatColor.AQUA + ", Инициатор жалобы " + ChatColor.GOLD + config.getString("main.reporter"));
			}
			p.sendMessage(ChatColor.GOLD + "=====================================================");
		}
		
		File[] files = NarutoCraft1.get().reportHandler.getFolder().listFiles();
		p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "Список ваших жалоб на игроков:");
		if(files.length == 0)
		{
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "Вы не отправили ещё ни одной жалобы на игроков");
			return;
		}
		
		FileConfiguration config;
		for(File f : files)
		{
			config = YamlConfiguration.loadConfiguration(f);
			if(config.getString("main.reporter").equalsIgnoreCase(p.getName()))
			p.sendMessage(ChatColor.AQUA + "ID: " + ChatColor.GOLD + config.getInt("main.id") + ChatColor.AQUA + ", Цель жалобы: " + ChatColor.GOLD + config.getString("main.player") + ChatColor.AQUA + ", Инициатор жалобы " + ChatColor.GOLD + config.getString("main.reporter"));
		}
		return;
	}
	
	@Command(aliases = {"inspect"}, desc = "begins inspect for player", usage = "<player> - begins inspect for player <player>", min = 1)
	@CommandPermissions(value = {"com.narutocraft.moderator"})
	public static void inspect(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			NarutoCraft1.logger.severe(ChatColor.RED + "[ERROR] This command can only be used by a player");
			return;
		}
		Player p = (Player)sender;
		
		String player = args.getString(0);
		
		Player o = Bukkit.getPlayer(player);
		if(o == null)
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Указанный вами игрок не в сети");
			return;
		}
		
		NarutoCraft1.get().reportHandler.inspect.put(p.getName(), o.getName());
		
		Player l = Bukkit.getPlayer(player);
		
		Location loc = l.getLocation();	
		p.teleport(loc);
		
		p.sendMessage(ChatColor.GOLD + "[INFO]" + ChatColor.AQUA + " Вы были успешно перемещены к подозреваемому");	
	}
	
	@Command(aliases = {"create"}, desc = "creates report for future edit and send", usage = "<target> - creates report on player <target> for future edit and send", min = 1)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void create(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			NarutoCraft1.logger.severe(ChatColor.RED + "[ERROR] This command can only be used by a player");
			return;
		}
		Player p = (Player)sender;
		
		String target = args.getString(0);
		
		if(target.equalsIgnoreCase(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Нельзя отправить жалобу на самого себя");
			return;
		}
		
		ConfigPlayer config = new ConfigPlayer(target);
		if(!config.isExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Указанный вами игрок не найден");
			return;
		}
		
		NarutoCraft1.get().reportHandler.createReport(p.getName(), target);
		
		p.sendMessage(ChatColor.GOLD + "[INFO]" + ChatColor.AQUA + " Вы успешно создали жалобу на игрока " + ChatColor.GOLD + target);
		p.sendMessage(ChatColor.GREEN + "Для редактирования жалобы используйте команду " + ChatColor.GOLD + "/report edit <id> <message>");
		p.sendMessage(ChatColor.GREEN + "Для просмотра ваших жалоб используйте " + ChatColor.GOLD + "/report list");
	}
	
	@Command(aliases = {"edit"}, desc = "edit report for future send", usage = "<id> <message> - edit report on player <target> for future send", min = 2)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void edit(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			NarutoCraft1.logger.severe(ChatColor.RED + "[ERROR] This command can only be used by a player");
			return;
		}
		Player p = (Player)sender;
		
		int id = args.getInteger(0);
		
		String string = args.getJoinedStrings(1);
		
		if(!NarutoCraft1.get().reportHandler.checkExists(id))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Указанная вами жалоба не найдена");
			return;
		}
		
		File file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "reports" + File.separator + id + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);	
		
		if(!config.getString("main.reporter").equalsIgnoreCase(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не можете редактировать жалобу, не принадлежащую вам");
			return;
		}
		
		if(config.getBoolean("main.ready"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Жалоба на игрока " + ChatColor.GOLD + config.getString("target") + ChatColor.RED + " уже отправлена");
			return;
		}
		
		NarutoCraft1.get().reportHandler.editReport(id, string);
		p.sendMessage(ChatColor.GOLD + "[INFO]" + ChatColor.GREEN + " Вы успешно редактировали свою жалобу");
		p.sendMessage(ChatColor.AQUA + "Для последующего редактирования используйте команду " + ChatColor.GOLD + "/report edit " + config.getInt("id") + " message");
		p.sendMessage(ChatColor.AQUA + "После окончания редактирования жалобы используйте команду " + ChatColor.GOLD + "/report send " + config.getInt("id"));
	}
	
	@Command(aliases = {"send"}, desc = "send report on player", usage = "<id> - send report <id>", min = 1)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void send(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			NarutoCraft1.logger.severe(ChatColor.RED + "[ERROR] This command can only be used by a player");
			return;
		}
		Player p = (Player)sender;
		
		int id = args.getInteger(0);
		
		if(!NarutoCraft1.get().reportHandler.checkExists(id))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Указанная вами жалоба не найдена");
			return;
		}
		
		File file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "reports" + File.separator + id + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if(!config.getString("main.reporter").equalsIgnoreCase(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не можете отправлять жалобу, не принадлежащую вам");
			return;
		}
		
		if(config.getBoolean("main.ready"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Жалоба на игрока " + ChatColor.GOLD + config.getString("target") + ChatColor.RED + " уже отправлена");
			return;
		}
		
		config.set("main.ready", true);
		p.sendMessage(ChatColor.GOLD + "[INFO] Вы успешно отправили жалобу на рассмотрение модераторам");
		try {
			config.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Player b : Bukkit.getOnlinePlayers())
		{
			if(b.hasPermission("com.narutocraft.moder"))
			{
				b.sendMessage(ChatColor.LIGHT_PURPLE + "[REPORT] Была получена жалоба на игрока " + ChatColor.GOLD + config.getString("target"));
				b.sendMessage(ChatColor.AQUA + "Для прочтения информации о жалобе используйте " + ChatColor.GOLD + "/report info " + config.getInt("id"));
			}
		}
		return;
	}
	
	@Command(aliases = {"remove", "del", "delete"}, desc = "remove report on player", usage = "<id> - remove report <id>", min = 1)
	@CommandPermissions(value = {"com.narutocraft.moder"})
	public static void remove(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			NarutoCraft1.logger.severe(ChatColor.RED + "[ERROR] This command can only be used by a player");
			return;
		}
		Player p = (Player)sender;
		
		int id = args.getInteger(0);
		
		if(!NarutoCraft1.get().reportHandler.checkExists(id))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Указанная вами жалоба не найдена");
			return;
		}
		
		File file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "reports" + File.separator + id + ".yml");
		file.delete();
		
		p.sendMessage(ChatColor.GOLD + "[INFO] Указанная вами жалоба успешно удалена");
	}
}
