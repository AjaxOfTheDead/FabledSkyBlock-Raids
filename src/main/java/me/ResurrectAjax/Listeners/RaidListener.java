package me.ResurrectAjax.Listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.visit.Visit;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.Database;
import me.ResurrectAjax.Mysql.FastDataAccess;
import me.ResurrectAjax.Playerdata.PlayerManager;
import me.ResurrectAjax.Raid.RaidBar;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;
import me.ResurrectAjax.Raid.RaidParty;
import me.ResurrectAjax.Raid.ItemStorage.ItemStorage;

public class RaidListener implements Listener{
	private Main main;
	private RaidManager raidManager;
	private Location spawnLocation;
	private IslandManager islandManager;
	private FastDataAccess fdb;
	private RaidMethods methods;
	private final SkyBlock skyblock;
	private Database db;
	
	public RaidListener(Main main) {
		this.main = main;
		raidManager = main.getRaidManager();
		islandManager = main.getSkyBlock().getIslandManager();
		skyblock = main.getSkyBlock();
		
		spawnLocation = main.getSpawnLocation();
		
		
		methods = main.getRaidMethods();
		
		fdb = main.getFastDataAccess();
		
		db = main.getRDatabase();
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
		if(!main.getStorage().getItemStorage().containsKey(event.getPlayer().getUniqueId())) return;
		Player player = event.getPlayer();
		
		event.setCancelled(true);
		
		if(event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(player.getInventory().getItemInMainHand().getType() == ItemStorage.getExitItem().getType()) methods.cancelRaid(player);
		if(player.getInventory().getItemInMainHand().getType() == ItemStorage.getRaidItem().getType()) {
			RaidBar bar = raidManager.addRaidBar(player.getUniqueId(), new RaidBar(main, player, "prepare"));
			
			for(UUID uuid : raidManager.getMembersParty(player.getUniqueId()).getMembers()) {
				if(Bukkit.getPlayer(uuid) == null || uuid.equals(player.getUniqueId())) continue;
				bar.addPlayer(player);
				
				methods.startSpectating(Bukkit.getPlayer(uuid), methods.getIslandSpectator().get(uuid), raidManager.getMembersParty(uuid));
				
			}
			methods.setCurrentRaid(player.getUniqueId(), db.insertRaidParty(raidManager.getMembersParty(player.getUniqueId())));
			
			RaidParty party = raidManager.getMembersParty(player.getUniqueId());
			for(UUID member : party.getMembers()) {
				if(Bukkit.getPlayer(member) == null) continue;
				if(methods.getAmplifiers(party).isEmpty()) continue;
				Bukkit.getPlayer(member).addPotionEffects(methods.getAmplifiers(party));	
			}
			
		}
		if(player.getInventory().getItemInMainHand().getType() == ItemStorage.getNextItem().getType()) methods.nextIsland(player);
		
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
			
	        if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
	            FileConfiguration configLoad = skyblock.getConfiguration();

	            boolean keepInventory = false;

	            if (configLoad.getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
	                if (skyblock.getPermissionManager().hasPermission(player.getLocation(),"KeepItemsOnDeath",
	                        IslandRole.Owner)) {
	                    keepInventory = true;
	                }
	            } else keepInventory = configLoad.getBoolean("Island.KeepItemsOnDeath.Enable");

	            if (keepInventory) {
	                event.setKeepInventory(true);
	                event.getDrops().clear();
	                event.setKeepLevel(true);
	                event.setDroppedExp(0);
	            }

	            if(methods.getIslandRaider().containsKey(player.getUniqueId())) {
	            	player.teleport(spawnLocation);
	            }
	            if (configLoad.getBoolean("Island.Death.AutoRespawn") && !methods.getIslandRaider().containsKey(player.getUniqueId())) {
	            	Bukkit.getScheduler().scheduleSyncDelayedTask(main.getSkyBlock(), () -> {
	                    player.spigot().respawn();
	                    player.setFallDistance(0.0F);
	                    player.setFireTicks(0);
	                }, 1L);
	            }
	        }
			
			for(UUID uuid : main.getIslandPositions().keySet()) {
				if(PlayerManager.getPlayersIsland(player.getUniqueId()).getOwnerUUID() == uuid || islandManager.getIslandByOwner(Bukkit.getOfflinePlayer(uuid)).getVisit().getVisitors().contains(player.getUniqueId())) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(main.getSkyBlock(), () -> {
		                player.spigot().respawn();
		                player.setFallDistance(0.0F);
		                player.setFireTicks(0);
		                Bukkit.getScheduler().scheduleSyncDelayedTask(main.getSkyBlock(), () -> {
		                	player.teleport(spawnLocation);
		                }, 1L);
		            }, 1L);
					if(PlayerManager.getPlayersIsland(player.getUniqueId()).getVisit().getVisitors().contains(player.getUniqueId())) {
						Visit visit = islandManager.getIslandByOwner(Bukkit.getOfflinePlayer(uuid)).getVisit();
						visit.removeVisitor(player.getUniqueId());
					}
				}
			}
		
