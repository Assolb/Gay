package com.narutocraft.exams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.narutocraft.main.NarutoCraft1;
import com.narutocraft.power.PowerPlayer;

public class ExamsChunins {
	
	public String[] opponents = null;
	
	ExamsHandler handler = new ExamsHandler();
	ExamsNestedCommands nestedCommands = new ExamsNestedCommands();
	List<Thread> examThreads = new ArrayList<Thread>(); 
	
	private void stop() // порешать насчет листа с тредами
	{
		for(Player player : Bukkit.getOnlinePlayers()) 
		{
			if(handler.getWinners().isEmpty()) 
			{
				handler.message(player, ChatColor.RED + "[Exams][INFO] Никто не прошел экзамен!");
			}
			else 
			{
				handler.message(player, ChatColor.LIGHT_PURPLE + "[Exams][INFO] Прошедшие экзамен:");
				for(String winnerNick : handler.getWinners()) 
				{
					// Bukkit.getPlayer(winnerNick).perm дать пермишн !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					handler.message(player, ChatColor.AQUA + "[Exams][INFO] " + winnerNick);
				}
			}
		}
		
		for(Thread thread : examThreads) 
		{
			while(!thread.isInterrupted()) 
			{	          
		          try {
		              Thread.sleep(1000);
		          } catch (InterruptedException e) {
		              thread.interrupt();
		              return;
		          }
			}
		}
		
		handler.getMembers().clear();
		handler.getLeaders().clear();
		handler.getLosers().clear();
		handler.getWinners().clear();
		handler.setStage(0);
		handler.setExam("");
		handler.canStart = true;
	}
	
	/*private Thread thread;
	private void countdown() 
	{
		thread = new Thread() 
		{
			public void run() 
			{
				if(!examThreads.contains(currentThread()))
					examThreads.add(currentThread());
				
				List<String> nicks = new ArrayList<String>();
				switch(handler.getStage()) 
				{
				case 2:
					nicks = handler.getMembers();
					break;
				case 3:
					nicks.add(opponents[0]);
					nicks.add(opponents[1]);
					break;
				default: break;
				}
				
				final Map<String, Location> locations = new HashMap<String, Location>();
				for(String nick : nicks)
					locations.put(nick, Bukkit.getPlayer(nick).getLocation());
				
				for(int i = 3; i > 0; i--) 
				{
					ChatColor color = null;
					switch(i) 
					{
						case 3: color = ChatColor.YELLOW; break;
						case 2: color = ChatColor.GOLD; break;
						case 1: color = ChatColor.RED; break;
						default: break;
					}
					
					handler.messageForFew(nicks, color + "" + ChatColor.BOLD + "[Exams][INFO] " + i);
					for(String nick : nicks)
						handler.teleport(locations.get(nick), nick);
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}				
				}
				currentThread().interrupt();
			}
		}; thread.start();
	} */
	
	private List<String> questions = new ArrayList<String>();
 	private	List<String> answers = new ArrayList<String>();
	private void settinQuestionsAndAnswers() 
	{
		questions.add("1"); 									///////////////////////////////////////// придумац вопросы и ответы
		questions.add("2");
		questions.add("3");
		
		answers.add("4");
		answers.add("5");
		answers.add("6");
	}
	
