package com.narutocraft.exp;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.network.PacketHandlerPlugin;
import com.narutocraft.network.S0PacketSendTitle;
import com.narutocraft.network.S0PacketSetHudStats;
import com.narutocraft.stats.ConfigPlayer;

public class ExpListener implements Listener{

	@EventHandler(priority = EventPriority.LOW)
	public void joinPlayer(PlayerJoinEvent e)
	{
		ConfigPlayer config = new ConfigPlayer(e.getPlayer().getName());
		ExpPlayerHandler.listHandlers.put(e.getPlayer().getName(), new ExpPlayerHandler(NarutoCraft1.get(), config));
	}
	
	@EventHandler
	public void quitPlayer(PlayerQuitEvent e)
	{
		ExpPlayerHandler.listHandlers.remove(e.getPlayer().getName());
	}
	
	@EventHandler
	public void movePlayer(PlayerMoveEvent e)
	{
		ExpPlayerHandler.listHandlers.get(e.getPlayer().getName()).timeHandler.updateActivity();
	}
	
	@EventHandler
	public void ubb(PlayerInteractEvent e)
	{
		NarutoCraft1.handler.sendPacketToPlayer(e.getPlayer(), new S0PacketSendTitle("ÑÀÑÓ", -1));
	}
}
