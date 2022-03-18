package me.ResurrectAjax.Commands.RaidParty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.songoda.skyblock.island.Island;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Playerdata.PlayerManager;
import me.ResurrectAjax.Raid.RaidMethods;
import me.ResurrectAjax.Raid.RaidParty;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class RaidPartyInvite extends CommandInterface{
	private RaidMethods methods;
	private Main main;
	public RaidPartyInvite(Main main, RaidPartyCommands partycommand) {
		this.main = main;
		methods = main.getRaidMethods();
	}


	public String getName() {
		return "invite";
	}


	public String getSyntax() {
		return "/raidparty invite <player>";
	}


	public String getDescription() {
		return "Invite a player to your party";
	}


	public String[] getArguments(UUID uuid) {
		String[] playernames = new String[PlayerManager.getOnlineIslandMembers(PlayerManager.getPlayersIsland(uuid)).size()-1];
		int count = 0;
		for(UUID players : PlayerManager.getOnlineIslandMembers(PlayerManager.getPlayersIsland(uuid))) {
			if(players.equals(uuid)) continue;
			playernames[count] = Bukkit.getPlayer(players).getName();
			count++;
		}
		return playernames;
	}


	public void perform(Player player, String[] args) {
		FileConfiguration language = main.getLanguage();
		if(args.length != 2) {
			player.sendMessage(RaidMethods.format(RaidMethods.convertSyntax(getSyntax())));
			return;
		}
		else if(Bukkit.getPlayer(args[1]) == null) {
			player.sendMessage(RaidMethods.format(language.getString("RaidParty.Error.PlayerNotExist.Message")));
			return;
		}
		else if(player.getUniqueId().equals(Bukkit.getPlayer(args[1]).getUniqueId())) {
			player.sendMessage(RaidMethods.format(language.getString("RaidParty.Invite.Send.SelfInvite.Message")));
			return;
		}
		else {
			Player receiver = Bukkit.getPlayer(args[1]);
			Island senderIsland = PlayerManager.getPlayersIsland(player.getUniqueId());
			
			if(PlayerManager.getOnlineIslandMembers(senderIsland).contains(receiver.getUniqueId())) {
				RaidParty party = main.getRaidManager().getMembersParty(player.getUniqueId());
				if(party == null || !party.getMembers().contains(receiver.getUniqueId())) {
					//create invite if player hasn't sent an invite yet
					HashMap<UUID, List<UUID>> playerInvites = main.getRaidManager().getPartyInvites();
					if(playerInvites.get(player.getUniqueId()) != null && playerInvites.get(player.getUniqueId()).contains(receiver.getUniqueId())) player.sendMessage(RaidMethods.format(language.getString("RaidParty.Invite.Send.AlreadySent.Message")));
					else {
						List<UUID> invites = playerInvites.get(player.getUniqueId()) != null ? playerInvites.get(player.getUniqueId()) : new ArrayList<UUID>();
						invites.add(receiver.getUniqueId());
						main.getRaidManager().getPartyInvites().put(player.getUniqueId(), invites);
						
						//send invite message with hovertext
						TextComponent accept = methods.createHoverText("Accept", "Click to accept", "/raidparty accept " + player.getName(), ChatColor.GREEN), 
								deny = methods.createHoverText("Deny", "Click to deny", "/raidparty deny " + player.getName(), ChatColor.RED), 
								cancel = methods.createHoverText("Cancel", "Click to cancel", "/raidparty cancelinvite " + receiver.getName(), ChatColor.RED);
						
						TextComponent text = new TextComponent(RaidMethods.format(language.getString("RaidParty.Invite.Receive.InviteReceived.Message"), player.getName()) + " ");
						text.addExtra(accept);
						text.addExtra(" | ");
						text.addExtra(deny);
						
						receiver.spigot().sendMessage(text);
						
						text = new TextComponent(RaidMethods.format(language.getString("RaidParty.Invite.Send.InviteSent.Message"), receiver.getName()) + " ");
						text.addExtra(cancel);
						
						player.spigot().sendMessage(text);
						
						//create runnable that runs code after 5min
						new BukkitRunnable() {
						    public void run() {
						    	//if player hasn't accepted, expire the invite
						    	HashMap<UUID, List<UUID>> partyInvites = main.getRaidManager().getPartyInvites();
						        if(!partyInvites.containsKey(player.getUniqueId()) || !partyInvites.get(player.getUniqueId()).contains(receiver.getUniqueId())) return;
						        player.sendMessage(RaidMethods.format(language.getString("RaidParty.Invite.Send.Expired.Message")));
					        	main.getRaidManager().getPartyInvites().get(player.getUniqueId()).remove(receiver.getUniqueId());
						    }
						}.runTaskTimer(main, 20*300, 20*300);
					}
				}
				else if(party.getMembers().contains(receiver.getUniqueId())) player.sendMessage(RaidMethods.format(language.getString("RaidParty.Invite.Send.AlreadyInParty.Message")));
			}
			else if(PlayerManager.getIslandMembers(senderIsland).contains(receiver.getUniqueId())) player.sendMessage(RaidMethods.format(language.getString("RaidParty.Error.PlayerNotOnline.Message")));
			else player.sendMessage(RaidMethods.format(language.getString("RaidParty.Error.PlayerNotAnIslandMember.Message")));
		}
		
	}


	@Override
	public List<CommandInterface> getSubCommands() {
		return null;
	}
}
