package com.gmail.bkunkcu.roleplaychat;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.bkunkcu.roleplaychat.Commands.RoleplayChatCommandExecutor;
import com.gmail.bkunkcu.roleplaychat.Nickname.DatabaseManager;
import com.gmail.bkunkcu.roleplaychat.Nickname.NicknameManager;

public class RoleplayChat extends JavaPlugin implements Listener {
		
	public FileManager FileManager = new FileManager(this);
	public DatabaseManager DatabaseManager = new DatabaseManager(this);
	public NicknameManager NicknameManager = new NicknameManager(this);
	public MessageBuilder MessageBuilder = new MessageBuilder(this);
	
	YamlConfiguration yml = new YamlConfiguration();
	
	@Override
	public void onEnable() {	
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("roleplaychat").setExecutor(new RoleplayChatCommandExecutor(this));
		
		FileManager.getFiles();
		DatabaseManager.open();
		NicknameManager.getIntegration();
	}
	
	@Override
	public void onDisable() {
		DatabaseManager.close();
	}
	
	public boolean hasPermission(Player player, String permission) {
		if(player == null)
			return true;
		else if(player != null && player.hasPermission(permission))
			return true;
		else
			return false;
	}
	
	public String getExactWorld(String world) {
		if(!FileManager.mirrors.containsKey(world)) {
			return world;
		}
		
		else {
			return FileManager.mirrors.get(world);
		}
	}
	
	//Listeners
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		if(MessageBuilder.isDefault(player)) {
			event.setCancelled(true);
			
			MessageBuilder.sendMessage(player, "default" , message);
		}
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String world = getExactWorld(player.getWorld().getName());
		
		String[] input = event.getMessage().split(" ", 2);
		String command = input[0].replace("/", "");
		
		if(input.length != 1) {
			String message = input[1];

			for(String key : FileManager.modes.get(world)) {
				File file = new File(getDataFolder(), world + "/chat.yml");
				
				try {
					yml.load(file);
				} catch (Exception e) {
					getLogger().info("Couldn't load chat.yml files. Disabling plugin!");
					Bukkit.getPluginManager().disablePlugin(this);
				}
				
				for(String s : yml.getStringList(key + ".commands")) {
					if(s.equalsIgnoreCase(command)) {
						event.setCancelled(true);
						MessageBuilder.sendMessage(player, key, message);
					}
				}
			}
		}
	}	
}