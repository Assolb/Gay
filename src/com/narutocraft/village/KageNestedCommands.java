package com.narutocraft.village;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;

import net.minecraft.server.v1_7_R3.ChatSerializer;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutChat;

public class KageNestedCommands {

	private static boolean isKage(String nick) 
	{
		for(String kageVillage : Village.config.getStringList("kages")) 
		{
			if(Village.config.getString("kages." + kageVillage).equals(nick))
				return true;
		}
		return false;
	}
	
	private static String getKageVillage(String nick) 
	{
		for(String kageVillage : Village.config.getStringList("kages")) 
		{
			if(Village.config.getString("kages." + kageVillage).equals(nick))
				return kageVillage;
		}
		return null;
	}
	
	public static String villageName(String village) 
	{
		switch(village) 
		{
		case "konohagakure":
			return "&4Конохагакуре Но Сато";
		case "kirigakure":
			return "&9Киригакуре Но Сато";
		case "kumogakure":
			return "&eКумогакуре Но Сато";
		case "sunagakure":
			return "&6Сунагакуре Но Сато";
		case "iwagakure":
			return "&8Ивагакуре Но Сато";
		case "otogakure":
			return "&5Отогакуре Но Сато";
		case "kusagakure":
			return "&2Кусагакуре Но Сато";
		case "yugakure":
			return "&dЮигакуре Но Сато";
		case "takigakure":
			return "&1Такигакуре Но Сато";
		case "uzushiogakure":
			return "&dУзушиогакуре Но Сато";
		default: break;			
		}
		return null;
	}
	
	public static String resourceName(String resourceType) 
	{
		switch(resourceType) 
		{
		case "money":
			return " иен";
		case "equipment":
			return " наборов снаряжения";
		case "fossils":
			return " килограмм обработанных ископаемых";
		case "food":
			return " килограмм еды";

		default: break;			
		}
		return null;
	}
	
	@Command(aliases = { "offers" }, desc = "startin exam", usage = "<exam> - startin the exam <exam>", min = 1) // описание изменить!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public static void offers(CommandContext args, CommandSender sender) 
	{
		Player player = (Player) sender;
		String nick = player.getName();
		
		if(!isKage(player.getName())) 
			return;
		
		String village = getKageVillage(nick);
		
		int listSize = Village.config.getStringList("kageOffers." + getKageVillage(nick)).size();
		int pages = 0;
		int currentPage = Integer.parseInt(args.getString(0));
		
		for(int i = 0; i < listSize; i = i + 5)
			pages++;
		
		int index = currentPage * 5;
		index = index - 5;
		while(index < currentPage * 5) 
		{
			String json = "";
			String offeringVillage = "";
			String offerType = Village.config.getString("kageOffers." + village + "." + index + ".offerType");
			
			switch(offerType) 
			{
			case "trade": // dark aqua - 3, aqua - b
				
				String givingVillage = Village.config.getString("kageOffers." + village + "." + index + ".givingVillage");
				String givingVillageResource = Village.config.getString("kageOffers." + village + "." + index + ".givingVillageResource");
				String receivingVillageResource = Village.config.getString("kageOffers." + village + "." + index + ".receivingVillageResource");
				double givingVillageResourceCount = Village.config.getDouble("kageOffers." + village + "." + index + ".receivingVillageResourceCount");
				double receivingVillageResourceCount = Village.config.getDouble("kageOffers." + village + "." + index + ".receivingVillageResourceCount");
				
				json = "{text:\"§3Деревня " + villageName(givingVillage) + " §3предлагает обмен §b" + givingVillageResourceCount + resourceName(givingVillageResource) + 
				" §3на §b" +  receivingVillageResourceCount + resourceName(receivingVillageResource) + 
				", §aПринять предложение§a\", clickEvent:{action:run_command,value:\"/village accept" + index + " " + offerType + "\"}" + 
				", §cВоздержаться§c\", clickEvent:{action:run_command,value:\"/village accept" + index + " " + offerType + "\"} }"; //СДЕЛАТЬ СЕЙВЫ
				
				Packet packet = new PacketPlayOutChat(ChatSerializer.a(json), true); 
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
				
			case "peace":
				offeringVillage = Village.config.getString("kageOffers." + village + "." + index + ".offeringVillage");
				
				json = "{text:\"§3Деревня " + villageName(offeringVillage) + " §3предлагает §bМир" +
				", §aПринять предложение§a\", clickEvent:{action:run_command,value:\"/village accept" + index + " " + offerType + "\"}" + 
				", §cВоздержаться§c\", clickEvent:{action:run_command,value:\"/village accept" + index + " " + offerType + "\"} }";
				
			case "allience":
				offeringVillage = Village.config.getString("kageOffers." + village + "." + index + ".offeringVillage");
				
				json = "{text:\"§3Деревня " + villageName(offeringVillage) + " §3предлагает §bСоюз" +
				", §aПринять предложение§a\", clickEvent:{action:run_command,value:\"/village accept" + index + " " + offerType + "\"}" + 
				", §cВоздержаться§c\", clickEvent:{action:run_command,value:\"/village accept" + index + " " + offerType + "\"} }";
				
			default: break;
			}			
			index++;
		}		
	}
	
