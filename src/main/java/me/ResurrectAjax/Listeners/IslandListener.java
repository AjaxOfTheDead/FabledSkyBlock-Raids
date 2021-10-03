package me.ResurrectAjax.Listeners;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.songoda.skyblock.api.event.island.IslandCreateEvent;
import com.songoda.skyblock.api.event.island.IslandDeleteEvent;
import com.songoda.skyblock.api.event.island.IslandLoadEvent;
import com.songoda.skyblock.api.event.island.IslandOwnershipTransferEvent;
import com.songoda.skyblock.api.island.IslandEnvironment;
import com.songoda.skyblock.api.island.IslandRole;
import com.songoda.skyblock.api.island.IslandWorld;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.playerdata.PlayerData;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.Database;
import me.ResurrectAjax.Mysql.FastDataAccess;

public class IslandListener implements Listener{
	Main main;
	FastDataAccess fdb;
	Database db;
	public IslandListener(Main main) {
		this.main = main;
		db = main.getRDatabase();
		fdb = main.getFastDataAccess();
	}
	
	@EventHandler
	public void onIslandCreate(IslandCreateEvent event) {
		Location islandLocation = event.getIsland().getLocation(IslandWorld.OVERWORLD, IslandEnvironment.MAIN);
		
		db.setSpawnZones(event.getPlayer().getUniqueId().toString(), (fdb.getStructures().get(event.getIsland().getStructure()).get("pos1X") +  
				islandLocation.getBlockX()) + ":" + (fdb.getStructures().get(event.getIsland().getStructure()).get("pos1Z") + islandLocation.getBlockZ()),
        		(fdb.getStructures().get(event.getIsland().getStructure()).get("pos2X") + islandLocation.getBlockX()) + ":" + (fdb.getStructures().get(event.getIsland().getStructure()).get("pos2Z") + 
        				islandLocation.getBlockZ()), islandLocation.getWorld().getName());
		
		Location spawn1, spawn2;
		spawn1 = new Location(islandLocation.getWorld(), fdb.getStructures().get(event.getIsland().getStructure()).get("pos1X") +  
				islandLocation.getBlockX(), 0, fdb.getStructures().get(event.getIsland().getStructure()).get("pos1Z") + islandLocation.getBlockZ());
		spawn2 = new Location(islandLocation.getWorld(), fdb.getStructures().get(event.getIsland().getStructure()).get("pos2X") + 
				islandLocation.getBlockX(), 255, fdb.getStructures().get(event.getIsland().getStructure()).get("pos2Z") + islandLocation.getBlockZ());
		fdb.putSpawnZone(event.getPlayer().getUniqueId(), spawn1, spawn2, islandLocation.getWorld().getName());
	}
	
	@EventHandler
	public void onIslandDelete(IslandDeleteEvent event) {
		db.deleteValues("SpawnZones", "uuid", event.getIsland().getOwnerUUID().toString());
		fdb.putAllSpawnZones();
	}
	
	@EventHandler
	public void onIslandOwnershipTransfer(IslandOwnershipTransferEvent event) {
		PlayerData playerdata = main.getSkyBlock().getPlayerDataManager().getPlayerData(event.getOwner().getUniqueId());
		
		
		db.updateOwnership(event.getOwner().getUniqueId().toString(), playerdata.getOwner().toString());
		fdb.putAllSpawnZones();
	}
}
