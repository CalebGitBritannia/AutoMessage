package me.shawshark.automessage;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class AutoMessager extends JavaPlugin implements CommandExecutor {

	public HashMap<Integer, String> messages = new HashMap<Integer, String>();

	public int messageid; // this is the currect message id we are up to.
	public int lastMessageID = -1;
	public int secondLastMessageID = -1;
	public int thirdlastMessageID = -1;

	public int timer = 40; // default 40

	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}

	public boolean random;
	
	public int bukkitidTask;

	public HashMap<Integer, String> getMessages() {
		return messages;
	}

	public void setMessages(HashMap<Integer, String> messages) {
		this.messages = messages;
	}

	public int getMessageid() {
		return messageid;
	}

	public void setMessageid(int messageid) {
		this.messageid = messageid;
	}

	public int getLastMessageID() {
		return lastMessageID;
	}

	public void setLastMessageID(int lastMessageID) {
		this.lastMessageID = lastMessageID;
	}

	public int getSecondLastMessageID() {
		return secondLastMessageID;
	}

	public void setSecondLastMessageID(int secondLastMessageID) {
		this.secondLastMessageID = secondLastMessageID;
	}

	public int getThirdlastMessageID() {
		return thirdlastMessageID;
	}

	public void setThirdlastMessageID(int thirdlastMessageID) {
		this.thirdlastMessageID = thirdlastMessageID;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public int getBukkitidTask() {
		return bukkitidTask;
	}

	public void setBukkitidTask(int bukkitidTask) {
		this.bukkitidTask = bukkitidTask;
	}

	public void onEnable() {
		
		getCommand("automessage").setExecutor(this);
		
		getConfig().addDefault("timer", 60);
		
		List<String> defaultMessages = new ArrayList<String>();
		defaultMessages.add("&eThis is default message 1, Please change this in &cconfig.yml");
		defaultMessages.add("&cThis is default message 2, You can change this in &cconfig.yml");
		defaultMessages.add("&fThis is default message 3, You can change this in &cconfig.yml");
		defaultMessages.add("&bThis is default message 4, You can change this in &cconfig.yml");
		defaultMessages.add("&6This is default message 5, You can change this in &cconfig.yml");
		defaultMessages.add("&7This is default message 6, You can change this in &cconfig.yml");
		
		getConfig().addDefault("Random", false);
		
		getConfig().addDefault("Messages", defaultMessages);
		getConfig().options().copyDefaults(true);
		saveConfig();
		loadMessages();
	}
	
	public void loadMessages() {
		
		getMessages().clear();
		
		setMessageid(1);
		setLastMessageID(-1);
		
		setRandom(getConfig().getBoolean("Random", false));
		
		setTimer(getConfig().getInt("timer"));
		
		int start = 1;
		
		if(getConfig().getStringList("Messages") != null) {
			for(String message : getConfig().getStringList("Messages")) {
				getMessages().put(start, message);
				start++;
			}

		}
		run();
	}
	
	
	public void run() {
		bukkitidTask = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if(!getMessages().isEmpty()) {
					
					for(Entry<Integer, String> i : getMessages().entrySet()) {
						
						if(random) {
							int randomgen = getRandom();
							if(i.getKey() == randomgen) {
								if(getLastMessageID() == randomgen || getSecondLastMessageID() == randomgen || getThirdlastMessageID() == randomgen) {
									continue;
								} else {

									String msg = i.getValue();
									String mes = ChatColor.translateAlternateColorCodes('&', msg);
									String URL= "NULL";
									if (mes.contains(" JSON_URL:")) {
										URL = mes.split(" JSON_URL:")[1];
										mes = mes.split(" JSON_URL:")[0];
									}
									for (Player player : Bukkit.getOnlinePlayers()) {
										TextComponent component = new TextComponent(mes);
										if (URL != "NULL") component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URL));
										player.spigot().sendMessage(component);
									}
									setThirdlastMessageID(getSecondLastMessageID());
									setSecondLastMessageID(getLastMessageID());
									setLastMessageID(i.getKey());
									break;
									
								}
							}
						} else {
							
							if(i.getKey() == getMessageid()) {
								
								String message = i.getValue();
								messageid++;

								String mes = ChatColor.translateAlternateColorCodes('&', message);
								String URL= "NULL";
								if (mes.contains(" JSON_URL:")) {
									URL = mes.split(" JSON_URL:")[1];
									mes = mes.split(" JSON_URL:")[0];
								}
								for (Player player : Bukkit.getOnlinePlayers()) {
									TextComponent component = new TextComponent(mes);
									if (URL != "NULL") component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URL));
									player.spigot().sendMessage(component);
								}
								if(getMessageid() == getMessages().size() + 1) {
									setMessageid(1);
								}
								
								break;
								
								
							}
							
						}
						
						
					}
					
				}
				

				
				
			} // for test reasons every 5 seconds.
		}.runTaskTimerAsynchronously(this, 100, getTimer() * 20).getTaskId();
		
	}
	
	public int getRandom() {
		int number = new Random().nextInt(getMessages().size());
		if(number == 0) {
			return 1;
		} else {
			return number;
		}
	}


	@Override
	public boolean onCommand(CommandSender sender, Command command, String arg2, String[] arg3) {
		
		if(!sender.hasPermission("automessage.reload")) { 
			PlayerUtils.msg(sender, "You don't have permissions to use this command");
			return true;
		}
		
		reloadConfig();
		saveConfig();
		
		getServer().getScheduler().cancelTask(getBukkitidTask());
		loadMessages();
		PlayerUtils.msg(sender, "&aYou've reloaded the config.yml config file.");
		return true;
	}
}
