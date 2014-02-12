package com.gmail.bkunkcu.roleplaychat.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.bkunkcu.roleplaychat.RoleplayChat;

public class SpyCommand implements RoleplayChatCommand {
	
	private RoleplayChat plugin;
	
	public SpyCommand(RoleplayChat plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Player player, String[] args) {
		if(plugin.FileManager.spy.contains(player.getName())) {							
			plugin.FileManager.spy.remove(player.getName());
			player.sendMessage(ChatColor.GRAY + "Detoggled spy");
		} 
		
		else {							
			plugin.FileManager.spy.add(player.getName());
			player.sendMessage(ChatColor.GRAY + "Toggled spy");
		}
		
		return true;
	}
	
	@Override
	public String getHelp(Player player) {
		if(plugin.hasPermission(player, getPermission()))
			return "/rc spy - Toggles spy mode.";
		
		return null;
	}

	@Override
	public String getPermission() {
		return "roleplaychat.spy";
	}

	@Override
	public boolean isPlayerOnly() {
		return true;
	}
	
}