package me.ResurrectAjax.Commands.RaidParty;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;

public class RaidPartyDeny extends CommandInterface{
	public static String NAME = "deny";
	
	private Main main;
	private RaidManager raidManager;
	public RaidPartyDeny(Main main) {
		this.main = main;
		raidManager = main.getRaidManager();
	}

	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raidparty deny <player>";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Deny a player's party invitation";
	}

	public String[] getArguments(UUID uuid) {
		String[] playernames = new String[raidManager.getPartyInvites().keySet().size()];
		int count = 0;
		for(UUID player : raidManager.getPartyInvites().keySet()) {
			if(!raidManager.getPartyInvites().get(player).contains(uuid)) continue;
			playernames[count] = Bukkit.getOfflinePlayer(player).getName();
			count++;	
		}
		return playernames;
	}

	public List<CommandInterface> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	public void perform(Player player, String[] args) {
		if(args.length != 2) return;
		if(Bukkit.getPlayer(args[1]) == null) return;
		Player receiver = Bukkit.getPlayer(args[1]);
		FileConfiguration language = main.getLanguage();
		
		if(!main.getRaidManager().getPartyInvites().get(receiver.getUniqueId()).contains(player.getUniqueId())) player.sendMessage(RaidMethods.format(language.getString("RaidParty.Invite.Receive.NoInvite.Message")));	
		else {
			main.getRaidManager().getPartyInvites().get(receiver.getUniqueId()).remove(player.getUniqueId());
			receiver.sendMessage(RaidMethods.format(language.getString("RaidParty.Invite.Receive.Denied.Message"), player.getName()));
		}
		
		
		
	}
}
