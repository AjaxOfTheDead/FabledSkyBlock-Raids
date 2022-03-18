package me.ResurrectAjax.Commands.Managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.ResurrectAjax.Commands.Raid.RaidCommand;
import me.ResurrectAjax.Commands.RaidHistory.RaidHistoryCommand;
import me.ResurrectAjax.Commands.RaidParty.RaidPartyCommands;
import me.ResurrectAjax.Commands.RaidSense.RaidSenseCommand;
import me.ResurrectAjax.Main.Main;

/**
 * Manages all the base commands
 * @author ResurrectAjax
 * */
public class CommandManager {
	private List<CommandInterface> commands = new ArrayList<CommandInterface>();
	
	/**
	 * Constructor of CommandManager<br>
	 * Loads all the base commands
	 * @param main instance of the {@link me.ResurrectAjax.Main.Main} class
	 * */
	public CommandManager(Main main) {
		commands = Arrays.asList(
			new RaidCommand(main),
			new RaidPartyCommands(main),
			new RaidHistoryCommand(main),
			new RaidSenseCommand(main)
		);
	}
	
	/**
	 * Gets a list of all the base commands
	 * @return list of all the base commands
	 * */
	public List<CommandInterface> getCommands() {
		return commands;
	}
	
	/**
	 * Gets a list of all the base command names
	 * @return list of all the command names
	 * */
	public List<String> getStringList() {
		List<String> commandStrings = new ArrayList<String>();
		for(CommandInterface command : commands) {
			commandStrings.add(command.getName());
		}
		return commandStrings;
	}
	
	/**
	 * Gets the base command by name
	 * @param name name of the command
	 * @return instance of {@link me.ResurrectAjax.Commands.Managers.CommandInterface}
	 * */
	public CommandInterface getCommandByName(String name) {

		for(CommandInterface command : commands) {
			if(!getStringList().contains(name.toLowerCase()) && command.getSubCommands() == null) continue;
			else if(!getStringList().contains(name.toLowerCase())) {
				for(CommandInterface subcommands : command.getSubCommands()) {
					if(subcommands.getName().equalsIgnoreCase(name)) return subcommands;
				}	
			}
			else return command;
		}	
		
		return null;
	}
}
