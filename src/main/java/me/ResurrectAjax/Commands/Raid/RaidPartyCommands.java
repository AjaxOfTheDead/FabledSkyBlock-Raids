package me.ResurrectAjax.Commands.Raid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;
import me.ResurrectAjax.Raid.RaidParty;
import me.ResurrectAjax.RaidGUI.GuiManager;

public class RaidPartyCommands extends CommandInterface{

	private Main main;
	private RaidMethods methods;
	private RaidManager raidManager;
	private GuiManager guiManager;
	private List<CommandInterface> subcommands = new ArrayList<CommandInterface>();
	
	public RaidPartyCommands(Main main) {
		this.main = main;
		methods = main.getRaidMethods();
		guiManager = main.getGuiManager();
		
		loadCommands();
	}
	
	public String getName() {
		return "raidparty";
	}

	public String getSyntax() {
		return "/raidparty";
	}

	public String getDescription() {
		return "Open the RaidParty GUI";
	}

	public String[] getArguments() {
		String[] arguments = new String[] {"invite", "kick", "chat", "accept", "deny", "help"};
		return arguments;
	}

	public void perform(Player player, String[] args) {
		FileConfiguration configLoad = main.getLanguage();
		String cmdName = "";
		for(CommandInterface command : subcommands) {
			if(command.getName().equalsIgnoreCase(args[0])) {
				command.perform(player, args);
				cmdName = command.getName();
			}
		}
		if(cmdName.equalsIgnoreCase("") && args.length > 0) {
			player.sendMessage(methods.format(convertSyntax(getSyntax())));
		}
		else if(args.length == 0) {
			if(raidManager.getMembersParty(player.getUniqueId()) != null) {
				RaidParty party = raidManager.getMembersParty(player.getUniqueId());
				if(party.getLeader().equals(player.getUniqueId())) {
					guiManager.partyLeaderGui1(player);	
				}
				else {
					//member gui
				}
			}
			else {
				player.sendMessage(methods.format(configLoad.getString("Raid.RaidParty.NoParty.Message")));
				subcommands.get(3).perform(player, args);
			}
		}
	}
	
	//replace the "%Syntax%" in the language.yml file
	public String convertSyntax(String syntax) {
		FileConfiguration configLoad = main.getLanguage();
		String syntaxMsg = configLoad.getString("Command.Execute.BadSyntax.Message");
		String newstr = syntaxMsg;
		
		if(newstr.contains("%Syntax%")) {
			newstr = syntaxMsg.replace("%Syntax%", syntax + "");
		}
		return newstr;
	}
	
	private void loadCommands() {
		
		subcommands = Arrays.asList(
				new RaidPartyInvite(main, this),
				new RaidPartyAccept(main),
				new RaidPartyDeny(main),
				new RaidHelp(main)
				);
	}

	public List<CommandInterface> getSubCommands() {
		
		return subcommands;
	}

}
