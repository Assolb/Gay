package com.narutocraft.report;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.narutocraft.main.NarutoCraft1;

public class ReportListener implements Listener{
	
	@EventHandler
	public void tpOnClickMouse(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		
		for(String l : NarutoCraft1.get().reportHandler.inspect.keySet())
		{
			if(l.equalsIgnoreCase(p.getName()))
			{
				if(e.getAction() == Action.RIGHT_CLICK_AIR)
				{
					Player k = Bukkit.getPlayer(NarutoCraft1.get().reportHandler.inspect.get(l));
					
					Location loc = k.getLocation();
					
					p.teleport(loc);
				}
			}
		}
	}
	
	@EventHandler
	public void exitEvent(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		
		for(String l : NarutoCraft1.get().reportHandler.inspect.keySet())
		{
			if(p.getName().equalsIgnoreCase(l) || p.getName().equalsIgnoreCase(NarutoCraft1.get().reportHandler.inspect.get(l)))
			{
				for(String s : NarutoCraft1.get().reportHandler.inspect.keySet())
				{
					if(s.equalsIgnoreCase(p.getName()))
					{
						NarutoCraft1.get().reportHandler.inspect.remove(s);
						return;
					}
				} 
				
				for(String s : NarutoCraft1.get().reportHandler.inspect.keySet())
				{
					if(NarutoCraft1.get().reportHandler.inspect.get(s).equalsIgnoreCase(p.getName()))
					{
						NarutoCraft1.get().reportHandler.inspect.remove(s);
						p.sendMessage(ChatColor.GOLD + "[INFO]" + ChatColor.RED + "Подозреваемый вышел с сервера, дальшнейшая слежка невозможна");
						return;
					}
				}
			}
		}
	}
}
