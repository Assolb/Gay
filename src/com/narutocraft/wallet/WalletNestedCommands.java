package com.narutocraft.wallet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;

public class WalletNestedCommands {

	public static WalletHandler handler = new WalletHandler();
	
	@Command(aliases = { "give" }, desc = "startin exam", usage = "<exam> - startin the exam <exam>", min = 1) // описание изменить!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public static void give(CommandContext args, CommandSender sender) 
	{
		// сделать инстанцоф
		Player player = (Player) sender;
		if(player.getItemInHand().getType() == Material.getMaterial(123)) //id
			handler.addMoney(player.getName(), player.getItemInHand(), args.getString(0), args.getString(1), Double.parseDouble(args.getString(2)));
		else 
			player.sendMessage(ChatColor.RED + "[Wallet] P.S. (псс.. возьми кошелек в руку..)");
	}
	
}
