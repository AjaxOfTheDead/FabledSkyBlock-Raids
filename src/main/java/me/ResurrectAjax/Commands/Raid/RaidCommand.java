package me.ResurrectAjax.Commands.Raid;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;
import me.ResurrectAjax.Raid.RaidParty;

public class RaidCommand extends CommandInterface{
	private RaidMethods raidMethods;
	private RaidManager raidManager;
	private Main main;
	
	public RaidCommand(Main main) {
		this.main = main;
		this.raidMethods = main.getRaidMethods();
		this.raidManager = main.getRaidManager();
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
	
	public String[] getArguments() {
		String[] arguments = new String[] {};
		return arguments;
	}

	public void perform(Player player, String[] args) {
		FileConfiguration language = main.getLanguage();
		
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
					player.sendMessage(raidMethods.format(language.getString("Raid.RaidParty.Raid.NotLeader.Message")));
				}
			}
		}
		else {
			player.sendMessage(raidMethods.format(language.getString("Raid.Error.AlreadyRaiding.Message")));
		}
	}

	@Override
	public List<CommandInterface> getSubCommands() {
		return null;
	}

}
