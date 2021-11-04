package me.ResurrectAjax.Listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.songoda.skyblock.api.event.island.IslandCreateEvent;
import com.songoda.skyblock.api.event.island.IslandDeleteEvent;
import com.songoda.skyblock.api.event.island.IslandOwnershipTransferEvent;
import com.songoda.skyblock.api.island.IslandEnvironment;
import com.songoda.skyblock.api.island.IslandWorld;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.structure.StructureManager;

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
		
		//get the original structures name
		StructureManager structureManager = main.getSkyBlock().getStructureManager();
		String realStructureName = structureManager.getStructure(event.getIsland().getStructure()).getOverworldFile().replace(".structure", "");
		
		//add strcuture to database/store in memory
		db.setSpawnZones(event.getPlayer().getUniqueId().toString(), (fdb.getStructures().get(realStructureName).get("pos1X") +  
				islandLocation.getBlockX()) + ":" + (fdb.getStructures().get(realStructureName).get("pos1Z") + islandLocation.getBlockZ()),
        		(fdb.getStructures().get(realStructureName).get("pos2X") + islandLocation.getBlockX()) + ":" + (fdb.getStructures().get(realStructureName).get("pos2Z") + 
        				islandLocation.getBlockZ()), islandLocation.getX() + ":" + islandLocation.getZ(), islandLocation.getWorld().getName());
		
		Location spawn1, spawn2;
		spawn1 = new Location(islandLocation.getWorld(), fdb.getStructures().get(realStructureName).get("pos1X") +  
				islandLocation.getBlockX(), 0, fdb.getStructures().get(realStructureName).get("pos1Z") + islandLocation.getBlockZ());
		spawn2 = new Location(islandLocation.getWorld(), fdb.getStructures().get(realStructureName).get("pos2X") + 
				islandLocation.getBlockX(), 255, fdb.getStructures().get(realStructureName).get("pos2Z") + islandLocation.getBlockZ());
		fdb.putSpawnZone(event.getPlayer().getUniqueId(), spawn1, spawn2, islandLocation, islandLocation.getWorld().getName());
	}
	
	@EventHandler
	public void onIslandDelete(IslandDeleteEvent event) {
		db.deleteValues("SpawnZones", "uuid", event.getIsland().getOwnerUUID().toString());
		fdb.removeSpawnZone(event.getIsland().getOwnerUUID());
	}
	
	@EventHandler
	public void onIslandOwnershipTransfer(IslandOwnershipTransferEvent event) {
		PlayerData playerdata = main.getSkyBlock().getPlayerDataManager().getPlayerData(event.getOwner().getUniqueId());
		
		
		db.updateOwnership(event.getOwner().getUniqueId().toString(), playerdata.getOwner().toString());
		fdb.putAllSpawnZones();
	}
}
