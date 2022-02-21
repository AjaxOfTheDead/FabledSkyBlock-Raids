package me.ResurrectAjax.Commands.Managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.ResurrectAjax.Commands.Raid.RaidCommand;
import me.ResurrectAjax.Commands.RaidHistory.RaidHistoryCommand;
import me.ResurrectAjax.Commands.RaidParty.RaidPartyCommands;
import me.ResurrectAjax.Commands.RaidSense.RaidSenseCommand;
import me.ResurrectAjax.Main.Main;

public class CommandManager {
	private List<CommandInterface> commands = new ArrayList<CommandInterface>();
	
	public CommandManager(Main main) {
		commands = Arrays.asList(
			new RaidCommand(main),
			new RaidPartyCommands(main),
			new RaidHistoryCommand(main),
			new RaidSenseCommand(main)
		);
	}
	
	public List<CommandInterface> getCommands() {
		return commands;
	}
	
	public List<String> getStringList() {
		List<String> commandStrings = new ArrayList<String>();
		for(CommandInterface command : commands) {
			commandStrings.add(command.getName());
		}
		return commandStrings;
	}
	
	public CommandInterface getCommandByName(String name) {

		for(CommandInterface command : commands) {
			if(getStringList().contains(name.toLowerCase())) {
				if(command.getName().equalsIgnoreCase(name)) {
					return command;
				}	
			}
			else {
				if(command.getSubCommands() != null) {
					for(CommandInterface subcommands : command.getSubCommands()) {
						if(subcommands.getName().equalsIgnoreCase(name)) {
							return subcommands;
						}
					}	
				}
			}
		}	
		
		return null;
	}
}
