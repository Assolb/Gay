package com.narutocraft.exams;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.power.PowerPlayer;
import com.narutocraft.teams.TeamsHandler;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;

public class ExamsNestedCommands {	
	
	public static ExamsHandler handler = new ExamsHandler();
	public static ExamsChunins chunins = new ExamsChunins(handler);
	public static ExamsGenins genins = new ExamsGenins(handler);
	
	private static void setDefaults() 
	{
		handler.setStage(0);
		handler.setExam("");
		handler.getMembers().clear();
		handler.getLeaders().clear();
		handler.canStart = true;
	}

	@Command(aliases = { "start" }, desc = "startin exam", usage = "<exam> - startin the exam <exam>", min = 1) // описание изменить!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public static void startinExam(CommandContext args, CommandSender sender)
	{
		Player player = (Player) sender;		
		
		if(handler.canStart == false) 
		{
			player.sendMessage(ChatColor.RED + "[Exams][ERROR] Вы не можете начать экзамен сейчас!");
			return;
		}	
		
		switch(args.getString(0)) 
		{
		case "genin":
			handler.canStart = false;
			handler.setExam("genin");
			break;
		case "chunin":
			handler.canStart = false;
			handler.setExam("chunin");
			break;
		default: 
			player.sendMessage(ChatColor.RED + "[Exams][ERROR] Введите команду по шаблону: /exams start <chunin/genin>");
			return;
		}
		notifications();
		countdown();
	}
	