	@Command(aliases = { "accept" }, desc = "startin exam", usage = "<exam> - startin the exam <exam>", min = 1) // описание изменить!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public static void accept(CommandContext args, CommandSender sender) 
	{
		if(sender instanceof Player)
			return;
		
		if(args.getString(0).equals(""))
			return;
		
		String village = args.getString(0);
		int index = Integer.parseInt(args.getString(1));
		String offerType = args.getString(2);
		
		Village thisVillage = new Village(village);
		Village otherVillage;
		
		String offeringVillage;

		switch(offerType) 
		{	
		case "trade":
			String givingVillage = Village.config.getString("kageOffers." + village + "." + index + ".givingVillage");
			String givingVillageResource = Village.config.getString("kageOffers." + village + "." + index + ".givingVillageResource");
			String receivingVillageResource = Village.config.getString("kageOffers." + village + "." + index + ".receivingVillageResource");
			double givingVillageResourceCount = Village.config.getDouble("kageOffers." + village + "." + index + ".receivingVillageResourceCount");
			double receivingVillageResourceCount = Village.config.getDouble("kageOffers." + village + "." + index + ".receivingVillageResourceCount");
			otherVillage = new Village(givingVillage);
			
			thisVillage.addResourceToTreasure(givingVillageResource, givingVillageResourceCount);
			otherVillage.addResourceToTreasure(receivingVillageResource, receivingVillageResourceCount);
			thisVillage.removeResourceFromTreasure(receivingVillageResource, receivingVillageResourceCount);
			otherVillage.removeResourceFromTreasure(givingVillageResource, givingVillageResourceCount);
			
		case "piece":
			offeringVillage = Village.config.getString("kageOffers." + village + "." + index + ".offeringVillage");
			otherVillage = new Village(offeringVillage);
			
			thisVillage.setDiplomacy(village, "piece");
			otherVillage.setDiplomacy(offeringVillage, "piece");
			
		case "allience":
			offeringVillage = Village.config.getString("kageOffers." + village + "." + index + ".offeringVillage");
			otherVillage = new Village(offeringVillage);
			
			thisVillage.setDiplomacy(village, "allience");
			otherVillage.setDiplomacy(offeringVillage, "allience");
			
		default: break;
		}
	}
	
	@Command(aliases = { "trade" }, desc = "startin exam", usage = "<exam> - startin the exam <exam>", min = 1) // описание изменить!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public static void trade(CommandContext args, CommandSender sender) 
	{
		if(!(sender instanceof Player))
			return;
		
		Player player = (Player) sender;
		String village = args.getString(0);
		Village handler = new Village(village);
		
		if(!isKage(player.getName()))
			return;
		
		if(!Village.config.getStringList("villages").contains(village))
			return;
				
		if(!handler.getDiplomacy(args.getString(0)).equals("war")) 
		{
			handler.tradeWithVillage(args.getString(0), args.getString(1), args.getString(2), Double.parseDouble(args.getString(3)), Double.parseDouble(args.getString(4)));
			player.sendMessage(ChatColor.GREEN + "[Village][INFO] Вы успешно отправили предложение на обмен деревне " + villageName(args.getString(0)));
		}
		else player.sendMessage(ChatColor.RED + "[Village][ERROR] Обмен с деревней" + villageName(args.getString(0)) + " не может быть произведен, поскольку вы враждуете");
	}
	
	@Command(aliases = { "war" }, desc = "startin exam", usage = "<exam> - startin the exam <exam>", min = 1) // описание изменить!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public static void war(CommandContext args, CommandSender sender) 
	{
		Player player = (Player) sender;
		String nick = player.getName();
		String village = args.getString(0);
		
		if(!isKage(nick))
			return;
		
		if(!Village.config.getStringList("villages").contains(village) && !village.equals("all"))
			return;
		
		if(village.equals("all")) 
		{
			for(String v : Village.config.getStringList("villages")) 
			{
				if(!v.equals(village))
					new Village(getKageVillage(nick)).setDiplomacy(v, "war");
			}
			
			for(Player p : Bukkit.getOnlinePlayers()) 
				p.sendMessage(ChatColor.BOLD + "Деревня" + villageName(getKageVillage(nick)) + ChatColor.WHITE + " объявила войну всему миру!");		
		}
		else 
		{
			new Village(getKageVillage(nick)).setDiplomacy(village, "war");
			for(Player p : Bukkit.getOnlinePlayers()) 
				p.sendMessage(ChatColor.BOLD + "Деревня" + villageName(getKageVillage(nick)) + ChatColor.WHITE + " объявила войну деревне " + villageName(village) + ChatColor.WHITE + " !");	
		}
	}
	
}
