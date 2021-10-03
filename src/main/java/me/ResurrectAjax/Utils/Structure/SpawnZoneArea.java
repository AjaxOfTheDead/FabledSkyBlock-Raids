package me.ResurrectAjax.Utils.Structure;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

public class SpawnZoneArea {
	private final Map<Integer, Location> positions;

    public SpawnZoneArea() {
        positions = new HashMap<>();
    }

    public Location getPosition(int position) {
        return positions.get(position);
    }

    public void setPosition(int position, Location location) {
        positions.put(position, location);
    }
}
