package com.narutocraft.power;

import com.narutocraft.stats.ConfigPlayer;

public class PowerPlayer {

	private String player;
	private ConfigPlayer config;
	
	public PowerPlayer(String player) {
		this.player = player;
		this.config = new ConfigPlayer(player);
	}
	
	public int getPower()
	{
		int lvlPower = (5 + (5 + (config.getLvl() - 1) * 2) * config.getLvl()) / 2;
		int donatePower = 0;
		
		for(String donate : config.getDonate())
		{
			donatePower += config.getDonatePower(donate);
		}
		
		return lvlPower + donatePower;
	}
}
