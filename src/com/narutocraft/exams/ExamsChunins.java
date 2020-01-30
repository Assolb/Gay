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

import com.narutocraft.power.PowerPlayer;

public class ExamsChunins {
	
	ExamsHandler handler = new ExamsHandler();
	ExamsNestedCommands nestedCommands = new ExamsNestedCommands();
	List<Thread> examThreads = new ArrayList<Thread>();
	
	private void stop() 
	{
		handler.messageForFew(Bukkit.getOnlinePlayers(), "СТОП САСАТЬ");
		nestedCommands.canStart = true; //!!!!!!!!!!!!!!!!!!!!!!!!
		handler.messageForFew(Bukkit.getOnlinePlayers(), "СТОП САСАТЬ");
		for(Thread thread : examThreads) 
		{
			if(thread.isAlive())
				thread.interrupt();
		}
		handler.messageForFew(Bukkit.getOnlinePlayers(), "СТОП САСАТЬ");
		handler.members.clear();
		handler.winners.clear();
		handler.leaders.clear();
		handler.losers.clear();
		handler.setStage(0);
		handler.setExam("");
		handler.messageForFew(Bukkit.getOnlinePlayers(), "СТОП САСАТЬ");
	}
	
	private void winnersShow() 
	{
		for(Player player : Bukkit.getOnlinePlayers()) 
		{
			handler.message(player, ChatColor.LIGHT_PURPLE + "[Exams][INFO] Прошедшие экзамен:");
			for(String winnerNick : handler.getWinners()) 
			{
				// Bukkit.getPlayer(winnerNick).perm дать пермишн !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				handler.message(player, ChatColor.AQUA + "[Exams][INFO] " + winnerNick);
			}
		}
		stop();
	}
	
	private void countdown() 
	{
		Thread thread = new Thread() 
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
	}
	
