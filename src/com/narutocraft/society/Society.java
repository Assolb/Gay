package com.narutocraft.society;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.stats.ConfigPlayer;

public class Society{
	
	private File file;
	private FileConfiguration config;
	private static final File folder = new File(NarutoCraft1.get().getDataFolder() + File.separator + "society");
	
	public String name;
	
	public Society(String name)
	{
		file = new File(folder + File.separator + name + ".yml");
		config = YamlConfiguration.loadConfiguration(file);
		this.name = name;
	}
	
	public boolean checkExists()
	{
		return name.equalsIgnoreCase("none") ? false : file.exists();
	}
	
	public static void enablePlugin()
	{
		if(folder.exists())
		{
			folder.mkdir();
		}
	}
	
	public void create() throws IOException
	{
		if(!checkExists())
		{
			file.createNewFile();
			config.createSection("main");
			config.set("main." + "owner", "none");
			config.set("main.lvl", 1);
			config.set("main.balance", 0);
			config.createSection("players");
			config.createSection("invites");
			config.createSection("log");
			config.save(file);
		}
	}
	
	public void remove()
	{
		if(checkExists())
		{
			file.delete();
		}
	}
	
	public boolean checkPlayer(String player)
	{
		Set<String> s = config.getConfigurationSection("players").getKeys(false);
		for(String j : s)
		{
			if(player.equalsIgnoreCase(j)) return true;
		}
		return false;
	}
	
	public boolean checkPlayerInInvites(String player)
	{
		Set<String> s = config.getConfigurationSection("invites").getKeys(false);
		for(String j : s)
		{
			if(player.equalsIgnoreCase(j)) return true;
		}
		return false;
	}
	
	public int getLvl()
	{
		return config.getInt("main.lvl");
	}
	
	public int getMoney()
	{
		return config.getInt("main.money");
	}
	
	public void kickPlayer(String player) throws IOException
	{
		if(!checkPlayer(player))
		{
			return;
		}
		
		config.set("players." + player, null);
		config.save(file);
	}
	
	public void invitePlayer(String player, String invitor) throws IOException
	{
		if(checkPlayerInInvites(player))
		{
			return;
		}
		
		config.set("invites." + player, invitor);
		config.save(file);
		
	}
	
	public List<String> getMembers()
	{
		List<String> list = new ArrayList<String>();
		list.addAll(config.getConfigurationSection("players").getKeys(false));
		
		Collections.sort(list, new Comparator<String>() {
			public int compare(String o1, String o2) {
				if(config.getString("players." + o1).equalsIgnoreCase("owner")) return -1;
				if(config.getString("players." + o2).equalsIgnoreCase("owner")) return 1;
				return config.getString("players." + o1).compareTo("players." + o2);
			} 
		});
		
		return list;
	}
	
	public static List<String> getMembers(String society)
	{
		Society s = new Society(society);
		
		List<String> list = new ArrayList<String>();
		
		if(s.checkExists())
		{
			list.addAll(s.config.getConfigurationSection("players").getKeys(false));
		}
		
		return list;
	}
	
	public List<String> getOnlineMembers()
	{	
		List<String> players = getMembers();
		List<String> online = new ArrayList<String>();
		
		for(String j : players)
		{
			if(Bukkit.getPlayer(j) == null) continue;
			online.add(j);
		}
		
		return online;
	}
	
	public String getOwner(String society)
	{
		Society s = new Society(society);
		
		if(!s.checkExists())
		{
			return "none";
		}
		
		return s.config.getString("main.owner");
	}
	
	public String getOwner()
	{
		
		if(checkExists())
		{
			return "none";
		}
		
		return config.getString("main.owner");
	}
	
	public static void setOwner(String society, String player, String adm) throws IOException
	{
		Society s = new Society(society);
		
		if(!s.checkExists())
		{
			return;
		}
		
		
		if(!s.getOwner(society).equalsIgnoreCase("none"))
		{
			ConfigPlayer config = new ConfigPlayer(s.getOwner());
			
			Player l = Bukkit.getPlayer(s.getOwner());
			
			if(l == null) config.addStartMessage("changeOwner", ChatColor.GOLD + "[INFO] " + ChatColor.RED + "Администратор " + ChatColor.GOLD + adm + " " + ChatColor.RED + " передал статус главы вашей организации игроку " + ChatColor.GOLD + player);
			else l.sendMessage(ChatColor.GOLD + "[INFO] " + ChatColor.RED + "Администратор " + ChatColor.GOLD + adm + " " + ChatColor.RED + " передал статус главы вашей организации игроку " + ChatColor.GOLD + player);
			s.config.set("players." + new Society(society).getOwner(), "member");
		}
		
		s.config.set("main.owner", player);
		s.config.set("players." + player, "owner");
		
		s.config.save(s.file);
	}
	
	public static String getSociety(String player)
	{
		File file;
		FileConfiguration config;
		
		File[] files = folder.listFiles();
		for(File f : files)
		{	
			file = f;
			config = YamlConfiguration.loadConfiguration(file);
			
			List<String> s = new ArrayList<String>();
			s.addAll(config.getConfigurationSection("players").getKeys(false));
			
			for(int i = 0; i < s.size(); i++)
			{
				if(player.equalsIgnoreCase(s.get(i))) return f.getName().replace(".yml", "");
			}
		}
		
		return "none";
	}
	
	public String getRolePlayer(String player)
	{
		if(!checkExists())
		{
			return "none";
		}
		
		if(!checkPlayer(player))
		{
			return "none";
		}
		
		return config.getString("players." + player);
	}
	
	public void promotePlayer(String player) throws IOException
	{
		String rank = getRolePlayer(player);
		if(!rank.equalsIgnoreCase("member")) return;
		
		config.set("players." + player , "officer");
		config.save(file);
	}
	
	public void demotePlayer(String player) throws IOException
	{
		String rank = getRolePlayer(player);
		if(rank.equalsIgnoreCase("member")) return;
		
		config.set("players." + player , "member");
		config.save(file);
	}
	
	public static List<String> getInvitedSocietys(String p)
	{
		File file;
		FileConfiguration config;
		
		List<String> list = new ArrayList<String>();
		
		File[] files = folder.listFiles();
		for(File f : files)
		{
			file = f;
			config = YamlConfiguration.loadConfiguration(file);
			
			List<String> s = new ArrayList<String>(); 
			s.addAll(config.getConfigurationSection("invites").getKeys(false));
			
			for(int i = 0; i < s.size(); i++)
			{
				if(p.equalsIgnoreCase(s.get(i))) list.add(f.getName().replace(".yml", ""));
			}
		}
		
		return list;
	}
	
	public void leave(String player) throws IOException
	{
		config.set("players." + player, null);
		config.save(file);
	}
	
	public void accept(String name) throws IOException
	{
		config.set("invites." + name, null);
		config.set("players." + name, "member");		
		config.save(file);
	}
	
	public void decline(String name) throws IOException
	{
		config.set("invites." + name, null);
		
		config.save(file);
	}
	
	public void log(String log)
	{
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd:MM:yyyy-HH:mm:ss");
		config.set("log.[" + format.format(now) + "]", log);
		try
		{
			config.save(file);
		}
		catch(IOException e){}
	}
}
