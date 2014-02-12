package com.gmail.bkunkcu.roleplaychat.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.bkunkcu.roleplaychat.RoleplayChat;

public class NickCommand implements RoleplayChatCommand {
	
	private RoleplayChat plugin;
	
	public NickCommand(RoleplayChat plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Player player, String[] args) {
		if(args.length == 1) {
			sender.sendMessage("");
			sender.sendMessage(ChatColor.DARK_AQUA + "---== RoleplayChat Nickname Help Page ==---");
			sender.sendMessage(ChatColor.GRAY + "- /rc nick <nickname> - Changes your nickname.");
			sender.sendMessage(ChatColor.GRAY + "- /rc nick off - Removes your nickname.");
			
			if(plugin.hasPermission(player, "roleplaychat.nick.others"))			
				sender.sendMessage(ChatColor.GRAY + "- /rc nick <username> <nickname> - Changes an other players nickname.");
				sender.sendMessage(ChatColor.GRAY + "- /rc nick <username> off - Removes an other players nickname.");
		}
		
		else if(args.length == 2) {
			if(player != null) {
				if(args[1].equalsIgnoreCase("off")) {
					plugin.NicknameManager.removeNickname(player, player.getName());
				}
				
				else {
					plugin.NicknameManager.setNickname(player, player.getName(), args[1]);
				}
			}
			
			else {
				sender.sendMessage(ChatColor.DARK_RED + "Only players can use this command");
			}
		}
		
		else if(args.length == 3) {
			if(plugin.hasPermission(player, "roleplaychat.nick.others")) {
				if(args[2].equalsIgnoreCase("off")) {
					plugin.NicknameManager.removeNickname(player, args[1]);
				}
				
				else {
					plugin.NicknameManager.setNickname(player, args[1], args[2]);
				}
			}
			
			else {
				sender.sendMessage(ChatColor.DARK_RED + "You don't have permissions to use this command");
			}
		}
		
		return true;
	}
	
	@Override
	public String getHelp(Player player) {
		if(plugin.hasPermission(player, getPermission()))
			return "/rc nick - Shows nickname help page.";
		
		return null;
	}

	@Override
	public String getPermission() {
		return "roleplaychat.nick";
	}

	@Override
	public boolean isPlayerOnly() {
		return false;
	}
}