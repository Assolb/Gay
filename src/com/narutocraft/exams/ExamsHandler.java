package com.narutocraft.exams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.network.S0PacketSendTitle;
import com.narutocraft.teams.TeamsHandler;

public class ExamsHandler implements Listener {
	
	public List<String> members = new ArrayList<String>();
	public List<String> winners = new ArrayList<String>();
	public List<String> losers = new ArrayList<String>();
	public List<String> leaders = new ArrayList<String>(); // СДЕЛАТЬ ПРОВЕРКИ НА КОЛВА ИГРОКОВ 
	
	public int currentStage = 0;
	public String currentExam = "";
	public boolean canStart = true;
	
	public Map<String, String> answer = new HashMap<String, String>();
	public Map<String, Location> initialPositions = new HashMap<String, Location>();
	
	public static File file;
	public static FileConfiguration config;
	
	public ExamsChunins chunins;

	public ExamsHandler() {}
	
	public void setChunins(ExamsChunins chunins) 
	{
		this.chunins = chunins;
	}

	public static void enablePlugin()
	{
		file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "examsPositions.yml");      // ПОЧЕКАТЬ ЧТО С КОРДАМИ БАШНИ
		if(!file.exists())
		{
			NarutoCraft1.get().saveResource("examsPositions.yml", false);
			config = YamlConfiguration.loadConfiguration(file);
			/*config.createSection("genin");
			config.createSection("chunin");
			config.createSection("waitinRoom");
			config.createSection("arena");
			List<String> list = new ArrayList<String>(); 
			list.add("123,123,123");
			list.add("1337,1337,1337"); 
			config.set("chunin.1", list); */
			try 
			{
				config.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else config = YamlConfiguration.loadConfiguration(file);	
	}
	
	/*public void messageOnScreen(String nick, String message) 
	{
		NarutoCraft1.handler.sendPacketToPlayer(Bukkit.getPlayer(nick), new S0PacketSendTitle(message, -1));
	}
	
	public void messageOnScreenForFew(List<String> nicks, String message) 
	{
		for(String nick : nicks)
			NarutoCraft1.handler.sendPacketToPlayer(Bukkit.getPlayer(nick), new S0PacketSendTitle(message, -1));
	}*/
	
	public void setInitialPosition(String nick, Location location) 
	{
		initialPositions.put(nick, location);
	}
	
	public Location getInitialPosition(String nick) 
	{
		return initialPositions.get(nick);
	}
	
	public void teleportInitialPosition(String nick) 
	{
		message(nick, ChatColor.GREEN + "[Exams][INFO] Вас вернули на место, где вы были до экзамена");
		Bukkit.getPlayer(nick).teleport(getInitialPosition(nick));
	}
	
	public void setStage(int stage) 
	{
		this.currentStage = stage;
	}
	
	public int getStage() 
	{
		return currentStage;
	}
	
	public void setExam(String exam) 
	{
		this.currentExam = exam;
	}
	
	public String getExam() 
	{
		return currentExam;
	}
	
	public List<String> getMembers() 
	{
		return members;
	}
	
	public String getMember(int index) 
	{
		return members.get(index);
	}
	
	public List<String> getWinners() 
	{
		return winners;
	}
	
	public String getWinner(int index) 
	{
		return winners.get(index);
	}
	
	public List<String> getLosers() 
	{
		return losers;
	}
	
	public List<String> getLeaders() 
	{
		return leaders;
	}
	
	public String getLeader(int index) 
	{
		return leaders.get(index);
	}
	
	public void addMember(String member) throws IOException
	{
		members.add(member);
	}
	
	public void addWinner(String winner) throws IOException
	{
		if(!getWinners().contains(winner)) 
		{
			winners.add(winner);
			if(getStage() == 3) 
			{
				teleportInitialPosition(winner); // 
				getMembers().remove(winner);
			}
			else if(getStage() < 3 && getStage() > 0) 
			{
				if(!getLosers().isEmpty())
					waitinRoomTeleport(winner);
				else
					teleportInitialPosition(winner);
			}
		}
	}
	
	public void addWinners(List<String> team) throws IOException
	{
		for(String winnerNick : team) 
		{
			if(!getWinners().contains(winnerNick)) 
			{
				winners.add(winnerNick);
				if(getStage() == 3) 
				{
					teleportInitialPosition(winnerNick); // 
					getMembers().remove(winnerNick);
				}
				else if(getStage() < 3 && getStage() > 0) 
				{
					if(!getLosers().isEmpty()) 
					{
						waitinRoomTeleport(winnerNick);
					}
					else
					{
						getMembers().remove(winnerNick);
						teleportInitialPosition(winnerNick);
					}
				}
			}
		}
	}
	
	public void addLoser(String loser) throws IOException
	{
		if(getLosers().contains(loser))
			return;
		if(getStage() < 3 && getStage() > 0) 
		{
			for(String teammateNick : getTeam(loser)) 
			{
				losers.add(teammateNick);
				message(teammateNick, ChatColor.RED + "" + ChatColor.BOLD + "[Exams][INFO] Вы были исключены с экзамена!");
				
				if(leaders.contains(teammateNick)) 
					leaders.remove(teammateNick);
				if(members.contains(teammateNick))
					members.remove(teammateNick);
			
				teleportInitialPosition(teammateNick);
			}
		}
		else 
		{
			losers.add(loser);
			message(loser, ChatColor.RED + "" + ChatColor.BOLD + "[Exams][INFO] Вы были исключены с экзамена!");
			
			if(leaders.contains(loser)) 
				leaders.remove(loser);			
			if(members.contains(loser))
				members.remove(loser);

			teleportInitialPosition(loser);
		}
	}
	
	public void addLeader(String leader) 
	{
		leaders.add(leader);
	}
	
	public void setAnswer(String nick, String playerAnswer) 
	{
		answer.put(nick, playerAnswer);
	}
	
	public String getAnswer(String nick) 
	{
		return answer.get(nick);
	}
	
	public List<String> getTeam(String nick)
	{
		return new TeamsHandler(TeamsHandler.getPlayerTeam(nick)).getMembers();
	}
	
	private double getX(String nick) 
	{
		return Math.round(Bukkit.getPlayer(nick).getLocation().getX());
	}
	
	private double getY(String nick) 
	{
		return Math.round(Bukkit.getPlayer(nick).getLocation().getY());
	}
	
	private double getZ(String nick) 
	{
		return Math.round(Bukkit.getPlayer(nick).getLocation().getZ());
	}
	
	public boolean willTeamDie(String nick) 
	{
		Player player = Bukkit.getPlayer(nick);
		if(player.getInventory().contains(Material.PAPER) && player.getInventory().contains(Material.BOOK))
		{
			List<Location> towerCoords = new ArrayList<>();
			for(String coords : config.getStringList("chunin.tower"))
				towerCoords.add(parsin(coords));
			for(String teammateNick : getTeam(nick)) 
			{	
				if(getX(teammateNick) >= towerCoords.get(1).getX() && getX(teammateNick) <= towerCoords.get(0).getX() 
						&& getY(teammateNick) >= towerCoords.get(0).getY()  
						&& getZ(teammateNick) >= towerCoords.get(1).getZ() && getZ(teammateNick) <= towerCoords.get(0).getZ())
					return false;
			}
		}
		else
		{
			System.out.println("team don't have two types of scrolls");
			return true;
		}
		return false;
	}
	
	public Location parsin(String coords) 
	{
		List<World> worlds = Bukkit.getWorlds();
		if(worlds.isEmpty())
		{
			System.out.println("no worlds yet");
			return null;
		}
		
		String[] splittedCoords = coords.split(",", 3);
		double x = Double.parseDouble(splittedCoords[0]); 
		double y = Double.parseDouble(splittedCoords[1]); 
		double z = Double.parseDouble(splittedCoords[2]);
		return new Location(worlds.get(0), x, y, z);
	}
	
	public void teleport(Location location, String nick) 
	{
		Bukkit.getPlayer(nick).teleport(location);
	}
	
	public void stageTeleport() throws IOException
	{
		if(getMembers().isEmpty()) 
		{
			System.out.println("members are empty");
			return;
		}
		
		switch(getStage()) 
		{
		case 1:
			for(int i = 0; i < getMembers().size(); i++)
				teleport(parsin(config.getStringList("chunin.1").get(i)), getMember(i));
			break;
		case 2:
			for(String leaderNick : getLeaders()) 
			{
				for(String teammateNick : getTeam(leaderNick))
					teleport(parsin(config.getStringList("chunin.2").get(getLeaders().indexOf(leaderNick))), teammateNick);
			}
			break;
		case 3:
			for(int i = 0; i < getMembers().size(); i++) 
			{
				if(config.getStringList("chunin.3").size() < getMembers().size()) 
				{
					if(i == getMembers().size() - 1) 
						i = 0;
				}
				teleport(parsin(config.getStringList("chunin.3").get(i)), getMember(i));
			}
			break;
		default: break;
		}
	}
	
	public List<Location> stageCoords(int stage)
	{
		List<Location> locations = new ArrayList<Location>();
		for(String coords : config.getStringList("chunin." + stage))
			locations.add(parsin(coords));
		return locations;
	}
	
	public void waitinRoomTeleport(String nick) 
	{
		Bukkit.getPlayer(nick).sendMessage(ChatColor.GREEN + "[Exams][INFO] Вы помещены в комнату ожидания!");
		teleport(parsin(config.getStringList("waitinRoom.1").get(0)), nick);
	}
	
	public void arenaTeleport() 
	{
		for(int i = 0; i < chunins.opponents.length; i++) 
			teleport(parsin(config.getStringList("arena.1").get(i)), chunins.opponents[i]);
	}
	
	public void messageForFew(List<String> list, String message) 
	{
		for(String nick : list) 
			Bukkit.getPlayer(nick).sendMessage(message);
	}
	
	public void messageForFew(Player[] list, String message) 
	{
		for(Player player : list) 
			player.sendMessage(message);
	}
	
	public void messageForFew(String[] list, String message) 
	{
		for(String nick : list)
			Bukkit.getPlayer(nick).sendMessage(message);		
	}
	
	public void message(String nick, String message) 
	{
		Bukkit.getPlayer(nick).sendMessage(message);
	}
	
	public void message(Player player, String message) 
	{
		player.sendMessage(message);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) throws IOException 
	{		
		if(getStage() != 0 ) 
		{
			if(event.getEntity() instanceof Player) 
			{
				Player player = (Player)event.getEntity();
				String nick = player.getName();
				
				if(getLosers().contains(nick)) 
					return;
				
				if(!getMembers().contains(nick) && chunins.opponents[0] != nick || !getMembers().contains(nick) && chunins.opponents[1] != nick)
					return;
				
				addLoser(nick);
				
				if(getStage() == 3) 
				{
					if(chunins.opponents[1].equals(nick)) 
						addWinner(chunins.opponents[0]);
					
					else if(chunins.opponents[0].equals(nick)) 
						addWinner(chunins.opponents[1]);
					
					return;
				}
				
				for(String teammateNick : getTeam(nick)) 
				{
					Player teammate = Bukkit.getPlayer(teammateNick);
					if(teammate.getInventory().contains(Material.BOOK) || teammate.getInventory().contains(Material.PAPER))		
					{
						for(ItemStack is : teammate.getInventory()) 
						{
							if(is.getType().equals(Material.BOOK) || is.getType().equals(Material.PAPER)) 
							{
								teammate.getWorld().dropItem(teammate.getLocation(), is);
								teammate.getInventory().removeItem(is);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent event) throws IOException
	{
		if(getStage() != 2)
			return;
		
		Player player = event.getPlayer();
		String nick = player.getName();
		Action action = event.getAction();
		
		if(action.equals(Action.RIGHT_CLICK_AIR) ||action.equals(Action.RIGHT_CLICK_BLOCK))
		{
			if(members.contains(nick) && player.getItemInHand().getType().equals(Material.BOOK) || player.getItemInHand().getType().equals(Material.PAPER)) 
			{ 
				/*if(willTeamDie(nick))
				{
					for(String teammateNick : getTeam(nick)) 
						Bukkit.getPlayer(teammateNick).setHealth(0.0D);
				}
				else
				{
					for(String teammateNick : getTeam(nick)) 
						addWinner(teammateNick);
				} */
				if(player.getInventory().contains(Material.PAPER) && player.getInventory().contains(Material.BOOK))
				{
					List<Location> towerCoords = new ArrayList<>();
					for(String coords : config.getStringList("chunin.tower"))
						towerCoords.add(parsin(coords));
					for(String teammateNick : getTeam(nick)) 
					{	
						if(getX(teammateNick) >= towerCoords.get(1).getX() && getX(teammateNick) <= towerCoords.get(0).getX() 
								&& getY(teammateNick) >= towerCoords.get(0).getY()  
								&& getZ(teammateNick) >= towerCoords.get(1).getZ() && getZ(teammateNick) <= towerCoords.get(0).getZ())
							addWinners(getTeam(nick));
					}
				}
				else
				{
					System.out.println("team don't have two types of scrolls");
					for(String teammateNick : getTeam(nick)) 
						Bukkit.getPlayer(teammateNick).setHealth(0.0D);
				}
			}
		}
	}
	
	@EventHandler
	public void playerChatin(AsyncPlayerChatEvent event)  // хули не робишь
	{
		Player player = event.getPlayer();
		if(getStage() == 1 && members.contains(player.getName())) 
		{
		    System.out.println(event.getMessage());
		    player.sendMessage(ChatColor.RED + "[Exams][INFO] Тихо! А если экзаменатор услышит?");
		    event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void scrollDroppin(PlayerDropItemEvent event) 
	{
		if(getStage() == 2 && getExam().equals("chunin")) 
		{
	        if(event.getItemDrop().getItemStack().getType().equals(Material.PAPER) || event.getItemDrop().getItemStack().getType().equals(Material.BOOK)) 
	            event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerLogin(PlayerLoginEvent event) 
	{
		Player player = event.getPlayer();
		if(losers.contains(player.getName())) 
		{
			player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "[Exams][INFO] Вы были исключены с экзамена!");
		}
	}
	
	@EventHandler
	public void playerQuitin(PlayerQuitEvent event) throws IOException 
	{
		Player player = event.getPlayer();
		String nick = player.getName();
		
		if(getMembers().contains(nick)) 
		{
			addLoser(nick);
		}
		else if(chunins.opponents[0].equals(nick)) 
		{			
			addLoser(nick);
			chunins.opponents[0] = "";
		}
		else if(chunins.opponents[1].equals(nick))
		{
			addLoser(nick);
			chunins.opponents[1] = "";
		}
	}
	
	@EventHandler
	public void playerMovin(PlayerMoveEvent event) 
	{
		if(getStage() == 0)
			return;
		
		Player player = event.getPlayer(); // ебануть условия
		String nick = player.getName();
		
		if(!getMembers().contains(nick) && chunins.opponents[0] != nick || !getMembers().contains(nick) && chunins.opponents[1] != nick || !getMembers().contains(nick) && getStage() != 3)
			return; 
		
		Location location = player.getLocation();
		double radius;
		int points;
		Location origin;
		
		switch(getStage()) 
		{
		case 1:
			player.sendMessage("виталя 1");
			if(location.getX() < -744 || location.getX() > -728 || location.getY() > 61 || location.getY() < 53 || location.getZ() > -1085 || location.getZ() < -1100) 
			{
				player.sendMessage(getStage() + " че за хуйня");
				try {
					addLoser(player.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case 2:
			player.sendMessage("виталя 2");
			radius = 201d;
			points = (int) Math.round(Math.PI * radius);
			origin = new Location(player.getWorld(), -150, 0, -1069);
			for (int i = 0; i < points; i++) 
			{
			    double angle = 2 * Math.PI * i / points;
			    Location point = origin.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
			    
			    if(point.getX() < -150) // z -869 -1279 x -350 52 | mid -150 -1069
			    {
			    	if(location.getX() < point.getX()) 
			    	{
			    		player.sendMessage("1");
			    		try {
							addLoser(nick);
						} catch (IOException e) {
							e.printStackTrace();
						}
			    	}
			    }
			    else 
			    {
			    	if(location.getX() > point.getX()) 
			    	{
			    		player.sendMessage("2");
			    		try {
							addLoser(nick);
						} catch (IOException e) {
							e.printStackTrace();
						}
			    	}
			    }
			    
			    if(point.getZ() < -1069) 
			    {
			    	if(location.getZ() < point.getZ()) 
			    	{
			    		player.sendMessage("3");
			    		try {
							addLoser(nick);
						} catch (IOException e) {
							e.printStackTrace();
						}
			    	}
			    }
			    else 
			    {
			    	if(location.getZ() > point.getZ()) 
			    	{
			    		player.sendMessage("4");
			    		try {
							addLoser(nick);
						} catch (IOException e) {
							e.printStackTrace();
						}
			    	}
			    }
			}
			break;
		case 3: // Z -1058 -996 X -1274 -1212
			radius = 62d;
			points = (int) Math.round(Math.PI * radius);
			origin = new Location(player.getWorld(), -150, 0, -1069);
			for (int i = 0; i < points; i++) 
			{
			    double angle = 2 * Math.PI * i / points;
			    Location point = origin.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
			    
			    if(point.getX() < -150) // Z -1058 -996 X -1274 -1212 MID -1243 -1027 
			    {
			    	if(location.getX() < point.getX()) 
			    	{
			    		try {
							addLoser(nick);
						} catch (IOException e) {
							e.printStackTrace();
						}
			    	}
			    }
			    else 
			    {
			    	if(location.getX() > point.getX()) 
			    	{
			    		try {
							addLoser(nick);
						} catch (IOException e) {
							e.printStackTrace();
						}
			    	}
			    }
			    
			    if(point.getZ() < -1069) 
			    {
			    	if(location.getZ() < point.getZ()) 
			    	{
			    		try {
							addLoser(nick);
						} catch (IOException e) {
							e.printStackTrace();
						}
			    	}
			    }
			    else 
			    {
			    	if(location.getZ() > point.getZ()) 
			    	{
			    		try {
							addLoser(nick);
						} catch (IOException e) {
							e.printStackTrace();
						}
			    	}
			    }
			}
			
			// доступ на арену
			if(chunins.opponents[0].equals(nick) || chunins.opponents[1].equals(nick))
				return;
			
			if(location.getX() > 123 && location.getX() < 123 && location.getY() > 123 && location.getZ() > 123 && location.getZ() < 123) 
			{
				try {
					addLoser(nick);
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
			break;			
		default: 
			break;
		}
	}
}
