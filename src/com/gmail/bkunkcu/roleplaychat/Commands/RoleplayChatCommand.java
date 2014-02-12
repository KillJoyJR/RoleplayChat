package com.gmail.bkunkcu.roleplaychat.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract interface RoleplayChatCommand {
	
	public abstract boolean onCommand(CommandSender sender, Player player, String[] args);

	public abstract String getHelp(Player player);

	public abstract String getPermission();
	
	public abstract boolean isPlayerOnly();
}