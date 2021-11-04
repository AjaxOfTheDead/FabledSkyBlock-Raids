package me.ResurrectAjax.RaidGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.ResurrectAjax.Main.Main;

public class Gui {
	private Inventory inventory;
	
	public Gui(Main main, Player player, int size) {
		inventory = Bukkit.createInventory(player, size);
		FileConfiguration guiConfig = main.getConfiguration();
		ItemStack backgroundItem = new ItemStack(Material.valueOf(guiConfig.getString("Raid.BackgroundItem")));
		
		for(int i = 0; i < inventory.getSize(); i++) {
			setSlot(backgroundItem, i);
		}
	}
	
	public void setSlot(ItemStack item, int slot) {
		inventory.setItem(slot, item);
	}
	
	public void setInventory(Inventory invent) {
		inventory = invent;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
}
