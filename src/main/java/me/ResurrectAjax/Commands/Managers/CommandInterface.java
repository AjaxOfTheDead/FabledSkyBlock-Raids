package me.ResurrectAjax.Commands.Managers;

import java.util.List;

import org.bukkit.entity.Player;

public abstract class CommandInterface {
	public abstract String getName();
	
	public abstract String getSyntax();
	
	public abstract String getDescription();
	
	public abstract String[] getArguments();
	
	public abstract List<CommandInterface> getSubCommands();
	
	public abstract void perform(Player player, String[] args);
}
