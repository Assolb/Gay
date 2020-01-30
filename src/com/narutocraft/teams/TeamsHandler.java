package com.narutocraft.teams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.narutocraft.main.NarutoCraft1;

public class TeamsHandler {

	private Team team;
	
	public TeamsHandler(Team team)
	{
		this.team = team;
	}
	
	public static Team getPlayerTeam(String p)
	{
		if(Team.folder.listFiles() == null)
		{
			return null;
		}
		
		if(Team.folder.listFiles().length == 0)
		{
			return null;
		}
		
		FileConfiguration config;
		for(File file : Team.folder.listFiles())
		{
			config = YamlConfiguration.loadConfiguration(file);
			if(config.getStringList("team.members").contains(p)) return new Team(NarutoCraft1.get(), config.getInt("team.id"));
		}
		return null;
	}
	
	public int getID()
	{
		return team.config.getInt("team.id");
	}
	
	public Team getTeam()
	{
		return team;
	}
	
	public List<String> getMembers()
	{
		return team.config.getStringList("team.members");
	}
	
	public String getJonin()
	{
		return team.config.getString("team.jonin");
	}
	
	public int getCountCompletedMissions()
	{
		String[] strings = {"E", "D", "C", "B", "A", "S", "SS", "SSS"};
		
		int count = 0;
		for(String s : strings)
		{
			count += team.config.getInt("team.missions." + s);
		}
		
		return count;
	}
	
	public int getCountCompletedMissions(String rank)
	{
		return team.config.getInt("team.missions." + rank);
	}
	
	public void addCompletedMission(String rank)
	{
		team.config.set("team.missions." + rank, getCountCompletedMissions(rank) + 1);
	}
	
	public List<String> getOnlineMembers()
	{
		List<String> members = new ArrayList<String>();
		
		for(String s : getMembers())
		{
			members.add(s);
		}
		
		members.add(getJonin());
		
		for(String s : members)
		{
			if(Bukkit.getPlayer(s) == null)
			{
				members.remove(s);
			}
		}
		
		return members;
	}
}
