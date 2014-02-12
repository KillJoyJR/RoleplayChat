package com.gmail.bkunkcu.roleplaychat.Nickname;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.gmail.bkunkcu.roleplaychat.RoleplayChat;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class NicknameManager {
	
	private GroupManager groupManager;
	private RoleplayChat plugin;
	private String integration;
	
	public NicknameManager(RoleplayChat plugin) {
		this.plugin = plugin;
	}
	
	public void getIntegration() {
		
		final PluginManager pluginManager = plugin.getServer().getPluginManager();
		final Plugin GMplugin = pluginManager.getPlugin("GroupManager");
		final Plugin ECplugin = pluginManager.getPlugin("EssentialsChat");
		final Plugin PEXplugin = pluginManager.getPlugin("PermissionsEx");
				
		if(GMplugin != null && GMplugin.isEnabled()) {
			integration = "GroupManager";
			groupManager = (GroupManager)GMplugin;
			plugin.getLogger().info("GroupManager found!");
		}
		
		if(ECplugin != null && ECplugin.isEnabled()) {
			plugin.getLogger().info("EssentialsChat found!");
		}
		
		if(PEXplugin != null && PEXplugin.isEnabled()) {
			integration = "PermissionsEx";
			plugin.getLogger().info("PermissionsEx found!");
		}
		
	}
	
	public String getNickname(Player player) {
		String nickname = null;
		
		if(plugin.getConfig().getBoolean("settings.useNickname")) {
			try {
				ResultSet rs = plugin.DatabaseManager.query("SELECT * FROM nicknames WHERE username='" + player.getName() + "'");
				while(rs.next()) {
					nickname = plugin.getConfig().getString("settings.nicknamePrefix") + rs.getString("nickname");
				}
			} catch (SQLException e) {}
			
			if(nickname != null) {
				return nickname;
			}
			
			else {
				return player.getName();
			}
		} 
		
		else {
			return player.getDisplayName();
		}	
	}
	
	public String getPrefix(Player player) {
		if(integration == "GroupManager" && plugin.getConfig().getBoolean("settings.useNickname")) {
			final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player);
			
			if (handler == null)
				return "";
			else
				return handler.getUserPrefix(player.getName()).replace("&", "§");
		}
		
		else if(integration == "PermissionsEx" && plugin.getConfig().getBoolean("settings.useNickname")) {
			PermissionUser user = PermissionsEx.getUser(player);
			
			if (user == null)
				return "";
			else
				return user.getPrefix().replace("&", "§");
		}
		
		else {
			return "";
		}
	}
	
	public String getSuffix(Player player) {
		if(integration == "GroupManager" && plugin.getConfig().getBoolean("settings.useNickname")) {
			final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player);
			
			if (handler == null)
				return "";
			else
				return handler.getUserSuffix(player.getName()).replace("&", "§");
		}
		
		else if(integration == "PermissionsEx" && plugin.getConfig().getBoolean("settings.useNickname")) {
			PermissionUser user = PermissionsEx.getUser(player);
			
			if (user == null)
				return "";
			else
				return user.getSuffix().replace("&", "§");
		}
		
		else {
			return "";
		}
	}
	
	public void setNickname(Player player, String username, String nickname) {
		if(nickname.matches("[a-zA-Z0-9]+") && nickname.length() <= 20) {
			ResultSet rs = plugin.DatabaseManager.query("SELECT * FROM nicknames WHERE username='" + username + "'");
			
			try {
				if(!rs.next()) {
					plugin.DatabaseManager.query("INSERT INTO nicknames (username, nickname)VALUES ('" + username + "', '" + nickname + "')");
				}
				
				else {
					plugin.DatabaseManager.query("UPDATE nicknames SET nickname='" + nickname + "' WHERE username='" + username + "'");
				}
			} 
			
			catch (SQLException e) {
				e.printStackTrace();
			}
			
			if(plugin.getConfig().getBoolean("settings.logging.console")) {
				Bukkit.getConsoleSender().sendMessage("Player " + username + "'s nickname has changed to " + nickname);
			}
			
			if(player == Bukkit.getPlayerExact(username)) {
				player.sendMessage("Your nickname has changed to " + ChatColor.GOLD + nickname);
			}
			
			else {					
				if(player != null)
					player.sendMessage("Player " + ChatColor.GOLD + username + ChatColor.RESET + "'s nickname has changed to " + ChatColor.GOLD + nickname);
				
				if(Bukkit.getPlayerExact(username) != null) 
					Bukkit.getPlayerExact(username).sendMessage("Your nickname has changed to " + ChatColor.GOLD + nickname);
			}
		}
		
		else {
			if(player != null)
				player.sendMessage("Nicknames should less than 20 characters and only contains a-zA-Z0-9");
			else
				Bukkit.getConsoleSender().sendMessage("Nicknames should less than 20 characters and only contains a-zA-Z0-9");
		}
	}
	
	public void removeNickname(Player player, String username) {
		boolean i = false;
		
		try {
			ResultSet rs = plugin.DatabaseManager.query("SELECT * FROM nicknames WHERE username='" + username + "'");
			
			if(rs.next()) {
				plugin.DatabaseManager.query("DELETE FROM  nicknames WHERE username='" + username + "'");
				i = true;
			}
			
			else {
				i = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(i == true) {
			if(plugin.getConfig().getBoolean("settings.logging.console")) {
				Bukkit.getConsoleSender().sendMessage("Player " + username + " is no longer using a nickname");
			}
			
			if(player == Bukkit.getPlayerExact(username)) {
				player.sendMessage("You are no longer using a nickname");
			}
			
			else {					
				if(player != null)
					player.sendMessage("Player " + ChatColor.GOLD + username + ChatColor.RESET + " is no longer using a nickname");
				
				if(Bukkit.getPlayerExact(username) != null) 
					Bukkit.getPlayerExact(username).sendMessage("You are no longer using a nickname");
			}	
		}
		
		else {
			if(player != null) {
				if(Bukkit.getPlayerExact(username) == player) {
					player.sendMessage("You are not using a nickname");
				}
				
				else {
					player.sendMessage("Player " + ChatColor.GOLD + username + ChatColor.RESET + " is not using a nickname");
				}
			}
			
			else {
				Bukkit.getConsoleSender().sendMessage("Player " + username + " is not using a nickname");
			}
		}
	}	
}