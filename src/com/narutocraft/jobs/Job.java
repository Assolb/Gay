package com.narutocraft.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.narutocraft.main.NarutoCraft1;

public class Job {
	
	public String name;
	
	public static File folder = new File(NarutoCraft1.get().getDataFolder() + File.separator + "jobs");
	
	public Job(String name)
	{
		this.name = name;
	}
	
	public static String getPlayerJob(String name)
	{
		File[] file1 = folder.listFiles();
		List<File> file = new ArrayList<File>();
		
		for(File f : file1)
		{
			if(f.getName().substring(f.getName().lastIndexOf("." + 1)).equalsIgnoreCase("yml"))
				file.add(f);
		}
		FileConfiguration config;
		for(File f : file)
		{
			config = YamlConfiguration.loadConfiguration(f);
			for(String s : config.getConfigurationSection("job.workers").getKeys(false))
			{
				if(name.equalsIgnoreCase(s)) return f.getName().replace(".yml", "");
			}
		}
		
		return "none";
	}
	
	public static Job getPlayerJobClass(String name)
	{
		String j = getPlayerJob(name);
		
		switch (j) {
		case "lawyers":
			return new Lawyers();
			
		default:
			break;
		}
		
		return null;
	}
	
	public static Job getJobClass(String job)
	{
		switch (job) {
		case "lawyers":
			return new Lawyers();
			
		default:
			break;
		}
		
		return null;
	}
}
