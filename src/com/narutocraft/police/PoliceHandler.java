package com.narutocraft.police;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.network.S0PacketSendTitle;

public class PoliceHandler implements Listener {
	
	public static File file;
	public static FileConfiguration config;
	
	public static void enablePlugin()
	{
		file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "police.yml");
		if(!file.exists())
		{
			NarutoCraft1.get().saveResource("police.yml", false);
			config = YamlConfiguration.loadConfiguration(file);
			config.createSection("members");
			config.createSection("arrest");
			config.createSection("wanted");
			config.createSection("called");
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		else config = YamlConfiguration.loadConfiguration(file);	
	}
	
	public String getHead()
	{
		for(String s : config.getConfigurationSection("members").getKeys(false))
		{
			if(config.getString("members." + s).equalsIgnoreCase("head"))
			{
				return s;
			}
		}
		return null;
	}
	
	public List<String> getMembers()
	{
		List<String> list = new ArrayList<String>();
		list.addAll(config.getConfigurationSection("members").getKeys(false));
		
		return list;
	}
	
	public List<Player> getOnlineMembers()
	{
		List<Player> online = new ArrayList<Player>();
		Player p;
		for(String s : getMembers())
		{
			p = Bukkit.getPlayer(s);
			if(p == null) continue;
			online.add(p);
		}
		return online;
	}
	
	public List<String> getArrested()
	{
		List<String> list = new ArrayList<String>();
		list.addAll(config.getConfigurationSection("arrest").getKeys(false));
		
		return list;
	}
	
	public String getReleasedDate(String p)
	{
		if(!getArrested().contains(p))
		{
			return null;
		}
		
		return config.getString("arrest." + p);			
	}
	
	public int getMinsForRelease(String p)
	{
		if(!getArrested().contains(p))
		{
			return 0;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("m.H.dd.MM.yyyy");
		
		Date tDate = new Date();
		try 
		{
			Date date1 = dateFormat.parse(tDate.getMinutes() + "." + tDate.getHours() + "." + tDate.getDay() + "." + tDate.getMonth() + "." + tDate.getYear());
			Date date2 = dateFormat.parse(getReleasedDate(p));
			long milliseconds = date2.getTime() - date1.getTime();
			int minutes = (int) (milliseconds / (60 * 1000));
			return minutes;
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public List<String> getWanted()
	{
		List<String> list = new ArrayList<String>();
		list.addAll(config.getConfigurationSection("wanted").getKeys(false));
		
		return list;
	}
	
	@EventHandler
	public void wantedJoining(PlayerJoinEvent e)
	{
		if(!getWanted().contains(e.getPlayer().getName()))
		{
			return;
		}
		
		//MESSAGE ABOUT THE WANTED JOINING
	}
	
	public void sendPoliceMessage(String sender, String message)
	{
		List<Player> list = getOnlineMembers();
		for(Player p : list)
		{
			p.sendMessage(ChatColor.GOLD + "[PoliceSystem] " + ChatColor.RESET + sender + ": " + message);
		}
	}
	
	public void sendPoliceMessage(String message)
	{
		List<Player> list = getOnlineMembers();
		for(Player p : list)
		{
			p.sendMessage(ChatColor.GOLD + "[PoliceSystem] " + message);
		}
	}
	
	public List<String> getCalled()
	{
		List<String> list = new ArrayList<String>();
		list.addAll(config.getConfigurationSection("called").getKeys(false));
		
		return list;
	}
	
	public Call getCall(String called)
	{
		return new Call(called);
	}
	
	public void sendCall(Player player, String called) throws IOException
	{
		if(config.getConfigurationSection("called").contains(called))
		{
			if(getCall(called).status != StatusEnum.COMPLETED)
			{
				return;
			}
			config.set("called." + called, null);
		}
		config.createSection("called." + called);
		config.set("called." + called + ".initiator", player.getName());
		config.set("called." + called + ".loc", player.getLocation().getBlockX() + " " + player.getLocation().getBlockY() + player.getLocation().getBlockZ());
		config.set("called." + called + ".searcher", "none");
		config.set("called." + called + ".trial-time", 0);
		config.set("called." + called + ".prison-time", 0);
		config.set("called." + called + ".status", "waiting");
		config.save(file);
		
		for(Player p : getOnlineMembers())
		{
			NarutoCraft1.handler.sendPacketToPlayer(p, new S0PacketSendTitle("[ВАМ ПОСТУПИЛ НОВЫЙ ВЫЗОВ, ПРОВЕРЬТЕ ЖУРНАЛ ВЫЗОВОВ]", 0xFFFFF));
		}
	}
	
	public void acceptCall(Player p, String called) throws IOException
	{
		if(!config.getConfigurationSection("called").contains(called))
		{
			return;
		}
		
		if(getCall(called).status != StatusEnum.WAITING)
		{
			return;
		}
		
		config.set("called." + called + ".status", "searching");
		config.set("called." + called + ".searcher", p.getName());
		config.save(file);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void fightVSWanted(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getDamager();
			Player p1 = (Player) e.getEntity();
			if(getMembers().contains(p.getName()))
			{
				if(getWanted().contains(p1.getName()))
				{
					if(new Call(p1.getName()).status != StatusEnum.SEARCHING)
					{
						return;
					}
					
					if(((Damageable)p1).getHealth() < e.getDamage() * 2)
					{
						config.set("called." + p1.getName() + ".status", "waitingroom");
						try 
						{
							config.save(file);
						} 
						catch (IOException e1) 
						{
							e1.printStackTrace();
						}
					}
					p1.damage(e.getDamage() * 2);
				}
			}
		}
	}
}
