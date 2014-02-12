package com.gmail.bkunkcu.roleplaychat.Commands;

import java.util.HashMap;

import com.gmail.bkunkcu.roleplaychat.RoleplayChat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RoleplayChatCommandExecutor implements CommandExecutor {
	
	private RoleplayChat plugin;
	private HashMap<String, RoleplayChatCommand> commands;
	
	public RoleplayChatCommandExecutor(RoleplayChat plugin) {
		this.plugin = plugin;
		getCommands();
	}
	
	private void getCommands() {
		this.commands = new HashMap<String, RoleplayChatCommand>();
		
		this.commands.put("reload", new ReloadCommand(this.plugin));
		this.commands.put("spy", new SpyCommand(this.plugin));
		this.commands.put("nick", new NickCommand(this.plugin));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if(sender instanceof Player) {
			player = (Player) sender;
		}
		
		if(commandLabel.equalsIgnoreCase("roleplaychat") || commandLabel.equalsIgnoreCase("rc")) {
			if(args.length < 1 || args == null) {
				sender.sendMessage(ChatColor.DARK_AQUA + "RoleplayChat is coded by Bkunkcu! Version: " + plugin.getDescription().getVersion());
			}
			
			else if(args[0].equalsIgnoreCase("help")) {
				getHelp(sender, player);
			}
			
			else if(this.commands.containsKey(args[0])) {
				RoleplayChatCommand command = this.commands.get(args[0]);
				
				if(command.isPlayerOnly() && player == null) {
					sender.sendMessage(ChatColor.DARK_RED + "Only players can use this command");
				}
				
				else {
					if(plugin.hasPermission(player, command.getPermission())) {
						try {
							command.onCommand(sender, player, args);
					    } catch (Exception e) {
					        e.printStackTrace();
					        
					        sender.sendMessage(ChatColor.DARK_RED + "There is an error occurred while performing this command. Please contact with server administrator");
					    }
					}
					
					else {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permissions to use this command");
					}
				}
			}
			
			else {
				sender.sendMessage(ChatColor.DARK_RED + "Couldn't find this command! For help: /rc help");
			}
		}
		
		player = null;
		return true;
	}
	
	private void getHelp(CommandSender sender, Player player)  {
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "---== RoleplayChat Help Page ==---");
		sender.sendMessage(ChatColor.GRAY + "- /rc help - Shows help page.");
		
		for(RoleplayChatCommand command : this.commands.values()) {
			if(command.getHelp(player) != null)
				sender.sendMessage(ChatColor.GRAY + "- " + command.getHelp(player));
		}
	}
}