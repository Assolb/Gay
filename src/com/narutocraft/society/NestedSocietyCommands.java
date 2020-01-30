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
			p.sendMessage(ChatColor.RED + "[ERROR] ����������� � ��������� " + ChatColor.GOLD + e + ChatColor.RED + " ��� ����������");
			return;
		}
		try
		{
			new Society(e).create();
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "����������� " + ChatColor.GOLD + e + ChatColor.GREEN + " ������� �������");
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
			p.sendMessage(ChatColor.RED + "[ERROR] ����������� � ��������� " + ChatColor.GOLD + e + ChatColor.RED + " �� ����������");
			return;
		}
		
		Society soc = new Society(e);
		
		p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "����������� " + ChatColor.GOLD + e + ChatColor.GREEN + " ���� ������� ��������������");
		List<String> player = soc.getMembers();
		List<String> online = soc.getOnlineMembers();
		Player ps = null;
		ConfigPlayer config = null;
		for(String s : player)
		{
			if(online.contains(s))
			{
				ps = Bukkit.getPlayer(s);
				ps.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "���� ����������� " + ChatColor.GOLD + e + ChatColor.GREEN + " ���� �������������� ��������������� " + ChatColor.GOLD + p == null ? "CONSOLE" : p.getName());
			}
			else 
			{
				config = new ConfigPlayer(s);
				try {
					config.addStartMessage("deleteSociety", ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "���� ����������� " + ChatColor.GOLD + e + ChatColor.GREEN + " ���� �������������� ��������������� " + ChatColor.GOLD + p == null ? "CONSOLE" : p.getName());
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
			p.sendMessage(ChatColor.RED + "[ERROR] �� �� �������� � �����-���� �����������");
			return;
		}
			
		if(s.checkPlayerInInvites(e))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] ����� " + ChatColor.GOLD + e + ChatColor.RED + " ��� ��� ��������� � �����������");
			return;
		}
								
		if(s.checkPlayer(e))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] ����� " + ChatColor.GOLD + e + ChatColor.RED + " ��� ������� � �����������");
			return;
		}
		
		if(s.getRolePlayer(p.getName()).equalsIgnoreCase("member"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] � ��� ������������ ���� ��� ����������� ������� � �����������");
			return;
		}
		
		if(!new ConfigPlayer(e).isExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] ����� " + ChatColor.GOLD + e + ChatColor.RED + " ������� �� ������� �� ������");
			return;
		}
		
		try
		{
			s.invitePlayer(e, p.getName());
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "����������� � ����������� ���� ������� ����������");
			
			ConfigPlayer zcfg = new ConfigPlayer(e);
			
			Player z = Bukkit.getPlayer(e);
			if(z == null)
			{
				zcfg.addStartMessage("invitetosociety", ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "�� ���� ���������� � ����������� " + ChatColor.GOLD + s.name + ChatColor.GREEN + " ������� " + ChatColor.GOLD + e);
				zcfg.addStartMessage("invitetosociety1", ChatColor.GREEN + "��� �������� ����������� ����������� ������� " + ChatColor.GOLD + "/society accept " + s.name);
				zcfg.addStartMessage("invitetosociety2", ChatColor.RED + "��� ������ �� ����������� ����������� ������� " + ChatColor.GOLD + "/society decline " + s.name);
				zcfg.addStartMessage("invitetosociety3", ChatColor.GREEN + "������ ���������� �� ����������� - " + ChatColor.GOLD + "/society info " + s.name);
				zcfg.addStartMessage("invitetosociety4", ChatColor.AQUA + "���������� ������ ���� ����������� - " + ChatColor.GOLD + "/society invites");
				return;
			}
			z.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + " �� ���� ���������� � ����������� " + ChatColor.GOLD + s.name + ChatColor.GREEN + " ������� " + ChatColor.GOLD + e);
			z.sendMessage(ChatColor.GREEN + "��� �������� ����������� ����������� ������� " + ChatColor.GOLD + "/society accept " + s.name);
			z.sendMessage(ChatColor.RED + "��� ������ ����������� ����������� ������� " + ChatColor.GOLD + "/society decline " + s.name);
			z.sendMessage(ChatColor.GREEN + "������ ���������� �� ����������� - " + ChatColor.GOLD + "/society info " + s.name);
			z.sendMessage(ChatColor.AQUA + "���������� ������ ���� ����������� - " + ChatColor.GOLD + "/society invites");
		
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
			p.sendMessage(ChatColor.RED + "[ERROR] �� �� �������� � �����-���� �����������");
			return;
		}
		
		if(s.getRolePlayer(p.getName()).equalsIgnoreCase("member"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] � ��� ������������ ���� ��� ������������� ���� ������");
			return;
		}
		
		if(!s.checkPlayer(e))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] ����� " + ChatColor.GOLD + e + ChatColor.RED + " �� ������� � �����������");
			return;
		}
		
		if(!s.getRolePlayer(e).equalsIgnoreCase("member") && !s.getRolePlayer(p.getName()).equalsIgnoreCase("owner"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] � ��� ������������ ���� ��� ������������� ���� ������ �� ������ " + ChatColor.GOLD + e);
			return;
		}
		
		try
		{
			s.kickPlayer(e);
			
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "����� " + ChatColor.GOLD + e + ChatColor.GREEN + " ������� �������� �� �����������");
			
			Player b = null;
			for(String x : s.getOnlineMembers())
			{
				b = Bukkit.getPlayer(x);
				b.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.RED + "����� " + ChatColor.GOLD + e + ChatColor.RED + " ��� �������� �� ����������� " + (s.getRolePlayer(p.getName()).equalsIgnoreCase("owner") ? "������ �����������" : "�������� " + ChatColor.GOLD + p.getName()));
			}
			
			b = Bukkit.getPlayer(e);
			if(b == null)
			{
				ConfigPlayer config = new ConfigPlayer(e);
				config.addStartMessage("kickplayerself", ChatColor.GOLD + "[INFO] " + ChatColor.RED + "�� ���� ��������� �� ����������� " + (s.getRolePlayer(p.getName()).equalsIgnoreCase("owner") ? "������ �����������" : "�������� " + ChatColor.GOLD + p.getName()));
				config.addStartMessage("kickplayerself1", ChatColor.AQUA + "�� ���� ��������, ��������� � ����� ��� �������, ���������� � " + ChatColor.GOLD + "https://vk.com/shuhingei");
			}
			b.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.RED + "�� ���� ��������� �� ����������� " + (s.getRolePlayer(p.getName()).equalsIgnoreCase("owner") ? "������ �����������" : "�������� " + ChatColor.GOLD + p.getName()));
			b.sendMessage(ChatColor.AQUA + "�� ���� ��������, ��������� � ����� ��� �������, ���������� � " + ChatColor.GOLD + "https://vk.com/shuhingei");
			
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
			p.sendMessage(ChatColor.RED + "[ERROR] ����������� " + ChatColor.GOLD + player + ChatColor.RED + " �� ����������");
			return;
		}
		
		if(!Society.getSociety(player).equalsIgnoreCase("none"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] ����� " + ChatColor.GOLD + player + ChatColor.RED + " ��� ������� � �����������");
			return;
		}
		
		if(!new ConfigPlayer(player).isExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] ��������� ���� ����� �� ����������");
			return;
		}
		
		try
		{ 
			Society.setOwner(e, player, p == null ? "CONSOLE" : p.getName()); 
			
			Player l = null;
			
			for(String k : s.getOnlineMembers())
			{
				l = Bukkit.getPlayer(k);
				l.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "������ ����� ����������� ��� �������� ����� " + ChatColor.GOLD + player + ChatColor.GREEN + " ������������� " + ChatColor.GOLD + p.getName());
			}
			
			l = Bukkit.getPlayer(player);
			if(l == null)
			{
				new ConfigPlayer(player).addStartMessage("setowner1", ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "�� ���� ��������� ������ ����������� " + ChatColor.GOLD + s.name + ChatColor.GREEN + " ��������������� " + ChatColor.GOLD + p.getName());
			}
			else l.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "�� ���� ��������� ������ ����������� " + ChatColor.GOLD + s.name + ChatColor.GREEN + " ��������������� " + ChatColor.GOLD + p.getName());
			
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "����� " + ChatColor.GOLD + player + ChatColor.AQUA + " ��� �������� ������ ����������� " + ChatColor.GOLD + e);
			
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
				p.sendMessage(ChatColor.RED + "[ERROR] �� �� �������� � �����-���� �����������");
				return;
			}
			p.sendMessage(ChatColor.RED + "[ERROR] ����������� " + ChatColor.GOLD + s.name + ChatColor.RED + " �� ����������");
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
			p.sendMessage(ChatColor.RED + "[ERROR] �� �� �������� � �����-���� �����������");
			return;
		}
		
		if(!s.getOwner(sName).equalsIgnoreCase(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] � ��� ������������ ���� ��� ��������� ������");
			return;
		}
		
		if(p.getName().equalsIgnoreCase(e))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] �� �� ������ �������� ������ ����");
			return;
		}
		
		if(!s.checkPlayer(e))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] ��������� ���� ����� �� ������� � ����� �����������");
			return;
		}
		
		if(!s.getRolePlayer(e).equalsIgnoreCase("member"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] ��������� ���� ����� ����� ������������ ����");
			return;
		}
		
		try 
		{
			s.promotePlayer(e);
			
			p.sendMessage(ChatColor.GOLD + "[INFO]" + ChatColor.GREEN + " ����� " + ChatColor.GOLD + e + ChatColor.GREEN + " ��� ������� ������� �� ����� - " + ChatColor.GOLD + "������");
			
			Player l = Bukkit.getPlayer(e);
			if(l == null)
			{
				ConfigPlayer config = new ConfigPlayer(e);
				config.addStartMessage("promoteself1", ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "�� ���� �������� �� ����� - " + ChatColor.GOLD + "������ " + ChatColor.AQUA + "������ �����������");
				return;
			}
			
			l.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "�� ���� �������� �� ����� - " + ChatColor.GOLD + "������ " + ChatColor.AQUA + "������ �����������");			
		
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
			p.sendMessage(ChatColor.RED + "[ERROR] �� �� �������� � �����-���� �����������");
			return;
		}
		
		if(!s.getOwner(sName).equalsIgnoreCase(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] � ��� ������������ ���� ��� ��������� ������");
			return;
		}
		
		if(p.getName().equalsIgnoreCase(e))
		{
			p.sendMessage( ChatColor.RED + "[ERROR] �� �� ������ �������� ������ ����");
			return;
		}
		
		if(!s.checkPlayer(e))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] ��������� ���� ����� �� ������� � ����� �����������");
			return;
		}
		
		if(s.getRolePlayer(e).equalsIgnoreCase("member")) 
		{
			p.sendMessage(ChatColor.RED + "[ERROR] ��������� ���� ����� ����� ����������� ����");
			return;
		}
		
		try 
		{
			s.demotePlayer(e);
			
			p.sendMessage(ChatColor.GOLD + "[INFO]" + ChatColor.GREEN + " ����� " + ChatColor.GOLD + e + ChatColor.GREEN + " ��� ������� ������� �� ����� - " + ChatColor.GOLD + "��������");
			
			Player l = Bukkit.getPlayer(e);
			if(l == null)
			{
				ConfigPlayer config = new ConfigPlayer(e);
				config.addStartMessage("promoteself1", ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "�� ���� �������� �� ����� - " + ChatColor.GOLD + "�������� " + ChatColor.AQUA + "������ �����������");
				return;
			}
			
			l.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.RED + "�� ���� �������� �� ����� - " + ChatColor.GOLD + "�������� " + ChatColor.RED + "������ �����������");			
		
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
			p.sendMessage(ChatColor.RED + "[ERROR] ������ ������������� ������� - " + ChatColor.GOLD + "/society accept <society>");
			p.sendMessage(ChatColor.AQUA + "[INFO] ��� ����, ����� ���������� ������ ����������� ����������� ������� - " + ChatColor.GOLD + "/society invites ");
			return;
		}
		
		if(s.checkExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] �� ��� �������� � �����������, ����������� " + ChatColor.GOLD + "/society leave " + ChatColor.RED + "����� �������� �����������");
			return;
		}
		
		s = new Society(args.getString(0));
		
		if(!s.checkExists())
		{
			p.sendMessage(ChatColor.RED + "[ERROR] ����������� " + ChatColor.GOLD + args.getString(0) + ChatColor.RED + " �� ����������");
			return;
		}
		
		if(!s.checkPlayerInInvites(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] �� �� ���� ���������� � ��� �����������");
			return;
		}
		
		try {
			s.accept(p.getName());
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "�� ������� �������� � ����������� " + ChatColor.GOLD + s.name);
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
			p.sendMessage(ChatColor.RED + "[ERROR] ������ ������������� ������� - " + ChatColor.GOLD + "/society decline <society>");
			p.sendMessage(ChatColor.AQUA + "[INFO] ��� ����, ����� ���������� ������ ����������� ����������� ������� - " + ChatColor.GOLD + "/society invites ");
			return;
		}
			
		s = new Society(args.getString(0));
		
		if(!s.checkPlayerInInvites(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] �� �� ���� ���������� � ��� �����������");
			return;
		}
		
		try {
			s.decline(p.getName());
			p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "�� ������� ��������� ����������� ���������� � ����������� " + ChatColor.GOLD + s.name);
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
				p.sendMessage(ChatColor.RED + "[ERROR] �� �� �������� � �����-���� �����������");
				return;
			}
		}
		else
		{
			s = new Society(args.getString(0));
			if(!s.checkExists())
			{
				p.sendMessage(ChatColor.RED  + "[ERROR] ��������� ���� ����������� �� ����������");
				return;
			}
		}
		
		p.sendMessage(ChatColor.GOLD + "[INFO] ���������� � ����� �����������: ");
		p.sendMessage(ChatColor.AQUA + "������� �����: " + ChatColor.GOLD + s.getOwner());
		p.sendMessage(ChatColor.AQUA + "������� �����������: " + ChatColor.GOLD + s.getLvl());
		p.sendMessage(ChatColor.AQUA + "����� �����������: " + ChatColor.GOLD + s.getMoney());
		p.sendMessage(ChatColor.AQUA + "����������� ����������: " + ChatColor.GOLD + s.getMembers().size());
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
			p.sendMessage(ChatColor.GOLD + "[INFO] � ��� ��� ����������� ��� ���������� � �����������");
			return;
		}
		
		p.sendMessage(ChatColor.GOLD + "[INFO] ������ ������������ ��� � ���� ���� �����������:");
		
		for(String s : list)
		{
			p.sendMessage(ChatColor.AQUA + s);
		}
		
		p.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.GREEN + "������ ���������� �� ������������ ��� ����������� - " + ChatColor.GOLD + "/society info <society>");
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
			p.sendMessage(ChatColor.RED + "[ERROR] �� �� �������� � �����-���� �����������");
			return;
		}
		
		if(s.getOwner().equalsIgnoreCase(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] �� �� ������ �������� ����������� ������ ������");
			p.sendMessage(ChatColor.GOLD + "��� ��������� �������������� ������ ���������� � ������ �� ��������������� �������");
			return;
		}
		
		try {
			s.leave(p.getName());
			
			p.sendMessage(ChatColor.GOLD + "[INFO] �� ������� �������� �����������");
			
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
		
		p.sendMessage(ChatColor.GOLD + "[INFO] �������� �������: ");
		p.sendMessage( "");
		p.sendMessage(ChatColor.GOLD + "/society create <society> - " + ChatColor.AQUA + "������� �����������");
		p.sendMessage(ChatColor.GOLD + "/society remove <society> - " + ChatColor.AQUA + "�������������� �����������");
		p.sendMessage(ChatColor.GOLD + "/society invite <player> - " + ChatColor.AQUA + "���������� ������ � �����������");
		p.sendMessage(ChatColor.GOLD + "/society kick <player> - " + ChatColor.AQUA + "��������� ������ �� �����������");
		p.sendMessage(ChatColor.GOLD + "/society create <society> - " + ChatColor.AQUA + "������� �����������");
		p.sendMessage(ChatColor.GOLD + "/society list [society] - " + ChatColor.AQUA + "������ ������� ����������� " + ChatColor.GOLD + "[society]");
		p.sendMessage(ChatColor.GOLD + "/society setowner <society> <player> - " + ChatColor.AQUA + "��������� ������ ������ �����������");
		p.sendMessage(ChatColor.GOLD + "/society promote <player> - " + ChatColor.AQUA + "�������� ������ �� ����� " + ChatColor.GOLD + "������");
		p.sendMessage(ChatColor.GOLD + "/society demote <player> - " + ChatColor.AQUA + "�������� ������ �� ����� " + ChatColor.GOLD + "��������");
		p.sendMessage(ChatColor.GOLD + "/society leave - " + ChatColor.AQUA + "�������� �����������");
		p.sendMessage(ChatColor.GOLD + "/society accept <society> - " + ChatColor.AQUA + "������� ����������� ��� ���������� � �����������");
		p.sendMessage(ChatColor.GOLD + "/society decline <society> - " + ChatColor.AQUA + "��������� ����������� ��� ���������� � �����������");
		p.sendMessage(ChatColor.GOLD + "/society invites - " + ChatColor.AQUA + "������ ����������� ��� ���������� � �����������");
		p.sendMessage(ChatColor.GOLD + "/society info [society] - " + ChatColor.AQUA + "������ ���������� �� ����������� " + ChatColor.GOLD + "[society]");
		p.sendMessage(ChatColor.GOLD + "/sc [message...] - " + ChatColor.AQUA + "�������� ��������� � ��� �����������");
	}
}
