package me.ResurrectAjax.RaidGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.ResurrectAjax.Commands.RaidHistory.RaidHistoryMap;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Playerdata.PlayerManager;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;

public class GuiManager {
	private Main main;
	private RaidManager raidManager;
	private FileConfiguration guiConfig;
	private List<String> MHF_Heads = new ArrayList<String>();
	private HashMap<UUID, Integer> selectedRaid = new HashMap<UUID, Integer>();
	private HashMap<UUID, Gui> currentGui = new HashMap<UUID, Gui>();
	
	public Gui getCurrentGui(UUID uuid) {
		return currentGui.get(uuid);
	}
	
	public void setCurrentGui(UUID uuid, Gui gui) {
		currentGui.put(uuid, gui);
	}

	public GuiManager(Main main) {
		this.main = main;
		this.raidManager = main.getRaidManager();
		
		MHF_Heads.addAll(Arrays.asList(
				"Back",
				"Next"
				));
		
	}
	
	public List<String> getMHFList() {
		return MHF_Heads;
	}
	
	public HashMap<UUID, Integer> getSelectedRaid() {
		return selectedRaid;
	}
	
	public Gui partyLeaderGui1(Player player, int playerPage) {
		guiConfig = main.getGuiConfig();
		Gui gui = new Gui(main, player, guiConfig.getInt("Raid.RaidParty.OverView.Rows"), RaidMethods.format(guiConfig.getString("Raid.RaidParty.OverView.GUIName")));
		Inventory inventory = gui.getInventory();
		
		
		List<String> lore = new ArrayList<String>();
		if(raidManager.getMembersParty(player.getUniqueId()).getLeader().equals(player.getUniqueId())) {
			lore.add(RaidMethods.format("&7Click to kick player"));
		}
		
		
		List<ItemStack> heads = new ArrayList<ItemStack>();
		for(UUID uuid : raidManager.getMembersParty(player.getUniqueId()).getMembers()) {
			heads.add(PlayerManager.getPlayerHead(uuid, Bukkit.getOfflinePlayer(uuid).getName(), lore));
			
		}
		
		gui.createItemList(29, 5, 3, inventory, heads, playerPage);
		
		gui.openInventory();
		
		return gui;
	}
	
	public Gui confirmGui(Player player, ItemStack item) {
		guiConfig = main.getGuiConfig();
		Gui gui = new Gui(main, player, guiConfig.getInt("Raid.RaidParty.Confirm.Rows"), RaidMethods.format(guiConfig.getString("Raid.RaidParty.Confirm.GUIName")));
		Inventory inventory = gui.getInventory();
		
		inventory.setItem(4, item);
		gui.openInventory();
		
		return gui;
	}
	
	
	public Gui historySelectGui(Player player) {
		guiConfig = main.getGuiConfig();
		Gui gui = new Gui(main, player, guiConfig.getInt("Raid.RaidHistory.HistorySelect.Rows"), RaidMethods.format(guiConfig.getString("Raid.RaidHistory.HistorySelect.GUIName")));
		gui.openInventory();
		
		return gui;
	}
	
	public Gui historyDefendingGUI(Player player, RaidHistoryMap map, int playerPage) {
		guiConfig = main.getGuiConfig();
		String guiName = RaidMethods.format(guiConfig.getString("Raid.RaidHistory.HistoryDefending.GUIName"));
		Gui gui = new Gui(main, player, guiConfig.getInt("Raid.RaidHistory.HistoryDefending.Rows"), guiName);
		RaidHistoryMap historyMap = main.getRaidHistoryMap();
		
		Inventory inventory = gui.getInventory();
		UUID islandUUID = PlayerManager.getIslandUUIDByMember(player.getUniqueId());
		List<ItemStack> raidLeadersHeads = new ArrayList<ItemStack>();
		
		LinkedHashMap<Integer, String[]> raidsHash = new LinkedHashMap<Integer, String[]>(historyMap.getRaidsByIslandUUID(islandUUID));
		
		for(int raidID : raidsHash.keySet()) {
			String[] raids = raidsHash.get(raidID);
			
 			int partyID = Integer.parseInt(raids[0]);
 			
 			
 			
 			UUID leaderUUID = historyMap.getPartyByID(partyID).keySet().iterator().next();
 			List<String> lore = new ArrayList<String>();
 			lore.add(raidID + "");
 			lore.add(raids[1]);
 			lore.add(RaidMethods.format("&7Click for details"));
 			raidLeadersHeads.add(PlayerManager.getPlayerHead(leaderUUID, RaidMethods.format("&c&l" + Bukkit.getOfflinePlayer(leaderUUID).getName()), lore));
		}
		
		gui.createItemList(11, 5, 4, inventory, raidLeadersHeads, playerPage);
		gui.openInventory();
		
		return gui;
	}
	