			if(methods.getIslandRaider().containsKey(player.getUniqueId())) {
				if(raidManager.getRaidParties().get(player.getUniqueId()) != null) {
					methods.checkLastRaider(player);
				}
				else {
					for(UUID uuid : methods.getIslandRaider().keySet()) {
						if(raidManager.getRaidParties().get(uuid) != null && methods.getIslandRaider().get(uuid) == methods.getIslandRaider().get(player.getUniqueId())) {
							methods.checkLastRaider(Bukkit.getPlayer(uuid));
						}
					}
				}

				raidManager.getCalledRaidCommands().remove(player.getUniqueId());
				methods.removeRaider(player.getUniqueId());
			}
		}
	
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		for(UUID uuid : main.getIslandPositions().keySet()) {
			if(methods.getRaidedIslands().containsValue(player.getUniqueId())) {
				if(islandManager.getIsland(player).getOwnerUUID() == uuid || PlayerManager.getPlayersIsland(uuid).getVisit().getVisitors().contains(player.getUniqueId())) {
					player.teleport(spawnLocation);
					if(islandManager.getIslandByOwner(Bukkit.getOfflinePlayer(uuid)).getVisit().getVisitors().contains(player.getUniqueId())) {
						Visit visit = islandManager.getIslandByOwner(Bukkit.getOfflinePlayer(uuid)).getVisit();
						visit.removeVisitor(player.getUniqueId());
					}
				}
			}
		}
		
		methods.onRaiderQuit(player);
		methods.onSpectatorQuit(player);
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player tpPlayer = event.getPlayer();
		
		FileConfiguration language = main.getLanguage();
		IslandManager islandManager = main.getSkyBlock().getIslandManager();
		
		
		for(Location location : methods.getRaidedIslands().keySet()) {
			for(Island island : islandManager.getIslands().values()) {
				if(islandManager.isLocationAtIsland(island, location)) {
			    	if(islandManager.isLocationAtIsland(island, event.getFrom())) {
			    		if(raidManager.getRaidedIslandOwners().contains(tpPlayer.getUniqueId()) || methods.getIslandRaider().containsKey(tpPlayer.getUniqueId())) {
			    			
			    			Block block = tpPlayer.getLocation().getBlock().getRelative(BlockFace.DOWN);
			    			if(event.getFrom().getBlockY() < 30 && block.getType() == Material.AIR) {
					    		event.setTo(spawnLocation);
					    		if(methods.getIslandRaider().containsKey(tpPlayer.getUniqueId())) {
						    		methods.onRaiderQuit(tpPlayer);
					    		}	
			    			}
			    		}
			    	}
			    	else {
				    	if(islandManager.isLocationAtIsland(island, event.getTo())) {
				    		event.setCancelled(true);
							tpPlayer.sendMessage(RaidMethods.format(language.getString("Raid.Raid.Teleport.DeniedSelf.Message")));
				    	}	
			    	}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		if(PlayerManager.getPlayersIsland(event.getPlayer().getUniqueId()) != null) {
			Island island = PlayerManager.getPlayersIsland(event.getPlayer().getUniqueId());
		 	Location playerIslandLocation = island.getLocation(IslandWorld.Normal, IslandEnvironment.Main);	
		 	
		 	for(Location location : methods.getRaidedIslands().keySet()) {
			 	OfflinePlayer owner = Bukkit.getOfflinePlayer(fdb.getOwnerByLocation(location));
				if(owner.isOnline()) {
					raidManager.getBossBar().get(methods.getRaidersByLocation(location).get(0)).addPlayer((Player)owner);	
				}
		 	}
		 	
			if(methods.getIslandRaider().containsValue(playerIslandLocation)) {
				
				RaidBar bar = raidManager.getBossBar().get(methods.getRaidersByLocation(playerIslandLocation).get(0));
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
	
	
	
}
