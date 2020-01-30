package com.narutocraft.anbu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.narutocraft.village.Village;

public class Anbu {

	public Village village;
	
	public File file;
	public FileConfiguration config;
	
	public Anbu(Village village)
	{
		this.village = village;
		
		this.file = village.file;
		this.config = village.config;
	}
	
	public List<String> getMembers()
	{
		List<String> list = new ArrayList<String>();
		
		for(String s : config.getConfigurationSection("village.anbu.members").getKeys(false))
		{
			list.add(s);
		}
		
		return list;
	}
	
	public boolean isContainsAtAnbu(String player) 
	{
		return config.getConfigurationSection("village.anbu.members").getKeys(false).contains(player);
	}
	
	public int getRank(String member)
	{
		try
		{
			int rank = config.getInt("village.anbu.members." + member);
			return rank;
		}
		
		catch(NullPointerException e)
		{
			return 0;
		}
	}
	
	public String getAnbuHead()
	{
		String head = "none";
		
		for(String s : config.getConfigurationSection("village.anbu.members").getKeys(false))
		{
			if(config.getInt("village.anbu.members." + s) == 5)
			{
				head = s;
			}
		}
		
		return head;
	}
	
	public List<String> getCurrentTasks()
	{
		List<String> list = new ArrayList<String>();
		for(String s : config.getConfigurationSection("village.anbu.current-missions").getKeys(false))
		{
			list.add(s);
		}
		
		return list;
	}
	
	public List<String> getTasks()
	{
		List<String> list = new ArrayList<String>();
		for(String s : config.getConfigurationSection("village.anbu.missions").getKeys(false))
		{
			list.add(s);
		}
		
		return list;
	}
	
}
