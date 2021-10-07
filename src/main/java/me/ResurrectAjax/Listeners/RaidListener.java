package me.ResurrectAjax.Listeners;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.visit.Visit;
import com.songoda.skyblock.visit.VisitManager;

import me.ResurrectAjax.Commands.Raid.RaidBar;
import me.ResurrectAjax.Commands.Raid.RaidMethods;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.FastDataAccess;
import me.ResurrectAjax.Raid.RaidParty;
import me.ResurrectAjax.Raid.ItemStorage.ItemStorage;
import net.md_5.bungee.api.ChatColor;

public class RaidListener implements Listener{
	private Main main;
	private Location spawnLocation;
	private FastDataAccess fdb;
	private RaidMethods methods;
	private FileConfiguration language;
	
	public RaidListener(Main main) {
		this.main = main;
		
		Config config = main.getSkyBlock().getFileManager().getConfig(new File(main.getSkyBlock().getDataFolder(), "locations.yml"));
		spawnLocation = main.getSkyBlock().getFileManager().getLocation(config, "Location.Spawn", true);
		
		methods = main.getRaidMethods();
		language = main.getLanguage();
		
		fdb = main.getFastDataAccess();
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(main.getStorage().getItemStorage().containsKey(event.getWhoClicked().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemSwap(PlayerSwapHandItemsEvent event) {
		if(main.getStorage().getItemStorage().containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player)event.getEntity();
			if(main.getStorage().getItemStorage().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		if(main.getStorage().getItemStorage().containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityHit(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			Player player = (Player)event.getDamager();
			if(main.getStorage().getItemStorage().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(main.getStorage().getItemStorage().containsKey(event.getPlayer().getUniqueId())) {
			Player player = event.getPlayer();
			
			if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(player.getInventory().getItemInMainHand().getType() == ItemStorage.getExitItem().getType()) {
					Location playerIsland = main.getSkyBlock().getIslandManager().getIsland(player).getLocation(IslandWorld.Normal, IslandEnvironment.Main);
					player.teleport(playerIsland);
					main.getRaidMethods().exitRaidSpectator(player);
					main.getRaidMethods().removeRaider(player.getUniqueId());
				}
				if(player.getInventory().getItemInMainHand().getType() == ItemStorage.getRaidItem().getType()) {
					Location tempIsland = main.getRaidMethods().getIslandSpectator().get(player.getUniqueId());
					Location raidIsland = new Location(tempIsland.getWorld(), tempIsland.getX(), 72, tempIsland.getZ());
					
					RaidBar bar = new RaidBar(main, player, "raid");
					
					OfflinePlayer owner = Bukkit.getOfflinePlayer(fdb.getOwnerByLocation(tempIsland));
					main.getSkyBlock().getIslandManager().loadIsland(owner);
					if(owner.isOnline()) {
						bar.addPlayer((Player)owner);	
					}
					
					player.teleport(raidIsland);
					main.getRaidMethods().exitRaidSpectator(player);
					main.getRaidMethods().addRaider(player.getUniqueId(), raidIsland);
					
					
					for(UUID visitor : main.getSkyBlock().getIslandManager().getIslandByOwner(owner).getVisit().getVisitors()) {
						if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(visitor))) {
							bar.addPlayer(Bukkit.getPlayer(visitor));
						}
					}
					
					for(IslandRole ir : IslandRole.getRoles()) {
						for(UUID member : main.getSkyBlock().getIslandManager().getIslandByOwner(owner).getRole(ir)) {
							if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(member))) {
								bar.addPlayer(Bukkit.getPlayer(member));
							}
						}
					}
					
				}
				if(player.getInventory().getItemInMainHand().getType() == ItemStorage.getNextItem().getType()) {
					
				}
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent event) {
		if(event.getTarget() instanceof Player) {
			Player player = (Player)event.getTarget();
			if(main.getStorage().getItemStorage().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player)event.getEntity();
			if(main.getStorage().getItemStorage().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = event.getEntity();
			
			for(UUID uuid : main.getIslandPositions().keySet()) {
				if(main.getSkyBlock().getIslandManager().getIsland(player).getOwnerUUID() == uuid || main.getSkyBlock().getIslandManager().getIslandByOwner(Bukkit.getPlayer(uuid)).getVisit().getVisitors().contains(player.getUniqueId())) {
					player.teleport(spawnLocation);
					if(main.getSkyBlock().getIslandManager().getIslandByOwner(Bukkit.getPlayer(uuid)).getVisit().getVisitors().contains(player.getUniqueId())) {
						Visit visit = main.getSkyBlock().getIslandManager().getIslandByOwner(Bukkit.getPlayer(uuid)).getVisit();
						visit.removeVisitor(player.getUniqueId());
					}
				}
			}
		
			if(methods.getIslandRaider().containsKey(player.getUniqueId())) {
				if(main.getRaidParties().get(player.getUniqueId()) != null) {
					checkLastRaider(player);
				}
				else {
					for(UUID uuid : methods.getIslandRaider().keySet()) {
						if(main.getRaidParties().get(uuid) != null && methods.getIslandRaider().get(uuid) == methods.getIslandRaider().get(player.getUniqueId())) {
							checkLastRaider(Bukkit.getPlayer(uuid));
						}
					}
				}
				methods.removeRaider(player.getUniqueId());
				player.teleport(spawnLocation);
			}
		}
	
	}
	
	public void checkLastRaider(Player player) {
		if(methods.isLastRaider(player)) {
			if(methods.getIslands().contains(methods.getIslandRaider().get(player.getUniqueId()))) {
				methods.getIslands().add(methods.getIslandRaider().get(player.getUniqueId()));	
			}
			RaidParty party = main.getRaidParties().get(player.getUniqueId());
			for(Player member : party.getMembers()) {
				if(member.isOnline()) {
					member.sendMessage(format(language.getString("Raid.Raid.Outcome.Lost.Message")));
				}
			}
		
		}
		else {
			main.getRaidParties().get(player.getUniqueId()).addDeadMember(player);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		RaidMethods methods = main.getRaidMethods();
		Player player = event.getPlayer();
		
		FileConfiguration language = main.getLanguage();
		
		if(main.getStorage().getItemStorage().containsKey(player.getUniqueId())) {
			methods.exitRaidSpectator(player);
		}
		
		if(methods.getIslandRaider().containsKey(player.getUniqueId())) {
			if(methods.isLastRaider(player)) {
				if(methods.getIslands().contains(methods.getIslandRaider().get(player.getUniqueId()))) {
					methods.getIslands().add(methods.getIslandRaider().get(player.getUniqueId()));	
				}
				if(main.getRaidParties().get(player.getUniqueId()) != null) {
					RaidParty party = main.getRaidParties().get(player.getUniqueId());
					for(Player member : party.getMembers()) {
						if(member.isOnline()) {
							member.sendMessage(format(language.getString("Raid.Raid.Outcome.Lost.Message")));
						}
					}
				}
				else {
					player.sendMessage(format(language.getString("Raid.Raid.Outcome.Lost.Message")));
				}
				
			}
			methods.removeRaider(player.getUniqueId());
			player.teleport(spawnLocation);
		}
	}
	
	public String format(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		
		for(UUID uuid : main.getRaidParties().keySet()) {
			for(Player player : main.getRaidParties().get(uuid).getMembers()) {
				if(event.getTo() == player.getLocation()) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		IslandManager islandManager = main.getSkyBlock().getIslandManager();
		
		if(islandManager.getIsland(event.getPlayer()) != null) {
			Island island = islandManager.getIsland(event.getPlayer());
		 	Location playerIslandLocation = island.getLocation(IslandWorld.Normal, IslandEnvironment.Main);	
		 	
			if(methods.getIslandRaider().containsValue(playerIslandLocation)) {
				
				RaidBar bar = main.getBossBar().get(methods.getRaidersByLocation(playerIslandLocation).get(0));
				for(UUID visitor : island.getVisit().getVisitors()) {
					if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(visitor))) {
						bar.addPlayer(Bukkit.getPlayer(visitor));
					}
				}
				
				for(IslandRole ir : IslandRole.getRoles()) {
					for(UUID member : island.getRole(ir)) {
						if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(member))) {
							bar.addPlayer(Bukkit.getPlayer(member));
						}
					}
				}
			}
		}
	}
	
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
}
