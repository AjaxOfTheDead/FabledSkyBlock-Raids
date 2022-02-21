package me.ResurrectAjax.Commands.Raid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.Database;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;
import me.ResurrectAjax.Raid.RaidParty;

public class RaidEnd extends CommandInterface{
	private RaidManager raidManager;
	private RaidMethods raidMethods;
	private Main main;
	public RaidEnd(Main main) {
		this.main = main;
		raidManager = main.getRaidManager();
		raidMethods = main.getRaidMethods();
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "end";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raid end";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "End the current raid";
	}

	@Override
	public String[] getArguments(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CommandInterface> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void perform(Player player, String[] args) {
		FileConfiguration configLoad = main.getLanguage();
		
		if(raidMethods.getIslandRaider().get(player.getUniqueId()) != null) {
			RaidParty party = raidManager.getMembersParty(player.getUniqueId());
			if(player.getUniqueId() == party.getLeader()) {
				endRaid(party.getLeader());
			}
			else {
				player.sendMessage(RaidMethods.format(configLoad.getString("RaidParty.Leader.NotLeader.Message")));
			}
		}
		else {
			player.sendMessage(RaidMethods.format(configLoad.getString("Raid.Raid.Raiding.NotRaiding.Message")));
		}
	}
	
	public static void endRaid(UUID uuid) {
		RaidManager raidManager = Main.getInstance().getRaidManager();
		RaidMethods raidMethods = Main.getInstance().getRaidMethods();
		RaidParty party = raidManager.getMembersParty(uuid);
		
		HashMap<UUID, Location> islandRaider = raidMethods.getIslandRaider();
		HashMap<Location, UUID> raidedIslands = raidMethods.getRaidedIslands();
		if(raidManager.getCalledRaidCommands().contains(party.getLeader())) {
			raidManager.getCalledRaidCommands().remove(party.getLeader());
		}
		
		raidedIslands.remove(islandRaider.get(party.getLeader()));
		Database db = Main.getInstance().getRDatabase();
		
		party.addNonContainerBlocks();
		if(!party.getBrokenBlocks().isEmpty()) {
			int raidID = raidMethods.getCurrentRaid(uuid);
			db.insertBlocks(prepareBlocks(party.getBrokenBlocks()), raidID);
			db.insertItems(party.getContainerItems(raidID));
		}
		
		Location loc = islandRaider.get(party.getLeader()), 
				 newLoc = new Location(loc.getWorld(), loc.getX(), 0, loc.getZ());
		raidMethods.getSpectatedIslands().remove(newLoc);
		for(UUID member : party.getMembers()) {
			raidMethods.removeRaider(member);
			if(Bukkit.getPlayer(member) != null) {
				Bukkit.getPlayer(member).teleport(raidManager.getStartPositions().get(uuid));	
			}
		}
		raidManager.getBossBar().get(party.getLeader()).cancelTask();
		raidManager.getBossBar().get(party.getLeader()).getBar().removeAll();
		

	}
	
	private static List<HashMap<ItemStack, Boolean>> prepareBlocks(List<ItemStack> blocks) {
		List<HashMap<ItemStack, Boolean>> blockList = new ArrayList<HashMap<ItemStack, Boolean>>();
		for(ItemStack blockItem : blocks) {
			HashMap<ItemStack, Boolean> preparedBlock = new HashMap<ItemStack, Boolean>();
			boolean isContainer = false;
			if(RaidMethods.CONTAINERTYPES.contains(blockItem.getType())) {
				isContainer = true;
			}
			
			preparedBlock.put(blockItem, isContainer);
			blockList.add(preparedBlock);
		}
		return blockList;
		
	}
	
}
