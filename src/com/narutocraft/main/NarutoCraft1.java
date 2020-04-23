package com.narutocraft.main;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.narutocraft.banks.NationalBank;
import com.narutocraft.duels.DuelsHandler;
import com.narutocraft.duels.DuelsNestedCommands;
import com.narutocraft.events.EventsHandler;
import com.narutocraft.exams.ExamsCommands;
import com.narutocraft.exams.ExamsHandler;
import com.narutocraft.exams.ExamsNestedCommands;
import com.narutocraft.exp.ExpListener;
import com.narutocraft.hospital.HospitalHandler;
import com.narutocraft.network.PacketHandlerPlugin;
import com.narutocraft.network.PacketManager;
import com.narutocraft.police.PoliceThread;
import com.narutocraft.report.ReportHandler;
import com.narutocraft.stats.ConfigListener;
import com.narutocraft.util.ChangeLang;
import com.narutocraft.util.Commands;
import com.narutocraft.util.RegisterHandlers;
import com.narutocraft.wallet.WalletHandler;
import com.narutocraft.wallet.WalletNestedCommands;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.SimpleInjector;
import com.sk89q.minecraft.util.commands.WrappedCommandException;

public class NarutoCraft1 extends JavaPlugin {
	
	private static NarutoCraft1 plugin;
	
	public EventsHandler eventsHandler;
	
	public ReportHandler reportHandler;
	
	public ExamsHandler examsHandler;
	
	public DuelsHandler duelsHandler;
	
	public WalletHandler walletHandler;
	
	public NationalBank bankHandler;
	
	public HospitalHandler hospitalHandler;
	
	private CommandsManager<CommandSender> commands;
	
	public static Logger logger = Bukkit.getLogger();
	
	public static PacketHandlerPlugin handler;
	
	public NarutoCraft1 instance()
	{
	    return plugin;
	}
	
	@Override
	public void onEnable() 
	{
	    plugin = this;
	    
	    Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "narutocraftserver");
	    Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "narutocraftserver", new PacketHandlerPlugin(this));

	    new PacketManager();
	    handler = new PacketHandlerPlugin(this);
	    
	    eventsHandler = new EventsHandler(this);
	    
	    reportHandler = new ReportHandler(this);
	    
	    hospitalHandler = new HospitalHandler(this);
	    
	    walletHandler = WalletNestedCommands.handler;
	    
	    duelsHandler = DuelsNestedCommands.handler;
	    
	    examsHandler = ExamsNestedCommands.handler; // ТОТ САМЫЙ КАСТЫЛЬ
	    
	    RegisterHandlers.register();
	    
	    registerCommands();
	
	    Bukkit.getPluginManager().registerEvents(new ConfigListener(), this);
	    
	    Bukkit.getPluginManager().registerEvents(new ExpListener(), this);
	    
	    Bukkit.getPluginManager().registerEvents(hospitalHandler, this);
	    
	    Bukkit.getPluginManager().registerEvents(walletHandler, this);
	    
	    Bukkit.getPluginManager().registerEvents(duelsHandler, this);
	    
	    Bukkit.getPluginManager().registerEvents(examsHandler, this); // Bukkit.getPluginManager().registerEvents(new ExamsHandler(), this);
	    
	    if(!new File(getDataFolder() + File.separator + "players").exists())
	    {
	    	new File(getDataFolder() + File.separator + "players").mkdir();
	    }
	    
	    Bukkit.getScheduler().scheduleSyncDelayedTask(this, new PoliceThread(this), 20L);
	}
	
	@Override
	public void onDisable() {
		Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin, "narutocraftserver");
	}
	 
	public void registerCommands()
	{
		this.commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender instanceof ConsoleCommandSender || sender.hasPermission(perm);
            }
        };
        
        this.commands.setInjector(new SimpleInjector(this));
        
	    CommandsManagerRegistration reg = new CommandsManagerRegistration(this, commands);
	    reg.register(Commands.class);
	    reg.register(ChangeLang.class);
	    reg.register(ExamsCommands.class);	
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		try {
	       this.commands.execute(cmd.getName(), args, sender, sender);
	    } catch (CommandPermissionsException e) {
	        sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
        	sender.sendMessage(ChatColor.RED + e.getUsage());
	    } catch (CommandUsageException e) {
	        sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
	        if (e.getCause() instanceof NumberFormatException) {
	        	sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
	        } else {
	            sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
	            e.printStackTrace();
            }
	    } catch (CommandException e) {
	        sender.sendMessage(ChatColor.RED + e.getMessage());
	        } 
	    return true;
	}
	
	public static NarutoCraft1 get()
	{
	    return plugin;
	}
	
}
