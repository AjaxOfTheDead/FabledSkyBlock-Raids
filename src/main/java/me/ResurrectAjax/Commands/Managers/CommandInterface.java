package me.ResurrectAjax.Commands.Managers;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Abstract class for commands
 * This is the frame for all the command classes
 * @author ResurrectAjax
 * */
public abstract class CommandInterface {
	/**
	 * Gets the name of the command
	 * @return name of the command
	 * */
	public abstract String getName();
	
	/**
	 * Gets the syntax of the command
	 * @return syntax of how the command should be used
	 * */
	public abstract String getSyntax();
	
	/**
	 * Gets the description of the command
	 * @return description of the command
	 * */
	public abstract String getDescription();
	
	/**
	 * Gets the arguments of the command
	 * @param uuid uuid of the player who sent the command
	 * @return array of the commands arguments
	 * */
	public abstract String[] getArguments(UUID uuid);
	
	
	/**
	 * Gets all the subcommands of the command
	 * @return list of the CommandInterface class which only get called by this class
	 * */
	public abstract List<CommandInterface> getSubCommands();
	
	/**
	 * Standard method for executing code
	 * @param player player who sent the command
	 * @param args array of arguments the player sent
	 * */
	public abstract void perform(Player player, String[] args);
}
