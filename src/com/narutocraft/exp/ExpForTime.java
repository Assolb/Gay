
package com.narutocraft.exp;

import java.io.IOException;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ExpForTime {
	
	private Date lastGetting;
	private Date lastActivity;
	
	private int gotCount = 0;
	
	private ExpPlayerHandler handler;
	
	private long diff;
	private long diffMinutes;
	
	public ExpForTime(ExpPlayerHandler handler, Date date)
	{
		lastGetting = date;
		lastActivity = date;
		
		this.handler = handler;
	}
	
	public void updateActivity()
	{
		lastActivity = new Date();
		findExp();
	}
	
	private void findExp()
	{
		diff = lastActivity.getTime() - lastGetting.getTime();
        diffMinutes = diff / (60 * 1000) % 60;
        
        if(diffMinutes > 10)
        {
        	if(diffMinutes < 20)
        	{
        		try {
					handler.config.addExp((10 * gotCount == 0 ? 10 : 10 * gotCount > 250 ? 250 : gotCount * 10));
				} catch (IOException e) {
					e.printStackTrace();
				}

        		Bukkit.getPlayer(handler.config.namePlayer).sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "Вы успешно получили " + ChatColor.GOLD + (10 * gotCount == 0 ? 10 : 10 * gotCount) + ChatColor.AQUA + " опыт(а) за продолжительное нахождение на сервере, продолжайте в том же духе!");
				lastGetting = lastActivity;
				gotCount++;
        		return;
        	}
        	
        	Bukkit.getPlayer(handler.config.namePlayer).sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "Вы долгое время находились в AFK режиме, опыт за время нахождения на сервере не получен");
        	lastGetting = lastActivity;
        }
	}}
