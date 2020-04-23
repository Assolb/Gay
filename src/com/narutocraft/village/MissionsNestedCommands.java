package com.narutocraft.village;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.narutocraft.banks.NationalBank;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;

public class MissionsNestedCommands {

	@Command(aliases = { "reward" }, desc = "startin exam", usage = "<exam> - startin the exam <exam>", min = 1) // описание изменить!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public static void reward(CommandContext args, CommandSender sender) 
	{
		String receiver = args.getString(0);
		String valuta = args.getString(1);
		double count = Double.parseDouble(args.getString(2));
		
		for(String village : Village.config.getStringList("villagers")) 
		{
			if(Village.config.getStringList("villagers." + village).contains(receiver)) 
			{
				new Village(village).addResourceToTreasure("money", Math.round(count / 4));
				new NationalBank(village).addMoneyOnAccount(receiver, valuta, count);
				break;
			}
		}
	}
	
}
