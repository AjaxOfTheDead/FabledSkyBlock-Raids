package me.ResurrectAjax.RaidGUI;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidMethods;

public class GuiManager {
	private Main main;
	private RaidMethods raidMethods;
	public GuiManager(Main main) {
		this.main = main;
		this.raidMethods = main.getRaidMethods();
	}
	
	public void partyLeaderGui1(Player player) {
		FileConfiguration guiConfig = main.getConfiguration();
		
		Gui gui = new Gui(main, player, 20);
		Inventory inventory = gui.getInventory();
		
		
		for(String key : guiConfig.getConfigurationSection("Raid.Items").getKeys(false)) {
			ItemStack item = new ItemStack(Material.valueOf(guiConfig.getString("Raid.Items." + key + ".Material")));
			List<String> lore = new ArrayList<String>();
			String name = raidMethods.format(guiConfig.getString("Raid.Items." + key + ".Name"));
			ItemMeta meta = item.getItemMeta();
			int slot = guiConfig.getInt("Raid.Items." + key + ".ItemSlot");
			
			for(String lores : guiConfig.getStringList("Raid.Items." + key + ".Lore")) {
				lore.add(raidMethods.format(lores));
			}
			
			meta.setDisplayName(name);
			
			item.setLore(lore);
			item.setItemMeta(meta);
			
			inventory.setItem(slot, item);
		}
	}
}
