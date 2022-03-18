package me.ResurrectAjax.Mysql;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Playerdata.PlayerManager;
import me.ResurrectAjax.RaidSense.RaidSenseTime;

public class FastDataAccess {
	private Database db;
	private final Main main;
	
	private HashMap<UUID, Location[]> spawnZones;
	private HashMap<UUID, Location> spawnPositions;
	private HashMap<String, HashMap<String, Integer>> structures;
	
	private HashMap<UUID, Double> raidSense = new HashMap<UUID, Double>();
	private HashMap<UUID, Integer> islandTime = new HashMap<UUID, Integer>();
	
	public FastDataAccess(Database db, Main main) {
		this.db = db;
		this.main = main;
		putAllSpawnZones();
		putAllStructures();
		loadAllRaidSense();
		loadAllIslandTime();
	}
	
	public void putAllSpawnZones() {
		spawnZones = new HashMap<UUID, Location[]>();
		spawnPositions = new HashMap<UUID, Location>();
		List<List<String>> databaseSpawnZones = db.getSpawnZones();
		
		for(int i = 0; i < databaseSpawnZones.size(); i++) {
			UUID uuid = UUID.fromString(databaseSpawnZones.get(i).get(0));
			
			String posX, posZ, world = databaseSpawnZones.get(i).get(4), posXspawn, posZspawn;
			Location pos1, pos2, spawnpos;
			Location[] zone = new Location[2];
			
			posX = databaseSpawnZones.get(i).get(1).split(":")[0];
			posZ = databaseSpawnZones.get(i).get(1).split(":")[1];
			pos1 = new Location(Bukkit.getWorld(world), Integer.parseInt(posX), 0, Integer.parseInt(posZ));
			
			posX = databaseSpawnZones.get(i).get(2).split(":")[0];
			posZ = databaseSpawnZones.get(i).get(2).split(":")[1];
			pos2 = new Location(Bukkit.getWorld(world), Integer.parseInt(posX), 0, Integer.parseInt(posZ));
			
			posXspawn = databaseSpawnZones.get(i).get(3).split(":")[0];
			posZspawn = databaseSpawnZones.get(i).get(3).split(":")[1];
			spawnpos = new Location(Bukkit.getWorld(world), Double.parseDouble(posXspawn), 0, Double.parseDouble(posZspawn));
			
			zone[0] = pos1;
			zone[1] = pos2;
			
			spawnZones.put(uuid, zone);
			spawnPositions.put(uuid, spawnpos);
		}
	}
	
	public HashMap<UUID, Location> getSpawnPositions() {
		return spawnPositions;
	}
	
	public void putAllStructures() {
		structures = new HashMap<String, HashMap<String, Integer>>();
		List<List<String>> databaseStructures = db.getStructures();
		
		for(int i = 0; i < databaseStructures.size(); i++) {
			String name = databaseStructures.get(i).get(0);
			
			String pos1X, pos1Z, pos2X, pos2Z;
			HashMap<String, Integer> positions = new HashMap<String, Integer>();
			
			pos1X = databaseStructures.get(i).get(1).split(":")[0];
			pos1Z = databaseStructures.get(i).get(1).split(":")[1];
			
			pos2X = databaseStructures.get(i).get(2).split(":")[0];
			pos2Z = databaseStructures.get(i).get(2).split(":")[1];
			
			positions.put("pos1X", Integer.parseInt(pos1X));
			positions.put("pos1Z", Integer.parseInt(pos1Z));
			positions.put("pos2X", Integer.parseInt(pos2X));
			positions.put("pos2Z", Integer.parseInt(pos2Z));
			
			structures.put(name, positions);
		}
	}
	
	public void putStructure(String name, int pos1x, int pos1z, int pos2x, int pos2z) {
		HashMap<String, Integer> positions = new HashMap<String, Integer>();
		
		positions.put("pos1X", pos1x);
		positions.put("pos1Z", pos1z);
		positions.put("pos2X", pos2x);
		positions.put("pos2Z", pos2z);
		
		structures.put(name, positions);
		
	}
	
	public void putSpawnZone(UUID uuid, Location first, Location second, Location spawnpos, String world) {
		Location[] zone = new Location[2];
		
		zone[0] = first;
		zone[1] = second;
		
		spawnZones.put(uuid, zone);
		spawnPositions.put(uuid, spawnpos);
		
	}

	
	public UUID getOwnerByLocation(Location location) {
		UUID owner = null;
		for(UUID uuid : spawnPositions.keySet()) {
			Location uuidPos = new Location(spawnPositions.get(uuid).getWorld(), spawnPositions.get(uuid).getX(), 0, spawnPositions.get(uuid).getZ()), 
					ownerPos = new Location(location.getWorld(), location.getX(), 0, location.getZ());
			if(uuidPos.equals(ownerPos)) {
				owner = uuid;
			}
		}
		return owner;
	}
	
	public void removeSpawnZone(UUID uuid) {
		spawnZones.remove(uuid);
		spawnPositions.remove(uuid);
	}
	
	public void loadAllRaidSense() {
		raidSense = db.getAllRaidSense();
	}
	
	public HashMap<String, HashMap<String, Integer>> getStructures() {
		return structures;
	}
	
	public HashMap<UUID, Location[]> getSpawnZones() {
		return spawnZones;
	}
	
	public HashMap<UUID, Double> getAllRaidSense() {
		return raidSense;
	}
	
	public void addIsland(UUID uuid, double sense) {
		RaidSenseTime raidSenseTime = main.getIslandTime();
		
		db.setIsland(uuid.toString(), sense);
		islandTime.put(uuid, 0);
		raidSenseTime.putPlayerLogTime(uuid);
		putRaidSense(uuid, sense);
		
	}
	
	public void putIslandTime(UUID uuid, Integer time) {
		islandTime.put(uuid, time);
	}
	
	public void saveIslandTimeToDatabase(UUID uuid) {
		UUID owner = PlayerManager.getPlayersIsland(uuid).getOwnerUUID();
		db.updateIslandTime(owner, islandTime.get(owner));
	}
	
	public void removeIsland(UUID uuid) {
		islandTime.remove(uuid);
		raidSense.remove(uuid);
		db.deleteIsland(uuid);
	}
	
	public void loadAllIslandTime() {
		islandTime = db.getAllIslandTime();
	}
	
	public Integer getIslandTime(UUID uuid) {
		Integer time = null;
		if(islandTime != null && islandTime.containsKey(uuid)) {
			time = islandTime.get(uuid);
		}
		return time;
	}
	
	public HashMap<UUID, Integer> getAllIslandTime() {
		return islandTime;
	}
	
	public Double getRaidSense(UUID uuid) {
		return raidSense.get(uuid);
	}
	
	public void saveRaidSenseToDatabase(UUID uuid, Double sense) {
		db.updateRaidSense(uuid, sense);
	}
	
	public void putRaidSense(UUID uuid, Double sense) {
		raidSense.put(uuid, sense);
	}
	
	public void updateIslandOwner(UUID oldOwner, UUID newOwner) {
		Double sense = raidSense.get(oldOwner);
		Integer time = islandTime.get(oldOwner);
		
		raidSense.remove(oldOwner);
		islandTime.remove(oldOwner);
		
		raidSense.put(newOwner, sense);
		islandTime.put(newOwner, time);
		
		db.updateOwnership(oldOwner.toString(), newOwner.toString());
	}
}
