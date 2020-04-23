package com.narutocraft.hospital;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.narutocraft.main.NarutoCraft1;

public class HospitalHandler implements Listener {	
	
	public static File file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "hospital.yml");
	public static FileConfiguration config;
	
	Map<String, Location> healinPlayers = new HashMap<String, Location>();
	
	public HospitalHandler(NarutoCraft1 narutoCraft1) {}

	public static void enablePlugin()
	{	   
		if(!file.exists())
		{
			NarutoCraft1.get().saveResource("hospital.yml", false);
			config = YamlConfiguration.loadConfiguration(file);
			
			config.createSection("maxHeath");		
			config.createSection("healinPoints");	
			config.createSection("hospitalCoords");
			
			config.set("maxHealth", 20);
			config.set("hospitalCoords.konohagakure.coords.1", "lower,123,123");
			config.set("hospitalCoords.konohagakure.coords.2", "more,123,123");
			
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
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(NarutoCraft1.get(), new Runnable() 
		{
		    @Override
		    public void run() 
		    {	
		    	for(Location location : healinPoints()) 
		    		Bukkit.getWorld("world").playEffect(location, Effect.SMOKE, 5);
		    }
		    
		}, 0L, 60L); 
	}
	
	public double getMaxHealth() 
	{
		return config.getDouble("maxHealth");
	}
	
	public static Location parsin(String coords) 
	{		
		String[] splittedCoords = coords.split(",", 3);
		double x = Double.parseDouble(splittedCoords[0]); 
		double y = Double.parseDouble(splittedCoords[1]); 
		double z = Double.parseDouble(splittedCoords[2]);
		return new Location(Bukkit.getWorld("world"), x, y, z);
	}
	
	public static List<Location> healinPoints()
	{
		List<Location> list = new ArrayList<Location>();
		for(String village : config.getStringList("healinPoints"))
		for(String location : config.getStringList("healinPoints." + village)) 
			list.add(parsin(location));
			
		return list;
	}
	
	public boolean isPlayerInHospital(Player player) 
	{
		Location playerLocation = player.getLocation();

		for(String village : config.getStringList("hospitalCoords")) 
		{
			Location xyz1 = parsin(config.getString("hospitalCoords." + village + ".coords.1"));
			Location xyz2 = parsin(config.getString("hospitalCoords." + village + ".coords.2"));
			
			if(xyz1.getX() < playerLocation.getX() && xyz2.getX() > playerLocation.getX()
				&& xyz1.getY() < playerLocation.getY() && xyz2.getY() > playerLocation.getY()
				&& xyz1.getZ() < playerLocation.getZ() && xyz2.getZ() > playerLocation.getZ())
			return true;
		}
		
		return false;
	}
	
	public boolean isPlayerOnHealPoint(Player player) 
	{
		for(Location location : healinPoints()) 
		{
			Location playerLocation = player.getLocation();
			if(Math.round(playerLocation.getX()) == Math.round(location.getX()) 
					&& Math.round(playerLocation.getY()) == Math.round(location.getY())
					&& Math.round(playerLocation.getZ()) == Math.round(location.getZ()))
				return true;
		}
		
		return false;
	}

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        final String nick = player.getName();
        
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) 
        {
        	if(isPlayerOnHealPoint(player)) 
        	{
        		if(healinPlayers.containsKey(player.getName())) 
        		{
        			player.sendMessage(ChatColor.RED + "[Hospital] Вы уже начали лечение!");
        			return;
        		}
       
    		    Thread heal = new Thread()
    		    {
					public void run()
        			{        				
        				Damageable damagblePlayer = (Damageable) player;
        				double health = damagblePlayer.getHealth();
        				
        				if(health >= getMaxHealth() + 0.0D) 
        				{
        					player.sendMessage(ChatColor.GREEN + "[Hospital] Вы не нуждаетесь в лечении!");
        					return;
        				}
        				
        				healinPlayers.put(nick, player.getLocation());
        				player.sendMessage(ChatColor.GREEN + "[Hospital] Вы начали лечиться");
        				
        				for(int i = (int) health; i < getMaxHealth(); i++) 
        				{
        					try 
        					{
								Thread.sleep(2000);
        					} catch (InterruptedException e) {
								e.printStackTrace();
							}
        					
        					damagblePlayer.setHealth(i + 0.0D);
        					
        					if(health == getMaxHealth() + 0.0D) 
        						break;
        				}
        				player.sendMessage(ChatColor.GREEN + "[Hospital] Лечение окончено!");
        				healinPlayers.remove(nick);
        				
        				currentThread().interrupt();
        			}
    		    };
    		    heal.start();
        	}      		
        } 
    }
    
    @EventHandler
    public void onPlayerMovin(PlayerMoveEvent event) 
    {
    	Player player = event.getPlayer();
    	if(healinPlayers.containsKey(player.getName())) 
    	{
    		if(!isPlayerInHospital(player)) 
    		{
    			player.sendMessage(ChatColor.RED + "[Hospital] Вы не можете выйти с больницы во время лечения!");
    			player.teleport(healinPlayers.get(player.getName()));
    		}
    		
    		/*if(playerLocation.getX() < config || playerLocation.getZ() >= -934 || playerLocation.getY() >= 72 || playerLocation.getY() <= 40 || playerLocation.getX() >= -733)
    		{
    			player.sendMessage(ChatColor.RED + "[Hospital] Вы не можете выйти с больницы во время лечения!");
    			player.teleport(hospitalLocation);
    		}
    			  			
    		if(playerLocation.getZ() <= -962)
			{
    			player.sendMessage(ChatColor.RED + "[Hospital] Вы не можете выйти с больницы во время лечения!");
    			if(playerLocation.getZ() < -963) 
    			{
    				player.teleport(hospitalLocation);
    				return;
    			}
    			playerLocation.setYaw(180);
    			player.teleport(playerLocation);
                Vector v = playerLocation.getDirection().multiply(-0.5);
                player.setVelocity(v);
			} */
		}
    }

}
