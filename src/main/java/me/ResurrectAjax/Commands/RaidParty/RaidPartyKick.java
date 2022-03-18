package me.ResurrectAjax.Commands.RaidParty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;

public class RaidPartyKick extends RaidPartyLeave{

	private Main main;
	public RaidPartyKick(Main main) {
		super(main);
		this.main = main;
		
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return "kick";
	}

	
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raidparty kick <player>";
	}

	
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Kick a player from your party";
	}
	
	public String[] getArguments(UUID uuid) {
		if(main.getRaidManager().getMembersParty(uuid) != null) {
			List<String> playerNames = new ArrayList<String>();
			for(UUID member : main.getRaidManager().getMembersParty(uuid).getMembers()) {
				if(!member.equals(uuid)) playerNames.add(Bukkit.getOfflinePlayer(member).getName());	
			}
			String[] names = new String[playerNames.size()];
			for(int i = 0; i < playerNames.size(); i++) {
				names[i] = playerNames.get(i);
			}	
			return names;
		}
		return null;
	}
	
	public void perform(Player player, String[] args) {
		FileConfiguration language = main.getLanguage();
		RaidManager raidManager = main.getRaidManager();
		
		if(args.length != 2) return;
		UUID memberUUID = null;
		for(UUID memberID : raidManager.getMembersParty(player.getUniqueId()).getMembers()) {
			OfflinePlayer member = Bukkit.getOfflinePlayer(memberID);
			if(member.getName().equalsIgnoreCase(args[1])) memberUUID = memberID;
		}
		if(memberUUID == null) {
			player.sendMessage(RaidMethods.format(language.getString("RaidParty.Error.NotInParty.Message")));
			return;
		}

		for(UUID memberID : raidManager.getMembersParty(player.getUniqueId()).getMembers()) {
			if(Bukkit.getPlayer(memberID) == null) continue;
			Player member = Bukkit.getPlayer(memberID);
			member.sendMessage(RaidMethods.format(language.getString("RaidParty.Kick.Message"), Bukkit.getOfflinePlayer(memberUUID).getName()));
			
		}
		super.removeFromParty(Bukkit.getOfflinePlayer(memberUUID), args);
			
		
		
	}

}
