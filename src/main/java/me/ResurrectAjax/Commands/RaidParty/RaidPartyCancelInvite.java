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

public class RaidPartyCancelInvite extends CommandInterface{
	public static String NAME = "cancelinvite";
	
	private Main main;
	private RaidManager raidManager;

	public RaidPartyCancelInvite(Main main) {
		this.main = main;
		this.raidManager = main.getRaidManager();
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raidparty cancelinvite <player>";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Cancel a party invitation";
	}

	public String[] getArguments(UUID uuid) {
		String[] playernames = new String[raidManager.getPartyInvites().get(uuid).size()];
		int count = 0;
		for(UUID player : raidManager.getPartyInvites().get(uuid)) {
			if(!player.equals(uuid)) {
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
				
				if(main.getRaidManager().getPartyInvites().get(player.getUniqueId()).contains(receiver.getUniqueId())) {
					main.getRaidManager().getPartyInvites().get(player.getUniqueId()).remove(receiver.getUniqueId());
					player.sendMessage(RaidMethods.format(language.getString("RaidParty.Invite.Send.Cancel.Canceled.Message")));
				}
				else {
					player.sendMessage(RaidMethods.format(language.getString("RaidParty.Invite.Send.Cancel.NoInvite.Message")));	
				}
			}
		}
	}
}
