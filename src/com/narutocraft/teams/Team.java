package com.narutocraft.teams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.narutocraft.main.NarutoCraft1;

public class Team {

	private NarutoCraft1 plugin;
	
	public FileConfiguration config;
	public static File folder = new File(NarutoCraft1.get().getDataFolder() + File.separator + "teams");
	public File file;
	
	public Team(NarutoCraft1 narutoCraft1, int team)
	{
		this.plugin = narutoCraft1;
		this.file = new File(folder + File.separator + team + ".yml");
		this.config = YamlConfiguration.loadConfiguration(file);
	}
	
	public boolean checkExists()
	{
		return file.exists();
	}
	
	/**
	 * @param  members - members[0] is Jonin, members[0 + i] is genins, if members.lenght == 3 then jonin is empty
	 * @throws IOException 
     *         
	*/
	public static int createTeam(String... members) throws IOException
	{	
		if(members.length < 3)
		{
			return -1;
		}
		
		if(members.length > 4)
		{
			return -1;
		}
		
		String jonin = null;
		List<String> genins = new ArrayList<String>();
		
		if(members.length == 4) jonin = members[0];
		
		for(int i = 1; i < 4; i++)
		{
			if(TeamsHandler.getPlayerTeam(members[i]) != null)
			{
				return -1;
			}
			genins.add(members[i]);
		}	
		
		int id = checkNextID();
		
		File file = new File(folder + File.separator + id + ".yml");
		file.createNewFile();
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		config.createSection("team");
		config.set("team.id", id);
		
		if(jonin != null) config.set("team.jonin", jonin);
		else config.set("team.jonin", "none");
		
		config.set("team.members", genins);
		config.createSection("team.missions");
		config.set("team.missions.SSS", 0);
		config.set("team.missions.SS", 0);
		config.set("team.missions.S", 0);
		config.set("team.missions.A", 0);
		config.set("team.missions.B", 0);
		config.set("team.missions.C", 0);
		config.set("team.missions.D", 0);
		config.set("team.missions.E", 0);
		
		config.save(file);
		
		return id;
	}
	
	public static int checkNextID()
	{
		if(folder.listFiles().length == 0 || folder.listFiles() == null)
		{
			return 1;
		}
		
		int id = 1;
		
		for(File file : folder.listFiles())
		{
			id++;
		}
		
		return id;
	}
	
	public static void enablePlugin()
	{		
		if(!folder.exists())
		{
			folder.mkdir();
		}
	}
	
}
