package com.narutocraft.util;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.narutocraft.donate.NestedDonateCommand;
import com.narutocraft.duels.DuelsNestedCommands;
import com.narutocraft.events.NestedEventsCommands;
import com.narutocraft.exp.NestedExpCommands;
import com.narutocraft.report.NestedReportCommands;
import com.narutocraft.society.NestedSocietyCommands;
import com.narutocraft.society.Society;
import com.narutocraft.teams.NestedTeamCommands;
import com.narutocraft.teams.Team;
import com.narutocraft.teams.TeamsHandler;
import com.narutocraft.village.KageNestedCommands;
import com.narutocraft.village.MissionsNestedCommands;
import com.narutocraft.wallet.WalletNestedCommands;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.NestedCommand;

public class Commands {

	@Command(aliases = {"donate"}, desc = "main command of donate system")
	@NestedCommand(value = {NestedDonateCommand.class})
	public static void donate(CommandContext args, CommandSender sender){}
	
	@Command(aliases = { "events" }, desc = "Events management commands")
	@NestedCommand({NestedEventsCommands.class})
	public static void events(CommandContext args, CommandSender sender) {}
	
	@Command(aliases = { "exp" }, desc = "main command of exp system")
	@NestedCommand(value = {NestedExpCommands.class})
	public static void exp(CommandContext args, CommandSender sender){}
	
	@Command(aliases = {"report"}, desc = "system of reports")
	@NestedCommand(value = {NestedReportCommands.class})
	public static void report(CommandContext args, CommandSender sender)
	{}
	
	@Command(aliases = {"society"}, desc = "Society managements commands")
	@NestedCommand(NestedSocietyCommands.class)
	public static void society(CommandContext args, CommandSender sender)
	{}
	
	@Command(aliases = {"duel"}, desc = "Duel system")
	@NestedCommand(DuelsNestedCommands.class)
	public static void duel(CommandContext args, CommandSender sender)
	{}
	
	@Command(aliases = {"wallet"}, desc = "Duel system")
	@NestedCommand(WalletNestedCommands.class)
	public static void wallet(CommandContext args, CommandSender sender)
	{}
	
	@Command(aliases = {"village"}, desc = "Duel system")
	@NestedCommand(KageNestedCommands.class)
	public static void village(CommandContext args, CommandSender sender)
	{}
	
	@Command(aliases = {"mission"}, desc = "Duel system")
	@NestedCommand(MissionsNestedCommands.class)
	public static void mission(CommandContext args, CommandSender sender)
	{}
	
	@Command(aliases = {"sc"}, desc = "Chat of members of society")
	public static void sc(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			Bukkit.getLogger().log(Level.SEVERE, "This command can be using only for players");
			return;
		}
		Player p = (Player)sender;
		
		String societyName = Society.getSociety(p.getName());
		
		if(societyName.equalsIgnoreCase("none"))
		{
			p.sendMessage(ChatColor.RED + "[ERROR] �� �� �������� �� � ����� �����������");
			return;
		}
		
		Society society = new Society(societyName);
		
		if(args.argsLength() == 0)
		{
			return;
		}
		
		String message = "";
		
		for(int i = 0; i < args.argsLength(); i++)
		{
			message = message + " " +  args.getString(i);
		}
		
		List<String> players = society.getOnlineMembers();
		
		Player member;
		 
		String role = (society.getRolePlayer(p.getName()).equalsIgnoreCase("member") ? "��������" : society.getRolePlayer(p.getName()).equalsIgnoreCase("oficer") ? "������" : "�����");
		
		for(String s : players)
		{
			member = Bukkit.getPlayer(s);
			member.sendMessage(ChatColor.GOLD + "[SC] " + 
					(role.equalsIgnoreCase("Owner") ? 
					ChatColor.GOLD : role.equalsIgnoreCase("Officer") ? 
					ChatColor.GREEN : ChatColor.AQUA) + 
					"[" + role + "] " + ChatColor.RESET + 
					p.getDisplayName() + ": " + ChatColor.GOLD 
					+ message);
		}
	}
	
	@Command(aliases = {"team"}, desc = "main command of team system")
	@NestedCommand(value = {NestedTeamCommands.class})
	public static void team(CommandContext args, CommandSender sender){}
	
	@Command(aliases = {"tc", "teamchat", "tchat"}, desc = "command for send team message", min = 1)
	public static void tc(CommandContext args, CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			return;
		}
		
		Player p = (Player) sender;
		
		String message = args.getJoinedStrings(0);
		
		if(TeamsHandler.getPlayerTeam(p.getName()) == null)
		{
			return;
		}
		
		Team team = TeamsHandler.getPlayerTeam(p.getName());
		TeamsHandler handler = new TeamsHandler(team);

		List<String> members = handler.getOnlineMembers();
		Player m;
		for(String s : members)
		{
			m = Bukkit.getPlayer(s);
			m.sendMessage(ChatColor.GOLD + "[TC]" + ChatColor.RESET + p.getName() + ": " + ChatColor.AQUA + message);
		}
	}
}