	public Gui historySpecificGUI(Player player, RaidHistoryMap map, int raidID, String date, int playerPage) {
		guiConfig = main.getGuiConfig();
		String guiName = RaidMethods.format(guiConfig.getString("Raid.RaidHistory.SpecificHistory.GUIName"));
		Gui gui = new Gui(main, player, guiConfig.getInt("Raid.RaidHistory.SpecificHistory.Rows"), guiName, date);
		
		gui.createTemplate(guiName, date);
		
		List<UUID> members = map.getPartyByRaidID(raidID).entrySet().iterator().next().getValue();
		UUID leader = map.getPartyByRaidID(raidID).entrySet().iterator().next().getKey();
		
		List<ItemStack> heads = new ArrayList<ItemStack>();
		for(UUID member : members) {
			List<String> lore = new ArrayList<String>();
			if(member.equals(leader)) {
				lore.add("Party Leader");
			}
			else {
				lore.add("Member");
			}
			heads.add(PlayerManager.getPlayerHead(member, Bukkit.getOfflinePlayer(member).getName(), lore));
		}
		
		gui.createItemList(20, 5, 3, gui.getInventory(), heads, playerPage);
		
		selectedRaid.put(player.getUniqueId(), raidID);
		gui.openInventory();
		
		return gui;
	}
	
	public Gui historyStolenItemsGUI(Player player, RaidHistoryMap map, int raidID, int playerPage) {
		guiConfig = main.getGuiConfig();
		Gui gui = new Gui(main, player, guiConfig.getInt("Raid.RaidHistory.StolenItemsHistory.Rows"), RaidMethods.format(guiConfig.getString("Raid.RaidHistory.StolenItemsHistory.GUIName")));
		
		List<ItemStack> blockList = new ArrayList<ItemStack>();
		LinkedHashMap<Integer, ItemStack> blocks = map.getBlocksByRaidID(raidID);
		for(int blockID : blocks.keySet()) {
			if(blocks.get(blockID).getType().isBlock()) {
				
				if(RaidMethods.CONTAINERTYPES.contains(blocks.get(blockID).getType())) {
					List<String> lore = new ArrayList<String>(Arrays.asList(
							RaidMethods.format("&5ID: " + blockID),
							RaidMethods.format("&7Click for details")
							));
					blocks.get(blockID).setLore(lore);
				}	
			}
			blockList.add(blocks.get(blockID));
		}
		
		gui.createItemList(18, 9, 4, gui.getInventory(), blockList, playerPage);
		gui.openInventory();
		
		return gui;
	}
	
	public Gui containerSpecificGUI(Player player, RaidHistoryMap map, int blockID, int playerPage) {
		guiConfig = main.getGuiConfig();
		Gui gui = new Gui(main, player, guiConfig.getInt("Raid.RaidHistory.StolenContainerHistory.Rows"), RaidMethods.format(guiConfig.getString("Raid.RaidHistory.StolenContainerHistory.GUIName")), blockID + "");
		
		List<ItemStack> itemList = new ArrayList<ItemStack>();
		LinkedHashMap<Integer, ItemStack> items = map.getItemsByContainerBlockID(blockID);
		for(int itemID : items.keySet()) {
			itemList.add(items.get(itemID));
		}
		
		gui.createItemList(18, 9, 4, gui.getInventory(), itemList, playerPage);
		gui.openInventory();
		
		return gui;
	}

}
