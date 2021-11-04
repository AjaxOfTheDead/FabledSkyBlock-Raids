package me.ResurrectAjax.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Commands.Managers.CommandManager;

public class TabCompletion implements TabCompleter{
	private CommandManager commandManager;
	public TabCompletion(Main main) {
		commandManager = main.getCommandManager();
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> tabCommands = new ArrayList<String>();
		if(sender instanceof Player) {
			for(CommandInterface commands : commandManager.getCommands()) {
				switch(args.length) {
					case 1:
						tabCommands.addAll(Arrays.asList(commands.getArguments()));
						break;
					case 2:
					case 3:
						if(commands.getSubCommands() != null) {
							for(CommandInterface subcommands : commands.getSubCommands()) {
								for(int i = 0; i < args.length; i++) {
									if(subcommands.getName().equalsIgnoreCase(args[i])) {
										if(subcommands.getArguments() != null) {
											tabCommands.addAll(Arrays.asList(subcommands.getArguments()));	
										}
									}
								}
							}	
						}
						else {
							tabCommands.addAll(Arrays.asList(commands.getArguments()));
						}
						break;
				}
			}
		}
		return tabCommands;
	}

}
