package com.narutocraft.exams;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.narutocraft.teams.TeamsHandler;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;

public class ExamsNestedCommands {	
	
	public static boolean canStart = true;
	public static ExamsChunins chunins;
	public static ExamsGenins genins = new ExamsGenins();
	public static ExamsHandler handler = new ExamsHandler();

	@Command(aliases = { "start" }, desc = "startin exam", usage = "<exam> - startin the exam <exam>", min = 1) // описание изменить
	public static void startinExam(CommandContext args, CommandSender sender)
	{
		chunins = new ExamsChunins();
		Player player = (Player)sender;
		if(!canStart) 
		{
			player.sendMessage(ChatColor.RED + "[Exams][ERROR] Вы не можете начать экзамен сейчас!");
			return;
		}
		
		if(handler.getStage() == 0 && handler.getExam() == "") 
		{
			switch(args.getString(0)) 
			{
			case "genin":
				examNotification("Генин");
				handler.setExam("genin");
				canStart = false;
				countd();
				break;
			case "chunin":
				examNotification("Чунин");
				handler.setExam("chunin");
				canStart = false;
				countd();
				break;
			default:
				player.sendMessage(ChatColor.RED + "[Exams][ERROR] Введите команду по шаблону: /exams start <chunin/genin>");
				break;
			}
		}
	}
	
	private static void examNotification(String examType) 
	{
		handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Экзамен на ранг " + examType + " скоро начнется!");
		handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] У вас 20 минут на регистрацию");
		handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] (для регистрации введите: /exams register " + handler.getExam());
	}
	
	private static void countd() 
	{
	    Thread cd = new Thread()
	    {
			public void run()
			{
				for(int i = 4; i > 0; i--) 
				{
					try 
					{
						Thread.sleep(10000); // СМЕНИТЬ НА 300000
					}
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
					int result = i * 5 - 5;
					if(result == 0)
						handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Регистрация завершена!");
					else
						handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] У вас осталось " + result + " минут!");
				}	
				
				if(handler.getMembers().size() < 3) // изменить на 16
				{
					handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Экзамен отменен, недостаточное количество участников!");
					canStart = true;
					return;
				} 
				if(handler.getExam() == "chunin") 
				{	
					try {
						chunins.startinChuninExam();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(handler.getExam() == "genin") 
				{
					genins.startinGeninExam();
				}
			}
	    }; cd.start();
	}
	
	@Command(aliases = { "register" }, desc = "registers to exam", usage = "<exams> - register to the exam <exam>", min = 0) // описание изменить
	public static void registerinToExams(CommandContext args, CommandSender sender) 
	{
		Player player = (Player)sender;
		String nick = player.getName();
		String permission = "";
		
		if(handler.getExam() == "genin") 
		{
			permission = "com.narutocraft.academyStudent";
		}
		else if(handler.getExam() == "chunin") 
		{
			permission = "com.narutocraft.genin";
		}
		else 
		{
			handler.message(player, ChatColor.RED + "[Exams][ERROR] В ближайшее время экзаменов не ожидается");
			return;
		}
		
		for(String teammateNick : handler.getTeam(nick)) 
		{
			if(Bukkit.getPlayer(teammateNick) == null) 
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
		if(handler.getStage() == 0 && handler.getExam() != "") 
		{
			for(String teammateNick : handler.getTeam(nick)) 
			{
				try {
					handler.addMember(teammateNick);
				} catch (IOException e) {
					e.printStackTrace();
				}
				handler.message(teammateNick, ChatColor.GREEN + "[Exams][INFO] Вы успешно зарегистрированы на экзамен!");
			}
			handler.addLeader(nick);
		}
	}
	
	@Command(aliases = { "answer" }, desc = "registers to exam", usage = "<exams> - answerin the question <answer>", min = 0) // описание изменить
	public static void answerin(CommandContext args, CommandSender sender) 
	{
		Player player = (Player) sender;
		String nick = player.getName();
		
		System.out.println("1");
		player.sendMessage("suck");
		
		if(handler.getStage() == 1) 
		{
			System.out.println("2");
			player.sendMessage("my");
			if(!handler.getMembers().contains(nick)) 
			{
				player.sendMessage("cock");
				handler.message(player, ChatColor.RED + "[Exams][ERROR] Вы не участвуете в экзамене");
				return;
			}
			else if(handler.getAnswer(nick) != "") 
			{
				player.sendMessage("potato");
				handler.message(player, ChatColor.RED + "[Exams][INFO] Вы уже ответили на вопрос!");
				return;
			}
			else if(handler.getAnswer(nick) == "")
			{
				player.sendMessage("who");
				handler.setAnswer(nick, args.getString(0));
				handler.message(player, ChatColor.GOLD + "[Exams][INFO] Ваш ответ - " + handler.getAnswer(nick));
			}
			return;
		}
		handler.message(player, ChatColor.RED + "[Exams][ERROR] Сейчас не проходит первый этап экзамена!");
	}
	
	// КОМАНДЫ ДЛЯ УДОБСТВА
	
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
	}
}
