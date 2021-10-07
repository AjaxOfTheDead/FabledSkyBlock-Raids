package me.ResurrectAjax.Commands.Raid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;

import me.ResurrectAjax.Main.Main;

public class RaidMethods {
	private Main main;
	private Random rand = new Random();
	private HashMap<UUID, Location> islandRaider = new HashMap<UUID, Location>();
	private HashMap<UUID, Location> islandSpectator = new HashMap<UUID, Location>();
	private List<UUID> raidedIsland = new ArrayList<UUID>();
	private List<Location> islands;
	
	public RaidMethods(Main main) {
		this.main = main;
		HashMap<UUID, Location> positions = main.getIslandPositions();
		islands = new ArrayList<Location>();
		for(UUID uuid : positions.keySet()) {
			islands.add(positions.get(uuid));
		}
	}
	
	public void exitRaidSpectator(Player player) {
		if(main.getStorage().getItemStorage().get(player.getUniqueId()) != null) {
			
			main.getBossBar().get(player.getUniqueId()).cancelTask();
			main.getBossBar().get(player.getUniqueId()).getBar().removePlayer(player);
			
			main.getStorage().restoreItems(player);
			
			for(Player players : Bukkit.getOnlinePlayers()) {
				players.showPlayer(main, player);
			}
			
			removeSpectator(player.getUniqueId());
			
			player.setAllowFlight(false);
			player.setInvulnerable(false);
		}
	}
	
	public Location pickIsland(UUID puuid) {
		List<Location> islandsNew = new ArrayList<Location>(islands);
		int chosen;
		
		if(islandsNew.contains(main.getIslandPositions().get(puuid))) {
			islandsNew.remove(main.getIslandPositions().get(puuid));
		}
		chosen = rand.nextInt(islandsNew.size());
		
		islandSpectator.put(puuid, islandsNew.get(chosen));
		islands.remove(islandsNew.get(chosen));
		
		return islandsNew.get(chosen);
		
	}
	
	public void endRaid(Location island) {
		island.add(island);
	}
	
	public boolean isLastRaider(Player player) {
		if(getRaidersByLocation(getIslandRaider().get(player.getUniqueId())).size() == 1) {
			for(UUID uuid : main.getBossBar().keySet()) {
				if(main.getBossBar().get(player.getUniqueId()) == main.getBossBar().get(uuid)) {
					main.getBossBar().get(uuid).cancelTask();
					main.getBossBar().get(uuid).getBar().removePlayer(Bukkit.getPlayer(uuid));
					if(getIslandRaider().get(uuid) == null) {
						
					}
				}
			}
			return true;
		}
		return false;
	}
	
	public void addRaider(UUID uuid, UUID raidStarter) {
		islandRaider.put(uuid, islandRaider.get(raidStarter));
	}
	
	public void addRaider(UUID uuid, Location island) {
		islandRaider.put(uuid, island);
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
	
	public List<UUID> getRaidedIslands() {
		return raidedIsland;
	}
	
	public List<Location> getIslands() {
		return islands;
	}
	
	public List<UUID> getRaidersByLocation(Location loc) {
		List<UUID> raiders = new ArrayList<UUID>();
		for(UUID uuid : islandRaider.keySet()) {
			if(islandRaider.get(uuid) == loc) {
				raiders.add(uuid);
			}
		}
		return raiders;
	}
}
