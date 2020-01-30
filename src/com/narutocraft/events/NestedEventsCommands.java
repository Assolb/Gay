package com.narutocraft.events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.stats.ConfigPlayer;
import com.narutocraft.util.Lang;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;

public class NestedEventsCommands {

	private static NarutoCraft1 plugin = NarutoCraft1.get();
	
	@Command(aliases = {"list"}, desc = "Shows a list of all upcoming events", usage = " - Shows a list of all upcoming events", min = 0)
	@CommandPermissions(value = {"com.narutocraft.events.list"})
	public void list(final CommandContext args, CommandSender sender)
	{
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			String list = "";
			
			if(plugin.eventsHandler.config.getConfigurationSection("events").getKeys(false).size() == 0) 
			{
				p.sendMessage(Lang.getLocal("events", "list-noevents", p.getName()));
				p.sendMessage(Lang.getLocal("events", "list-noevents1", p.getName()));
				return;
			}
			
			List<String> x = new ArrayList<String>();
			x.addAll(plugin.eventsHandler.config.getConfigurationSection("events").getKeys(false));
			
			for(int i = 0; i < plugin.eventsHandler.config.getConfigurationSection("events").getKeys(false).size(); i++)
			{
				list = list + ChatColor.GOLD + x.get(i);
				if(x.size() - 1 != i)
				{
					list = list + ChatColor.RESET + " ,";
				}
			}
			
			p.sendMessage(Lang.getLocal("events", "list-upcoming-events", p.getName()) + ":");
			p.sendMessage(list);
		}
	}
	
	@Command(aliases = {"create"}, desc = "Creates a upcoming events", usage = "<event> [true/false] - creates a upcoming event with name <event> and required [true/false]", min = 2)
	@CommandPermissions(value = {"com.narutocraft.events.create"})
	public static void create(final CommandContext args, CommandSender sender)
	{
		Player p = null;
		if(!(sender instanceof Player))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] This command can only be used by a player");
			return;
		}
		
		p = (Player) sender;
		
		Location location = p.getLocation();
		String loc = location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
		
		String e = args.getString(0);
		boolean required = (args.getString(1).equalsIgnoreCase("true") ? true : false);
		
		if(plugin.eventsHandler.checkExists(e))
		{
			p.sendMessage(Lang.getLocal("events", "check-exists-true", p.getName()));
			return;
		}
		
		plugin.eventsHandler.config.createSection("events." + e);
		
		plugin.eventsHandler.config.set("events." + e + ".name", e);
		plugin.eventsHandler.config.set("events." + e + ".creator", p.getName());
		plugin.eventsHandler.config.set("events." + e + ".coords", loc);
		plugin.eventsHandler.config.set("events." + e + ".must", required);
		
		plugin.eventsHandler.config.createSection("events." + e + ".commoners");
		
		try {
			plugin.eventsHandler.config.save(plugin.eventsHandler.file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		p.sendMessage(Lang.getLocal("events", "create-successful", p.getName(), e));
	}
	
	@Command(aliases = {"remove", "delete", "del"}, desc = "Remove a event", usage = "<event> - remove event with name <event>", min = 1)
	@CommandPermissions(value = {"com.narutocraft.events.remove"})
	public static void remove(final CommandContext args, CommandSender sender)
	{
		Player p = null;
		if(sender instanceof Player)
		{
			p = (Player) sender;
		}
		
		String e = args.getString(0);
		
		if(!plugin.eventsHandler.checkExists(e))
		{
			p.sendMessage(Lang.getLocal("events", "check-exists-false", p.getName()));
			return;
		}
		
		if(!plugin.eventsHandler.currentEvent.equalsIgnoreCase("none"))
		{
			p.sendMessage(Lang.getLocal("events", "remove-unable", p.getName(), e));
			return;
		}
		
		plugin.eventsHandler.config.set("events." + e, null);
		try {
			plugin.eventsHandler.config.save(plugin.eventsHandler.file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		p.sendMessage( Lang.getLocal("events", "remove-successful", p.getName(), e));
	}
	
	@Command(aliases = {"start"}, desc = "Start a event", usage = "<event> - starting event with name <event>", min = 1)
	@CommandPermissions(value = {"com.narutocraft.events.start"})
	public static void start(final CommandContext args, CommandSender sender)
	{
		Player p = null;
		if(sender instanceof Player) p = (Player) sender;
		
		String e = args.getString(0);
		
		if(!plugin.eventsHandler.checkExists(e))
		{
			p.sendMessage( Lang.getLocal("events", "check-exists-false", p.getName()));
			return;
		}
		
		plugin.eventsHandler.currentEvent = e;
		for(Player b : Bukkit.getOnlinePlayers())
		{
			if(!plugin.eventsHandler.config.getBoolean("events." + e + ".must"))
			{
				b.sendMessage(Lang.getLocal("events", "start-event", b.getName(), e));
				b.sendMessage(Lang.getLocal("events", "start-event1", b.getName(), e));
			}
			else 
			{ 	
				b.sendMessage(Lang.getLocal("events", "start-event-required", b.getName(), e));
				b.sendMessage(Lang.getLocal("events", "start-event-required1", b.getName()));
				
				String[] coord = plugin.eventsHandler.config.getString("events." + e + ".coords").split(";");
				b.teleport(new Location(b.getWorld(), Double.parseDouble(coord[0]), Double.parseDouble(coord[1]), Double.parseDouble(coord[2])));
				
				Location location = p.getLocation();
				String loc = location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
				
				plugin.eventsHandler.config.set("events." + e + ".commoners." + p.getName(), loc);
				try {
					plugin.eventsHandler.config.save(plugin.eventsHandler.file);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			}
		}
	}
	
	@Command(aliases = {"finish"}, desc = "finish a event", usage = "<event> - finishing the event with name <event>", min = 1)
	@CommandPermissions(value = {"com.narutocraft.events.finish"})
	public static void finish(final CommandContext args, CommandSender sender)
	{
		Player p = null;
		if(sender instanceof Player) p = (Player) sender;
		
		String e = args.getString(0);
		
		if(!plugin.eventsHandler.currentEvent.equalsIgnoreCase(e))
		{
			p.sendMessage(Lang.getLocal("events", "finish-event-unable", p.getName()));
			return;
		}
		
		for (Player player : Bukkit.getServer().getOnlinePlayers())
		{
			player.sendMessage(Lang.getLocal("events", "finish-event", player.getName(), e)); 
			player.sendMessage(Lang.getLocal("events", "finish-event-thanks", player.getName()));				
		}
		
		for(String s : plugin.eventsHandler.config.getConfigurationSection("events." + e + ".commoners").getKeys(false))
		{
			String player = s;
			
			String[] coords = plugin.eventsHandler.config.getString("events." + e + ".commoners." + s).split(";");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);	
			
			Player b = Bukkit.getPlayer(s);
			if(b == null)
			{
				ConfigPlayer cp = new ConfigPlayer(s);
				try {
					cp.addStartCommand("tpafterevent", "tp " + s + " " + x + " " + y + " " + z);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				continue;
			}
			b.teleport(new Location(p.getWorld(), x, y, z));	
		}
		plugin.eventsHandler.config.set("events." + e, null);
		try {
			plugin.eventsHandler.config.save(plugin.eventsHandler.file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		plugin.eventsHandler.currentEvent = "none";
	}
	
	@Command(aliases = {"join"}, desc = "join to the event", usage = "<event> - join to the event <event>", min = 1)
	@CommandPermissions(value = {"com.narutocraft.player"})
	public static void join(final CommandContext args, CommandSender sender)
	{
		Player p = null;
		if(sender instanceof Player) p = (Player) sender;
		
		if(p == null)
		{
			p.sendMessage(ChatColor.RED + "[ERROR] This command can only be used by a player");
			return;
		}
		
		Location location = p.getLocation();
		String loc = location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
		
		String e = args.getString(0);
		
		if(!plugin.eventsHandler.checkExists(e))
		{
			p.sendMessage(Lang.getLocal("events", "check-exists-false", p.getName()));
			return;
		}
		
		if(!plugin.eventsHandler.currentEvent.equalsIgnoreCase(e))
		{
			p.sendMessage(Lang.getLocal("events", "join-impossible", p.getName(), e));
			return;
		}
		
		if(plugin.eventsHandler.checkAttend(p.getName()))
		{
			p.sendMessage(Lang.getLocal("events", "join-impossible", p.getName()));
			return;
		}

		String[] coord = plugin.eventsHandler.config.getString("events." + e + ".coords").split(";");
		p.teleport(new Location(p.getWorld(), Double.parseDouble(coord[0]), Double.parseDouble(coord[1]), Double.parseDouble(coord[2])));
		
		p.sendMessage(Lang.getLocal("events", "join-successful", p.getName(), e));
		
		plugin.eventsHandler.config.set("events." + e + ".commoners." + p.getName(), loc);
		try {
			plugin.eventsHandler.config.save(plugin.eventsHandler.file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	}
}

