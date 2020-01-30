package com.narutocraft.society;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.narutocraft.stats.ConfigPlayer;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;

public class NestedSocietyCommands {

	@Command(aliases = {"create"}, desc = "creates a society", usage = "<society> - creates society with name <society>", min = 1)
	@CommandPermissions(value = {"com.narutocraft.society.create"})
	public static void create(CommandContext args, CommandSender sender)
	{
		Player p = null;
		if(sender instanceof Player)
		{
			p = (Player)sender;
		}
		
		String e = args.getString(0);
		
		if(new Society(e).checkExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Организация с названием " + ChatColor.GOLD + e + ChatColor.RED + " уже существует");
			return;
		}
		try
		{
			new Society(e).create();
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "Организация " + ChatColor.GOLD + e + ChatColor.GREEN + " успешно создана");
		}
		catch(IOException s)
		{
			s.printStackTrace();
		}				
	}
	
	@Command(aliases = {"remove", "clear", "delete", "del"}, desc = "delete a society", usage = "<society> - deletes society with name <society>", min = 1)
	@CommandPermissions(value = {"com.narutocraft.society.delete"})
	public static void delete(CommandContext args, CommandSender sender)
	{
		Player p = null;
		if(sender instanceof Player)
		{
			p = (Player)sender;
		}
		
		String e = args.getString(0);
		
		if(!new Society(e).checkExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Организация с названием " + ChatColor.GOLD + e + ChatColor.RED + " не существует");
			return;
		}
		
		Society soc = new Society(e);
		
		p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "Организация " + ChatColor.GOLD + e + ChatColor.GREEN + " была успешно расформирована");
		List<String> player = soc.getMembers();
		List<String> online = soc.getOnlineMembers();
		Player ps = null;
		ConfigPlayer config = null;
		for(String s : player)
		{
			if(online.contains(s))
			{
				ps = Bukkit.getPlayer(s);
				ps.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "Ваша организация " + ChatColor.GOLD + e + ChatColor.GREEN + " была расформирована администратором " + ChatColor.GOLD + p == null ? "CONSOLE" : p.getName());
			}
			else 
			{
				config = new ConfigPlayer(s);
				try {
					config.addStartMessage("deleteSociety", ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "Ваша организация " + ChatColor.GOLD + e + ChatColor.GREEN + " была расформирована администратором " + ChatColor.GOLD + p == null ? "CONSOLE" : p.getName());
				} catch (IOException z) {
					z.printStackTrace();
				}
			}
		}
		soc.remove();
	}
	@Command(aliases = {"invite"}, desc = "invite player to the society", usage = "<player> - invites player to your society", min = 1)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void invite(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		
		String e = args.getString(0);
		
		Player p = (Player) sender;
		
		Society s = new Society(Society.getSociety(p.getName()));
		
		if(!s.checkExists())	
		{			
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не состоите в какой-либо организации");
			return;
		}
			
		if(s.checkPlayerInInvites(e))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Игрок " + ChatColor.GOLD + e + ChatColor.RED + " уже был приглашен в организацию");
			return;
		}
								
		if(s.checkPlayer(e))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Игрок " + ChatColor.GOLD + e + ChatColor.RED + " уже состоит в организации");
			return;
		}
		
		if(s.getRolePlayer(p.getName()).equalsIgnoreCase("member"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] У вас недостаточно прав для приглашения игроков в организацию");
			return;
		}
		
		if(!new ConfigPlayer(e).isExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Игрок " + ChatColor.GOLD + e + ChatColor.RED + " никогда не заходил на сервер");
			return;
		}
		
		try
		{
			s.invitePlayer(e, p.getName());
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "Приглашение в организацию было успешно отправлено");
			
			ConfigPlayer zcfg = new ConfigPlayer(e);
			
			Player z = Bukkit.getPlayer(e);
			if(z == null)
			{
				zcfg.addStartMessage("invitetosociety", ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "Вы были приглашены в организацию " + ChatColor.GOLD + s.name + ChatColor.GREEN + " игроком " + ChatColor.GOLD + e);
				zcfg.addStartMessage("invitetosociety1", ChatColor.GREEN + "Для принятия предложения используйте команду " + ChatColor.GOLD + "/society accept " + s.name);
				zcfg.addStartMessage("invitetosociety2", ChatColor.RED + "Для отказа от предложения используйте команду " + ChatColor.GOLD + "/society decline " + s.name);
				zcfg.addStartMessage("invitetosociety3", ChatColor.GREEN + "Узнать информацию об организации - " + ChatColor.GOLD + "/society info " + s.name);
				zcfg.addStartMessage("invitetosociety4", ChatColor.AQUA + "Посмотреть список всех предложений - " + ChatColor.GOLD + "/society invites");
				return;
			}
			z.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + " вы были приглашены в организацию " + ChatColor.GOLD + s.name + ChatColor.GREEN + " игроком " + ChatColor.GOLD + e);
			z.sendMessage(ChatColor.GREEN + "Для принятия предложения используйте команду " + ChatColor.GOLD + "/society accept " + s.name);
			z.sendMessage(ChatColor.RED + "Для отказа предложения используйте команду " + ChatColor.GOLD + "/society decline " + s.name);
			z.sendMessage(ChatColor.GREEN + "Узнать информацию об организации - " + ChatColor.GOLD + "/society info " + s.name);
			z.sendMessage(ChatColor.AQUA + "Посмотреть список всех предложений - " + ChatColor.GOLD + "/society invites");
		
			s.log(p.getName() + " invite player " + e);
			
		}
		catch(IOException z){}
	}
	
	@Command(aliases = {"kick"}, desc = "kick player from the society", usage = "<player> - kicks player from your society", min = 1)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void kick(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		Player p = (Player)sender;
		
		String e = args.getString(0);
		
		Society s = new Society(Society.getSociety(p.getName()));
		
		if(!s.checkExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не состоите в какой-либо организации");
			return;
		}
		
		if(s.getRolePlayer(p.getName()).equalsIgnoreCase("member"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] У вас недостаточно прав для использования этой печати");
			return;
		}
		
		if(!s.checkPlayer(e))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Игрок " + ChatColor.GOLD + e + ChatColor.RED + " не состоит в организации");
			return;
		}
		
		if(!s.getRolePlayer(e).equalsIgnoreCase("member") && !s.getRolePlayer(p.getName()).equalsIgnoreCase("owner"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] У вас недостаточно прав для использования этой печати на игроке " + ChatColor.GOLD + e);
			return;
		}
		
		try
		{
			s.kickPlayer(e);
			
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "Игрок " + ChatColor.GOLD + e + ChatColor.GREEN + " успешно исключён из организации");
			
			Player b = null;
			for(String x : s.getOnlineMembers())
			{
				b = Bukkit.getPlayer(x);
				b.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.RED + "Игрок " + ChatColor.GOLD + e + ChatColor.RED + " был исключён из организации " + (s.getRolePlayer(p.getName()).equalsIgnoreCase("owner") ? "главой организации" : "офицером " + ChatColor.GOLD + p.getName()));
			}
			
			b = Bukkit.getPlayer(e);
			if(b == null)
			{
				ConfigPlayer config = new ConfigPlayer(e);
				config.addStartMessage("kickplayerself", ChatColor.GOLD + "[INFO] " + ChatColor.RED + "вы были исключены из организации " + (s.getRolePlayer(p.getName()).equalsIgnoreCase("owner") ? "главой организации" : "офицером " + ChatColor.GOLD + p.getName()));
				config.addStartMessage("kickplayerself1", ChatColor.AQUA + "По всем вопросам, связанным с киком без причины, обращаться к " + ChatColor.GOLD + "https://vk.com/shuhingei");
			}
			b.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.RED + "вы были исключены из организации " + (s.getRolePlayer(p.getName()).equalsIgnoreCase("owner") ? "главой организации" : "офицером " + ChatColor.GOLD + p.getName()));
			b.sendMessage(ChatColor.AQUA + "По всем вопросам, связанным с киком без причины, обращаться к " + ChatColor.GOLD + "https://vk.com/shuhingei");
			
			s.log(p.getName() + " kicked player " + e);
		}
		catch(IOException z){}
	}
	
	@Command(aliases = {"setowner"}, desc = "Makes the player a owner of society", usage = "<society> <player> - makes the player <player> a owner of society <society>", min = 2)
	@CommandPermissions(value = {"com.narutocraft.society.setowner"})
	public static void setOwner(CommandContext args, CommandSender sender)
	{
		Player p = null;
		if(sender instanceof Player)
		{
			p = (Player)sender;
		}
		
		String e = args.getString(0);
		String player = args.getString(1);
		
		Society s = new Society(e);
		
		if(!s.checkExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Организации " + ChatColor.GOLD + player + ChatColor.RED + " не существует");
			return;
		}
		
		if(!Society.getSociety(player).equalsIgnoreCase("none"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Игрок " + ChatColor.GOLD + player + ChatColor.RED + " уже состоит в организации");
			return;
		}
		
		if(!new ConfigPlayer(player).isExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Указанный вами игрок не существует");
			return;
		}
		
		try
		{ 
			Society.setOwner(e, player, p == null ? "CONSOLE" : p.getName()); 
			
			Player l = null;
			
			for(String k : s.getOnlineMembers())
			{
				l = Bukkit.getPlayer(k);
				l.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "Главой вашей организации был назначен игрок " + ChatColor.GOLD + player + ChatColor.GREEN + " администратом " + ChatColor.GOLD + p.getName());
			}
			
			l = Bukkit.getPlayer(player);
			if(l == null)
			{
				new ConfigPlayer(player).addStartMessage("setowner1", ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "Вы были назначены главой организации " + ChatColor.GOLD + s.name + ChatColor.GREEN + " администратором " + ChatColor.GOLD + p.getName());
			}
			else l.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "Вы были назначены главой организации " + ChatColor.GOLD + s.name + ChatColor.GREEN + " администратором " + ChatColor.GOLD + p.getName());
			
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "Игрок " + ChatColor.GOLD + player + ChatColor.AQUA + " был назначен главой организации " + ChatColor.GOLD + e);
			
			s.log(p.getName() + " set player " + e + " to owner of society");
		} 
		
		catch(IOException z){}			
	}
	
	@Command(aliases = {"list"}, desc = "List of players in the society", usage = "[society] - shows list of players in the society [society]", min = 0, max = 1)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void list(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		Player p = (Player)sender;
		
		Society s = new Society(Society.getSociety(p.getName()));
		if(args.argsLength() == 1)
		{
			if(p.hasPermission("com.narutocraft.society.others"))
			s = new Society(args.getString(0));
		}
		
		if(!s.checkExists())
		{
			if(args.argsLength() < 1 || !p.hasPermission("com.narutocraft.society.others"))
			{
				p.sendMessage(ChatColor.RED + "[ERROR] Вы не состоите в какой-либо организации");
				return;
			}
			p.sendMessage(ChatColor.RED + "[ERROR] Организации " + ChatColor.GOLD + s.name + ChatColor.RED + " не существует");
			return;
		}
		
		List<String> list = s.getMembers();
		int i = 0;
		String message = "";

		for(String j : list)
		{
			if(message.length() + j.length() + 2 > 100)
			{
				p.sendMessage(message);
				message = "";
			}
			message = message + (s.getRolePlayer(j).equalsIgnoreCase("owner") ? ChatColor.DARK_RED : s.getRolePlayer(j).equalsIgnoreCase("officer") ? ChatColor.AQUA : ChatColor.GREEN) + j;
			if(i < j.length() - 1) message = message + ChatColor.RESET + ", ";
			i++;
		}
		p.sendMessage(message);	
	}
	
	@Command(aliases = {"promote"}, desc = "Promotes rank of player to officer", usage = "<player> - promotes rank of player <player> to officer", min = 1)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void promote(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		Player p = (Player)sender;
		
		String e = args.getString(0);
		
		String sName = Society.getSociety(p.getName());
		
		Society s = new Society(sName);
		
		if(!s.checkExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не состоите в какой-либо организации");
			return;
		}
		
		if(!s.getOwner(sName).equalsIgnoreCase(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] У вас недостаточно прав для повышения игрока");
			return;
		}
		
		if(p.getName().equalsIgnoreCase(e))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не можете повысить самого себя");
			return;
		}
		
		if(!s.checkPlayer(e))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Указанный вами игрок не состоит в вашей организации");
			return;
		}
		
		if(!s.getRolePlayer(e).equalsIgnoreCase("member"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Указанный вами игрок имеет максимальный ранг");
			return;
		}
		
		try 
		{
			s.promotePlayer(e);
			
			p.sendMessage(ChatColor.GOLD + "[INFO]" + ChatColor.GREEN + " Игрок " + ChatColor.GOLD + e + ChatColor.GREEN + " был успешно повышен до ранга - " + ChatColor.GOLD + "офицер");
			
			Player l = Bukkit.getPlayer(e);
			if(l == null)
			{
				ConfigPlayer config = new ConfigPlayer(e);
				config.addStartMessage("promoteself1", ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "Вы были повышены до ранга - " + ChatColor.GOLD + "офицер " + ChatColor.AQUA + "главой организации");
				return;
			}
			
			l.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "Вы были повышены до ранга - " + ChatColor.GOLD + "офицер " + ChatColor.AQUA + "главой организации");			
		
			s.log(e + " was promoted to officer by " + p.getName());
		}
		
		catch(IOException z){}
	}
	
	@Command(aliases = {"demote"}, desc = "Demotes rank of player to officer", usage = "<player> - demotes rank of player <player> to member", min = 1)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void demote(CommandContext args, CommandSender sender)
	{

		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		Player p = (Player)sender;
		
		String e = args.getString(0);
		
		String sName = Society.getSociety(p.getName());
		
		Society s = new Society(sName);
		
		if(!s.checkExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не состоите в какой-либо организации");
			return;
		}
		
		if(!s.getOwner(sName).equalsIgnoreCase(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] У вас недостаточно прав для понижения игрока");
			return;
		}
		
		if(p.getName().equalsIgnoreCase(e))
		{
			p.sendMessage( ChatColor.RED + "[ERROR] Вы не можете понизить самого себя");
			return;
		}
		
		if(!s.checkPlayer(e))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Указанный вами игрок не состоит в вашей организации");
			return;
		}
		
		if(s.getRolePlayer(e).equalsIgnoreCase("member")) 
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Указанный вами игрок имеет минимальный ранг");
			return;
		}
		
		try 
		{
			s.demotePlayer(e);
			
			p.sendMessage(ChatColor.GOLD + "[INFO]" + ChatColor.GREEN + " Игрок " + ChatColor.GOLD + e + ChatColor.GREEN + " был успешно понижен до ранга - " + ChatColor.GOLD + "участник");
			
			Player l = Bukkit.getPlayer(e);
			if(l == null)
			{
				ConfigPlayer config = new ConfigPlayer(e);
				config.addStartMessage("promoteself1", ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "Вы были понижены до ранга - " + ChatColor.GOLD + "участник " + ChatColor.AQUA + "главой организации");
				return;
			}
			
			l.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.RED + "Вы были понижены до ранга - " + ChatColor.GOLD + "участник " + ChatColor.RED + "главой организации");			
		
			s.log(p.getName() + " demoted player " + e);
		}
		catch(IOException z){}
    }
	
	@Command(aliases = {"accept"}, desc = "Accept invite to society", usage = "<society> - accept invites to society <society>", min = 0)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void accept(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		Player p = (Player)sender;
		
		Society s = new Society(Society.getSociety(p.getName()));
		
		if(args.argsLength() < 1)
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Пример использования команды - " + ChatColor.GOLD + "/society accept <society>");
			p.sendMessage(ChatColor.AQUA + "[INFO] Для того, чтобы посмотреть список приглашений используйте команду - " + ChatColor.GOLD + "/society invites ");
			return;
		}
		
		if(s.checkExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы уже состоите в организации, используйте " + ChatColor.GOLD + "/society leave " + ChatColor.RED + "чтобы покинуть организацию");
			return;
		}
		
		s = new Society(args.getString(0));
		
		if(!s.checkExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Организация " + ChatColor.GOLD + args.getString(0) + ChatColor.RED + " не существует");
			return;
		}
		
		if(!s.checkPlayerInInvites(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не были приглашены в эту организацию");
			return;
		}
		
		try {
			s.accept(p.getName());
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "Вы успешно вступили в организацию " + ChatColor.GOLD + s.name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		s.log(p.getName() + " joined to society");
	}
	
	@Command(aliases = {"decline"}, desc = "decline invite to society", usage = "<society> - decline invites to society <society>", min = 0)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void decline(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		Player p = (Player)sender;
		
		Society s = new Society(Society.getSociety(p.getName()));
		
		if(args.argsLength() < 1)
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Пример использования команды - " + ChatColor.GOLD + "/society decline <society>");
			p.sendMessage(ChatColor.AQUA + "[INFO] Для того, чтобы посмотреть список приглашений используйте команду - " + ChatColor.GOLD + "/society invites ");
			return;
		}
			
		s = new Society(args.getString(0));
		
		if(!s.checkPlayerInInvites(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не были приглашены в эту организацию");
			return;
		}
		
		try {
			s.decline(p.getName());
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "Вы успешно отклонили предложение вступления в организацию " + ChatColor.GOLD + s.name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		s.log(p.getName() + " decline invite to society");
	}
	
	@Command(aliases = {"info"}, desc = "show the info of a society", usage = "[society] - shows the info of the society [society]", min = 0)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void info(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		Player p = (Player)sender;
		
		Society s = null;
		if(args.argsLength() == 0)
		{
			s = new Society(Society.getSociety(p.getName()));
			if(!s.checkExists())
			{
				p.sendMessage(ChatColor.RED + "[ERROR] Вы не состоите в какой-либо организации");
				return;
			}
		}
		else
		{
			s = new Society(args.getString(0));
			if(!s.checkExists())
			{
				p.sendMessage(ChatColor.RED  + "[ERROR] Указанная вами организация не существует");
				return;
			}
		}
		
		p.sendMessage(ChatColor.GOLD + "[INFO] Информация о вашей организации: ");
		p.sendMessage(ChatColor.AQUA + "Никнейм главы: " + ChatColor.GOLD + s.getOwner());
		p.sendMessage(ChatColor.AQUA + "Уровень организации: " + ChatColor.GOLD + s.getLvl());
		p.sendMessage(ChatColor.AQUA + "Казна организации: " + ChatColor.GOLD + s.getMoney());
		p.sendMessage(ChatColor.AQUA + "Колличество участников: " + ChatColor.GOLD + s.getMembers().size());
	}
	
	@Command(aliases = {"invites"}, desc = "show invites to societys", usage = "- shows invites to societys", min = 0)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void invites(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		Player p = (Player)sender;
		
		List<String> list = Society.getInvitedSocietys(p.getName());
		
		if(list.size() == 0)
		{
			p.sendMessage(ChatColor.GOLD + "[INFO] У вас нет предложений для вступления в организации");
			return;
		}
		
		p.sendMessage(ChatColor.GOLD + "[INFO] Список пригласивших вас в свои ряды организаций:");
		
		for(String s : list)
		{
			p.sendMessage(ChatColor.AQUA + s);
		}
		
		p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "узнать информацию об интересующей вас организации - " + ChatColor.GOLD + "/society info <society>");
	}
	
	@Command(aliases = {"leave"}, desc = "leave from a society", usage = "- leave from a society", min = 0)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void leave(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		Player p = (Player)sender;
		
		Society s = new Society(Society.getSociety(p.getName()));
		
		if(!s.checkExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы ни состоите в какой-либо организации");
			return;
		}
		
		if(s.getOwner().equalsIgnoreCase(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] Вы не можете покинуть организацию будучи главой");
			p.sendMessage(ChatColor.GOLD + "Для получения дополнительной помощи обратитесь к одному из администраторов сервера");
			return;
		}
		
		try {
			s.leave(p.getName());
			
			p.sendMessage(ChatColor.GOLD + "[INFO] Вы успешно покинули организацию");
			
			s.log(p.getName() + " leave from society");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Command(aliases = {"help"}, desc = "shows commands of plugin", usage = "- shows commands of plugin", min = 0)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void help(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		Player p = (Player)sender;
		
		p.sendMessage(ChatColor.GOLD + "[INFO] Основные команды: ");
		p.sendMessage( "");
		p.sendMessage(ChatColor.GOLD + "/society create <society> - " + ChatColor.AQUA + "Создать организацию");
		p.sendMessage(ChatColor.GOLD + "/society remove <society> - " + ChatColor.AQUA + "Расформировать организацию");
		p.sendMessage(ChatColor.GOLD + "/society invite <player> - " + ChatColor.AQUA + "Пригласить игрока в организацию");
		p.sendMessage(ChatColor.GOLD + "/society kick <player> - " + ChatColor.AQUA + "Исключить игрока из организации");
		p.sendMessage(ChatColor.GOLD + "/society create <society> - " + ChatColor.AQUA + "Создать организацию");
		p.sendMessage(ChatColor.GOLD + "/society list [society] - " + ChatColor.AQUA + "Список игроков организации " + ChatColor.GOLD + "[society]");
		p.sendMessage(ChatColor.GOLD + "/society setowner <society> <player> - " + ChatColor.AQUA + "Назначить игрока главой организации");
		p.sendMessage(ChatColor.GOLD + "/society promote <player> - " + ChatColor.AQUA + "Повысить игрока до ранга " + ChatColor.GOLD + "офицер");
		p.sendMessage(ChatColor.GOLD + "/society demote <player> - " + ChatColor.AQUA + "Понизить игрока до ранга " + ChatColor.GOLD + "участник");
		p.sendMessage(ChatColor.GOLD + "/society leave - " + ChatColor.AQUA + "Покинуть организацию");
		p.sendMessage(ChatColor.GOLD + "/society accept <society> - " + ChatColor.AQUA + "Принять приглашение для вступления в организацию");
		p.sendMessage(ChatColor.GOLD + "/society decline <society> - " + ChatColor.AQUA + "Отклонить приглашение для вступления в организацию");
		p.sendMessage(ChatColor.GOLD + "/society invites - " + ChatColor.AQUA + "Список приглашений для вступления в организации");
		p.sendMessage(ChatColor.GOLD + "/society info [society] - " + ChatColor.AQUA + "Узнать информацию об организации " + ChatColor.GOLD + "[society]");
		p.sendMessage(ChatColor.GOLD + "/sc [message...] - " + ChatColor.AQUA + "Написать сообщение в чат организации");
	}
}
