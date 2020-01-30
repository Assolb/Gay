package com.narutocraft.stats;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.narutocraft.exp.EnumLevels;
import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.network.S0PacketSetHudStats;

public class ConfigListener implements Listener { 
	
	@EventHandler
	public void createConfig(PlayerJoinEvent e){
		try
		{
			if(!ConfigPlayer.getConfigPlayer(e.getPlayer().getName()).exists())
	        {			
				ConfigPlayer.getConfigPlayer(e.getPlayer().getName()).createNewFile();
				
				File file = ConfigPlayer.getConfigPlayer(e.getPlayer().getName());
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			
				config.set("language", "RU");
				
				config.createSection("jutsu");
				
				config.createSection("stats");
				
				config.createSection("joins");
				config.createSection("joins.messages");
				config.createSection("joins.commands");
				
				config.set("stats.chakra", 100);
				config.set("stats.maxchakra", 100);
				
				config.set("stats.money", 0);
				
				config.set("stats.job", "none");
				
				config.set("stats.village", "none");
				
				config.set("stats.exp", 0);
				config.set("stats.lvl", 0);
				config.set("stats.lvlexp", 5);
				
				List<String> list = new ArrayList<String>();
				config.set("donate", list);
				
				config.save(file);
	        }
			
			File file = ConfigPlayer.getConfigPlayer(e.getPlayer().getName());
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			ConfigPlayer pc = new ConfigPlayer(e.getPlayer().getName());
				
			Set<String> set = config.getConfigurationSection("joins.messages").getKeys(false);
			if(set.size() > 0)
			{
				for(String s : set)
				{
					e.getPlayer().sendMessage(config.getString("joins.messages." + s));
					pc.removeStartMessage(s);
				}
			}
				
			set = config.getConfigurationSection("joins.commands").getKeys(false);
			if(set.size() > 0)
			{
				for(String s : set)
				{
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), config.getString("joins.commands." + s));
					pc.removeStartCommand(s);
				}
			}
		}
		catch(IOException s)
		{
			s.printStackTrace();
		}
	}

	@EventHandler
	public void enableHud(PlayerJoinEvent e)
	{
		ConfigPlayer config = new ConfigPlayer(e.getPlayer().getName());
		NarutoCraft1.handler.sendPacketToPlayer(e.getPlayer(), new S0PacketSetHudStats("" + 0, "" + config.getChakra()));
		NarutoCraft1.handler.sendPacketToPlayer(e.getPlayer(), new S0PacketSetHudStats("" + 1, "" + config.getMaxChakra()));
		NarutoCraft1.handler.sendPacketToPlayer(e.getPlayer(), new S0PacketSetHudStats("" + 2, "" + EnumLevels.values()[config.getLvl()].getExp()));
		NarutoCraft1.handler.sendPacketToPlayer(e.getPlayer(), new S0PacketSetHudStats("" + 3, "" + config.getExp()));
		NarutoCraft1.handler.sendPacketToPlayer(e.getPlayer(), new S0PacketSetHudStats("" + 4, "" + EnumLevels.values()[config.getLvl() + 1].getExp()));
	}
}
