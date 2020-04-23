package com.narutocraft.banks;

import java.io.File;
import java.io.IOException;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.village.Village;

public class NationalBank implements Listener {

	public static File file;
	public static FileConfiguration config;
	
	public static String village;
	
	public static void enablePlugin()
	{
		file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "nationalBank.yml");      // ПОЧЕКАТЬ ЧТО С КОРДАМИ БАШНИ
		if(!file.exists())
		{
			NarutoCraft1.get().saveResource("nationalBank.yml", false);
			config = YamlConfiguration.loadConfiguration(file);
			
			config.createSection("accounts");
			config.createSection("treasures");
			config.createSection("course");
			
			config.set("course.konohagakure", 10);
			config.set("course.kirigakure", 10);
			config.set("course.kumogakure", 10);
			config.set("course.sunagakure", 10);
			config.set("course.iwagakure", 10);
			
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
		if(!config.getStringList("treasures").contains(village)) 
			addMoneyToBankTreasury(1000000);
	}
	
	public NationalBank(String village) 
	{
		this.village = village;
	}
	
	public static double getBankTreasury() 
	{		
		return config.getDouble("treasures." + village);		
	}
	
	public static void addMoneyToBankTreasury(double money) 
	{
		config.set("treasures." + village, getBankTreasury() + money); 
	}
	
	public boolean removeMoneyFromBankTreasury(double money) 
	{
		if(getBankTreasury() > money) 
		{
			config.set("treasures." + village, getBankTreasury() - money);
			return true;
		}
		return false;
	}
	
	private double getSum(String nick, String village) 
	{
		double sum = 0;
		for(String valuta : config.getStringList("accounts." + village + "." + nick)) 
		{
			if(valuta != "ien") 
				sum = sum + (config.getDouble("accounts." + village + "." + nick + "." + valuta) * 10);
			else
				sum = sum + config.getDouble("accounts." + village + "." + nick + "." + valuta);
		}
		
		return sum;
	}
	
	public double getMoneyOnAccount(String nick, String valuta) 
	{
		return config.getDouble("accounts." + village + "." + nick + "." + valuta);
	}
	
	public void addMoneyOnAccount(String nick, String valuta, double count) 
	{
		if(getMoneyOnAccount(nick, valuta) < 0) 
		{
			if(getMoneyOnAccount(nick, valuta) + count >= 0) 
			{
				if(valuta == "ien")
					addMoneyToBankTreasury(count - (0 - getMoneyOnAccount(nick, valuta)));
				else
					addMoneyToBankTreasury(count - (0 - getMoneyOnAccount(nick, valuta)) * getValutaCourse(valuta));
			}
			else 
			{
				if(valuta == "ien")
					addMoneyToBankTreasury(count);
				else
					addMoneyToBankTreasury(count * getValutaCourse(valuta));
			}
		}
		else 
		{
			if(!removeMoneyFromBankTreasury(count))
			{
				System.out.println("[NationalBank] boolean removeMoneyFromBankTreasury is false");
				return;
			}
		}
		
		config.set("accounts." + village + "." + nick + "." + valuta, getMoneyOnAccount(nick, valuta) + count);
	}
	
	public void removeMoneyFromAccount(String nick, String valuta, double count) 
	{
		config.set("accounts." + village + "." + nick + "." + valuta, getMoneyOnAccount(nick, valuta) - count);
		// сообщение полиции о долгах
	}
	
	public static double getValutaCourse(String valuta) // сделать на процентах от казны
	{	
		return config.getDouble("course." + valuta);		// ПЕРЕПИСАТЬ В ВАЛЮТЕ НА НАЗВАНИЕ ДЕРЕВЕНЬ ПО ТИПУ KONOHAGAKURE
	}
	
	public void everyHour() 
	{
		BukkitRunnable thread = new BukkitRunnable() 
		{
			public void run() 
			{
				GregorianCalendar calendar = new GregorianCalendar();
				for(;;) 
				{
					if(calendar.get(Calendar.MINUTE) == 0) continue;
					
					for(String nick : config.getStringList("accounts." + village)) 
					{
						if(Bukkit.getPlayer(nick) != null) continue; 
						
						for(String valuta : config.getStringList("accounts." + village + "." + nick)) 
						{
							if(getMoneyOnAccount(nick, valuta) > 0) 
							{
								addMoneyOnAccount(nick, valuta, getMoneyOnAccount(nick, valuta) / 10000);
							}
							else if(getMoneyOnAccount(nick, valuta) < 0)
							{
								removeMoneyFromAccount(nick, valuta, getMoneyOnAccount(nick, valuta) / 1000);
							}
						}
					}
					
					for(String valuta : config.getStringList("course"))
						config.set("course." + valuta, new Village(valuta).getResourceFromTreasure("money") / 100000); 
					
					try {
						Thread.sleep(3540000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}; thread.runTask(NarutoCraft1.get());
	}
	
	public void createAccount(String nick) 
	{
		config.set("accounts." + village + "." + nick + ".konoha", 0);
		config.set("accounts." + village + "." + nick + ".kiri", 0);
		config.set("accounts." + village + "." + nick + ".kumo", 0);
		config.set("accounts." + village + "." + nick + ".suna", 0);
		config.set("accounts." + village + "." + nick + ".iwa", 0);
		config.set("accounts." + village + "." + nick + ".ien", 0);
	}
}
