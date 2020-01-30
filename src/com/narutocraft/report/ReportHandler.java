package com.narutocraft.report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import com.narutocraft.main.NarutoCraft1;

public class ReportHandler implements Listener
{
	private NarutoCraft1 plugin;
	
	public HashMap<String, String> inspect = new HashMap<String, String>();
	
	public ReportHandler(NarutoCraft1 narutoCraft) {
		plugin = narutoCraft;
	}
	
	public static void enablePlugin()
	{
		File file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "reports");
		
		if(!file.exists())
		{
			file.mkdir();
		}
	}
	
	public void createReport(String reporter, String target)
	{
		File[] files = new File(NarutoCraft1.get().getDataFolder() + File.separator + "reports").listFiles();
		
		File file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "reports" + File.separator + files.length + ".yml");
	
		try
		{
			file.createNewFile();
			
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			config.createSection("main");
			config.set("main.id", files.length);
			config.set("main.player", target);
			config.set("main.reporter", reporter);
			config.set("main.ready", false);
			
			List<String> list = new ArrayList<String>();
			
			config.set("main.report", list);
			
			config.save(file);
		}
		catch(IOException e){}
	}
	
	public HashMap<File, FileConfiguration> getFileReport(int report)
	{
		File file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "reports" + File.separator + report + ".yml");
		if(!file.exists()) return null;
		
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				
		HashMap<File, FileConfiguration> hashMap = new HashMap<File, FileConfiguration>();
		hashMap.put(file, config);
		
		return hashMap;
	}
	
	public File getFolder()
	{
		return new File(NarutoCraft1.get().getDataFolder() + File.separator + "reports");
	}
	
	public boolean checkExists(int report)
	{
		File file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "reports" + File.separator + report + ".yml");
		
		return file.exists();
	}
	
	public void editReport(int report, String string)
	{
		HashMap<File, FileConfiguration> hash = getFileReport(report);
		
		List<File> list = new ArrayList<File>();
		list.addAll(hash.keySet());
		
		File file = list.get(0);
		FileConfiguration config = hash.get(file);
		
		if(config.getBoolean("main.ready"))
		{
			return;
		}
		
		List<String> rep = config.getStringList("main.report");
		
		rep.add(string);
		
		config.set("main.report", rep);
		try {
			config.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
 