	private static void notifications() 
	{
		if(handler.getExam().equals("chunin")) 
		{
			handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Экзамен на ранг Чунин скоро начнется!");
		}
		else if(handler.getExam().equals("genin"))
		{
			handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Экзамен на ранг Генин скоро начнется!");
		}
		
		handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] У вас 20 минут на регистрацию");
		handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] (для регистрации введите: /exams register");
	}
	
	private static void countdown() 
	{
		Thread thread = new Thread()
	    {
			public void run()
			{				
				for(int i = 4; i > 0; i--) 
				{
					try {
						Thread.sleep(5000); // СМЕНИТЬ НА 300000
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					int result = i * 5 - 5;
					if(result == 0)
						handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Регистрация на экзамен завершена!");
					else
						handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] На регистрацию осталось " + result + " минут!");
				}	
				
				if(handler.getMembers().size() < 3) // изменить на 16
				{
					handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Экзамен отменен, недостаточное количество участников!");
					setDefaults();
					return;
				} 
				
				for(String memberNick : handler.getMembers())
					handler.setInitialPosition(memberNick, Bukkit.getPlayer(memberNick).getLocation());
			    
			    switch(handler.getExam()) 
			    {
			    case "chunin":
			    	chunins.startinChuninExam();
			    	break;
			    case "genin":
			    	genins.startinGeninExam();
			    	break;
			    default: break;
			    }
				
				currentThread().interrupt();
			}
	    }; thread.start();
	}
	
	@Command(aliases = { "register" }, desc = "registers to exam", usage = "<exams> - register to the exam <exam>", min = 0) // описание изменить
	public static void registerinToExams(CommandContext args, CommandSender sender) 
	{
		
		Player player = (Player)sender;			
		String nick = player.getName();
		String permission = "";
		
		if(handler.getExam().equals("genin")) 
		{
			permission = "com.narutocraft.academyStudent";
		}
		else if(handler.getExam().equals("chunin")) 
		{
			permission = "com.narutocraft.genin";
		}
		else 
		{
			handler.message(player, ChatColor.RED + "[Exams][ERROR] В ближайшее время экзаменов не ожидается");
			return;
		}
		
		try {
			handler.getTeam(nick);
		} catch(Exception e) {
			handler.message(nick, ChatColor.RED + "[Exams][ERROR] Вы не состоите в команде"); //// !!!!!!!!!!!!
		}
		
		if(handler.getMembers().size() > 48) 
		{
			handler.message(player, ChatColor.RED + "[Exams][INFO] К глубокому сожалению, экзамен достиг лимита участников!");
			return;
		}
		
		for(String teammateNick : handler.getTeam(nick)) 
		{
			if(Bukkit.getPlayer(teammateNick).equals(null)) 
			{
				handler.message(player, ChatColor.RED + "[Exams][ERROR] Кто-то из вашей команды не в сети!");
				return;
			}
			else if(handler.getMembers().contains(teammateNick)) 
			{
				handler.message(player, ChatColor.RED + "[Exams][ERROR] Ваша команда уже зарегистрирована на экзамен!");
				return;
			}
			else if(!Bukkit.getPlayer(teammateNick).hasPermission(permission)) 
			{
				handler.message(player, ChatColor.RED + "[Exams][ERROR] У кого-то из вашей команды недостаточно прав!");
				return;
			}
		}
		
		for(String teammateNick : handler.getTeam(nick)) 
		{
			try {
				handler.addMember(teammateNick);
			} catch (IOException e) {
				e.printStackTrace();
			}	
			handler.setInitialPosition(teammateNick, Bukkit.getPlayer(teammateNick).getLocation());
			handler.message(teammateNick, ChatColor.GREEN + "[Exams][INFO] Вы успешно зарегистрированы на экзамен!");
		}
		
		handler.addLeader(nick);
		
		for(Player p : Bukkit.getOnlinePlayers()) // убрать
		{
			for(String s : handler.getMembers())
				p.sendMessage(s);
		}		
	}
	
	@Command(aliases = { "answer" }, desc = "registers to exam", usage = "<exams> - answerin the question <answer>", min = 0) // описание изменить
	public static void answerin(CommandContext args, CommandSender sender) 
	{
		Player player = (Player) sender;
		String nick = player.getName();
		
		if(handler.getStage() == 1) 
		{
			if(!handler.getMembers().contains(nick)) 
			{
				handler.message(player, ChatColor.RED + "[Exams][ERROR] Вы не участвуете в экзамене");
				return;
			}
			else if(!handler.getAnswer(nick).equals("")) 
			{
				handler.message(player, ChatColor.RED + "[Exams][INFO] Вы уже ответили на вопрос!");
				return;
			}
			else if(handler.getAnswer(nick).equals(""))
			{
				handler.setAnswer(nick, args.getString(0));
				handler.message(player, ChatColor.GOLD + "[Exams][INFO] Ваш ответ - " + handler.getAnswer(nick));
			}
			return;
		}
		handler.message(player, ChatColor.RED + "[Exams][ERROR] Сейчас не проходит первый этап экзамена!");
	}
	
	
	// для удобства
	
	@Command(aliases = { "tp" }, desc = "givin operator's permissions", usage = "/exams op", min = 0) // описание изменить
	public static void tp(CommandContext args, CommandSender sender) 
	{
		Player player = (Player) sender;
		handler.waitinRoomTeleport(player.getName());
	}
	
	@Command(aliases = { "power" }, desc = "givin operator's permissions", usage = "/exams op", min = 0) // описание изменить
	public static void power(CommandContext args, CommandSender sender) 
	{
		Player player = (Player) sender;
		player.sendMessage("" + new PowerPlayer(player.getName()).getPower());
	}
	
	@Command(aliases = { "skill" }, desc = "givin operator's permissions", usage = "/exams op", min = 0) // описание изменить
	public static void loser(CommandContext args, CommandSender sender) 
	{
/*		Player player = (Player) sender;
		for(int i = 0; i<360; i = i++)
		{
			double pX = player.getLocation().getX();
			double pZ = player.getLocation().getZ();
			double z = pZ + Math.cos(i*Math.PI/180);
			double x = pX + Math.sin(i*Math.PI/180);
			double y = player.getLocation().getY() + 1;

			player.getWorld().playEffect(new Location(player.getWorld(), x, y, z), Effect.SMOKE, 100);

			}*/
	}
