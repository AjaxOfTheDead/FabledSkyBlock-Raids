package me.ResurrectAjax.Playerdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidMethods;

public class PlayerManager {
	public static ItemStack getPlayerHead(UUID uuid, String displayName, List<String> lore) {
		boolean isNewVersion = Arrays.stream(Material.values())
				.map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
		
		Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
		ItemStack item = new ItemStack(type, 1);
		
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		
		meta.setDisplayName(RaidMethods.format(displayName));
		
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getPlayerHead(String name) {
		boolean isNewVersion = Arrays.stream(Material.values())
				.map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
		
		Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
		ItemStack item = new ItemStack(type, 1);
		
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(name));
		
		List<String> lore = new ArrayList<String>();
		switch(name) {
			case "MHF_ArrowLeft":
				meta.setDisplayName(RaidMethods.format("&6&lBack"));
				lore.add(RaidMethods.format("&7Go to the previous page"));
				break;
			case "MHF_ArrowRight":
				meta.setDisplayName(RaidMethods.format("&6&lNext"));
				lore.add(RaidMethods.format("&7Go to the next page"));
				break;
		}
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	
	public static Island getPlayersIsland(UUID uuid) {
		Island island = null;
		for(Island is : Main.getInstance().getSkyBlock().getIslandManager().getIslands().values()) {
			if(getIslandMembers(is).contains(uuid)) {
				island = is;
			}
		}
		return island;
	}
	
	
	public static List<UUID> getOnlineIslandMembers(Island island) {
		List<UUID> members = new ArrayList<UUID>(); 
		for(UUID member : getIslandMembers(island)) {
			if(Bukkit.getPlayer(member) != null) {
				members.add(member);
			}
		}
		return members;
	}
	
	public static List<UUID> getIslandMembers(Island island) {
		List<UUID> members = new ArrayList<UUID>(); 
		for(IslandRole ir : IslandRole.getRoles()) {
			for(UUID member : island.getRole(ir)) {
				members.add(member);
			}
		}
		return members;
	}
	
    
    public static UUID getIslandUUIDByMember(UUID member) {
    	IslandManager islandManager = Main.getInstance().getSkyBlock().getIslandManager();
    	for(UUID uuid : islandManager.getIslands().keySet()) {
    		Island island = islandManager.getIsland(Bukkit.getOfflinePlayer(uuid));
    		if(getIslandMembers(island).contains(member)) {
    			return island.getIslandUUID();
    		}
    	}
    	return null;
    	
    }
}
