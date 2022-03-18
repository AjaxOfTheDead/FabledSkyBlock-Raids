package me.ResurrectAjax.Raid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;

import me.ResurrectAjax.Commands.Raid.ExitGui;
import me.ResurrectAjax.Commands.Raid.RaidEnd;
import me.ResurrectAjax.Commands.RaidHistory.SpecificHistory;
import me.ResurrectAjax.Commands.RaidHistory.StolenItems;
import me.ResurrectAjax.Commands.RaidParty.RaidPartyAccept;
import me.ResurrectAjax.Commands.RaidParty.RaidPartyCancelInvite;
import me.ResurrectAjax.Commands.RaidParty.RaidPartyDeny;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.FastDataAccess;
import me.ResurrectAjax.Playerdata.PlayerManager;
import me.ResurrectAjax.Raid.ItemStorage.ItemStorage;
import me.ResurrectAjax.RaidSense.RaidSenseTime;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

/**
 * A class that contains methods which are used across the plugin 
 * 
 * @author ResurrectAjax
 */
public class RaidMethods {
	
	private Main main;
	private RaidManager raidManager;
	private IslandManager islandManager;
	private Location spawnLocation;
	private Random rand = new Random();
	private FastDataAccess fdb;
	ItemStorage storage;
	private HashMap<UUID, Location> islandRaider = new HashMap<UUID, Location>();
	private HashMap<UUID, Location> islandSpectator = new HashMap<UUID, Location>();
	private HashMap<Location, UUID> raidedIsland = new HashMap<Location, UUID>();
	private HashMap<UUID, Integer> currentRaid = new HashMap<UUID, Integer>();
	
	private List<Location> spectatedIslands = new ArrayList<Location>();
	
	public static List<Material> CONTAINERTYPES = new ArrayList<Material>(Arrays.asList(
			Material.BARREL,
			Material.BLAST_FURNACE,
			Material.BREWING_STAND,
			Material.CHEST,
			Material.TRAPPED_CHEST,
			Material.DISPENSER,
			Material.DROPPER,
			Material.FURNACE,
			Material.HOPPER,
			Material.SHULKER_BOX,
			Material.SMOKER
			));
	
	public RaidMethods(Main main) {
		this.main = main;
		
		//load the managers
		this.raidManager = main.getRaidManager();
		this.islandManager = main.getSkyBlock().getIslandManager();
		
		//get fast data access
		fdb = main.getFastDataAccess();
		
		//load spawn
		spawnLocation = main.getSpawnLocation();
		
		storage = main.getStorage();
	}
	
	public List<Location> getSpectatedIslands() {
		return spectatedIslands;
	}
	
	/**
	 * @param syntax Convert string that contains '%Syntax%' into the syntax of the sent command
	 * @return String with the syntax given by {@link me.ResurrectAjax.Commands.Managers.CommandInterface#getSyntax()}
	 * */
	public static String convertSyntax(String syntax) {
		FileConfiguration configLoad = Main.getInstance().getLanguage();
		String syntaxMsg = configLoad.getString("Command.Error.BadSyntax.Message");
		String newstr = syntaxMsg;
		
		if(newstr.contains("%Syntax%")) {
			newstr = syntaxMsg.replace("%Syntax%", syntax + "");
		}
		return newstr;
	}
	
	public Integer getCurrentRaid(UUID uuid) {
		return currentRaid.get(uuid);
	}
	
	public void setCurrentRaid(UUID uuid, Integer id) {
		currentRaid.put(uuid, id);
	}
	
