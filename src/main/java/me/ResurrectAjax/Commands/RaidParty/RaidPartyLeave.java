package me.ResurrectAjax.Commands.RaidParty;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;
import me.ResurrectAjax.Raid.RaidParty;

public class RaidPartyLeave extends CommandInterface{
	private Main main;
	public RaidPartyLeave(Main main) {
		this.main = main;
	}

	
	public String getName() {
		// TODO Auto-generated method stub
		return "leave";
	}

	
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raidparty leave";
	}

	
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Leave your party";
	}

	
	public String[] getArguments(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<CommandInterface> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void perform(Player player, String[] args) {
		RaidManager raidManager = main.getRaidManager();
		FileConfiguration language = main.getLanguage();
		
		if(raidManager.getMembersParty(player.getUniqueId()) != null) {
			for(UUID memberID : raidManager.getMembersParty(player.getUniqueId()).getMembers()) {
				if(Bukkit.getPlayer(memberID) != null) {
					Player member = Bukkit.getPlayer(memberID);
					member.sendMessage(RaidMethods.format(language.getString("RaidParty.Leave.Message"), player.getName()));
				}
			}
			removeFromParty(player, args);	
		}
		else {
			player.sendMessage(RaidMethods.format(language.getString("RaidParty.Error.NoParty.Message")));
		}
		
	}
	
	public void removeFromParty(OfflinePlayer player, String[] args) {
		RaidManager raidManager = main.getRaidManager();
		
		if(raidManager.getMembersParty(player.getUniqueId()) != null) {
			RaidParty party = raidManager.getMembersParty(player.getUniqueId());
			party.removeMember(player.getUniqueId());
		}
	}
}
