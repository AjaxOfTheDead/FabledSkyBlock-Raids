package me.ResurrectAjax.Commands.Raid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Commands.RaidHelp.RaidHelp;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;
import me.ResurrectAjax.Raid.RaidParty;

public class RaidCommand extends CommandInterface{
	private RaidMethods raidMethods;
	private RaidManager raidManager;
	private Main main;
	private List<CommandInterface> subcommands = new ArrayList<CommandInterface>();
	
	public RaidCommand(Main main) {
		this.main = main;
		this.raidMethods = main.getRaidMethods();
		this.raidManager = main.getRaidManager();
		
		loadCommands();
	} 
	
	public String getName() {
		return "raid";
	}
	
	public String getSyntax() {
		return "/raid";
	}
	
	public String getDescription() {
		return "Raid an island";
	}
	
	public String[] getArguments(UUID uuid) {
		String[] arguments = new String[subcommands.size()];
		for(int i = 0; i < subcommands.size(); i++) {
			arguments[i] = subcommands.get(i).getName();
		}
		return arguments;
	}
	
	public List<CommandInterface> getSubCommands() {
		return subcommands;
	}

	public void perform(Player player, String[] args) {
		FileConfiguration language = main.getLanguage();
		
		String cmdName = "";
		if(args.length > 0) {
			for(CommandInterface command : subcommands) {
				if(command.getName().equalsIgnoreCase(args[0])) {
					command.perform(player, args);
					cmdName = command.getName();
				}
			}
		}
		if(cmdName.equalsIgnoreCase("") && args.length > 0) {
			player.sendMessage(RaidMethods.format(RaidMethods.convertSyntax(getSyntax())));
		}
		else if(args.length == 0) {
			if(!raidManager.getCalledRaidCommands().contains(player.getUniqueId())) {
				RaidParty party = raidManager.getMembersParty(player.getUniqueId());
				if(party != null && party.getLeader().equals(player.getUniqueId())) {
					for(UUID uuid : party.getMembers()) {
						if(Bukkit.getPlayer(uuid) != null) {
							Player member = Bukkit.getPlayer(uuid);
							raidManager.addStartPosition(uuid, member.getLocation());
						}
					}
					raidMethods.enterSpectateMode(player);	
					raidManager.getCalledRaidCommands().add(player.getUniqueId());
				
				}
				else {
					if(party == null) {
						raidManager.addStartPosition(player.getUniqueId(), player.getLocation());
						raidMethods.enterSpectateMode(player);
					}
					else {
						player.sendMessage(RaidMethods.format(language.getString("RaidParty.Leader.NotLeader.Message")));
					}
				}
			}
			else {
				player.sendMessage(RaidMethods.format(language.getString("Raid.Error.AlreadyRaiding.Message")));
			}	
		}
	}
	
	public void loadCommands() {
		subcommands = Arrays.asList(
				new RaidEnd(main),
				new RaidHelp(main)
				);
	}

}
