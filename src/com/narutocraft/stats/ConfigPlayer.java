package com.narutocraft.stats;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.narutocraft.donate.EnumDonate;
import com.narutocraft.exp.EnumLevels;
import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.network.S0PacketSendTitle;
import com.narutocraft.network.S0PacketSetHudStats;
import com.narutocraft.util.ListHelper;

public class ConfigPlayer {
	
	private static File folder = new File(NarutoCraft1.get().getDataFolder() + File.separator + "players");
	
	public String namePlayer;
	
	private File file;
	private FileConfiguration config;
	
	public ConfigPlayer(String name)
	{
	    namePlayer = name;	
	    file = new File(folder + File.separator + namePlayer + ".yml");
	    config = YamlConfiguration.loadConfiguration(file);
	}
	
	public static void enablePlugin()
	{
		if(!folder.exists())
		{
			folder.mkdir();
		}
	}
	
	public boolean isExists()
	{
		return file.exists();
	}
	
	public int getExp()
	{
		return config.getInt("stats.exp");
	}
	
	public int getLvl()
	{
		return config.getInt("stats.lvl");
	}
	
	public int getLvlExp()
	{
		return config.getInt("stats.lvlexp");
	}
	
	public void addExp(int exp) throws IOException
	{
		config.set("stats.exp", getExp() + exp);
		config.save(file);
		
		Player p = Bukkit.getPlayer(namePlayer);

		Bukkit.getLogger().info("SUCCESSFULY");
		
		int currentExp = getExp();
		NarutoCraft1.handler.sendPacketToPlayer(p, new S0PacketSetHudStats("" + 3, "" + currentExp));
		
		while(EnumLevels.values()[getLvl() + 1].getExp() <= currentExp)
		{
			addLvl(1);
			NarutoCraft1.handler.sendPacketToPlayer(p, new S0PacketSendTitle("[�� ������� �������� �������]", -1));
			NarutoCraft1.handler.sendPacketToPlayer(p, new S0PacketSetHudStats("" + 2, "" + EnumLevels.values()[getLvl()].getExp()));
			NarutoCraft1.handler.sendPacketToPlayer(p, new S0PacketSetHudStats("" + 4, "" + EnumLevels.values()[getLvl() + 1].getExp()));
		}
		
	}
	
	public void addMoney(int count) throws IOException
	{
		config.set("stats.money", getMoney() + count);
		config.save(file);
	}
	
	public void takeMoney(int count) throws IOException
	{
		config.set("stats.money", (getMoney() - count < 0 ? 0 : getMoney() - count));
		config.save(file);
	}
	
	public int getChakra()
	{
		return config.getInt("stats.chakra");
	}
	
	public void addStartMessage(String name, String message) throws IOException
	{
		config.set("joins.messages." + name, message);
		config.save(file);
	}
	
	public void addLvl(int lvl) throws IOException
	{
		config.set("stats.lvl", getLvl() + lvl);
		config.set("stats.lvlexp", getLvlExp() + lvl);
		config.save(file);
	}
	
	public String getJobString()
	{
		return config.getString("stats.job");
	}
	
	public String getVillage()
	{
		return config.getString("stats.village");
	}
	
	public void setJob(String job) throws IOException
	{
		config.set("stats.job", job);
		config.save(file);
	}
	
	public void addStartCommand(String name, String command) throws IOException
	{
		config.set("joins.commands." + name, command);
		config.save(file);
	}
	
	public void removeStartMessage(String name) throws IOException
	{
		config.set("joins.messages." + name, null);
		config.save(file);
	}
	
	public void removeStartCommand(String name) throws IOException
	{
		config.set("joins.commands." + name, null);
		config.save(file);
	}
	
	public int getMaxChakra()
	{
		return config.getInt("stats.maxchakra");
	}
	
	public int getMoney()
	{
		return config.getInt("stats.money");
	}
	
	public void addChakra(int chakra) throws IOException
	{
		config.set("stats.chakra", getChakra() + chakra > getMaxChakra() ? getMaxChakra() : getChakra() + chakra);
		config.save(file);
	}
	
	public void restoreChakra(int chakra) throws IOException
	{
		config.set("stats.chakra", getChakra() - chakra < 0 ? 0 : getChakra() - chakra);
		config.save(file);
	}
	
	public void changeLang()
	{
		if(config.getString("language").equalsIgnoreCase("ru_RU"))
		{
			config.set("language", "en_EN");
			try {
				config.save(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		config.set("language", "ru_RU");
		try {
			config.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getLang()
	{
		return config.getString("language");
	}
	
	public void addDonate(String donate) throws IOException
	{
		ListHelper<String> list = new ListHelper<String>();
		config.set("donate", list.addKtoList(config.getStringList("donate"), donate));
		config.save(file);
	}
	
	public boolean checkDonate(EnumDonate donate)
	{
		return config.getStringList("donate").contains(donate.toString());
	}
	
	public boolean checkDonate(String donate)
	{
		return config.getStringList("donate").contains(donate);
	}
	
	public int getDonatePower(String donate)
	{
		return EnumDonate.valueOf(donate).power;
	}
	
	public int getDonatePower(EnumDonate donate)
	{
		return donate.power;
	}
	
	public List<String> getDonate()
	{
		return config.getStringList("donate");
	}
	
	public static File getConfigPlayer(String name) throws IOException
	{
		File config = new File(folder + File.separator + name + ".yml");
		return config;
	}
	
}