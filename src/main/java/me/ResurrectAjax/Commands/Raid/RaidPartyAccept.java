package me.ResurrectAjax.Commands.Raid;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidMethods;
import me.ResurrectAjax.Raid.RaidParty;

public class RaidPartyAccept extends CommandInterface{
	private Main main;
	private RaidMethods methods;
	public RaidPartyAccept(Main main) {
		this.main = main;
		methods = main.getRaidMethods();
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "accept";
	}

	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raidparty accept <player>";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Accept a player's party invitation";
	}

	public String[] getArguments() {
		String[] playernames = new String[Bukkit.getOnlinePlayers().size()];
		int count = 0;
		for(Player players : Bukkit.getOnlinePlayers()) {
			playernames[count] = players.getName();
			count++;
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
						main.getRaidManager().getRaidParties().get(receiver.getUniqueId()).addMember(player);
						for(UUID member : main.getRaidManager().getRaidParties().get(receiver.getUniqueId()).getMembers()) {
							if(Bukkit.getPlayer(member) != null) {
								Bukkit.getPlayer(member).sendMessage(methods.format(methods.formatPlayer(language.getString("Raid.RaidParty.Invite.Receive.Accepted.Message"), player)));		
							}
						}
						main.getRaidManager().getPartyInvites().get(receiver.getUniqueId()).remove(player.getUniqueId());
					}
					else {
						player.sendMessage(methods.format(language.getString("Raid.RaidParty.Invite.Receive.AlreadyInParty.Message")));	
					}
				}
				else {
					player.sendMessage(methods.format(language.getString("Raid.RaidParty.Invite.Receive.NoInvite.Message")));	
				}
				
			}
		}
		
	}

}