/*	// КОМАНДЫ ДЛЯ УДОБСТВА
	
	@Command(aliases = { "gm" }, desc = "registers to exam", usage = "<exam> - register to the exam <exam>", min = 0) // описание изменить
	public static void gamemode(CommandContext args, CommandSender sender) 
	{
		if(!((Player)sender).hasPermission("com.narutocraft.exams.gm"))
			return;
		for(String memberNick : handler.getMembers()) 
		{
			Player member = Bukkit.getPlayer(memberNick);		
			
			if(args.getString(0) == "0")
				member.setGameMode(GameMode.SURVIVAL);
			else if(args.getString(0) == "1")
				member.setGameMode(GameMode.CREATIVE);
			else
				return;
		}
		((Player)sender).sendMessage("completed.");
	}
	
	@Command(aliases = { "op" }, desc = "givin operator's permissions", usage = "/exams op", min = 0) // описание изменить
	public static void op(CommandContext args, CommandSender sender) 
	{
		if(!((Player)sender).hasPermission("com.narutocraft.exams.op"))
			return;
		for(Player player : Bukkit.getOnlinePlayers()) 
		{
			player.setOp(true);
		}
		((Player)sender).sendMessage("completed.");
	}
	
	@Command(aliases = { "check" }, desc = "givin operator's permissions", usage = "/exams op", min = 0) // описание изменить
	public static void check(CommandContext args, CommandSender sender) 
	{
		List<String> list = handler.config.getStringList("chunin.1");
		System.out.println(list.get(1));
		System.out.println("completed.");
	}
	
	@Command(aliases = { "add" }, desc = "givin operator's permissions", usage = "/exams op", min = 0) // описание изменить
	public static void add(CommandContext args, CommandSender sender) 
	{
		String nick = args.getString(0);
		if(Bukkit.getPlayer(nick) == null)
			return;
		try {
			handler.addMember(nick);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bukkit.getPlayer(nick).sendMessage("вас добавили в участники экзамена");
		((Player)sender).sendMessage("completed.");
	}
	
	@Command(aliases = { "stage" }, desc = "givin operator's permissions", usage = "/exams op", min = 0) // описание изменить
	public static void stage(CommandContext args, CommandSender sender) throws IOException 
	{
		int stage = args.getInteger(0);
		if(!canStart)
			return;
		handler.setStage(stage);
		if(stage == 2)
			chunins.secondStage();
		else if(stage == 3)
			chunins.thirdStage();
		else
			((Player)sender).sendMessage("sosesh.");	
		((Player)sender).sendMessage("completed.");	
	}
	
	@Command(aliases = { "members" }, desc = "givin operator's permissions", usage = "/exams op", min = 0) // описание изменить
	public static void members(CommandContext args, CommandSender sender) 
	{
		for(String member : handler.getMembers())
			((Player)sender).sendMessage(member);
		((Player)sender).sendMessage("completed.");	
	}
	
	@Command(aliases = { "locations" }, desc = "givin operator's permissions", usage = "/exams op", min = 0) // описание изменить
	public static void locations(CommandContext args, CommandSender sender) 
	{
		final int stage = args.getInteger(0);
		final Player player = ((Player)sender);
		Thread thread = new Thread() 
		{
			public void run() 
			{
				List<Location> locations = handler.stageCoords(stage);
				if(handler.config.getStringList("chunin." + stage).isEmpty()) 
				{
					player.sendMessage("coords are empty");
					return;
				}
				if(locations.isEmpty()) 
				{
					player.sendMessage("locations are empty");
					return;
				}
				for(int i = 0; i < locations.size(); i++) 
				{
					handler.teleport(locations.get(i), player.getName());
					player.sendMessage(handler.config.getStringList("chunin." + stage).get(i));
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}; thread.start();
	}
	
	@Command(aliases = { "getstage" }, desc = "givin operator's permissions", usage = "/exams op", min = 0) // описание изменить
	public static void getstage(CommandContext args, CommandSender sender) 
	{
		((Player)sender).sendMessage("" + handler.getStage());	
		((Player)sender).sendMessage("completed.");	
	}
	
	@Command(aliases = { "setstage" }, desc = "givin operator's permissions", usage = "/exams op", min = 0) // описание изменить
	public static void setstage(CommandContext args, CommandSender sender) throws IOException 
	{
		handler.setStage(1);
		((Player)sender).sendMessage("" + handler.getStage());	
		((Player)sender).sendMessage("completed.");	
	} */
}
