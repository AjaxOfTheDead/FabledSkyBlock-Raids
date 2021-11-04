package me.ResurrectAjax.Raid.ItemStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ResurrectAjax.Main.Main;

public class ItemStorage {
	
	private HashMap<UUID, ItemStack[]> inventory = new HashMap<UUID, ItemStack[]>();
	private HashMap<UUID, ItemStack[]> armor = new HashMap<UUID, ItemStack[]>();
	private Main main = null;
	private static ItemStack exit, raid, next;
	
	public ItemStorage(Main main) {
		this.main = main;
	}
	
	public void saveToStorage(Player player) {
		inventory.put(player.getUniqueId(), player.getInventory().getStorageContents());
		armor.put(player.getUniqueId(), player.getInventory().getArmorContents());
		player.getInventory().clear();
		
		if(main.getRaidManager().getLeaders().contains(player.getUniqueId())) {
			addSpectateTools(player);
		}
	}
	
	public void addSpectateTools(Player player) {
		exit = new ItemStack(Material.valueOf(main.getConfiguration().getString("Raid.RaidFinder.Menu.Exit.Item")));
		raid = new ItemStack(Material.valueOf(main.getConfiguration().getString("Raid.RaidFinder.Menu.Raid.Item")));
		next = new ItemStack(Material.valueOf(main.getConfiguration().getString("Raid.RaidFinder.Menu.Next.Item")));
		
		FileConfiguration configLoad = main.getLanguage();
		
		ItemMeta exitm = exit.getItemMeta(), raidm = raid.getItemMeta(), nextm = next.getItemMeta();
		
		exitm.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Raid.RaidFinder.Menu.Exit.Name")));
		raidm.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Raid.RaidFinder.Menu.Raid.Name")));
		nextm.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Raid.RaidFinder.Menu.Next.Name")));
		
		List<String> itemLore = new ArrayList<>();
		for (String itemLoreList : configLoad.getStringList("Raid.RaidFinder.Menu.Exit.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
        }
		exitm.setLore(itemLore);
		itemLore = new ArrayList<>();
		for (String itemLoreList : configLoad.getStringList("Raid.RaidFinder.Menu.Raid.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
        }
		raidm.setLore(itemLore);
		itemLore = new ArrayList<>();
		for (String itemLoreList : configLoad.getStringList("Raid.RaidFinder.Menu.Next.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
        }
		nextm.setLore(itemLore);
		
		exit.setItemMeta(exitm);
		raid.setItemMeta(raidm);
		next.setItemMeta(nextm);
		
		player.getInventory().setItem(main.getConfiguration().getInt("Raid.RaidFinder.Menu.Exit.ItemSlot"), exit);
		player.getInventory().setItem(main.getConfiguration().getInt("Raid.RaidFinder.Menu.Raid.ItemSlot"), raid);
		player.getInventory().setItem(main.getConfiguration().getInt("Raid.RaidFinder.Menu.Next.ItemSlot"), next);
	}
	
	public static ItemStack getExitItem() {
		exit = new ItemStack(Material.valueOf(Main.getInstance().getConfiguration().getString("Raid.RaidFinder.Menu.Exit.Item")));
		return exit;
	}
	public static ItemStack getRaidItem() {
		raid = new ItemStack(Material.valueOf(Main.getInstance().getConfiguration().getString("Raid.RaidFinder.Menu.Raid.Item")));
		return raid;
	}
	public static ItemStack getNextItem() {
		next = new ItemStack(Material.valueOf(Main.getInstance().getConfiguration().getString("Raid.RaidFinder.Menu.Next.Item")));
		return next;
	}
	
	public void restoreItems(Player player) {
		player.getInventory().clear();
		player.getInventory().setContents(inventory.get(player.getUniqueId()));
		player.getInventory().setArmorContents(armor.get(player.getUniqueId()));
		inventory.remove(player.getUniqueId());
		armor.remove(player.getUniqueId());
	}
	
	public HashMap<UUID, ItemStack[]> getItemStorage() {
		return inventory;
	}
	public HashMap<UUID, ItemStack[]> getArmorStorage() {
		return armor;
	}
}
