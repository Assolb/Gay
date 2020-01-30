
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

        		Bukkit.getPlayer(handler.config.namePlayer).sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "�� ������� �������� " + ChatColor.GOLD + (10 * gotCount == 0 ? 10 : 10 * gotCount) + ChatColor.AQUA + " ����(�) �� ��������������� ���������� �� �������, ����������� � ��� �� ����!");
				lastGetting = lastActivity;
				gotCount++;
        		return;
        	}
        	
        	Bukkit.getPlayer(handler.config.namePlayer).sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.AQUA + "�� ������ ����� ���������� � AFK ������, ���� �� ����� ���������� �� ������� �� �������");
        	lastGetting = lastActivity;
        }
	}}
