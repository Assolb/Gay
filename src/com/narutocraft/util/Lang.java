package com.narutocraft.util;

import java.io.File;
import java.io.IOException;

import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.stats.ConfigPlayer;

public class Lang {

	private NarutoCraft1 plugin;

	public Lang(NarutoCraft1 plugin) {
		this.plugin = plugin;
	}
	
	public static void enablePlugin()
	{
		File file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "langs");
		
		if(!file.exists()){
			file.mkdir();
		}
		
		NarutoCraft1.get().saveResource("ru_RU.yml", false);
		NarutoCraft1.get().saveResource("en_EN.yml", false);
		File ru = new File(NarutoCraft1.get().getDataFolder() + File.separator + "ru_RU.yml");
		File en = new File(NarutoCraft1.get().getDataFolder() + File.separator + "en_EN.yml");
		
		FileConfiguration config;
		
		try {
			
			config = YamlConfiguration.loadConfiguration(ru);
			writeRU(ru, config);
			
			config = YamlConfiguration.loadConfiguration(en);
			writeEN(en, config);
			
			FileUtils.copyFileToDirectory(ru, file);
			FileUtils.copyFileToDirectory(en, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ru.delete();
		en.delete();
	}
	
	public static void writeRU(File f, FileConfiguration c) throws IOException
	{
		File file = f;
		FileConfiguration config = c;
		
		config.createSection("lang");
		config.createSection("lang.events");
			
		config.set("lang.events.list-noevents", "&6[INFO] &d� ������ ������ �������� � ����������� ������� �����������!");
		config.set("lang.events.list-noevents1", "&a����� ������ ���� � ����� �������, ����������� ��� ������� �� ����� ������� ���������!");
		config.set("lang.events.list-upcoming-events", "&6[INFO] &b������ ����������� �������");
		config.set("lang.events.console-failed", "&c[ERROR] This command can only be used by a player!");
		config.set("lang.events.check-exists-true", "&c[ERROR] ����������� � ����� ��������� ��� ����������");
		config.set("lang.events.check-exists-false", "&c[ERROR] ����������� � ����� ��������� �� ����������");
		config.set("lang.events.create-successful", "&6[INFO] &a�� ������� ������������� ����������� - &6{0}");
		config.set("lang.events.remove-unable", "&c[ERROR] ����������� &6{0} &c��� ��������! �������� ����������!");
		config.set("lang.events.remove-successful", "&6[INFO] &a�� ������� ������� ����������� &6{0}");
		config.set("lang.events.start-event", "&6[INFO] &d��������! ����������� &6{0} &d���� ��������!");
		config.set("lang.events.start-event1", "&a��� �������� ������� � ����������� ����������� ������� &6/events join {0}");
		config.set("lang.events.start-event-required", "&6[INFO] &d��������! ������������ ����������� &6{0} &d���� ��������!");
		config.set("lang.events.start-event-required1", "&c������� � ����������� ����������� ��� ���� ������� � ����!");
		config.set("lang.events.finish-event-unable", "&c[ERROR] ��������� ���� ����������� � ������ ������ �� ��������!");
		config.set("lang.events.finish-event", "&6[INFO] &d������� &6{0} &d���� ���������!");
		config.set("lang.events.finish-event-thanks", "&a���� ������� �� �������!");
		config.set("lang.events.join-impossible", "&c[ERROR] � ������ ������ ������������� � ����������� &6{0} ����������");
		config.set("lang.events.join-already", "&c[ERROR] �� ��� ���������� � �����-���� �����������");
        config.set("lang.events.join-successful", "&6[INFO] &a�� ������� �������������� � ����������� &6{0}");
        config.set("lang.general.change-successful", "&6[INFO] &a�� ������� �������� ������� ���� �� &6RU");
        
        config.save(file);
	}
	
	public static void writeEN(File f, FileConfiguration c) throws IOException
	{
		File file = f;
		FileConfiguration config = c;
		
		config.createSection("lang");
		config.createSection("lang.events");
			
		config.set("lang.events.list-noevents", "&6[INFO] &dAt this moment information about upcoming events is not exists!");
		config.set("lang.events.list-noevents1", "&aTo always keep abreast of developments, we recommend that you follow our group on Facebook!");
		config.set("lang.events.list-upcoming-events", "&6[INFO] &bList of upcoming events");
		config.set("lang.events.console-failed", "&c[ERROR] This command can only be used by a player!");
		config.set("lang.events.check-exists-true", "&c[ERROR] The event with this name is exists");
		config.set("lang.events.check-exists-false", "&c[ERROR] The event with this name is not exists");
		config.set("lang.events.create-successful", "&6[INFO] &aYou are successful created the event - &6{0}");
		config.set("lang.events.remove-unable", "&c[ERROR] Event &6{0} &cis started! Unable to delete!");
		config.set("lang.events.remove-successful", "&6[INFO] &aYou are sucessful deleted the event &6{0}");
		config.set("lang.events.start-event", "&6[INFO] &dAttention! The event &6{0} &dwas started!");
		config.set("lang.events.start-event1", "&aFor attend the event use &6/events join {0}");
		config.set("lang.events.start-event-required", "&6[INFO] &dAttention! Required event &6{0} &dwas started!");
		config.set("lang.events.start-event-required1", "&cAttending the event is required for everybody online players!");
		config.set("lang.events.finish-event-unable", "&c[ERROR] This event is not current event");
		config.set("lang.events.finish-event", "&6[INFO] &dThe event &6{0} &dwas finished");
		config.set("lang.events.finish-event-thanks", "&aThank you all for attending!");
		config.set("lang.events.join-impossible", "&c[ERROR] At this moment joining the event &6{0} &cis not possible");
		config.set("lang.events.join-already", "&c[ERROR] You are already attending the event");
        config.set("lang.events.join-successful", "&6[INFO] &aYou are successful joined to event &6{0}");
        config.set("lang.general.change-successful", "&6[INFO] &aYou are successful changed your language on &6EN");
        
        config.save(file);
	}
	
	
	
	public static String getLocal(String plugin, String type, String player, String... string)
	{
		File file = null;
	
		if(player == null) file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "langs" + File.separator + "en_EN.yml");
		
		else
		{
			ConfigPlayer cf = new ConfigPlayer(player);
			file = new File(NarutoCraft1.get().getDataFolder() + File.separator + "langs" + File.separator + cf.getLang() + ".yml");
		}
		
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if(string.length == 0)
			return config.getString("lang." + plugin + "." + type).replace("&", "�");
		String s = config.getString("lang." + plugin + "." + type).replace("&", "�");;
		for(int i = 0; i < string.length; i++)
		{	
			s = s.replace("{" + Integer.toString(i) + "}", string[i]);			
		}
		return s;
	}
}
