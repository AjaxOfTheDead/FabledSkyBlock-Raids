package me.ResurrectAjax.Commands.Raid;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Commands.Managers.CommandManager;
import me.ResurrectAjax.Main.Main;

public class RaidHelp extends CommandInterface{
	private CommandManager commandManager;
	private Main main;
	
	public RaidHelp(Main main) {
		this.main = main;
	}


	public String getName() {
		// TODO Auto-generated method stub
		return "help";
	}


	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raidparty help <page>";
	}


	public String getDescription() {
		// TODO Auto-generated method stub
		return "Get a list of all the raidparty commands";
	}


	public String[] getArguments() {
		return null;
	}


	public List<CommandInterface> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}


	public void perform(Player player, String[] args) {
		commandManager = main.getCommandManager();
		
		if(args.length == 2) {
			if(main.getRaidMethods().isInteger(args[1])) {
				if(Integer.parseInt(args[1]) == 0) {
					createList(player, 1);
				}
				else {
					createList(player, Integer.parseInt(args[1]));
				}
			}
			else {
				createList(player, 1);	
			}
		}
		else if(args.length == 1){
			createList(player, 1);
		}

		
	}
	
	public void createList(Player player, int page) {
		List<String> commandList = new ArrayList<String>();
		int nr = 8, helpSize = 0;
		
		for(CommandInterface commands : commandManager.getCommands()) {
			if(commands.getSubCommands() != null) {
				for(CommandInterface subcommands : commands.getSubCommands()) {
					String message;
					message = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "* " + ChatColor.RED + subcommands.getSyntax() + ChatColor.GRAY + " - " + ChatColor.WHITE + ChatColor.ITALIC + subcommands.getDescription();
					
					commandList.add(message);
				}
			}
		}
		
		if(commandList.size() % nr != 0) {
			helpSize = (commandList.size() / nr) + 1;
		}
		else {
			helpSize = (commandList.size() / nr);
		}
		
		for(int i = (page * nr)-nr; i < page * nr; i++) {
			if(commandList.size() > i) {
				player.sendMessage(commandList.get(i));	
			}
		}
		
		if(page > helpSize) {
			
		}
	}
}
