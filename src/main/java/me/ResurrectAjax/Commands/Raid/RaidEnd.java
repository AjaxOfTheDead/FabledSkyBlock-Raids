package me.ResurrectAjax.Commands.Raid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.Database;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;
import me.ResurrectAjax.Raid.RaidParty;

/**
 * Class for ending a raid
 * 
 * @author ResurrectAjax
 * */
public class RaidEnd extends CommandInterface{
	private RaidManager raidManager;
	private RaidMethods raidMethods;
	private Main main;
	
	/**
	 * Constructor of RaidEnd class<br>
	 * @param main instance of the {@link me.ResurrectAjax.Main.Main} class
	 * */
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
		
		if(raidMethods.getIslandRaider().get(player.getUniqueId()) == null) player.sendMessage(RaidMethods.format(configLoad.getString("Raid.Raid.Raiding.NotRaiding.Message")));
		else {
			RaidParty party = raidManager.getMembersParty(player.getUniqueId());
			if(player.getUniqueId() == party.getLeader()) endRaid(party.getLeader());
			else player.sendMessage(RaidMethods.format(configLoad.getString("RaidParty.Leader.NotLeader.Message")));
		}
	}
	
	/**
	 * Ends a raid
	 * @param uuid uuid of the party leader
	 * */
	public static void endRaid(UUID uuid) {
		RaidManager raidManager = Main.getInstance().getRaidManager();
		RaidMethods raidMethods = Main.getInstance().getRaidMethods();
		RaidParty party = raidManager.getMembersParty(uuid);
		
		HashMap<UUID, Location> islandRaider = raidMethods.getIslandRaider();
		if(raidManager.getCalledRaidCommands().contains(party.getLeader())) raidManager.getCalledRaidCommands().remove(party.getLeader());
		
		raidMethods.getRaidedIslands().remove(islandRaider.get(uuid));
		Database db = Main.getInstance().getRDatabase();
		
		party.addNonContainerBlocks();
		List<ItemStack> items = new ArrayList<ItemStack>();
		if(!party.getStolenItems().isEmpty()) {
			party.addBrokenBlock(new ItemStack(Material.BARREL));
			
			for(UUID player : party.getStolenItems().keySet()) {
				items.addAll(party.getStolenItems().get(player));
			}
			party.addContainerItems(Material.BARREL, items.toArray(new ItemStack[items.size()]));
		}
		
		int raidID = raidMethods.getCurrentRaid(party.getLeader());
		if(!party.getBrokenBlocks().isEmpty()) db.insertBlocks(prepareBlocks(party.getBrokenBlocks()), raidID);
		if(!party.getContainerItems(raidID).isEmpty()) db.insertItems(party.getContainerItems(raidID));
		
		Location loc = islandRaider.get(uuid), 
				 newLoc = new Location(loc.getWorld(), loc.getX(), 0, loc.getZ());
		raidMethods.getSpectatedIslands().remove(newLoc);
		for(UUID member : party.getMembers()) {
			raidMethods.removeRaider(member);
			if(Bukkit.getPlayer(member) != null) Bukkit.getPlayer(member).teleport(raidManager.getStartPositions().get(member));	
		}
		raidManager.getBossBar().get(party.getLeader()).cancelTask();
		raidManager.getBossBar().get(party.getLeader()).getBar().removeAll();
		
		for(UUID member : party.getMembers()) {
			if(Bukkit.getPlayer(member) == null) continue;
			if(raidMethods.getAmplifiers(party).isEmpty()) continue;
			for(PotionEffect effect : raidMethods.getAmplifiers(party))
			Bukkit.getPlayer(member).removePotionEffect(effect.getType());	
		}
		

	}
	
	private static List<HashMap<ItemStack, Boolean>> prepareBlocks(List<ItemStack> blocks) {
		List<HashMap<ItemStack, Boolean>> blockList = new ArrayList<HashMap<ItemStack, Boolean>>();
		for(ItemStack blockItem : blocks) {
			HashMap<ItemStack, Boolean> preparedBlock = new HashMap<ItemStack, Boolean>();
			boolean isContainer = false;
			if(RaidMethods.CONTAINERTYPES.contains(blockItem.getType())) isContainer = true;
			
			preparedBlock.put(blockItem, isContainer);
			blockList.add(preparedBlock);
		}
		return blockList;
		
	}
	
}
