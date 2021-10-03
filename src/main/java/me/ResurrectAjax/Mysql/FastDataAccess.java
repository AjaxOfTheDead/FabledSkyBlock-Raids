package me.ResurrectAjax.Mysql;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class FastDataAccess {
	private Database db;
	
	private HashMap<UUID, Location[]> spawnZones;
	private HashMap<String, HashMap<String, Integer>> structures;
	
	public FastDataAccess(Database db) {
		this.db = db;
	}
	
	public void putAllSpawnZones() {
		spawnZones = new HashMap<UUID, Location[]>();
		List<List<String>> databaseSpawnZones = db.getSpawnZones();
		
		for(int i = 0; i < databaseSpawnZones.size(); i++) {
			UUID uuid = UUID.fromString(databaseSpawnZones.get(i).get(0));
			
			String posX, posY, world = databaseSpawnZones.get(i).get(3);
			Location pos1, pos2;
			Location[] zone = new Location[2];
			
			posX = databaseSpawnZones.get(i).get(1).split(":")[0];
			posY = databaseSpawnZones.get(i).get(1).split(":")[1];
			pos1 = new Location(Bukkit.getWorld(world), Integer.parseInt(posX), 0, Integer.parseInt(posY));
			
			posX = databaseSpawnZones.get(i).get(2).split(":")[0];
			posY = databaseSpawnZones.get(i).get(2).split(":")[1];
			pos2 = new Location(Bukkit.getWorld(world), Integer.parseInt(posX), 0, Integer.parseInt(posY));
			
			zone[0] = pos1;
			zone[1] = pos2;
			
			spawnZones.put(uuid, zone);
		}
	}
	
	public void putAllStructures() {
		structures = new HashMap<String, HashMap<String, Integer>>();
		List<List<String>> databaseStructures = db.getStructures();
		
		for(int i = 0; i < databaseStructures.size(); i++) {
			String name = databaseStructures.get(i).get(0);
			
			String pos1X, pos1Z, pos2X, pos2Z;
			Location pos1, pos2;
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
	
	public void putStructure(String name, Location first, Location second) {
		int pos1X, pos1Z, pos2X, pos2Z;
		HashMap<String, Integer> positions = new HashMap<String, Integer>();
		
		pos1X = first.getBlockX();
		pos1Z = first.getBlockZ();
		
		pos2X = second.getBlockX();
		pos2Z = second.getBlockZ();
		
		positions.put("pos1X", pos1X);
		positions.put("pos1Z", pos1Z);
		positions.put("pos2X", pos2X);
		positions.put("pos2Z", pos2Z);
		
		structures.put(name, positions);
		
	}
	
	public void putSpawnZone(UUID uuid, Location first, Location second, String world) {
		Location[] zone = new Location[2];
		
		zone[0] = first;
		zone[1] = second;
		
		spawnZones.put(uuid, zone);
		
	}
	
	public HashMap<String, HashMap<String, Integer>> getStructures() {
		return structures;
	}
	
	public HashMap<UUID, Location[]> getSpawnZones() {
		return spawnZones;
	}
}
