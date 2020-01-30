package com.narutocraft.police;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Call {

	public String called;
	
	public String initiator;
	
	public String searcher;
	
	public StatusEnum status;
	
	public Location callLoc;
	
	public Call(String called)
	{
		this.called = called;
		
		String loc = PoliceHandler.config.getString("called." + called + ".loc");
		callLoc = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(loc.split(" ")[0]), Double.parseDouble(loc.split(" ")[1]), Double.parseDouble(loc.split(" ")[2]));
	
		initiator = PoliceHandler.config.getString("called." + called + ".initiator");
		searcher = PoliceHandler.config.getString("called." + called + ".searcher");
		
		String s = PoliceHandler.config.getString("called." + called + ".status");
		status = s.equalsIgnoreCase("waiting") ? StatusEnum.WAITING : s.equalsIgnoreCase("searching") ? StatusEnum.SEARCHING : s.equalsIgnoreCase("waitingroom") ? StatusEnum.WAITINGROOM : StatusEnum.COMPLETED;
	}
}
