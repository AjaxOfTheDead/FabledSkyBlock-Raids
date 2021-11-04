package me.ResurrectAjax.Raid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandWorld;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.FastDataAccess;
import me.ResurrectAjax.Raid.ItemStorage.ItemStorage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class RaidMethods {
	private Main main;
	private RaidManager raidManager;
	private IslandManager islandManager;
	private FileConfiguration language;
	private Location spawnLocation;
	private Random rand = new Random();
	private FastDataAccess fdb;
	private HashMap<UUID, Location> islandRaider = new HashMap<UUID, Location>();
	private HashMap<UUID, Location> islandSpectator = new HashMap<UUID, Location>();
	private HashMap<Location, UUID> raidedIsland = new HashMap<Location, UUID>();
	
	private List<Location> islands;
	
	public RaidMethods(Main main) {
		this.main = main;
		
		//load the managers
		this.raidManager = main.getRaidManager();
		this.islandManager = main.getSkyBlock().getIslandManager();
		
		//load all island positions in the database
		HashMap<UUID, Location> positions = main.getIslandPositions();
		islands = new ArrayList<Location>();
		for(UUID uuid : positions.keySet()) {
			islands.add(positions.get(uuid));
		}
		//get fast data access
		fdb = main.getFastDataAccess();
		
		//get the contents of language.yml
		language = main.getLanguage();
		
		//load spawn
		spawnLocation = main.getSpawnLocation();
	}
	
	//exit spectator mode
	public void exitRaidSpectator(Player player) {
		//check if player was a spectator
		if(getIslandSpectator().containsKey(player.getUniqueId())) {
			
			//check if player was the last spectator of that island
			OfflinePlayer partyLeader = Bukkit.getOfflinePlayer(raidManager.getMembersParty(player.getUniqueId()).getLeader());
			if(isLastSpectator(player)) {
				raidManager.getBossBar().get(partyLeader.getUniqueId()).cancelTask();
			}
			for(UUID membere : raidManager.getRaidParties().get(partyLeader.getUniqueId()).getMembers()) {
				if(Bukkit.getPlayer(membere) != null) {
					OfflinePlayer member = Bukkit.getOfflinePlayer(membere);
					if(member.isOnline()) {
						Player members = (Player)member;
						raidManager.getBossBar().get(partyLeader.getUniqueId()).getBar().removePlayer(members);
						
						members.getInventory().clear();
						
						for(Player players : Bukkit.getOnlinePlayers()) {
							players.showPlayer(main, members);
						}
					}	
				}
			}
		}
	}
	
	//method to create a hovertext
	public TextComponent createHoverText(String string, String hover, String command, ChatColor color) {
		TextComponent message = new TextComponent(string);
		
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(color + hover)));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		
		message.setBold(true);
		message.setColor(color);
		
		
		return message;
	}
	
	//pick a random island to spectate
	public Location pickIsland(UUID puuid) {
		for(Location is : islands) {
			if(is.getBlockY() == 72) {
				islands.remove(is);
				Location newloc = new Location(is.getWorld(), is.getX(), 0, is.getZ());
				islands.add(newloc);
			}
		}
		List<Location> islandsNew = new ArrayList<Location>(islands);
		int chosen;
		
		islandsNew.remove(main.getIslandPositions().get(puuid));
		
		chosen = rand.nextInt(islandsNew.size());
		
		islands.remove(islandsNew.get(chosen));
		
		
		return islandsNew.get(chosen);
		
	}
	
	public void checkLeader(UUID player) {
		if(Bukkit.getPlayer(raidManager.getMembersParty(player).getLeader()) != null) {
			
			Player leader = Bukkit.getPlayer(raidManager.getMembersParty(player).getLeader());
			RaidParty party = raidManager.getRaidParties().get(leader.getUniqueId());
			List<Player> members = new ArrayList<Player>();
			
			for(UUID member : party.getMembers()) {
				
				if(!member.equals(player)) {
					if(Bukkit.getPlayer(member) != null) {
						members.add(Bukkit.getPlayer(member));	
					}
				}
			}
			raidManager.setLeader(Bukkit.getPlayer(player), party);
			
		}
		if(raidManager.getMembersParty(player).getMembers().size() == 1) {
			disbandRaidParty(Bukkit.getPlayer(player));
		}
	}
	
	public void disbandRaidParty(Player player) {
		RaidParty party = raidManager.getMembersParty(player.getUniqueId());
		raidManager.getRaidParties().remove(party.getLeader());
		party.removeAll();
	}
	
	//check if player was the last raider of that island
	public boolean isLastRaider(Player player) {
		if(getRaidersByLocation(getIslandRaider().get(player.getUniqueId())).size() == 1) {
			return true;
		}
		return false;
	}
	
	//check if player was the last spectator of that island
	public boolean isLastSpectator(Player player) {
		if(getSpectatorsByLocation(getIslandSpectator().get(player.getUniqueId())).size() == 1) {
			return true;
		}
		return false;
	}
	
	//add a raider to the list using the raidLeader
	public void addRaider(UUID uuid, UUID raidStarter) {
		islandRaider.put(uuid, islandRaider.get(raidStarter));
		raidManager.getRaidParties().get(raidStarter).addMember(Bukkit.getPlayer(uuid));
	}
	
	//add a raider to the list
	public void addRaider(UUID uuid, Location island) {
		islandRaider.put(uuid, island);
		
		//add raided island to the list of raided islands
		raidedIsland.put(island, fdb.getOwnerByLocation(island));
	}
	
	
	public void startRaid(Player player, Location spectateLocation, RaidParty party) {
		//check if any player in online players is equal to this player or is a member of this player's party
		for(Player players : Bukkit.getOnlinePlayers()) {
			if(players != player && !party.getMembers().contains(players.getUniqueId())) {
				players.hidePlayer(main, player);
			}
		}
		player.setInvulnerable(true);
		player.teleport(spectateLocation);
		player.setAllowFlight(true);
		
	}
	
	public boolean isInteger(String nmrStr) {
		try {
			Integer.parseInt(nmrStr);
		}
		catch(Exception ex) {
			return false;
		}
		return true;
	}
	
	public void enterSpectateMode(Player player) {
		//check if there are other islands
		if(main.getIslandPositions().size() > 1) {
			Location location =  pickIsland(player.getUniqueId());
			Location spectateLocation = new Location(location.getWorld(), location.getX(), 72, location.getZ());
			
			FileConfiguration language = main.getLanguage();
			
			ItemStorage storage = main.getStorage();
			
			//create party if the player doesn't gave one
			HashMap<UUID, RaidParty> parties = raidManager.getRaidParties();

			RaidParty party = null;
			RaidBar bar = null;
			//check partylists size
			if(parties.keySet().size() >= 1) {
				if(raidManager.getMembersParty(player.getUniqueId()) == null) {
					//create party if not exist
					raidManager.addRaidParty(player.getUniqueId(), new RaidParty(player));	
					
					party = raidManager.getRaidParties().get(player.getUniqueId());
					bar = raidManager.addRaidBar(player.getUniqueId(), new RaidBar(main, player, "spectate"));
				}
				else {
					party = raidManager.getMembersParty(player.getUniqueId());
					
					if(party.getLeader().equals(player.getUniqueId())) {
						bar = raidManager.addRaidBar(player.getUniqueId(), new RaidBar(main, player, "spectate"));
					}
				}				
			}
			else {
				//create party if not exist
				raidManager.addRaidParty(player.getUniqueId(), new RaidParty(player));	
				
				party = raidManager.getRaidParties().get(player.getUniqueId());
				bar = raidManager.addRaidBar(player.getUniqueId(), new RaidBar(main, player, "spectate"));
			}
			
			String title = language.getString("Raid.RaidFinder.RaidFinderTitles.Title"), subtitle = language.getString("Raid.RaidFinder.RaidFinderTitles.Subtitle")
					, actionbar = language.getString("Raid.RaidFinder.RaidFinderTitles.Actionbar");
			
			OfflinePlayer owner = Bukkit.getOfflinePlayer(fdb.getOwnerByLocation(new Location(location.getWorld(), location.getX(), 0, location.getZ())));
			main.getSkyBlock().getIslandManager().loadIsland(owner);
			for(UUID uuid : party.getMembers()) {
				if(Bukkit.getPlayer(uuid) != null) {
					Player players = Bukkit.getPlayer(uuid);
					islandSpectator.put(players.getUniqueId(), location);
					
					storage.saveToStorage(players);
					startRaid(players, spectateLocation, party);
					bar.addPlayer(players);
					
					new RaidTitles(title, subtitle, actionbar, players, null, main);
				}

			}
			
			
		}
		else {
			FileConfiguration language = main.getLanguage();
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', language.getString("Raid.Error.NotEnoughIslands.Message")));
		}
	}
	
	//method to replace the "%Player%" in language.yml with the player's name
	public String formatPlayer(String string, Player player) {
		String newstr = string;
		if(string.contains("%Player%")) {
			newstr = string.replace("%Player%", player.getName());
		}
		return newstr;
	}
	
	public void removeRaider(UUID uuid) {
		islandRaider.remove(uuid);
	}
	
	public HashMap<UUID, Location> getIslandSpectator() {
		return islandSpectator;
	}
	
	public void removeSpectator(UUID uuid) {
		islandSpectator.remove(uuid);
	}
	
	public HashMap<UUID, Location> getIslandRaider() {
		return islandRaider;
	}
	
	public HashMap<Location, UUID> getRaidedIslands() {
		return raidedIsland;
	}
	
	public List<Location> getIslands() {
		return islands;
	}
	
	//method to return all the raiders that are raiding a specific islandlocation
	public List<UUID> getRaidersByLocation(Location loc) {
		List<UUID> raiders = new ArrayList<UUID>();
		for(UUID uuid : islandRaider.keySet()) {
			if(islandRaider.get(uuid) == loc) {
				raiders.add(uuid);
			}
		}
		return raiders;
	}
	
	//method to return all the spectators that are spectating a specific islandlocation
	public List<UUID> getSpectatorsByLocation(Location loc) {
		List<UUID> spectators = new ArrayList<UUID>();
		for(UUID uuid : islandSpectator.keySet()) {
			if(islandSpectator.get(uuid) == loc) {
				spectators.add(uuid);
			}
		}
		return spectators;
	}
	
	//method to convert '&' color codes to colors
	public String format(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	//method to run when a raider exits the server
	public void onRaiderQuit(Player player) {
	
		if(getIslandRaider().containsKey(player.getUniqueId())) {
			if(raidManager.getRaidParties().get(player.getUniqueId()) != null) {
				checkLastRaider(player);
			}
			else {
				for(UUID uuid : getIslandRaider().keySet()) {
					if(raidManager.getRaidParties().get(uuid) != null && getIslandRaider().get(uuid) == getIslandRaider().get(player.getUniqueId())) {
						checkLastRaider(Bukkit.getPlayer(uuid));
					}
				}
			}
			removeRaider(player.getUniqueId());
			player.teleport(spawnLocation);
			raidManager.getCalledRaidCommands().remove(player.getUniqueId());
		}
	}
	
	//method to run when a spectator exits the server
	public void onSpectatorQuit(Player player) {
	
		if(getIslandSpectator().containsKey(player.getUniqueId())) {
			if(raidManager.getRaidParties().get(player.getUniqueId()) != null) {
				checkLastSpectator(player);
			}
			else {
				for(UUID uuid : getIslandRaider().keySet()) {
					if(raidManager.getRaidParties().get(uuid) != null && getIslandRaider().get(uuid) == getIslandRaider().get(player.getUniqueId())) {
						checkLastSpectator(Bukkit.getPlayer(uuid));
					}
				}
			}
			
			main.getStorage().restoreItems(player);
			cancelRaid(player);
			raidManager.getCalledRaidCommands().remove(player.getUniqueId());
		}
	}
	
	//method to check if a player is the last of the island's raiders
	public void checkLastRaider(Player player) {
		if(getIslandRaider().containsKey(player.getUniqueId())) {
			RaidParty party = raidManager.getMembersParty(player.getUniqueId());
			if(isLastRaider(player)) {
				Location raidedIsland = getIslandRaider().get(player.getUniqueId());
				if(!getIslands().contains(raidedIsland)) {
					getIslands().add(raidedIsland);	
				}
				for(UUID member : party.getMembers()) {
					if(Bukkit.getPlayer(member) != null) {
						Bukkit.getPlayer(member).sendMessage(format(language.getString("Raid.Raid.Outcome.Lost.Message")));
					
					}
				}
				for(IslandRole role : IslandRole.getRoles()) {
					for(UUID memberUUID : islandManager.getIsland(Bukkit.getOfflinePlayer(fdb.getOwnerByLocation(raidedIsland))).getRole(role)) {
						OfflinePlayer member = Bukkit.getOfflinePlayer(memberUUID);
						if(member.isOnline()) {
							Player onlineMember = (Player)member;
							onlineMember.sendMessage(format(language.getString("Raid.Raid.Outcome.Won.Message")));
						}
					}	
				}
				getRaidedIslands().remove(raidedIsland);
				raidManager.getBossBar().get(party.getLeader()).cancelTask();
				raidManager.getBossBar().get(party.getLeader()).getBar().removeAll();
				
				checkLeader(player.getUniqueId());
			}
			else {
				raidManager.getRaidParties().get(party.getLeader()).addDeadMember(player);
			}	
		}
	}
	
	//method to check if a player is the last of the island's spectators
	public void checkLastSpectator(Player player) {
		if(getIslandSpectator().containsKey(player.getUniqueId())) {
			if(isLastSpectator(player)) {
				if(!getIslands().contains(getIslandSpectator().get(player.getUniqueId()))) {
					getIslands().add(getIslandSpectator().get(player.getUniqueId()));	
				}
				RaidParty party = raidManager.getMembersParty(player.getUniqueId());
				if(party.getLeader().equals(player.getUniqueId())) {
					for(UUID member : party.getMembers()) {
						if(Bukkit.getPlayer(member) != null) {
							Bukkit.getPlayer(member).sendMessage(format(language.getString("Raid.RaidParty.Leader.LeftParty.Message")));
						}
					}	
				}
				raidManager.getBossBar().get(party.getLeader()).cancelTask();
				raidManager.getBossBar().get(party.getLeader()).getBar().removeAll();
				
				checkLeader(player.getUniqueId());
			}	
		}
	}
	
	//method to check if a block is inside the spawnzone
	public boolean inSpawnZone(Block block) {
    	FastDataAccess fdb = main.getFastDataAccess();
    	Location spawnPos1, spawnPos2;
		
		for(UUID uuid : fdb.getSpawnZones().keySet()) {
    		spawnPos1 = fdb.getSpawnZones().get(uuid)[0];
    		spawnPos2 = fdb.getSpawnZones().get(uuid)[1];
    		
    		int posXmax = 0, posZmax = 0, posXmin = 0, posZmin = 0;
    		if(spawnPos1.getBlockX() > spawnPos2.getBlockX()) {
    			posXmax = spawnPos1.getBlockX();
    			posXmin = spawnPos2.getBlockX();
    		}
    		else {
    			posXmax = spawnPos2.getBlockX();
    			posXmin = spawnPos1.getBlockX();
    		}
    		if(spawnPos1.getBlockZ() > spawnPos2.getBlockZ()) {
    			posZmax = spawnPos1.getBlockZ();
    			posZmin = spawnPos2.getBlockZ();
    		}
    		else {
    			posZmax = spawnPos2.getBlockZ();
    			posZmin = spawnPos1.getBlockZ();
    		}
    		
    		org.bukkit.util.Vector vector = new org.bukkit.util.Vector(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()),
    				vector1 = new org.bukkit.util.Vector(posXmin, 0, posZmin),
					vector2 = new org.bukkit.util.Vector(posXmax, 255, posZmax);
    		
    		if(vector.isInAABB(vector1, vector2)) {
           		return true;
    		}
    		
		}
    	return false;
    }
	
	//method to run when a raid stops
	public void cancelRaid(Player player) {
		if(raidManager.getLeaders().contains(player.getUniqueId())) {
			for(UUID uuid : raidManager.getRaidParties().get(player.getUniqueId()).getMembers()) {
				if(Bukkit.getOfflinePlayer(uuid) != null) {
					OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);
					((Player)member).teleport(raidManager.getStartPositions().get(uuid));
					if(!islands.contains(getIslandSpectator().get(member.getUniqueId()))) {
						islands.add(getIslandSpectator().get(member.getUniqueId()));	
					}	
				}
			}
			exitRaidSpectator(player);
			for(UUID member : raidManager.getMembersParty(player.getUniqueId()).getMembers()) {
				if(Bukkit.getPlayer(member) != null) {
					main.getStorage().restoreItems(Bukkit.getPlayer(member));
					removeSpectator(member);	
				}
			}
			raidManager.getCalledRaidCommands().remove(player.getUniqueId());
		}
	}
	
	//method to run when a spectator decides to raid an island
	public void raidIslandFromSpectator(Player player) {
		Location tempIsland = getIslandSpectator().get(player.getUniqueId());
		Location raidIsland = new Location(tempIsland.getWorld(), tempIsland.getX(), 72, tempIsland.getZ());
		
		RaidBar bar = raidManager.addRaidBar(player.getUniqueId(), new RaidBar(main, player, "raid"));
		
		OfflinePlayer owner = Bukkit.getOfflinePlayer(fdb.getOwnerByLocation(tempIsland));
		islandManager.loadIsland(owner);
		
		for(UUID member : raidManager.getRaidParties().get(player.getUniqueId()).getMembers()) {
			if(Bukkit.getPlayer(member) != null) {
				removeSpectator(member);
				Bukkit.getPlayer(member).teleport(raidIsland);
				
				Bukkit.getPlayer(member).setAllowFlight(false);
				Bukkit.getPlayer(member).setInvulnerable(false);	
				addRaider(member, raidIsland);
				main.getStorage().restoreItems(Bukkit.getPlayer(member));
			}
		}
		
		for(IslandRole ir : IslandRole.getRoles()) {
			for(UUID member : islandManager.getIslandByOwner(owner).getRole(ir)) {
				if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(member))) {
					bar.addPlayer(Bukkit.getPlayer(member));
				}
			}
		}
	}
	
	//method for spectators to go to the next island
	public void nextIsland(Player player) {
		islands.add(islandSpectator.get(player.getUniqueId()));
		exitRaidSpectator(player);
		for(UUID member : raidManager.getMembersParty(player.getUniqueId()).getMembers()) {
			if(Bukkit.getPlayer(member) != null) {
				main.getStorage().restoreItems(Bukkit.getPlayer(member));
				removeSpectator(member);	
			}
		}
		enterSpectateMode(player);
	}
}
