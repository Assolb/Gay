package com.narutocraft.village;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import com.narutocraft.banks.NationalBank;
import com.narutocraft.main.NarutoCraft1;

public class Village {
	
	public static File file;
	public static FileConfiguration config;
	
	public static String village;
	
	public static NationalBank bank = new NationalBank(village);
	
	public static void enablePlugin()
	{
		file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "villages.yml");      
		if(!file.exists())
		{
			NarutoCraft1.get().saveResource("villages.yml", false);
			config = YamlConfiguration.loadConfiguration(file);
			
			config.createSection("villages");
			config.createSection("treasures");	
			config.createSection("kages");	
			config.createSection("villagers");
			config.createSection("diplomacy");
			config.createSection("kageOffers");
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else config = YamlConfiguration.loadConfiguration(file);
		
		for(String village : config.getStringList("villages")) 
		{
			if(!config.getStringList("treasures").contains(village)) 
			{
				config.set("treasures." + village + ".money", 1000000);
				config.set("treasures." + village + ".equipment", 0);
				config.set("treasures." + village + ".food", 0);
				config.set("treasures." + village + ".fossils", 0);
			}
		}
			
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public Village(String village) 
	{
		this.village = village;
	}

	public double getResourceFromTreasure(String resource)
	{		
		return config.getDouble("treasures." + village + "." + resource);
	}
	
	public void addResourceToTreasure(String resource, double count) 
	{
		config.set("treasures." + village + "." + resource, getResourceFromTreasure(resource) + count);
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean removeResourceFromTreasure(String resource, double count) 
	{
		if(getResourceFromTreasure(resource) - count < 0)
			return false;		
		
		config.set("treasures." + village + "." + resource, getResourceFromTreasure(resource) - count);
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public String getKage() 
	{
		return config.getString("kages." + village);
	}
	
	public void setKage(String nick) 
	{
		config.set("kages." + village, nick);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void registerVillager(String nick) 
	{
		bank.createAccount(nick);
		removeResourceFromTreasure("money", 1000);
		bank.addMoneyOnAccount(nick, "ien", 1000);
		config.set("villagers." + village, nick);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean tradeWithVillage(String secondVillage, String firstVillageResource, String secondVillageResource, double firstVillageResourceCount, double secondVillageResourceCount) 
	{
		if(config.getDouble("treasury." + village + "." + firstVillageResource) >= firstVillageResourceCount) 
		{
			if(config.getDouble("treasury." + secondVillage + "." + secondVillageResource) >= secondVillageResourceCount) 
			{
				final int size = config.getStringList("kageOffers." + secondVillage).size();
				config.set("kageOffers." + secondVillage + "." + size + ".offerType", "trade");
				config.set("kageOffers." + secondVillage + "." + size + ".givingVillage", this.village);
				config.set("kageOffers." + secondVillage + "." + size + ".givingVillageResource", firstVillageResource);
				config.set("kageOffers." + secondVillage + "." + size + ".receivingVillageResource", secondVillageResource);
				config.set("kageOffers." + secondVillage + "." + size + ".givingVillageResource", firstVillageResourceCount);
				config.set("kageOffers." + secondVillage + "." + size + ".receivingVillageResource", secondVillageResourceCount);
				
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return true;
			}
		}		
		return false;
	}
	
	public void setDiplomacy(String village, String diplomacy) 
	{
		for(String diplomacyType : config.getStringList("diplomacy." + this.village)) 
		{
			if(config.getStringList("diplomacy." + this.village + "." + diplomacyType).contains(village))
				config.set("diplomacy." + this.village + "." + diplomacyType, config.getStringList("diplomacy." + this.village + "." + diplomacyType).remove(village));
		}
		
		config.set("diplomacy." + this.village + "." + diplomacy, config.getStringList("diplomacy." + this.village + "." + diplomacy).add(village));
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getDiplomacy(String village) 
	{
		for(String diplomacyType : config.getStringList("diplomacy." + this.village)) 
		{
			if(config.getStringList("diplomacy." + this.village + "." + diplomacyType).contains(village))
				return diplomacyType;
		}
		
		return null;
	}
}
