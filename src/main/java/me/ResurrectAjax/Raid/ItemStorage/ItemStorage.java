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
	
	public ItemStorage(Main main) {
		this.main = main;
	}
	
	public void saveToStorage(Player player) {
		player.getInventory().clear();
		ItemStack exit = new ItemStack(Material.valueOf(main.getConfiguration().getString("Raid.Spectate.Menu.Exit.Item"))),
					raid = new ItemStack(Material.valueOf(main.getConfiguration().getString("Raid.Spectate.Menu.Raid.Item"))),
					next = new ItemStack(Material.valueOf(main.getConfiguration().getString("Raid.Spectate.Menu.Next.Item")));
		
		FileConfiguration configLoad = main.getLanguage();
		
		ItemMeta exitm = exit.getItemMeta(), raidm = raid.getItemMeta(), nextm = next.getItemMeta();
		
		exitm.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Raid.Spectate.Menu.Exit.Name")));
		raidm.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Raid.Spectate.Menu.Raid.Name")));
		nextm.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Raid.Spectate.Menu.Next.Name")));
		
		List<String> itemLore = new ArrayList<>();
		for (String itemLoreList : configLoad.getStringList("Raid.Spectate.Menu.Exit.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
        }
		exitm.setLore(itemLore);
		itemLore = new ArrayList<>();
		for (String itemLoreList : configLoad.getStringList("Raid.Spectate.Menu.Raid.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
        }
		raidm.setLore(itemLore);
		itemLore = new ArrayList<>();
		for (String itemLoreList : configLoad.getStringList("Raid.Spectate.Menu.Next.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
        }
		nextm.setLore(itemLore);
		
		exit.setItemMeta(exitm);
		raid.setItemMeta(raidm);
		next.setItemMeta(nextm);
		
		player.getInventory().setItem(main.getConfiguration().getInt("Raid.Spectate.Menu.Exit.ItemSlot"), exit);
		player.getInventory().setItem(main.getConfiguration().getInt("Raid.Spectate.Menu.Raid.ItemSlot"), raid);
		player.getInventory().setItem(main.getConfiguration().getInt("Raid.Spectate.Menu.Next.ItemSlot"), next);
		inventory.put(player.getUniqueId(), player.getInventory().getStorageContents());
		armor.put(player.getUniqueId(), player.getInventory().getArmorContents());
		
		
	}
	
	public void removeFromStorage(Player player) {
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
