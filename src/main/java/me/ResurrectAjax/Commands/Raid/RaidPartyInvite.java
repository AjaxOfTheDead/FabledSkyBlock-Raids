package me.ResurrectAjax.Commands.Raid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidMethods;
import me.ResurrectAjax.Raid.RaidParty;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class RaidPartyInvite extends CommandInterface{
	private RaidMethods methods;
	private Main main;
	private FileConfiguration language;
	private RaidPartyCommands partycommand;
	public RaidPartyInvite(Main main, RaidPartyCommands partycommand) {
		this.main = main;
		methods = main.getRaidMethods();
		language = main.getLanguage();
		this.partycommand = partycommand;
		
	}


	public String getName() {
		return "invite";
	}


	public String getSyntax() {
		return "/raidparty invite <player>";
	}


	public String getDescription() {
		return "invite a player to your party";
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


	public void perform(Player player, String[] args) {
		if(args.length == 2) {
			//check if player didn't enter his own name
			if(!player.getName().equalsIgnoreCase(args[1])) {
				
				//check if entered name is a player that exists/is online
				if(Bukkit.getPlayer(args[1]) != null) {
					Player receiver = Bukkit.getPlayer(args[1]);
					
					RaidParty party = main.getRaidManager().getMembersParty(player.getUniqueId());
					if(party == null || !party.getMembers().contains(receiver.getUniqueId())) {
						//create invite if player hasn't sent an invite yet
						HashMap<UUID, List<UUID>> playerInvites = main.getRaidManager().getPartyInvites();
						if(playerInvites.get(player.getUniqueId()) == null || !playerInvites.get(player.getUniqueId()).contains(receiver.getUniqueId())) {
							List<UUID> invites = playerInvites.get(player.getUniqueId()) != null ? playerInvites.get(player.getUniqueId()) : new ArrayList<UUID>();
							invites.add(receiver.getUniqueId());
							main.getRaidManager().getPartyInvites().put(player.getUniqueId(), invites);
							
							//send invite message with hovertext
							TextComponent accept = methods.createHoverText("Accept", "Click to accept", "/raidparty accept " + player.getName(), ChatColor.GREEN), 
									deny = methods.createHoverText("Deny", "Click to deny", "/raidparty deny " + player.getName(), ChatColor.RED);
							
							TextComponent text = new TextComponent(methods.formatPlayer(methods.format(language.getString("Raid.RaidParty.Invite.Receive.InviteReceived.Message")) + " ", player));
							text.addExtra(accept);
							text.addExtra(" | ");
							text.addExtra(deny);
							
							receiver.spigot().sendMessage(text);
							
							//create runnable that runs code after 5min
							new BukkitRunnable() {
							    public void run() {
							    	//if player hasn't accepted, expire the invite
							    	HashMap<UUID, List<UUID>> partyInvites = main.getRaidManager().getPartyInvites();
							        if(partyInvites.containsKey(player.getUniqueId()) && partyInvites.get(player.getUniqueId()).contains(receiver.getUniqueId())) {
							        	player.sendMessage(methods.format(language.getString("Raid.RaidParty.Invite.Send.Expired.Message")));
							        	main.getRaidManager().getPartyInvites().get(player.getUniqueId()).remove(receiver.getUniqueId());
							        }
							    }
							}.runTaskTimer(main, 20*300, 20*300);
						}
						else {
							player.sendMessage(methods.format(language.getString("Raid.RaidParty.Invite.Send.AlreadySent.Message")));
						}
					}
					else {
						if(party.getMembers().contains(receiver.getUniqueId())) {
							player.sendMessage(methods.format(language.getString("Raid.RaidParty.Invite.Send.AlreadyInParty.Message")));	
						}
					}
					
				}
				else {
					player.sendMessage(methods.format(language.getString("Raid.Error.PlayerNotExist.Message")));
				}	
			}
			else {
				player.sendMessage(methods.format(language.getString("Raid.RaidParty.Invite.Send.SelfInvite.Message")));
			}
		}
		else {
			player.sendMessage(methods.format(partycommand.convertSyntax(getSyntax())));
		}
		
	}


	@Override
	public List<CommandInterface> getSubCommands() {
		return null;
	}
}
