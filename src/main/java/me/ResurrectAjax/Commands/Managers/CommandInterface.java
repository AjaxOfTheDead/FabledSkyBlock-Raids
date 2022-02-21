package me.ResurrectAjax.Commands.Managers;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public abstract class CommandInterface {
	public abstract String getName();
	
	public abstract String getSyntax();
	
	public abstract String getDescription();
	
	public abstract String[] getArguments(UUID uuid);
	
	public abstract List<CommandInterface> getSubCommands();
	
	public abstract void perform(Player player, String[] args);
}
