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
	
	public String[] opponents = new String[2];
	
	public ExamsHandler handler;
	//ExamsNestedCommands nestedCommands;
	List<Thread> examThreads = new ArrayList<Thread>(); 
	
	public ExamsChunins(ExamsHandler handler) 
	{
		this.handler = handler;
		handler.setChunins(this);
	}
	
	
	public void examStop() // порешать насчет листа с тредами
	{		
		List<String> members = handler.getMembers();
		for(String memberNick : members) 
		{
			if(!handler.getWinners().contains(memberNick)) 
			{
				try {
					handler.addLoser(memberNick);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(handler.getWinners().isEmpty()) 
		{
			handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.RED + "[Exams][INFO] Никто не прошел экзамен!");
		}
		else 
		{
			handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.AQUA + "[Exams][INFO] Прошедшие экзамен:");
			for(String winnerNick : handler.getWinners()) 
				handler.messageForFew(Bukkit.getOnlinePlayers(), ChatColor.AQUA + "[Exams][INFO] " + winnerNick);
		}
		
		handler.canStart = true;
		handler.getMembers().clear();
		handler.getLeaders().clear();
		handler.getLosers().clear();
		handler.getWinners().clear();
		handler.setStage(0);
		handler.setExam("");
		opponents[0] = "";
		opponents[1] = "";
		
		for(Thread thread : examThreads) 
		{
			while(!thread.isInterrupted()) 
			{	          
		          try {
		              Thread.sleep(1000);
		          } catch (InterruptedException e) {
		              thread.interrupt();
		          }
			}
		}
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
			for(ItemStack is : member.getInventory().getContents()) 
			{
				if(is != null) 
				{
					if(is.getType() == skyScroll) 
						member.getInventory().remove(new ItemStack(skyScroll, is.getAmount()));
					else if(is.getType() == earthScroll) 
						member.getInventory().remove(new ItemStack(earthScroll, is.getAmount()));
				}
			}
		}
	}
	
	private void stagePreparation() 
	{
		
		handler.setStage(handler.getStage() + 1);
		
		switch(handler.getStage())  
		{
		case 1:
			for(String memberNick : handler.getMembers())
				handler.setAnswer(memberNick, "");
			settinQuestionsAndAnswers();
			break;
		case 2:
			if(handler.getMembers().size() < 12) 
			{
				if(handler.getMembers().size() <= 6 && handler.getMembers().size() > 0) 
				{
					try {
						handler.addWinners(handler.getMembers());
					} catch (IOException e) {
						e.printStackTrace();
					}
					examStop();
					return;
				}
				else if(handler.getMembers().size() > 6) 
				{
					stageEndin();
					return;
				}
				else if(handler.getMembers().isEmpty())
				{
					examStop();
					return;
				}
			}
			scrollGivin();
			break;
		case 3:
			if(handler.getMembers().size() < 2) 
			{
				if(handler.getMembers().isEmpty()) 
				{
					for(String memberNick : handler.getMembers())
						try {
							handler.addWinner(memberNick);
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
				else 
				{
					examStop();
					return;
				}
			}
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
	
	public void stageEndin() 
	{
		if(handler.getLosers().size() != 0) 
		{
			handler.messageForFew(handler.getMembers(), ChatColor.RED + "[Exams][INFO] Экзамен покинуло " + handler.getLosers().size() + " участников"); 
			//handler.messageOnScreenForFew(handler.getMembers(), ChatColor.RED + "[Exams][INFO] Экзамен покинуло " + handler.getLosers().size() + " участников"); 
		}
		
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
			handler.getWinners().clear();
			scrollRemovin();
			thirdStage();
			break;
		case 3:
			examStop();
			break;
		default: break;
		}		
	}
	
	public void startinChuninExam() 
	{
		firstStage();
	}
	
	private void firstStage() 
	{	
		Thread thread = new Thread()
		{
			public void run() 
			{
				if(!examThreads.contains(currentThread()))
					examThreads.add(currentThread());
				
				stagePreparation();

				handler.messageForFew(Bukkit.getOnlinePlayers(), "этап: " + handler.getStage());
				handler.messageForFew(handler.getMembers(), ChatColor.GREEN + "[Exams][INFO] Для ответа используйте /exams answer *ваш ответ*");
				handler.messageForFew(handler.getMembers(), ChatColor.GREEN + "[Exams][INFO] У вас будет минута на ответ");
				
				for(int i = 0; i < questions.size(); i++) 
				{
					for(String memberNick : handler.getMembers()) 
					{
						handler.setAnswer(memberNick, "");
						handler.message(memberNick, ChatColor.GOLD + "[Exams][QUESTION] " + questions.get(i));
					}
					
					try {
						Thread.sleep(12000); // 60000
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
					
					List<String> losers = new ArrayList<String>();
					/*for(String memberNick : handler.getMembers()) 
					{
						if(Bukkit.getPlayer(memberNick) != null) 
						{
							handler.message(memberNick, ChatColor.GREEN + "[Exams][INFO] Время окончено, проверка началась");                   УБРАТЬ ИЗ КОММЕНТАРИЕВ
							
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
					} */
					
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
						examStop();
				}
				
				stageEndin();
				currentThread().interrupt();
			}
		}; thread.start();
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
					
					handler.messageForFew(handler.getMembers(), color + "[Exams][INFO] " + i);
					//handler.messageOnScreenForFew(handler.getMembers(), color + "[Exams][INFO] " + i);
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
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
					
					if(handler.getMembers().size() <= Math.round(membersSize / 2)) 
					{
						handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Exams][INFO] Половина участников поражена, этап окончен!");
						break;
					}
	
					if(i % 300 == 0) 
					{
						handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] До конца этапа " + i/60 + " минут!");
						//handler.messageOnScreenForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] До конца этапа " + i/60 + " минут!");
					}
				} 
				
				for(String memberNick : handler.getMembers()) 
				{
					if(!handler.getWinners().contains(memberNick))
						try {
							handler.addLoser(memberNick);
						} catch (IOException e) {
							e.printStackTrace();
						}
				}

				
				/*handler.messageForFew(Bukkit.getOnlinePlayers(), "wait...");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.messageForFew(Bukkit.getOnlinePlayers(), "ready!"); */
				
				stageEndin();
				currentThread().interrupt();
			}
		}; thread.start();
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
					for(String memberNick : handler.getMembers()) 
					{
						if(new PowerPlayer(memberNick).getPower() > maxPower) 
						{
							maxPower = new PowerPlayer(memberNick).getPower();
							opponents[0] = memberNick;
						}		
					}
					handler.getMembers().remove(opponents[0]); // по поводу вайла
					
					if(handler.getMembers().size() == 0) 
					{
						handler.message(opponents[0], ChatColor.GREEN + "[Exams][INFO] Вы остались одни в списке участвующих и автоматически проходите экзамен");
						try {
							handler.addWinner(opponents[0]);
						} catch (IOException e) {
							e.printStackTrace();
						}
						examStop();
						return;
					}
					
					maxPower = 0;
					for(String memberNick : handler.getMembers()) 
					{
						if(opponents[0] != memberNick)
						{							
							if(new PowerPlayer(memberNick).getPower() > maxPower) 
							{
								maxPower = new PowerPlayer(memberNick).getPower();
								opponents[1] = memberNick;
							}	
						}
					}
					
					handler.getMembers().remove(opponents[1]);
					handler.arenaTeleport();
					
					handler.messageForFew(handler.getMembers(), ChatColor.GOLD + "[Exams][INFO] А сейчас сразятся " + opponents[0] + " и " + opponents[1]);
					handler.messageForFew(opponents, ChatColor.GOLD + "[Exams][INFO] А сейчас сразятся " + opponents[0] + " и " + opponents[1]);
					
					//handler.messageOnScreenForFew(handler.getMembers(), ChatColor.GOLD + "[Exams][INFO] А сейчас сразятся " + opponents[0] + " и " + opponents[1]); !!!!!!!!!!!!!!!!!!!!!!!!!!!!
					//handler.messageOnScreenForFew(opponents, ChatColor.GOLD + "[Exams][INFO] А сейчас сразятся " + opponents[0] + " и " + opponents[1]);
					
					// COUNTDOWN ////////////////////
					
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
						
						handler.message(opponents[0], color + "" + ChatColor.BOLD + "[Exams][INFO] " + i);
						handler.message(opponents[1], color + "" + ChatColor.BOLD + "[Exams][INFO] " + i);
						
						//handler.messageOnScreen(opponents[0], color + "" + ChatColor.BOLD + "[Exams][INFO] " + i); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
						//handler.messageOnScreen(opponents[1], color + "" + ChatColor.BOLD + "[Exams][INFO] " + i);
						
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
					
					handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] До конца поединка осталось 120 секунд");
					handler.messageForFew(opponents, ChatColor.LIGHT_PURPLE + "[Exams][INFO] До конца поединка осталось 120 секунд");
					
					//handler.messageOnScreenForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] До конца поединка осталось 120 секунд"); !!!!!!!!!!!!!!!!!!
					//handler.messageOnScreenForFew(opponents, ChatColor.LIGHT_PURPLE + "[Exams][INFO] До конца поединка осталось 120 секунд");
					
					final int winnersSize = handler.getWinners().size();
					for(int count = 120; count > 0; count--) 
					{	
						if(count == 60) 
						{
							handler.messageForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] До конца поединка осталось 60 секунд");
							handler.messageForFew(opponents, ChatColor.LIGHT_PURPLE + "[Exams][INFO] До конца поединка осталось 60 секунд");
							
							//handler.messageOnScreenForFew(handler.getMembers(), ChatColor.LIGHT_PURPLE + "[Exams][INFO] До конца поединка осталось 60 секунд"); !!!!!!!!!!!!!!!!!!!!!!!!!!!
							//handler.messageOnScreenForFew(opponents, ChatColor.LIGHT_PURPLE + "[Exams][INFO] До конца поединка осталось 60 секунд");
						}
						
						if(handler.getWinners().size() > winnersSize) 
						{
							handler.messageForFew(handler.getMembers(), 
											ChatColor.GOLD + "" + ChatColor.BOLD + "[Exams][INFO] У нас есть победитель! " +
											ChatColor.AQUA + handler.getWinners().get(winnersSize) + 
											ChatColor.GOLD  + ChatColor.BOLD + " разделал в щепки " + ChatColor.AQUA + handler.getLosers().get(winnersSize));
							
							handler.messageForFew(opponents, 
									ChatColor.GOLD + "" + ChatColor.BOLD + "[Exams][INFO] У нас есть победитель! " +
									ChatColor.AQUA + handler.getWinners().get(winnersSize) + 
									ChatColor.GOLD  + ChatColor.BOLD + " разделал в щепки " + ChatColor.AQUA + handler.getLosers().get(winnersSize));
							
							/*handler.messageOnScreenForFew(handler.getMembers(), 
									ChatColor.GOLD + "" + ChatColor.BOLD + "[Exams][INFO] У нас есть победитель! " +
									ChatColor.AQUA + handler.getWinners().get(winnersSize) + 
									ChatColor.GOLD  + ChatColor.BOLD + " разделал в щепки " + ChatColor.AQUA + handler.getLosers().get(winnersSize)); !!!!!!!!!!!!!!!!!!!!!!!!
					
							handler.messageOnScreenForFew(opponents, 
									ChatColor.GOLD + "" + ChatColor.BOLD + "[Exams][INFO] У нас есть победитель! " +
									ChatColor.AQUA + handler.getWinners().get(winnersSize) + 
									ChatColor.GOLD  + ChatColor.BOLD + " разделал в щепки " + ChatColor.AQUA + handler.getLosers().get(winnersSize));	*/						
							break;
						}
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					if(handler.getWinners().size() == winnersSize) 
					{
						handler.messageForFew(handler.getMembers(), ChatColor.GOLD + "[Exams][INFO] Никто не был побежден за 120 секунд, соперники исключены");
						handler.messageForFew(opponents, ChatColor.GOLD + "[Exams][INFO] Никто не был побежден за 120 секунд, соперники исключены");
						
						//handler.messageOnScreenForFew(handler.getMembers(), ChatColor.GOLD + "[Exams][INFO] Никто не был побежден за 120 секунд, соперники исключены"); ПОДУМАТЬ НАД ЦВЕТАМИ СООБЩЕНИЙ
						//handler.messageOnScreenForFew(opponents, ChatColor.GOLD + "[Exams][INFO] Никто не был побежден за 120 секунд, соперники исключены"); !!!!!!!!!!! на 2 этапе тоже сделать онскрин
						
						for(String opponentNick : opponents) 
						{
							try {
								handler.addLoser(opponentNick);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					
					opponents[0] = "";
					opponents[1] = "";
				}
				
				stageEndin();
				currentThread().interrupt();
			}
		}; thread.start();
	} 
}