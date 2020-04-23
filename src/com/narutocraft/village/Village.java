package com.narutocraft.village;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.narutocraft.main.NarutoCraft1;

public abstract class Village {
	
	public File file;
	public FileConfiguration config;
	
	public String name;
	public EnumVillages village;
	
	public Village(String name)
	{
		file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "villages" + File.separator + name + ".yml");
		
		config = YamlConfiguration.loadConfiguration(file);
		
		this.name = name;
		
	}
	
	public static void enablePlugin()
	{
		if(!new File(NarutoCraft1.get().getDataFolder() + File.separator + "villages").exists())
		{
			new File(NarutoCraft1.get().getDataFolder() + File.separator + "villages").mkdir();
		}
		
		
	}
	
	public boolean checkExists()
	{
		return file.exists();
	}
	
	public void create() throws IOException
	{
		file.createNewFile();
		
		config.createSection("village");
			config.set("village.name", name);
			config.set("village.coffer", 0);
			config.set("village.kage", "none");
		config.createSection("village.anbu");
			config.createSection("village.anbu.members");
			config.set("village.anbu.coffer", 0);
			config.createSection("village.anbu.missions");
			//config.createSection("village.anbu.missions.ID");
			//config.set("village.anbu.missions.jksk.type", "kill");
			//config.set("village.anbu.missions.jksk.player", <player>);
			//config.set("village.anbu.missions.jksk.reward", <money>);
		config.createSection("village.anbu.current-missions");
		config.save(file);
	}
	
	public int getCoffer()
	{
		return config.getInt("village.coffer");
	}
	
	public void consumeFromCoffer(int amount)
	{
		config.set("village.coffer", getCoffer() - amount < 0 ? 0 : getCoffer() - amount);
		try 
		{
			config.save(file);
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public String getKage()
	{
		return config.getString("village.kage");
	}
	
	public void addToCoffer(int amount)
	{
		config.set("village.coffer", getCoffer() + amount);
		try 
		{
			config.save(file);
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
