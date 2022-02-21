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
import me.ResurrectAjax.Raid.RaidParty;

public class RaidPartyAccept extends CommandInterface{
	public static String NAME = "accept";
	
	private Main main;
	private RaidManager raidManager;
	public RaidPartyAccept(Main main) {
		this.main = main;
		raidManager = main.getRaidManager();
	}

	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raidparty accept <player>";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Accept a player's party invitation";
	}

	public String[] getArguments(UUID uuid) {
		String[] playernames = new String[raidManager.getPartyInvites().keySet().size()];
		int count = 0;
		for(UUID player : raidManager.getPartyInvites().keySet()) {
			if(raidManager.getPartyInvites().get(player).contains(uuid)) {
				playernames[count] = Bukkit.getOfflinePlayer(player).getName();
				count++;	
			}
		}
		return playernames;
	}

	public List<CommandInterface> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	public void perform(Player player, String[] args) {
		if(args.length == 2) {
			if(Bukkit.getPlayer(args[1]) != null) {
				Player receiver = Bukkit.getPlayer(args[1]);
				FileConfiguration language = main.getLanguage();
				
				if(main.getRaidManager().getPartyInvites().get(receiver.getUniqueId()).contains(player.getUniqueId())) {
					if(main.getRaidManager().getRaidParties().get(receiver.getUniqueId()) == null) {
						main.getRaidManager().addRaidParty(receiver.getUniqueId(), new RaidParty(receiver));	
					}
					
					if(main.getRaidManager().getMembersParty(player.getUniqueId()) == null) {
						main.getRaidManager().getRaidParties().get(receiver.getUniqueId()).addMember(player.getUniqueId());
						for(UUID member : main.getRaidManager().getRaidParties().get(receiver.getUniqueId()).getMembers()) {
							if(Bukkit.getPlayer(member) != null) {
								Bukkit.getPlayer(member).sendMessage(RaidMethods.format(language.getString("RaidParty.Invite.Receive.Accepted.Message"), player.getName()));		
							}
						}
						main.getRaidManager().getPartyInvites().get(receiver.getUniqueId()).remove(player.getUniqueId());
					}
					else {
						player.sendMessage(RaidMethods.format(language.getString("RaidParty.Invite.Receive.AlreadyInParty.Message")));	
					}
				}
				else {
					player.sendMessage(RaidMethods.format(language.getString("RaidParty.Invite.Receive.NoInvite.Message")));	
				}
				
			}
		}
		
	}

}
