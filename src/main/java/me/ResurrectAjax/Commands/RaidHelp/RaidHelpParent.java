package me.ResurrectAjax.Commands.RaidHelp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Commands.Managers.CommandManager;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidMethods;

public class RaidHelpParent extends CommandInterface{
	protected Main main;
	private int helpSize = 0;
	
	public RaidHelpParent(Main main) {
		this.main = main;
	}


	public String getName() {
		// TODO Auto-generated method stub
		return "help";
	}


	public String getSyntax() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}


	public String[] getArguments(UUID uuid) {
		String[] arguments = new String[helpSize+1];
		for(int i = 0; i < helpSize; i++) {
			arguments[i] = i+"";
		}
		return null;
	}


	public List<CommandInterface> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}


	public void perform(Player player, String[] args) {

		
	}
	
	public void perform(Player player, String name, String[] args) {
		
		if(args.length == 2) {
			if(RaidMethods.isInteger(args[1])) {
				if(Integer.parseInt(args[1]) == 0) {
					createList(player, name, 1);
				}
				else {
					createList(player, name, Integer.parseInt(args[1]));
				}
			}
			else {
				createList(player, name, 1);	
			}
		}
		else if(args.length == 1){
			createList(player, name, 1);
		}
		
	}
	
	public void createList(Player player, String command, int page) {
		CommandManager commandManager = main.getCommandManager();
		List<String> commandList = new ArrayList<String>();
		FileConfiguration language = main.getLanguage();
		int nr = 8;
		
		CommandInterface commands = commandManager.getCommandByName(command);
		if(commands.getSubCommands() != null) {
			List<CommandInterface> subcommands = new ArrayList<CommandInterface>(commands.getSubCommands());
			List<String> blacklistNames = new ArrayList<String>();
			for(String blacklistItem : RaidMethods.getBlackListCommands()) {
				blacklistNames.add(blacklistItem);
			}
			for(CommandInterface subcommand : commands.getSubCommands()) {
				if(blacklistNames.contains(subcommand.getName())) {
					subcommands.remove(subcommand);
				}
			}
			subcommands.add(commandManager.getCommandByName(command));
			for(CommandInterface subcommand : subcommands) {
				String message;
				message = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "* " + ChatColor.RED + subcommand.getSyntax() + ChatColor.GRAY + " - " + ChatColor.WHITE + ChatColor.ITALIC + subcommand.getDescription();
				
				commandList.add(message);
			}
		}
		
		
		if(commandList.size() % nr != 0) {
			helpSize = (commandList.size() / nr) + 1;
		}
		else {
			helpSize = (commandList.size() / nr);
		}
		
		player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Raid" + ChatColor.WHITE + " - " + ChatColor.YELLOW + command);
		
		for(int i = (page * nr)-nr; i < page * nr; i++) {
			if(commandList.size() > i) {
				player.sendMessage(commandList.get(i));	
			}
		}
		
		if(page > helpSize) {
			player.sendMessage(RaidMethods.format(language.getString("Raid.Error.EndOfHelp.Message")));
		}
	}
}
