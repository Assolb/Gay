package com.narutocraft.wallet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.narutocraft.banks.NationalBank;
import com.narutocraft.main.NarutoCraft1;

public class WalletHandler implements Listener {
	
	public static File file;
	public static FileConfiguration config;
	
	public static void enablePlugin()
	{
		file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "wallets.yml");   
		if(!file.exists())
		{
			NarutoCraft1.get().saveResource("wallets.yml", false);
			config = YamlConfiguration.loadConfiguration(file);
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
	
	private double getLimit() 
	{
		return 2000;
	}
	
	private double getValutaCourse(String valuta) 
	{	
		return NationalBank.config.getDouble("course." + valuta);		// ПЕРЕПИСАТЬ В ВАЛЮТЕ НА НАЗВАНИЕ ДЕРЕВЕНЬ ПО ТИПУ KONOHAGAKURE
	}
	
	private double getSum(String nick, ItemStack wallet) 
	{
		double sum = 0.0;
		
		for(String valuta : NationalBank.config.getStringList("valuta"))
			sum = sum + config.getDouble(nick + "." + wallet + "." + valuta) * getValutaCourse(valuta);
		
		sum = sum + config.getDouble(nick + "." + wallet + ".ien");		
		return sum;
	}
	
	private String moneyType(String valuta) 
	{
		switch(valuta) 
		{
		case "konohagakure":
			return ChatColor.DARK_RED + " Рё Страны Огня";
		case "kirigakure":
			return ChatColor.BLUE + " Рё Страны Воды";
		case "kumogakure":
			return ChatColor.YELLOW + " Рё Страны Молнии";
		case "sunagakure":
			return ChatColor.GOLD + " Рё Страны Ветра";
		case "iwagakure":
			return ChatColor.DARK_GRAY + " Рё Страны Земли";
		case "ien":
			return ChatColor.DARK_GREEN + " Иен";
		default: break;
		}
		
		return null;
	}
	
	private String moneyType(String valuta, double count) 
	{
		switch(valuta) 
		{
		case "konohagakure":
			return ChatColor.DARK_RED + "" + count + " Рё Страны Огня";
		case "kirigakure":
			return ChatColor.BLUE + "" + count + " Рё Страны Воды";
		case "kumogakure":
			return ChatColor.YELLOW + "" + count + " Рё Страны Молнии";
		case "sunagakure":
			return ChatColor.GOLD + "" + count + " Рё Страны Ветра";
		case "iwagakure":
			return ChatColor.DARK_GRAY + "" + count + " Рё Страны Земли";
		case "ien":
			return ChatColor.DARK_GREEN + "" + count + " Иен";
		default: break;
		}
		return null;
	}
	
	public void addMoney(String sender, ItemStack senderWallet, String receiver, String valuta, double money) 
	{
		double radius = 2.0D;
        double radiusSquared = radius*radius;
        
        List<Entity> entities = Bukkit.getPlayer(sender).getNearbyEntities(radius, radius, radius); 
        
        if(!entities.contains(Bukkit.getPlayer(receiver)))
        {
        	Bukkit.getPlayer(sender).sendMessage(ChatColor.RED + "[Wallet] Человек, которому вы хотите передать деньги, находится далеко от вас");
        	return;
        }
        
        for (Entity entity : entities) 
        {        
            if(entity.getLocation().distanceSquared(Bukkit.getPlayer(sender).getLocation()) > radiusSquared) continue; 
 
            if(entity instanceof Player) continue;

            if(entity == Bukkit.getPlayer(receiver)) 
            {
            	if(config.getDouble(sender + "." + senderWallet + valuta) >= money) 
            	{
            		boolean success = false;
            		for(String s : config.getConfigurationSection("config.itemstacks").getKeys(false))
            		{
            			ItemStack is = config.getItemStack(receiver);
            			if((config.getDouble(receiver + "." + is + "." + "." + valuta) + money * 10 < getLimit()) && valuta != "ien" || 
            					(config.getDouble(receiver + "." + is + "." + "." + valuta) + money < getLimit()) && valuta == "ien") 
            			{
        					config.set(sender + "." + senderWallet + "." + valuta, config.getDouble(sender + "." + senderWallet + "." + valuta) - money);
        					Bukkit.getPlayer(receiver).sendMessage(ChatColor.GREEN + "[Wallet] " + sender + " передал вам " + money + moneyType(valuta));
        					Bukkit.getPlayer(receiver).sendMessage(ChatColor.GREEN + "[Wallet] Вы передали " + receiver + " " + money + moneyType(valuta));
            				break;
            			}
            		}       
            		
            		if(!success)
            			Bukkit.getPlayer(sender).sendMessage(ChatColor.RED + "[Wallet] У человека, которому вы хотите передать деньги, нет места для денег");
            	}
            	else 
            	{
            		Bukkit.getPlayer(sender).sendMessage(ChatColor.RED + "[Wallet] У вас недостаточно денег в кошельке");
            	}
            }
        }
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();
		String nick = player.getName();
		Action action = event.getAction();
		
		if(action.equals(Action.RIGHT_CLICK_AIR) ||action.equals(Action.RIGHT_CLICK_BLOCK)) 
		{	
			if(player.getItemInHand().getType() == Material.getMaterial(4483)) // id 
			{ 
				ItemStack item = player.getItemInHand();
				if(!config.contains(nick)) 
				{
					config.createSection(nick);	
					for(String valuta : NationalBank.config.getStringList("valuta"))
						config.set(nick + "." + item + "." + valuta, 0);
				}
								
				player.sendMessage(ChatColor.LIGHT_PURPLE + "[Wallet] У вас в кошельке:");
				
				for(String valuta : NationalBank.config.getStringList("valuta")) 
				{
					if(config.getDouble(nick + "." + item + "." + valuta) != 0)
						player.sendMessage(moneyType(valuta, config.getDouble(nick + "." + item + "." + valuta)));
				}
				
				player.sendMessage(ChatColor.LIGHT_PURPLE + "[Wallet] Всего: " + getSum(nick, item) + " иен");
			}					
		}
	}
}