	private void membersClearin() 
	{
		for(String memberNick : handler.getMembers()) 
		{
			if(Bukkit.getPlayer(memberNick) == null)
				try {
					handler.addLoser(memberNick);
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	private void losersList() 
	{
		if(handler.getLosers().size() == 0)
			return;
		handler.messageForFew(handler.getMembers(), ChatColor.RED + "[Exams][INFO] Экзамен покинуло " + handler.getLosers().size() + " участников"); 
	}
	
	private void stageNotification() 
	{
		for(String memberNick : handler.getMembers()) 
		{
			Player member = Bukkit.getPlayer(memberNick);
			switch(handler.getStage()) 
			{
			case 1:
				member.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Начался 1-ый этап экзамена на ранг Чунин!");
				member.sendMessage(ChatColor.LIGHT_PURPLE + "[Exams][INFO] Суть этапа заключается в ответе на вопросы");
				member.sendMessage(ChatColor.LIGHT_PURPLE + "[Exams][INFO] Если не отвечаете - дисквалифицируетесь вместе со своей командой");
				break;
			case 2:
				member.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Начался 2-ой этап экзамена на ранг Чунин!");
				member.sendMessage(ChatColor.LIGHT_PURPLE + "[Exams][INFO] Каждой команде выдается свиток, всего существует 2 типа свитков");
				member.sendMessage(ChatColor.LIGHT_PURPLE + "[Exams][INFO] Ваша задача - заполучить 2 типа свитков и донести их до башни, а затем открыть");
				member.sendMessage(ChatColor.LIGHT_PURPLE + "[Exams][INFO] До башни свитки открывать не стоит");
				member.sendMessage(ChatColor.LIGHT_PURPLE + "[Exams][INFO] При выбывании одного сокомандовца, выбывает вся команда");
				break;
			case 3:
				member.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Начался 3-ий этап экзамена на ранг Чунин!");
				member.sendMessage(ChatColor.LIGHT_PURPLE + "[Exams][INFO] Данный этап является боями 1 на 1, каждый бой по 2 минуты");
				member.sendMessage(ChatColor.LIGHT_PURPLE + "[Exams][INFO] Запрещено уходить за пределы арены, использовать природную чакру");
				member.sendMessage(ChatColor.LIGHT_PURPLE + "[Exams][INFO] Противников выбирают по силе, определенной ранее");
				member.sendMessage(ChatColor.LIGHT_PURPLE + "[Exams][INFO] Вы можете сражаться как и со своими напарниками, так и с членами других команд");
				break;
				default: break;
			}
		}
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
	
	private boolean canStartStage() 
	{
		boolean b = true;
		if(ExamsHandler.members.size() < 3) 				////////////////////////// ИЗМЕНИТЬ НА 12
			b = false;                                       
		return b;
	}
	
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
	
	private void stagePreparation() 
	{
		membersClearin();
		handler.messageForFew(Bukkit.getOnlinePlayers(), "" + handler.getStage());
		System.out.println("suka soset adminu");
		handler.setStage(handler.getStage() + 1);	
		handler.messageForFew(Bukkit.getOnlinePlayers(), "" + handler.getStage());
		
		System.out.println("nikto ne soset adminu");
		if(!canStartStage())  ///////////////////////////////// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! УБРАЦ
		{
			handler.messageForFew(Bukkit.getOnlinePlayers(), "сосать админу мало людей");
			stop();
			return;
		} 
		System.out.println("return dlya uebanov");
		switch(handler.getStage())  /// ТУТ КАРОЧЕ ПУТАНИЦА С ЭТАПАМИ ТИПА МЕНЯЕТСЯ ЭТАП И ЕБАШИТ СРАЗУ ДРУГОЕ
		{
		case 1:
			settinQuestionsAndAnswers();
			break;
		case 2:			
			scrollGivin();
			countdown();
			break;
		case 3:
			handler.winners.clear();
			countdown();
			break;
		default: break;
		}
		
		System.out.println("suck pls");		
		stageNotification();
		try {
			handler.stageTeleport();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void stageEndin() throws IOException 
	{
		losersList();
		
		switch(handler.getStage()) 
		{
		case 1:
			handler.losers.clear();
			break;
		case 2:
			scrollRemovin();
			handler.losers.clear();
			break;
		case 3:
			winnersShow();
			stop();
			break;
		default: break;
		}
		membersClearin();
		
	}
	
	public void startinChuninExam() throws IOException 
	{
		firstStage();		
	}
	
	public void firstStage() throws IOException // СДЕЛАТЬ ПРИ НЕПРАВИЛЬНОМ ОТВЕТЕ КАКОЙ-ТО ИСХОД
	{		
		stagePreparation();
		Thread questionsThread = new Thread() 
		{
			public void run() 
			{
				if(!examThreads.contains(currentThread()))
					examThreads.add(currentThread());

				handler.messageForFew(handler.getMembers(), ChatColor.GREEN + "[Exams][INFO] Для ответа используйте /exams answer *ваш ответ*");
				handler.messageForFew(handler.getMembers(), ChatColor.GREEN + "[Exams][INFO] У вас будет минута на ответ");
				System.out.println("1");
				for(int i = 0; i < questions.size(); i++) 
				{
					System.out.println("2");
					for(String memberNick : handler.getMembers()) 
					{
						handler.setAnswer(memberNick, "");
						handler.message(memberNick, ChatColor.GOLD + "[Exams][QUESTION] " + questions.get(i));
					}
					
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
					
					List<String> losers = new ArrayList<String>();
					for(String memberNick : handler.getMembers()) 
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
					
					for(String loserNick : losers) 
					{
						try {
							handler.addLoser(loserNick);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					losers.clear();
					
					if(handler.getMembers().isEmpty()) 
					{
						handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.RED + "[Exams][INFO] Прошедших экзамен нет");
						stop();
					}
				}
				for(String memberNick : handler.getMembers()) 
					handler.setAnswer(memberNick, "");
				try {
					stageEndin();
					secondStage();
				} catch (IOException e) {
					e.printStackTrace();
				}
				currentThread().interrupt();
			}
		}; questionsThread.start(); 
	}
	
	public void secondStage() 
	{
		stagePreparation();
		Thread thread = new Thread() 
		{
			public void run() 
			{	
				if(!examThreads.contains(currentThread()))
					examThreads.add(currentThread());
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
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
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				try {
					stageEndin();
					thirdStage();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				currentThread().interrupt();
			}
		}; thread.start();
	}
	
	public String[] opponents = null;
	public boolean canContinue = false;
	public void thirdStage() throws IOException 
	{
		stagePreparation();
		Thread thread = new Thread() 
		{
			public void run() 
			{
				if(!examThreads.contains(currentThread()))
					examThreads.add(currentThread());
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				while(handler.getMembers().size() != 0)
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
					
					final int winnersSize = handler.getWinners().size();
					int count = 120;
					while(count != 0) 
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
								try {
									handler.addLoser(opponents[i]);
								} catch (IOException e) {
									e.printStackTrace();
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
				try {
					stageEndin();
				} catch (IOException e) {
					e.printStackTrace();
				}
				currentThread().interrupt();
			}
		}; thread.start();
	}
}
