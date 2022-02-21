package me.ResurrectAjax.RaidGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import me.ResurrectAjax.Commands.RaidHistory.RaidHistoryMap;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Playerdata.PlayerManager;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;
import net.md_5.bungee.api.ChatColor;

public class GuiClickEvent implements Listener{
	private Main main;
	private RaidManager raidManager;
	private GuiManager guiManager;
	private HashMap<UUID, UUID> confirmUUID = new HashMap<UUID, UUID>();
	private HashMap<UUID, Integer> containerID = new HashMap<UUID, Integer>();
	public GuiClickEvent(Main main) {
		this.main = main;
		guiManager = main.getGuiManager();
		raidManager = main.getRaidManager();
	}
	
	@EventHandler
	public void onGuiClick(InventoryClickEvent event) {
		FileConfiguration guiConfig = main.getGuiConfig();
		RaidMethods raidMethods = main.getRaidMethods();
		Player player = (Player)event.getWhoClicked();
		RaidHistoryMap map = main.getRaidHistoryMap();
		
		List<String> GUINames = new ArrayList<String>();
		for(String section : guiConfig.getConfigurationSection("Raid").getKeys(false)) {
			for(String GUIName : guiConfig.getConfigurationSection("Raid." + section).getKeys(false)) {
				GUINames.add(RaidMethods.format(guiConfig.getString("Raid." + section + "." + GUIName + ".GUIName")));
			}	
		}
		if(GUINames.contains(event.getView().getTitle()) || raidMethods.isValidDate(event.getView().getTitle()) || guiManager.isInCustomGui(player.getUniqueId())) {
			event.setCancelled(true);
			
			String title = event.getView().getTitle();
			ItemStack currentItem = event.getCurrentItem();
			
			if(currentItem != null) {
				if(raidManager.getMembersParty(player.getUniqueId()) != null) {
					List<String> lore = Arrays.asList(
							RaidMethods.format("&7Click to kick player")
							);
					List<UUID> partyMembers = raidManager.getMembersParty(player.getUniqueId()).getMembers();
					for(UUID uuid : partyMembers) {
						String itemSection = Gui.getItemSection(Gui.getGuiSection(title), currentItem.getItemMeta().getDisplayName());
						if(currentItem.equals(PlayerManager.getPlayerHead(uuid, Bukkit.getOfflinePlayer(uuid).getName(), lore)) && !guiManager.getMHFList().contains(itemSection) && !uuid.equals(player.getUniqueId())) {
							guiManager.confirmGui(player, currentItem);
							confirmUUID.put(player.getUniqueId(), uuid);
						}
					}
				}
				
				if(currentItem.getType().equals(Material.PLAYER_HEAD)) {
					if(guiManager.getMHFList().contains(ChatColor.stripColor(currentItem.getItemMeta().getDisplayName()))) {
						int page = Integer.parseInt(ChatColor.stripColor(currentItem.getItemMeta().getLore().get(1)));
						if(containerID.containsKey(player.getUniqueId())) {
							guiManager.containerSpecificGUI(player, map, containerID.get(player.getUniqueId()), page);
						}
						else {
							if(!raidMethods.isValidDate(title)) {
								switch(Gui.getGuiSection(title)[1]) {
								case "OverView":
									guiManager.partyLeaderGui1(player, page);
									break;
								case "HistoryDefending":
									guiManager.historyDefendingGUI(player, map, page);
									break;
								case "StolenItemsHistory":
									guiManager.historyStolenItemsGUI(player, map, main.getGuiManager().getSelectedRaid().get(player.getUniqueId()), page);
									break;
								}
							}
							else {
								guiManager.historySpecificGUI(player, map, main.getGuiManager().getSelectedRaid().get(player.getUniqueId()), title, page);
							}	
						}
						
						
					}
					
					if(currentItem.getItemMeta().getLore() != null && currentItem.getItemMeta().getLore().size() > 1) {
						if(raidMethods.isValidDate(currentItem.getItemMeta().getLore().get(1)) && !raidMethods.isValidDate(title)) {
							String lore = currentItem.getItemMeta().getLore().get(0);
							int raidID = Integer.parseInt(lore);
							guiManager.historySpecificGUI(player, map, raidID, currentItem.getItemMeta().getLore().get(1), 0);
						}	
					}
				}
				else {
					String configSection = "";
					if(raidMethods.isValidDate(title)) {
						String itemSection = Gui.getItemSection(new String[] {"RaidHistory", "SpecificHistory"}, currentItem.getItemMeta().getDisplayName());
						configSection = "Raid.RaidHistory.SpecificHistory.Items." + itemSection;
					}
					else if(Gui.getGuiItems(Gui.getGuiSection(title)) != null && Gui.getGuiItems(Gui.getGuiSection(title)).contains(currentItem)) {
						String itemSection = Gui.getItemSection(Gui.getGuiSection(title), currentItem.getItemMeta().getDisplayName());
						configSection = "Raid." + Gui.getGuiSection(title)[0] + "." + Gui.getGuiSection(title)[1] + ".Items." + itemSection;
					}
					else {
						if(RaidMethods.CONTAINERTYPES.contains(currentItem.getType()) && currentItem.getLore() != null) {
							if(RaidMethods.getIntFromString(currentItem.getLore().get(0)) != null) {
								int blockID = RaidMethods.getIntFromString(currentItem.getLore().get(0));
								containerID.put(player.getUniqueId(), blockID);
								guiManager.containerSpecificGUI(player, map, blockID, 0);	
							}
						}
						
						String itemSection = Gui.getItemSection(new String[] {"RaidHistory", "StolenContainerHistory"}, currentItem.getItemMeta().getDisplayName());
						configSection = "Raid.RaidHistory.StolenContainerHistory.Items." + itemSection;
						
					}
					
					if(guiConfig.contains(configSection)) {
						runCommandIfExists(player, configSection);	
						containerID.remove(player.getUniqueId());
					}	
				}
			}
			
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {

	}
	
	private void runCommandIfExists(Player player, String configSection) {
		FileConfiguration guiConfig = main.getGuiConfig();
		
		if(guiConfig.getConfigurationSection(configSection).contains("RunCommand")) {
			player.closeInventory();
			
			String player2 = "";
			if(confirmUUID.containsKey(player.getUniqueId())) {
				player2 = Bukkit.getOfflinePlayer(confirmUUID.get(player.getUniqueId())).getName();
				confirmUUID.remove(player.getUniqueId());
			}
			player.performCommand(RaidMethods.format(guiConfig.getString(configSection + ".RunCommand"), player2));
		}	
	}
}