	private void scrollGivin() 
	{
		for(int i = 0; i < handler.getLeaders().size(); i++) 
		{		
			for(String leaderNick : handler.getLeaders()) 
			{
				Player leader = Bukkit.getPlayer(leaderNick);
				if(i % 2 == 0)
				{
					leader.getInventory().addItem(new ItemStack(Material.BOOK, 1)); // заменить на норм вещь
					leader.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "[Exams][INFO] Вы - лидер команды. Вам выдан свиток Земли");
				}
				else 
				{
					leader.getInventory().addItem(new ItemStack(Material.PAPER, 1)); // заменить на норм вещь
					leader.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "[Exams][INFO] Вы - лидер команды. Вам выдан свиток Неба");
				}
			}
		}
	}
	
	private void scrollRemovin() 
	{
		Material skyScroll = Material.PAPER;
		Material earthScroll = Material.BOOK;
		
		for(String memberNick : handler.getMembers()) 
		{
			Player member = Bukkit.getPlayer(memberNick);
			for(ItemStack is : member.getInventory()) 
			{
				if(is.getType() == skyScroll)
					member.getInventory().remove(new ItemStack(skyScroll, is.getAmount()));
				else if(is.getType() == earthScroll)
					member.getInventory().remove(new ItemStack(earthScroll, is.getAmount()));
			}
		}
	}
	
	private void stagePreparation() 
	{
		for(Player p : Bukkit.getOnlinePlayers()) 
		{
			for(String s : handler.getMembers())
				p.sendMessage(s);
		}
		
		handler.setStage(handler.getStage() + 1);
		
		if(handler.getMembers().size() < 3)  ///////////////////////////////// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! УБРАЦ
		{
			handler.messageForFew(handler.getMembers(), ChatColor.RED + "[Exams][ERROR] Для начала этапа недостаточно людей!");
			stop();
			return;
		} 
		
		switch(handler.getStage())  /// ТУТ КАРОЧЕ ПУТАНИЦА С ЭТАПАМИ ТИПА МЕНЯЕТСЯ ЭТАП И ЕБАШИТ СРАЗУ ДРУГОЕ
		{
		case 1:
			settinQuestionsAndAnswers();
			break;
		case 2:			
			scrollGivin();
			break;
		case 3:
			// чет сделать
			break;
		default: break;
		}	
		
		switch(handler.getStage()) 
		{
		case 1:
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Начался 1-ый этап экзамена на ранг Чунин!");
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] Суть этапа заключается в ответе на вопросы");
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] Если не отвечаете - дисквалифицируетесь вместе со своей командой");
			break;
		case 2:
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Начался 2-ой этап экзамена на ранг Чунин!");
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] Каждой команде выдается свиток, всего существует 2 типа свитков");
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] Ваша задача - заполучить 2 типа свитков и донести их до башни, а затем открыть");
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] До башни свитки открывать не стоит");
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] При выбывании одного сокомандовца, выбывает вся команда");
			break;
		case 3:
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Начался 3-ий этап экзамена на ранг Чунин!");
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] Данный этап является боями 1 на 1, каждый бой по 2 минуты");
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] Запрещено уходить за пределы арены, использовать природную чакру");
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] Противников выбирают по силе, определенной ранее");
			handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] Вы можете сражаться как и со своими напарниками, так и с членами других команд");
			break;
		default: break;
		}
		
		try {
			handler.stageTeleport();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stageEndin() throws IOException 
	{
		if(handler.getLosers().size() == 0)
			return;
		handler.messageForFew(handler.getMembers(), ChatColor.RED + "[Exams][INFO] Экзамен покинуло " + handler.getLosers().size() + " участников"); 
		
		switch(handler.getStage()) 
		{
		case 1:
			handler.getLosers().clear();
			for(String memberNick : handler.getMembers()) 
				handler.setAnswer(memberNick, "");
			secondStage();
			break;
		case 2:
			handler.getLosers().clear();
			scrollRemovin();
			thirdStage();
			break;
		case 3:
			stop();
			break;
		default: break;
		}		
	}
	
	public void startinChuninExam() 
	{
		for(String memberNick : handler.getMembers())
			handler.setAnswer(memberNick, "");
		firstStage();
	}
	
	private void firstStage() 
	{
		stagePreparation();

		handler.messageForFew(Bukkit.getOnlinePlayers(), "этап: " + handler.getStage());
		handler.messageForFew(handler.getMembers(), ChatColor.GREEN + "[Exams][INFO] Для ответа используйте /exams answer *ваш ответ*");
		handler.messageForFew(handler.getMembers(), ChatColor.GREEN + "[Exams][INFO] У вас будет минута на ответ");
		
		Thread thread = new Thread()
		{
			public void run() 
			{
				if(!examThreads.contains(currentThread()))
					examThreads.add(currentThread());
				
				for(int i = 0; i < questions.size(); i++) 
				{
					for(String memberNick : handler.getMembers()) 
					{
						handler.setAnswer(memberNick, "");
						handler.message(memberNick, ChatColor.GOLD + "[Exams][QUESTION] " + questions.get(i));
					}
					handler.messageForFew(Bukkit.getOnlinePlayers(), "" + handler.getStage());
					
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
					
					List<String> losers = new ArrayList<String>();
					for(String memberNick : handler.getMembers()) 
					{
						if(Bukkit.getPlayer(memberNick) != null) 
						{
							handler.message(memberNick, ChatColor.GREEN + "[Exams][INFO] Время окончено, проверка началась");
							
							if(handler.getAnswer(memberNick).equalsIgnoreCase(""))
							{
								handler.message(memberNick, ChatColor.RED + "[Exams][INFO] Вы ничего не ответили и были исключены!");
								losers.add(memberNick);
							}
							else if(handler.getAnswer(memberNick).equalsIgnoreCase(answers.get(i))) 
							{
								handler.message(memberNick, ChatColor.GREEN + "[Exams][INFO] Верно!");
							}
							else if(handler.getAnswer(memberNick).equalsIgnoreCase(answers.get(i))) 
							{
								losers.add(memberNick);
								handler.message(memberNick, ChatColor.GREEN + "[Exams][INFO] Не верно!");
							}
						}
					} 
					for(String loserNick : losers) 
					{
						handler.waitinRoomTeleport(loserNick);
						Bukkit.getPlayer(loserNick).setHealth(0.0D);
					}
					losers.clear();
					
					if(handler.getMembers().isEmpty()) 
					{
						handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.RED + "[Exams][INFO] Прошедших экзамен нет");
						stop();
					}
					
					currentThread().interrupt();
				}
			}
		}; thread.start();
		
		try {
			thread.join();
		} catch (InterruptedException i) {
			i.printStackTrace();
		}
		
		try {
			stageEndin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void secondStage() 
	{
		stagePreparation();
		Thread thread = new Thread()
		{
			public void run() 
			{	
				if(!examThreads.contains(currentThread()))
					examThreads.add(currentThread());
				
				// COUNTDOWN
				
				final Map<String, Location> locations = new HashMap<String, Location>();
				for(String nick : handler.getMembers())
					locations.put(nick, Bukkit.getPlayer(nick).getLocation());
				
				for(int i = 3; i > 0; i--) 
				{
					ChatColor color = null;
					switch(i) 
					{
						case 3: color = ChatColor.YELLOW; break;
						case 2: color = ChatColor.GOLD; break;
						case 1: color = ChatColor.RED; break;
						default: break;
					}
					
					handler.messageForFew(handler.getMembers(), color + "" + ChatColor.BOLD + "[Exams][INFO] " + i);
					for(String nick : handler.getMembers()) 
					{
						if(Bukkit.getPlayer(nick) != null)
							handler.teleport(locations.get(nick), nick);
					}
						
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				// ///////////////////////////////////////////
				
				final int membersSize = handler.getMembers().size();
				for(int i = 1200; i > 0; i--) 
				{
					if(handler.getMembers().isEmpty()) 
					{
						handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.RED + "[Exams][INFO] Прошедших экзамен нет");
						stop();
					}
					
					if(handler.getMembers().size() <= membersSize / 2) 
					{
						handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Половина участников поражена, этап окончен!");
						break;
					}
	
					if(i % 300 == 0) 
					{
						handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] До конца этапа " + i/60 + " минут!");
					}
					
					currentThread().interrupt();
				}
			}
		}; thread.start();
		
		try {
			thread.join();
		} catch (InterruptedException i) {
			i.printStackTrace();
		}
		
		try {
			stageEndin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void thirdStage() 
	{
		stagePreparation();
		Thread thread = new Thread()
		{
			public void run() 
			{		
				if(!examThreads.contains(currentThread()))
					examThreads.add(currentThread());
				
				while(handler.getMembers().size() > 0)
				{
					int maxPower = 0;
					for(String member : handler.getMembers()) 
					{
						if(new PowerPlayer(member).getPower() > maxPower) 
						{
							maxPower = new PowerPlayer(member).getPower();
							opponents[0] = member;
						}			
					}
					handler.getMembers().remove(opponents[0]);		
					if(handler.getMembers().size() == 0) 
						opponents[1] = handler.getWinners(handler.getWinners().size() - 1); // узнать по поводу вайл
					else 
					{
						maxPower = 0;
						for(String member : handler.getMembers()) 
						{
							if(new PowerPlayer(member).getPower() > maxPower) 
							{
								maxPower = new PowerPlayer(member).getPower();
								if(opponents[0] == member) 
									handler.getWinners(handler.getWinners().size() - 1);
								else
									opponents[1] = member;
							}	
						}
					}
					
					handler.getMembers().remove(opponents[1]);
					handler.arenaTeleport();
					handler.messageForFew(handler.getMembers(), ChatColor.GOLD + "[Exams][INFO] А сейчас сразятся " + opponents[0] + " и " + opponents[1]);
					
					// COUNTDOWN
					
					final Map<String, Location> locations = new HashMap<String, Location>();
					for(String nick : opponents)
						locations.put(nick, Bukkit.getPlayer(nick).getLocation());
					
					for(int i = 3; i > 0; i--) 
					{
						ChatColor color = null;
						switch(i) 
						{
							case 3: color = ChatColor.YELLOW; break;
							case 2: color = ChatColor.GOLD; break;
							case 1: color = ChatColor.RED; break;
							default: break;
						}
						
						handler.messageForFew(opponents, color + "" + ChatColor.BOLD + "[Exams][INFO] " + i);
						for(String nick : opponents) 
						{
							if(Bukkit.getPlayer(nick) != null)
								handler.teleport(locations.get(nick), nick);
						}
							
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					// //////////////////////////////
					
					final int winnersSize = handler.getWinners().size();
					int count = 120;
					while(count > 0) 
					{
						if(handler.getLosers().size() > winnersSize && count != 0) 
						{
							for(String memberNick : handler.getMembers()) 
								handler.message(memberNick, 
												ChatColor.GOLD + "" + ChatColor.BOLD + "[Exams][INFO] У нас есть победитель! " +
												ChatColor.AQUA + handler.getLeaders().get(winnersSize) + 
												ChatColor.GOLD + " разделал в щепки " + ChatColor.AQUA + handler.getLosers().get(winnersSize));
							break;
						}
						else 
						{
							handler.messageForFew(handler.getMembers(), ChatColor.GOLD + "[Exams][INFO] Никто не был побежден за 120 секунд, соперники исключены");
							for(int i = 0; i < 2; i++) 
							{
								try {
									handler.addLoser(opponents[i]);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					opponents[0] = "";
					opponents[1] = "";
				}
				
				currentThread().interrupt();
			}
		}; thread.start();
		
		try {
			thread.join();
		} catch (InterruptedException i) {
			i.printStackTrace();
		}
		
		try {
			stageEndin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
}