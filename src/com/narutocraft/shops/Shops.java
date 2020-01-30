package com.narutocraft.shops;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.narutocraft.main.NarutoCraft1;

public class Shops {

	public static FileConfiguration config;
	public static File file;
	
	public static HashMap<String, String> helper = new HashMap<String, String>();
	
	public Shops(String name)
	{
		
	}
	
	public static void enablePlugin()
	{
		file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "shops.yml");
		config = YamlConfiguration.loadConfiguration(file);
		if(!file.exists())
		{
			try 
			{
				file.createNewFile();
				config.createSection("shops");
				config.save(file);
			}
			
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		for(String s : config.getConfigurationSection("shops").getKeys(false))
		{
			helper.put(config.getString("shops." + s + ".id"), s);
		}
	}
}