	//exit spectator mode
	public void exitRaidSpectator(Player player) {
		//check if player was a spectator
		if(getIslandSpectator().containsKey(player.getUniqueId())) {
			
			//check if player was the last spectator of that island
			UUID partyLeader = raidManager.getMembersParty(player.getUniqueId()).getLeader();
			if(isLastSpectator(player)) {
				raidManager.getBossBar().get(partyLeader).cancelTask();
			}
			for(UUID membere : raidManager.getRaidParties().get(partyLeader).getMembers()) {
				if(Bukkit.getPlayer(membere) != null) {
					OfflinePlayer member = Bukkit.getOfflinePlayer(membere);
					if(member.isOnline()) {
						Player members = (Player)member;
						raidManager.getBossBar().get(partyLeader).getBar().removePlayer(members);
						
						members.getInventory().clear();
					}
					for(Player players : Bukkit.getOnlinePlayers()) {
						players.showPlayer(main, Bukkit.getPlayer(membere));
					}
				}
			}
			raidManager.getBossBar().get(partyLeader).cancelTask();
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
	public Location pickIsland(UUID puuid, List<Location> islands) {
		List<Location> islandsNew = new ArrayList<Location>(islands);
		int chosen;
		
		islandsNew.remove(main.getIslandPositions().get(puuid));
		
		chosen = rand.nextInt(islandsNew.size());
		
		if(spectatedIslands.contains(islandsNew.get(chosen))) {
			islandsNew.remove(chosen);
			pickIsland(puuid, islandsNew);
		}
		else {
			spectatedIslands.add(islandsNew.get(chosen));
		}
		
		
		return islandsNew.get(chosen);
		
	}
	
	public boolean isValidDate(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(ChatColor.stripColor(date).trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
	}
	
	public void checkLeader(UUID player) {
		if(Bukkit.getPlayer(raidManager.getMembersParty(player).getLeader()) == null) {
			
			RaidParty party = raidManager.getMembersParty(player);
			
			for(int i = 0; i < party.getMembers().size(); i++) {
				if(Bukkit.getPlayer(party.getMembers().get(i)) != null) {
					party.setLeader(party.getMembers().get(i));
					break;
				}
			}
			
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
		raidManager.getRaidParties().get(raidStarter).addMember(uuid);
	}
	
	//add a raider to the list
	public void addRaider(UUID uuid, Location island) {
		islandRaider.put(uuid, island);
		
		//add raided island to the list of raided islands
		raidedIsland.put(island, fdb.getOwnerByLocation(island));
	}
	
	
	public void startSpectating(Player player, Location spectateLocation, RaidParty party) {
		//check if any player in online players is equal to this player or is a member of this player's party
		for(Player players : Bukkit.getOnlinePlayers()) {
			if(players != player && !party.getMembers().contains(players.getUniqueId())) {
				players.hidePlayer(main, player);
			}
		}
		player.setInvulnerable(true);
		
		IslandManager islandManager = main.getSkyBlock().getIslandManager();
		islandManager.loadIslandAtLocation(spectateLocation);
		player.teleport(spectateLocation);
		player.setAllowFlight(true);
		
		
	}
	
	public static boolean isInteger(String nmrStr) {
		try {
			Integer.parseInt(nmrStr);
		}
		catch(Exception ex) {
			return false;
		}
		return true;
	}
	
	public static Integer getIntFromString(String str) {
		String nStr = ChatColor.stripColor(str);
		return Integer.parseInt(nStr.replaceAll("[\\D]", ""));
	}
	
	public void enterSpectateMode(Player player) {
		FileConfiguration language, configLoad;
		language = main.getLanguage();
		configLoad = main.getConfiguration();
		//check if there are other islands
		if(main.getIslandPositions().size() > 1) {
			UUID raiderOwner = PlayerManager.getPlayersIsland(player.getUniqueId()).getOwnerUUID();
			
			if(fdb.getRaidSense(raiderOwner) >= configLoad.getDouble("Raid.RaidFinder.RaidSensePrice")) {
				fdb.putRaidSense(raiderOwner, fdb.getRaidSense(raiderOwner) - configLoad.getDouble("Raid.RaidFinder.RaidSensePrice"));
				
				Location location =  pickIsland(raiderOwner, getIslands());
				Location spectateLocation = new Location(location.getWorld(), location.getX(), 72, location.getZ());
				
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
						bar = raidManager.addRaidBar(player.getUniqueId(), new RaidBar(main, player, "raidfinder"));
					}
					else {
						party = raidManager.getMembersParty(player.getUniqueId());
						
						if(party.getLeader().equals(player.getUniqueId())) {
							bar = raidManager.addRaidBar(player.getUniqueId(), new RaidBar(main, player, "raidfinder"));
						}
					}				
				}
				else {
					//create party if not exist
					raidManager.addRaidParty(player.getUniqueId(), new RaidParty(player));	
					
					party = raidManager.getMembersParty(player.getUniqueId());
					bar = raidManager.addRaidBar(player.getUniqueId(), new RaidBar(main, player, "raidfinder"));
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
						startSpectating(players, spectateLocation, party);
						bar.addPlayer(players);
						
						new RaidTitles(title, subtitle, actionbar, players, null, main);
					}

				}
			}
			else {
				cancelRaid(player);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', language.getString("Raid.RaidFinder.Error.InsufficientFunds.Message")));
			}
			
			
			
		}
		else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', language.getString("Raid.Error.NotEnoughIslands.Message")));
		}
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
		List<Location> islands = new ArrayList<Location>();
		for(UUID uuid : main.getIslandPositions().keySet()) {
			Location loc = main.getIslandPositions().get(uuid), 
					 newLoc = new Location(loc.getWorld(), loc.getX(), 0, loc.getZ());;
			islands.add(newLoc);
		}
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
	public static String format(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static List<String> FORMATS = new ArrayList<String>(Arrays.asList(
			"%Player%",
			"%Date%",
			"%TimeLeft%",
			"%ID%",
			"%RaidSense%"
			));
 	public static String format(String msg, String value) {
 		String newStr = msg;
 		for(String format : FORMATS) {
 			if(msg.contains(format)) {
 				newStr = msg.replace(format, value);
 			}
 		}
 		return ChatColor.translateAlternateColorCodes('&', newStr);
	}
	
	//method to run when a raider exits the server
	public void onRaiderQuit(Player player) {
	
		if(getIslandRaider().containsKey(player.getUniqueId())) {
			if(raidManager.getMembersParty(player.getUniqueId()) != null) {
				checkLastRaider(player);
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
				for(UUID uuid : getIslandSpectator().keySet()) {
					if(raidManager.getRaidParties().get(uuid) != null && getIslandSpectator().get(uuid) == getIslandSpectator().get(player.getUniqueId())) {
						checkLastSpectator(Bukkit.getPlayer(uuid));
					}
				}
			}
			
			player.teleport(raidManager.getStartPositions().get(player.getUniqueId()));
			main.getStorage().restoreItems(player);
			raidManager.getCalledRaidCommands().remove(player.getUniqueId());
		}
	}
	
	//method to check if a player is the last of the island's raiders
	public void checkLastRaider(Player player) {
		FileConfiguration language;
		language = main.getLanguage();
		if(getIslandRaider().containsKey(player.getUniqueId())) {
			RaidParty party = raidManager.getMembersParty(player.getUniqueId());
			if(isLastRaider(player)) {
				Location raidedIsland = getIslandRaider().get(player.getUniqueId());
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
				RaidEnd.endRaid(player.getUniqueId());
				
				checkLeader(player.getUniqueId());
			}
			else {
				raidManager.getRaidParties().get(party.getLeader()).addDeadMember(player.getUniqueId());
			}	
		}
	}

	
	//method to check if a player is the last of the island's spectators
	public void checkLastSpectator(Player player) {
		FileConfiguration language;
		language = main.getLanguage();
		if(getIslandSpectator().containsKey(player.getUniqueId())) {
			if(isLastSpectator(player)) {
				Location loc = getIslandSpectator().get(player.getUniqueId()), 
						 newLoc = new Location(loc.getWorld(), loc.getX(), 0, loc.getZ());
				if(spectatedIslands.contains(newLoc)) {
					spectatedIslands.remove(newLoc);
				}
				RaidParty party = raidManager.getMembersParty(player.getUniqueId());
				if(party.getLeader().equals(player.getUniqueId())) {
					for(UUID member : party.getMembers()) {
						if(Bukkit.getPlayer(member) != null) {
							Bukkit.getPlayer(member).sendMessage(format(language.getString("RaidParty.Leader.LeftParty.Message")));
						}
					}	
				}
				raidManager.getBossBar().get(party.getLeader()).cancelTask();
				raidManager.getBossBar().get(party.getLeader()).getBar().removeAll();
				
				checkLeader(player.getUniqueId());
				
				cancelRaid(player);
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
			for(UUID uuid : raidManager.getMembersParty(player.getUniqueId()).getMembers()) {
				if(Bukkit.getPlayer(uuid) != null) {
					OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);
					((Player)member).teleport(raidManager.getStartPositions().get(uuid));
					Location loc = getIslandSpectator().get(member.getUniqueId()), 
							 newLoc = new Location(loc.getWorld(), loc.getX(), 0, loc.getZ());
					if(spectatedIslands.contains(newLoc)) {
						spectatedIslands.remove(newLoc);
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
		Location tempIsland = islandSpectator.get(player.getUniqueId());
		Location raidIsland = new Location(tempIsland.getWorld(), tempIsland.getX(), 72, tempIsland.getZ());
		
		OfflinePlayer owner = Bukkit.getOfflinePlayer(fdb.getOwnerByLocation(tempIsland));
		islandManager.loadIsland(owner);
		
		RaidBar bar = raidManager.addRaidBar(player.getUniqueId(), new RaidBar(main, player, "raid"));
		
		for(UUID member : raidManager.getMembersParty(player.getUniqueId()).getMembers()) {
			if(Bukkit.getPlayer(member) != null) {
				Bukkit.getPlayer(member).teleport(raidIsland);
				
				Bukkit.getPlayer(member).setAllowFlight(false);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
				    public void run() {
				    	Bukkit.getPlayer(member).setInvulnerable(false);
				    }
			    }, 20);
				removeSpectator(member);
				main.getStorage().restoreItems(Bukkit.getPlayer(member));
				addRaider(member, raidIsland);
				
				bar.addPlayer(Bukkit.getPlayer(member));
				
				for(Player players : Bukkit.getOnlinePlayers()) {
					players.showPlayer(main, Bukkit.getPlayer(member));
				}
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
		Location loc = islandSpectator.get(player.getUniqueId()), 
				 newLoc = new Location(loc.getWorld(), loc.getX(), 0, loc.getZ());
		spectatedIslands.remove(newLoc);
		exitRaidSpectator(player);
		for(UUID member : raidManager.getMembersParty(player.getUniqueId()).getMembers()) {
			if(Bukkit.getPlayer(member) != null) {
				main.getStorage().restoreItems(Bukkit.getPlayer(member));
				removeSpectator(member);	
			}
		}
		enterSpectateMode(player);
	}
	
	public List<PotionEffect> getAmplifiers(RaidParty party) {
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		
		FileConfiguration configLoad;
		configLoad = main.getConfiguration();
		
		if(!configLoad.getConfigurationSection("Raid.RaidSense.SenseMultipliers").getKeys(false).iterator().hasNext()) return effects;
		
		UUID raidIslandOwner = PlayerManager.getPlayersIsland(party.getLeader()).getOwnerUUID();
		String configSection = "Raid.RaidSense.SenseMultipliers";
		List<String> minValues = new ArrayList<String>(configLoad.getConfigurationSection(configSection).getKeys(false));
		for(int i = 0; i < minValues.size(); i++) {
			if(i+1 < minValues.size()) {
				if(fdb.getRaidSense(raidIslandOwner) < Integer.parseInt(minValues.get(i)) || fdb.getRaidSense(raidIslandOwner) > Integer.parseInt(minValues.get(i+1))) continue;
				for(PotionEffect effect : getPotionEffects(configLoad.getConfigurationSection(configSection + "." + minValues.get(i) + ".PotionEffects"))) {
					effects.add(effect);	
				}		
			}
			else {
				if(fdb.getRaidSense(raidIslandOwner) < Integer.parseInt(minValues.get(i))) continue;
				for(PotionEffect effect : getPotionEffects(configLoad.getConfigurationSection(configSection + "." + minValues.get(i) + ".PotionEffects"))) {
					effects.add(effect);	
				}
				
			}
		}
		return effects;
	}
	
	private List<PotionEffect> getPotionEffects(ConfigurationSection section) {
		List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
		for(String effect : section.getKeys(false)) {
			int amplifier = section.getInt(effect + ".Amplifier"),
				duration = RaidSenseTime.convertHoursMinutesSecondsToSeconds(section.getString(effect + ".Duration"))*20;
			
			PotionType type = PotionType.valueOf(effect);
			potionEffects.add(new PotionEffect(type.getEffectType(), duration, amplifier-1));
		}
		return potionEffects;
	}
	
	
	public static List<String> getBlackListCommands() {
		return new ArrayList<String>(Arrays.asList(
				RaidPartyAccept.NAME,
				RaidPartyDeny.NAME,
				RaidPartyCancelInvite.NAME,
				ExitGui.NAME,
				StolenItems.NAME,
				SpecificHistory.NAME
				));
	}
	
	public UUID getIslandUUIDByLocation(Location location) {
		Island island = main.getSkyBlock().getIslandManager().getIsland(Bukkit.getOfflinePlayer(fdb.getOwnerByLocation(location)));
		
		return island.getIslandUUID();
	}
}
