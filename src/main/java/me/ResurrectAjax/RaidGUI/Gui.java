package me.ResurrectAjax.RaidGUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.songoda.skyblock.island.Island;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Playerdata.PlayerManager;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;

public class Gui {
	private Inventory inventory;
	private FileConfiguration guiConfig;
	private Player player;
	private Main main;
	
	public Gui(Main main, Player player, int size, String name) {
		this.main = main;
		this.player = player;
		guiConfig = main.getGuiConfig();
		inventory = Bukkit.createInventory(new RaidInventoryHolder(), size*9, name);
		
		createTemplate(name);
		
	}
	
	public Gui(Main main, Player player, int size, String name, String replaceStr) {
		this.main = main;
		this.player = player;
		guiConfig = main.getGuiConfig();
		inventory = Bukkit.createInventory(new RaidInventoryHolder(), size*9, RaidMethods.format(name, replaceStr));
		
		createTemplate(name, replaceStr);
		
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
	
	public void openInventory() {
		player.openInventory(inventory);
		main.getGuiManager().getCustomGuiBoolean().put(player.getUniqueId(), true);
	}
	
	public void createTemplate(String guiName) {
		templateLayout(Gui.getGuiSection(guiName)[0] + "." + Gui.getGuiSection(guiName)[1], null);
	}
	
	public void createTemplate(String guiName, String replaceStr) {
		templateLayout(Gui.getGuiSection(guiName)[0] + "." + Gui.getGuiSection(guiName)[1], replaceStr);
	}
	
	private void templateLayout(String guiName, String replaceStr) {
		
		Inventory inventory = getInventory();
		ItemStack backgroundItem = new ItemStack(Material.valueOf(guiConfig.getString("Raid." + guiName + ".BackgroundItem")));
		
		for(int i = 0; i < inventory.getSize(); i++) {
			setSlot(backgroundItem, i);
		}
		
		for(String key : guiConfig.getConfigurationSection("Raid." + guiName + ".Items").getKeys(false)) {
			ItemStack item = new ItemStack(Material.valueOf(guiConfig.getString("Raid." + guiName + ".Items." + key + ".Material")));
			List<String> lore = new ArrayList<String>();
			String name = format(guiConfig.getString("Raid." + guiName + ".Items." + key + ".Name"));
			ItemMeta meta = item.getItemMeta();
			int slot = guiConfig.getInt("Raid." + guiName + ".Items." + key + ".ItemSlot");
			
			for(String lores : guiConfig.getStringList("Raid." + guiName + ".Items." + key + ".Lore")) {
				lore.add(RaidMethods.format(format(lores), replaceStr));
			}
			
			meta.setDisplayName(RaidMethods.format(format(name), replaceStr));
			
			meta.setLore(lore);
			item.setItemMeta(meta);
			
			inventory.setItem(slot, item);
		}
	}
	
	
	public void createItemList(int startIndex, int width, int height, Inventory inventory, List<ItemStack> heads, int page) {
		ItemStack air = new ItemStack(Material.AIR);

		HashMap<int[], ItemStack> headSlots = new HashMap<int[], ItemStack>();
		int totalPages = (heads.size() / (width*(height-1))+1);
		
		int number = 0;
		for(int i = 0; i < totalPages; i++) {
			for(int j = 0; j < height; j++) {
				for(int k = 0; k < width; k++) {
					inventory.setItem(k + startIndex + (9*j), air);
					if(j == (height-1)) {
						if(k == 0) {
							if(page == 0) {
								inventory.setItem(k + startIndex + (9*j), PlayerManager.getPlayerHead("MHF_ArrowLeft", 0));	
							}
							else {
								inventory.setItem(k + startIndex + (9*j), PlayerManager.getPlayerHead("MHF_ArrowLeft", page-1));
							}
						}
						if(k == width-1) {
							if(page == totalPages-1) {
								inventory.setItem(k + startIndex + (9*j), PlayerManager.getPlayerHead("MHF_ArrowRight", totalPages-1));	
							}
							else {
								inventory.setItem(k + startIndex + (9*j), PlayerManager.getPlayerHead("MHF_ArrowRight", page+1));	
							}
						}
					}
					else if(j < height){
						if(!headSlots.containsKey(new int[] {i, j, k})) {
							if(heads.size() > number) {
								headSlots.put(new int[] {i, j, k}, heads.get(number));
								
							}
							else {
								inventory.setItem(startIndex + k + (9*j), air);
							}
						}
						number++;
					}
				}
			}
		}
		for(int[] slot : headSlots.keySet()) {
			if(slot[0] == page) {
				inventory.setItem(startIndex + slot[2] + (9*slot[1]), headSlots.get(slot));
			}
		}
	}
	
	
	public static List<ItemStack> getGuiItems(String[] sectionGui) {
		Main mn = Main.getInstance();
		FileConfiguration guiConf = mn.getGuiConfig();
		List<ItemStack> items = new ArrayList<ItemStack>();
		
		if(guiConf.contains("Raid." + sectionGui[0] + "." + sectionGui[1] + ".Items")) {
			for(String key : guiConf.getConfigurationSection("Raid." + sectionGui[0] + "." + sectionGui[1] + ".Items").getKeys(false)) {
				ItemStack item = new ItemStack(Material.valueOf(guiConf.getString("Raid." + sectionGui[0] + "." + sectionGui[1] + ".Items." + key + ".Material")));
				List<String> lore = new ArrayList<String>();
				String name = RaidMethods.format(guiConf.getString("Raid." + sectionGui[0] + "." + sectionGui[1] + ".Items." + key + ".Name"));
				ItemMeta meta = item.getItemMeta();
				
				for(String lores : guiConf.getStringList("Raid." + sectionGui[0] + "." + sectionGui[1] + ".Items." + key + ".Lore")) {
					lore.add(RaidMethods.format(lores));
				}
				
				meta.setDisplayName(name);
				
				meta.setLore(lore);
				item.setItemMeta(meta);
				
				items.add(item);
			}		
		}
		
		return items;
	}
	
	public static String[] getGuiSection(String displayName) {
		String[] sectionGui = new String[2];
		
		Main mn = Main.getInstance();
		FileConfiguration guiConf = mn.getGuiConfig();
		for(String section : guiConf.getConfigurationSection("Raid").getKeys(false)) {
			for(String gui : guiConf.getConfigurationSection("Raid." + section).getKeys(false)) {
				String display = guiConf.getString("Raid." + section + "." + gui + ".GUIName");
				if(RaidMethods.format(display).equals(displayName)) {
					sectionGui[0] = section;
					sectionGui[1] = gui;
				}	
			}
		}
		return sectionGui;
	}
	
	public static String getItemSection(String[] guiName, String displayName) {
		String guiSection = "Raid." + guiName[0] + "." + guiName[1] + ".Items";
		
		Main mn = Main.getInstance();
		FileConfiguration guiConf = mn.getGuiConfig();
		for(String item : guiConf.getConfigurationSection(guiSection).getKeys(false)) {
			String name = guiConf.getString(guiSection + "." + item + ".Name");
			if(RaidMethods.format(name).equals(displayName)) {
				return item;
			}
		}
		return null;
	}
	
	private String format(String str) {
		String nStr = str;
		for(String format : RaidMethods.FORMATS) {
			if(str.contains(format)) {
				switch(format) {
				case "%RaidSense%":
					UUID islandUUID = PlayerManager.getIslandUUIDByMember(player.getUniqueId());
					Island island = main.getSkyBlock().getIslandManager().getIslandByUUID(islandUUID);
					nStr = RaidMethods.format(str, main.getFastDataAccess().getRaidSense(island.getOwnerUUID()) + "");
					break;
				}
			}
		}
		return RaidMethods.format(nStr);
	}
}
