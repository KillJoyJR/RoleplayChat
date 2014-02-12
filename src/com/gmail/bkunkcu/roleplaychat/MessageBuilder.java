package com.gmail.bkunkcu.roleplaychat;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MessageBuilder {
	
	private RoleplayChat plugin;
	YamlConfiguration yml = new YamlConfiguration();
	
	public MessageBuilder(RoleplayChat plugin) {
		this.plugin = plugin;
	}
		
	public boolean isDefault(Player player) {
		if(plugin.FileManager.modes.get(plugin.getExactWorld(player.getWorld().getName())).contains("default")) {
			return true;
		}
		
		return false;
	}
	
	public void sendMessage(Player player, String key, String message) {		
		File file = new File(plugin.getDataFolder(), plugin.getExactWorld(player.getWorld().getName()) + "/chat.yml");
		
		try {
			yml.load(file);
		} catch (Exception e) {
			plugin.getLogger().info("Couldn't load chat.yml files. Disabling plugin!");
			Bukkit.getPluginManager().disablePlugin(plugin);
		}
		
		if(!yml.getBoolean(key + ".permission") || player.hasPermission("roleplaychat." + key)) {
			String displayMessage = yml.getString(key + ".format").replace("&", "§").replace("%username%", plugin.NicknameManager.getNickname(player)).replace("%message%", message).replace("%prefix%", plugin.NicknameManager.getPrefix(player)).replace("%suffix%", plugin.NicknameManager.getSuffix(player));
			
			if(plugin.getConfig().getBoolean("settings.logging.console")) {
				Bukkit.getConsoleSender().sendMessage(displayMessage.replace("§", "&"));
			}
			
			for(Player receiver : Bukkit.getOnlinePlayers()) {
				
				if(yml.getInt(key + ".radius") == -1) {
					receiver.sendMessage(displayMessage);
				}
				
				else if (yml.getInt(key + ".radius") == 0) {
					if(receiver.getWorld().getName() == player.getWorld().getName()) {
						receiver.sendMessage(displayMessage);
					}
					
					else if(receiver.getWorld().getName() != player.getWorld().getName() && plugin.FileManager.spy.contains(receiver.getName())) {
						receiver.sendMessage("§8[SPY] §r" + displayMessage);
					}
				}
				
				else {
					if(receiver.getWorld().getName() == player.getWorld().getName()) {
						double distance =  player.getLocation().distance(receiver.getLocation());
						
						if(distance <= yml.getInt(key + ".radius")) {
							receiver.sendMessage(displayMessage);
						}
						
						else if(distance > yml.getInt(key + ".radius") && plugin.FileManager.spy.contains(receiver.getName())) {
							receiver.sendMessage("§8[SPY] §r" + displayMessage);
						}
					}
					
					else if(receiver.getWorld().getName() != player.getWorld().getName() && plugin.FileManager.spy.contains(receiver.getName())) {
						receiver.sendMessage("§8[SPY] §r" + displayMessage);
					}
				}
			}					
		} 
		
		else {
			player.sendMessage("§4You don't have permissions to use this command");
		}
	}

}