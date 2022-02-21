package me.ResurrectAjax.Listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
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
		Player player = event.getPlayer();
		
		FileConfiguration configLoad = main.getConfiguration();
		
		//get the original structures name
		StructureManager structureManager = main.getSkyBlock().getStructureManager();
		String realStructureName = structureManager.getStructure(event.getIsland().getStructure()).getOverworldFile().replace(".structure", "");
		
		//add strcuture to database/store in memory
		HashMap<String, Integer> struct = fdb.getStructures().get(realStructureName);
		db.setSpawnZones(player.getUniqueId().toString(), (struct.get("pos1X") +  
				islandLocation.getBlockX()) + ":" + (struct.get("pos1Z") + islandLocation.getBlockZ()),
        		(struct.get("pos2X") + islandLocation.getBlockX()) + ":" + (struct.get("pos2Z") + 
        				islandLocation.getBlockZ()), islandLocation.getX() + ":" + islandLocation.getZ(), islandLocation.getWorld().getName());
		
		Location spawn1, spawn2;
		spawn1 = new Location(islandLocation.getWorld(), struct.get("pos1X") +  
				islandLocation.getBlockX(), 0, struct.get("pos1Z") + islandLocation.getBlockZ());
		spawn2 = new Location(islandLocation.getWorld(), struct.get("pos2X") + 
				islandLocation.getBlockX(), 255, struct.get("pos2Z") + islandLocation.getBlockZ());
		fdb.putSpawnZone(player.getUniqueId(), spawn1, spawn2, islandLocation, islandLocation.getWorld().getName());
		fdb.addIsland(player.getUniqueId(), configLoad.getDouble("Raid.RaidSense.StartAmount"));
		main.getIslandTime().putPlayerLogTime(player.getUniqueId());
	}
	
	@EventHandler
	public void onIslandDelete(IslandDeleteEvent event) {
		db.deleteValues("SpawnZones", "uuid", event.getIsland().getOwnerUUID().toString());
		fdb.removeSpawnZone(event.getIsland().getOwnerUUID());
		fdb.removeIsland(event.getIsland().getOwnerUUID());
	}
	
	@EventHandler
	public void onIslandOwnershipTransfer(IslandOwnershipTransferEvent event) {
		PlayerData playerdata = main.getSkyBlock().getPlayerDataManager().getPlayerData(event.getOwner().getUniqueId());
		
		fdb.updateIslandOwner(event.getOwner().getUniqueId(), playerdata.getOwner());
		fdb.putAllSpawnZones();
	}
}
