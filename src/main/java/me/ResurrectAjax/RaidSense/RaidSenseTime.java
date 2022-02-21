package me.ResurrectAjax.RaidSense;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.songoda.skyblock.island.Island;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.FastDataAccess;
import me.ResurrectAjax.Playerdata.PlayerManager;

public class RaidSenseTime {
	private HashMap<UUID, Long> playerLogTime = new HashMap<UUID, Long>();
	private FastDataAccess fdb;
	private Main main;
	public RaidSenseTime(Main main) {
		this.main = main;
		fdb = main.getFastDataAccess();
	}
	
	public void putPlayerLogTime(UUID uuid) {
		if(com.songoda.skyblock.api.island.IslandManager.hasIsland(Bukkit.getOfflinePlayer(uuid))) {
			
			playerLogTime.put(uuid, System.currentTimeMillis()/1000);
			
		}
	}
	
	public void addToIslandTime(UUID uuid) {
		if(com.songoda.skyblock.api.island.IslandManager.hasIsland(Bukkit.getOfflinePlayer(uuid))) {
			if(playerLogTime != null && playerLogTime.containsKey(uuid)) {
				
				Island island = PlayerManager.getPlayersIsland(uuid);
				int islandTime = fdb.getIslandTime(island.getOwnerUUID()) != null ? fdb.getIslandTime(island.getOwnerUUID()) : 0;
				islandTime += ((System.currentTimeMillis()/1000) - playerLogTime.get(uuid));
				fdb.putIslandTime(island.getOwnerUUID(), islandTime);
			}
		}
		
	}
	
	public void checkTimeTask() {
    	FileConfiguration configLoad = main.getConfiguration();
    	if(configLoad.getBoolean("Raid.RaidSense.ScheduledTasks.Enabled")) {
        	int totalseconds = convertHoursMinutesSecondsToSeconds(configLoad.getString("Raid.RaidSense.ScheduledTasks.AddRaidSense.Interval"));
        	double sense = configLoad.getDouble("Raid.RaidSense.ScheduledTasks.AddRaidSense.RaidSense");
    		
    		Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
    		    @Override
    		    public void run() {
    		    	
    		        for(Player player : Bukkit.getOnlinePlayers()) {
    		        	if(com.songoda.skyblock.api.island.IslandManager.hasIsland(player)) {
    		        		
    		        		Island is = PlayerManager.getPlayersIsland(player.getUniqueId());
    		        		
    		        		addToIslandTime(player.getUniqueId());
    		        		putPlayerLogTime(player.getUniqueId());
    		        		
    				        
    				    	double newsense;
    				    	if(fdb.getIslandTime(is.getOwnerUUID())%totalseconds == 0) {
    			        		newsense = fdb.getRaidSense(is.getOwnerUUID()) + sense;
    			        		fdb.putRaidSense(is.getOwnerUUID(), newsense);
    				    	}
    		        	}
    		        }
    		        
    		    }
    		}, 0L, 20L); //0 Tick initial delay, 20 Tick (1 Second) between repeats	
    	}
	}
	
	public static int convertHoursMinutesSecondsToSeconds(String input) {
		HashMap<String, Integer> hourminsec = new HashMap<String, Integer>();
		String[] numbers = input.split("\\D");
		String[] letters = input.replaceAll("\\d", "").split("");
		for(int i = 0; i < letters.length; i++) {
			hourminsec.put(letters[i], Integer.parseInt(numbers[i]));
		}
		
		Integer hours = hourminsec.get("h"), 
				minutes = hourminsec.get("m"), 
				seconds = hourminsec.get("s");
		
		if(hours == null && minutes == null && seconds == null) {
			throw new IllegalArgumentException("Please use the right time formats(h|m|s): config.yml");
		}
		else {
			if(hours == null) {
				hours = 0;
			}
			if(minutes == null) {
				minutes = 0;
			}
			if(seconds == null) {
				seconds = 0;
			}	
		}

		int totalseconds = (hours*3600) + (minutes*60) + seconds;
		return totalseconds;
	}
	
	public void savePlayerTime(Player player) {
		if(com.songoda.skyblock.api.island.IslandManager.hasIsland(Bukkit.getOfflinePlayer(player.getUniqueId()))) {
			
			Island island = PlayerManager.getPlayersIsland(player.getUniqueId());
			
			if(PlayerManager.getOnlineIslandMembers(island).size() == 1) {
				RaidSenseTime islandTime = main.getIslandTime();
				islandTime.addToIslandTime(player.getUniqueId());
				fdb.saveIslandTimeToDatabase(player.getUniqueId());	
				fdb.saveRaidSenseToDatabase(island.getOwnerUUID(), fdb.getRaidSense(island.getOwnerUUID()));
			}	
		}
	}
}
