package me.ResurrectAjax.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Commands.Managers.CommandManager;
import me.ResurrectAjax.Raid.RaidMethods;

public class TabCompletion implements TabCompleter{
	private CommandManager commandManager;
	public TabCompletion(Main main) {
		commandManager = main.getCommandManager();
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> tabCommands = new ArrayList<String>();
		if(sender instanceof Player) {
			UUID uuid = ((Player) sender).getUniqueId();

			if(commandManager.getStringList().contains(command.getName().toLowerCase())) {
				CommandInterface commands = commandManager.getCommandByName(command.getName());
				switch(args.length) {
					case 1:
						if(commands.getArguments(uuid) != null) {
							tabCommands.addAll(Arrays.asList(commands.getArguments(uuid)));	
						}
						break;
					case 2:
					case 3:
						if(commands.getSubCommands() != null) {
							for(CommandInterface subcommands : commands.getSubCommands()) {
								for(int i = 0; i < args.length; i++) {
									if(subcommands.getName().equalsIgnoreCase(args[i])) {
										if(subcommands.getArguments(uuid) != null) {
											tabCommands.addAll(Arrays.asList(subcommands.getArguments(uuid)));	
										}
									}
								}
							}	
						}
						else {
							if(commands.getArguments(uuid) != null) {
								tabCommands.addAll(Arrays.asList(commands.getArguments(uuid)));	
							}
						}
						break;
				}
			}
			
		}
		
		for(String commands : RaidMethods.getBlackListCommands()) {
			if(tabCommands.contains(commands)) {
				tabCommands.remove(commands);
			}
		}
		return tabCommands;
	}
}
