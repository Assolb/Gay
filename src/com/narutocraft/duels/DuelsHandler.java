package com.narutocraft.duels;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.power.PowerPlayer;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;

public class DuelsHandler implements Listener {
	
	public static FileConfiguration config;

	private Map<String, String> pending = new HashMap<String, String>();
	private Map<String, String[]> opponentsOnStadium = new HashMap<String, String[]>();
	
	public static void enablePlugin()
	{	
		File file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "duels.yml");      
		if(!file.exists())
		{
			NarutoCraft1.get().saveResource("duels.yml", false);
			config = YamlConfiguration.loadConfiguration(file);
			
			config.createSection("duelsResults");		
			config.createSection("stadiumsPoints");	
			
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
	
	public void addPending(String nick, String village, int stadiumNumber) 
	{
		pending.put(nick, village + "_" + stadiumNumber);
	}
	
	public void removePending(String nick) 
	{
		pending.put(nick, null);
	}
	
	public boolean isNickInPending(String nick)
	{
		if(pending.containsKey(nick) && pending.get(nick).equals(null))
			return true;
		
		return false;
	}
	
	public boolean isBusyStadium(String village, int stadiumNumber) 
	{
		if(!opponentsOnStadium.get(village + "_" + stadiumNumber).equals(null)) 
			return true;
		
		return false;
	}
	
	private void messageNearStadium(String message, String village, int stadiumNumber) // СДЕЛАТЬ
	{
		
	}
	
	private void sendResults(String winner, String loser, int exp) 
	{
		config.set("duelsResults." + config.getStringList("duelsResults").size() + ".winner", winner);
		config.set("duelsResults." + config.getStringList("duelsResults").size() + ".loser", loser);
		config.set("duelsResults." + config.getStringList("duelsResults").size() + ".exp", exp);
	}
	
	private void addExp(String[] opponents, int wIndex, int lIndex) 
	{
		int exp = 0;
		
		if(new PowerPlayer(opponents[wIndex]).getPower() > new PowerPlayer(opponents[lIndex]).getPower()) 
		{
			exp = (int) Math.round(Math.random() * 50);
		}
		else if(new PowerPlayer(opponents[wIndex]).getPower() > new PowerPlayer(opponents[lIndex]).getPower()) 
		{
			while(exp >= 50)
				exp = (int) Math.round(Math.random() * 300);
		}
		
		Bukkit.getPlayer(opponents[wIndex]).sendMessage(ChatColor.GREEN + "[INFO] Вам добавлено " + exp + " опыта!");
		// добавка 	
	}
	
	private void teleportOnStadium(String key) 
	{
		String[] opponents = opponentsOnStadium.get(key);
		for(int i = 0; i < 2; i++) 
		{
			Player player = Bukkit.getPlayer(opponents[i]);
			player.teleport(new Location(player.getWorld(), config.getDouble("stadiumsPoints." + key + "_x", i), 
					config.getDouble("stadiumsPoints." + key + "_y", i), config.getDouble("stadiumsPoints." + key + "_z", i))); // СТРУКТУРА
		}
	}
	
	public void duel(final String firstOpponent, final String secondOpponent, final String village, final int stadiumNumber) 
	{
		final String[] opponents = null;
		opponents[0] = firstOpponent;
		opponents[1] = secondOpponent;
		
		Thread thread = new Thread() 
		{
			public void run() 
			{				
				for(int i = 0; i < 60; i++) 
				{
					if(!isNickInPending(opponents[1])) 
						break;
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if(isNickInPending(opponents[1])) 
				{
					removePending(opponents[1]);
					Bukkit.getPlayer(opponents[0]).sendMessage(ChatColor.RED + "[INFO] " + ChatColor.AQUA + opponents[1] + ChatColor.RED + " отказался от дуэля!"); // сделать это на экран а не в чат
					currentThread().interrupt();
					return;
				}
				
				opponentsOnStadium.put(village + "_" + stadiumNumber, opponents);
				teleportOnStadium(village + "_" + stadiumNumber);
				
				for(;;)
				{
					if(opponentsOnStadium.get(village + "_" + stadiumNumber)[0].equals(null)) 
					{
						messageNearStadium(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + opponents[1] + ChatColor.GOLD + " победил в дуэле, сражаясь с " +  ChatColor.AQUA + opponents[0], village, stadiumNumber);
						currentThread().interrupt();
						break;
					}
					else if(opponentsOnStadium.get(village + "_" + stadiumNumber)[1].equals(null)) 
					{
						messageNearStadium(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + opponents[0] + ChatColor.GOLD + " победил в дуэле, сражаясь с " + ChatColor.AQUA + opponents[0], village, stadiumNumber);
						currentThread().interrupt();
						break;
					}
					else if(opponentsOnStadium.get(village + "_" + stadiumNumber) == null)
					{
						messageNearStadium(ChatColor.GOLD + "[INFO] Дуэль между " + ChatColor.AQUA + opponents[0] + ChatColor.GOLD  +" и " + ChatColor.AQUA + opponents[1] + ChatColor.GOLD + " окончился ничьей!", village, stadiumNumber);
						currentThread().interrupt();
						break;
					}
				}
			}
			
			
		}; thread.start();
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) 
	{
		if(event.getEntity() instanceof Player) 
		{
			Player player = (Player) event.getEntity();
			String nick = player.getName();
			for(String key : opponentsOnStadium.keySet()) 
			{
				String [] opponents = opponentsOnStadium.get(key);
				if(opponents[0].equals(nick) && player.getKiller().equals(Bukkit.getPlayer(opponents[1])))
				{
					addExp(opponentsOnStadium.get(key),  1, 0);				
					opponents[0] = null;
					opponentsOnStadium.put(key, opponents);
				}
				else if(opponents[1].equals(nick) && player.getKiller().equals(Bukkit.getPlayer(opponents[0])))	
				{
					addExp(opponentsOnStadium.get(key),  0, 1);					
					opponents[1] = null;
					opponentsOnStadium.put(key, opponents);
				}
				else if(opponents[0].equals(nick) && !player.getKiller().equals(Bukkit.getPlayer(opponents[1])) || opponents[1].equals(nick) && !player.getKiller().equals(Bukkit.getPlayer(opponents[0]))) 
				{
					opponentsOnStadium.put(key, null);
				}
			}
		}
	}
	
	@EventHandler
	public void onLeavin(PlayerQuitEvent event) 
	{
		if(isNickInPending(event.getPlayer().getName()))
			removePending(event.getPlayer().getName());
		for(String key : opponentsOnStadium.keySet()) 
		{
			String [] opponents = null;
			if(opponentsOnStadium.get(key)[0].equals(event.getPlayer().getName())) 
			{
				addExp(opponentsOnStadium.get(key),  1, 0);
				opponents[1] = null;
				opponentsOnStadium.put(key, opponents);
				
			}
			else if(opponentsOnStadium.get(key)[1].equals(event.getPlayer().getName()))	
			{
				addExp(opponentsOnStadium.get(key),  0, 1);				
				opponents[1] = null;
				opponentsOnStadium.put(key, opponents);
			}
		}
	}
}
