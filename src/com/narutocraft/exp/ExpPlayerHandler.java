package com.narutocraft.exp;

import java.util.Date;
import java.util.HashMap;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.stats.ConfigPlayer;

public class ExpPlayerHandler {
	
	public static HashMap<String, ExpPlayerHandler> listHandlers = new HashMap<String, ExpPlayerHandler>();
	
	private final NarutoCraft1 plugin;
	public final ConfigPlayer config;
	
	public ExpForTime timeHandler;
	
	public ExpPlayerHandler(NarutoCraft1 narutoCraft1, ConfigPlayer config)
	{
		this.plugin = narutoCraft1;
		this.config = config;
		
		timeHandler = new ExpForTime(this, new Date());
	}
}
