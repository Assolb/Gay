package com.narutocraft.events;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.narutocraft.main.NarutoCraft1;

public class EventsHandler {

	public static NarutoCraft1 plugin;
	
	public static File file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "events.yml");
	public static FileConfiguration config;
	
	public String currentEvent = "none";
	
	public EventsHandler(NarutoCraft1 narutoCraft1) {
		plugin = narutoCraft1;
	}
	
	public static void enablePlugin()
	{
		if(!file.exists())
		{
			try {
				plugin.saveResource("events.yml", true);
				config = YamlConfiguration.loadConfiguration(file);
				config.createSection("events");
				config.save(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	public boolean checkExists(String event)
	{
		if(config.getConfigurationSection("events").getKeys(false).size() > 0){
			for(String s : config.getConfigurationSection("events").getKeys(false))
			{
				if(event.equalsIgnoreCase(s)) return true;
			}
		}
		return false;
	}
	
	public String getCurrentEvent()
	{
		return plugin.eventsHandler.currentEvent;
	}
	
	public boolean checkAttend(String player)
	{
		if(currentEvent.equalsIgnoreCase("none"))
		{
			return false;
		}
		if(config.getConfigurationSection("events." + currentEvent + ".commoners").getKeys(false).size() > 0)
		{
			for(String s : plugin.eventsHandler.config.getConfigurationSection("events." + currentEvent + ".commoners").getKeys(false))
			{
				if(s.equalsIgnoreCase(player)) return true;
			}
		}
		return false;
	}
	
}
