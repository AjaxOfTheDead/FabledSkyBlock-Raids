package me.ResurrectAjax.Commands.Managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.ResurrectAjax.Commands.Raid.RaidCommand;
import me.ResurrectAjax.Commands.Raid.RaidPartyCommands;
import me.ResurrectAjax.Main.Main;

public class CommandManager {
	private List<CommandInterface> commands = new ArrayList<CommandInterface>();
	
	public CommandManager(Main main) {
		commands = Arrays.asList(
			new RaidCommand(main),
			new RaidPartyCommands(main)
			
		);
	}
	
	public List<CommandInterface> getCommands() {
		return commands;
	}
}
