package me.ResurrectAjax.Listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.sound.SoundManager;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.FastDataAccess;
import net.md_5.bungee.api.ChatColor;

public class BlockListeners implements Listener{
	private Main main;

    public BlockListeners(Main main) {
		this.main = main;
    }
	
	//ResurrectAjax
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	if(inSpawnZone(event)) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
    	if(inSpawnZone(event)) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void startBlockBurnEvent(BlockBurnEvent event) {
    	if(inSpawnZone(event)) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void startBlockIgnite(BlockIgniteEvent event) {
    	if(inSpawnZone(event)) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
    	if(inSpawnZone(event)) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
    	if(inSpawnZone(event)) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
    	if(inSpawnZone(event)) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onWaterFlow(BlockFromToEvent event) {
    	if(inSpawnZone(event)) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
    	Iterator<Block> it = event.blockList().iterator();
    	
    	while(it.hasNext()) {
    		if(inSpawnZone(it.next())) {
        		it.remove();
        	}
    	}
    }
    
    //check if event is called in spawnzone
    public boolean inSpawnZone(Event event) {
    	FastDataAccess fdb = main.getFastDataAccess();
    	PlayerBucketEvent bucket = null;
    	BlockFromToEvent blockTo = null;
    	BlockEvent block = null;
    	Location spawnPos1, spawnPos2;
    	SoundManager soundManager = main.getSkyBlock().getSoundManager();
    	Player player = null;
    	int x = 0, y = 0, z = 0;

    	if(event instanceof BlockFromToEvent) {
    		blockTo = (BlockFromToEvent)event;
    		
    		x = blockTo.getToBlock().getX();
    		y = blockTo.getToBlock().getY();
    		z = blockTo.getToBlock().getZ();
    	}

    	if(event instanceof BlockEvent && !(event instanceof BlockFromToEvent)) {
    		block = (BlockEvent)event;
    		
    		x = block.getBlock().getX();
    		y = block.getBlock().getY();
    		z = block.getBlock().getZ();
    		
    		if(event instanceof BlockPlaceEvent) {
    			player = ((BlockPlaceEvent) event).getPlayer();
    		}
    		if(event instanceof BlockBreakEvent) {
    			player = ((BlockBreakEvent) event).getPlayer();
    		}
    	}
    	
    	if(event instanceof PlayerBucketEvent) {
    		bucket = (PlayerBucketEvent)event;
    		
    		x = bucket.getBlock().getX();
    		y = bucket.getBlock().getY();
    		z = bucket.getBlock().getZ();
    		
    		player = bucket.getPlayer();
    	}
		
    	List<Event> events = new ArrayList<Event>();
    	events.add(block);
    	events.add(blockTo);
    	events.add(bucket);
    	
    	for(Event evente : events) {
    		if(evente != null) {
    			for(UUID uuid : fdb.getSpawnZones().keySet()) {
            		spawnPos1 = fdb.getSpawnZones().get(uuid)[0];
            		spawnPos2 = fdb.getSpawnZones().get(uuid)[1];
            		
            		boolean ok = false;
            		
            		int posXmax = 0, posZmax = 0, posXmin = 0, posZmin = 0;
            		
            		if(evente instanceof BlockEvent && !(evente instanceof BlockFromToEvent)) {
            			if(spawnPos1.getWorld().getName().equals(((BlockEvent)evente).getBlock().getWorld().getName())) {
            				ok = true;
            			}
            		}
            		else if(evente instanceof PlayerBucketEvent) {
            			if(spawnPos1.getWorld().getName().equals(((PlayerBucketEvent)evente).getBlock().getWorld().getName())) {
            				ok = true;
            			}
            		}
            		else if(evente instanceof BlockFromToEvent) {
            			if(spawnPos1.getWorld().getName().equals(((BlockFromToEvent)evente).getToBlock().getWorld().getName())) {
            				ok = true;
            			}
            		}
            		
            		if(ok) {
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
                		
                		org.bukkit.util.Vector vector = new org.bukkit.util.Vector(x, y, z),
                				vector1 = new org.bukkit.util.Vector(posXmin, 0, posZmin),
            					vector2 = new org.bukkit.util.Vector(posXmax, 255, posZmax);
                		
                		if(vector.isInAABB(vector1, vector2)) {
                			if(player != null) {
                				
                				player.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getFileManager().getConfig(new File(main.getDataFolder(), "language.yml"))
                						.getFileConfiguration().getString("Command.Island.SpawnZone.Permission.Message")));
                				soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                			}
                			return true;
                		}
            		}
            		
        		}
    		}
    	}
		
    
    	return false;
    }
    
    //check for tnt explosion in spawnzone
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
    //ResurrectAjax
}
