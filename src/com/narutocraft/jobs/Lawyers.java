package com.narutocraft.jobs;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Lawyers extends Job {
	
	public static File file;
	public static FileConfiguration config;
	
	public Lawyers()
	{
		super("lawyers");
	}
	
	public static void enableJob() throws IOException
	{
		file = new File(folder + File.separator + "laywers.yml");
		if(file.exists())
		{
			file.createNewFile();
			config = YamlConfiguration.loadConfiguration(file);
			config.createSection("job");
			config.createSection("job.workers");
			config.createSection("job.tasks");
			config.save(file);
		}
		
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	public static void callLawyers() 
	{
		for(String lawyerName : config.getStringList("job.workers")) 
		{
			if(Bukkit.getPlayer(lawyerName) != null) 
				Bukkit.getPlayer(lawyerName).sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "[Jobs][INFO] Вас вызывают в качестве адвоката. Подробнее: /lawyer tasks");
		}
	}

}
