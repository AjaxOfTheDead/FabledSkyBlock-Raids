package me.ResurrectAjax.Raid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.ResurrectAjax.Commands.RaidHistory.RaidHistoryMap;
import me.ResurrectAjax.Main.Main;

public class RaidParty {
	private UUID leader;
	private List<UUID> members = new ArrayList<UUID>();
	private List<UUID> deadMembers = new ArrayList<UUID>();
	private List<ItemStack> brokenBlocks = new ArrayList<ItemStack>();
	private HashMap<Material, Integer> nonContainerBlocks = new HashMap<Material, Integer>();
	
	private List<HashMap<Material, ItemStack[]>> containerItems = new ArrayList<HashMap<Material,  ItemStack[]>>();
	
	public RaidParty(Player leader) {
		this.leader = leader.getUniqueId();
		addMember(leader.getUniqueId());
	}
	
	public void setLeader(UUID uuid) {
		leader = uuid;
	}
	
	public void removeDeadMembers() {
		for(UUID member : deadMembers) {
			deadMembers.remove(member);
		}
	}
	
	public void addBrokenBlock(ItemStack item) {
		if(RaidMethods.CONTAINERTYPES.contains(item.getType())) {
			brokenBlocks.add(item);	
		}
		else {
			if(nonContainerBlocks.containsKey(item.getType())) {
				int amount = nonContainerBlocks.get(item.getType());
				nonContainerBlocks.put(item.getType(), amount+1);
			}
			else {
				nonContainerBlocks.put(item.getType(), 1);
			}
		}
		
		
	}
	
	public void addContainerItems(Material material, ItemStack[] items) {
		HashMap<Material, ItemStack[]> itemHash = new HashMap<Material, ItemStack[]>();
		
		itemHash.put(material, items);
		containerItems.add(itemHash);
	}
	
	public void assignBlockID(int raidID, LinkedHashMap<Integer, ItemStack[]> containerHash) {
		RaidHistoryMap map = Main.getInstance().getRaidHistoryMap();
		for(HashMap<Material, ItemStack[]> item : containerItems) {
			for(int blockID : map.getBlocksByRaidID(raidID).keySet()) {
				if(item.containsKey(map.getBlocksByRaidID(raidID).get(blockID).getType())) {
					if(!containerHash.containsKey(blockID)) {
						containerHash.put(blockID, item.get(map.getBlocksByRaidID(raidID).get(blockID).getType()));
						break;
					}
				}
			}	
		}
	}
	
	public LinkedHashMap<Integer, ItemStack[]> getContainerItems(int raidID) {
		LinkedHashMap<Integer, ItemStack[]> containerHash = new LinkedHashMap<Integer, ItemStack[]>();
		assignBlockID(raidID, containerHash);
		
		
		
		return containerHash;
	}
	
	public List<ItemStack> getBrokenBlocks() {
		return brokenBlocks;
	}
	
	public void addNonContainerBlocks() {
		for(Material material : nonContainerBlocks.keySet()) {
			brokenBlocks.add(new ItemStack(material, nonContainerBlocks.get(material)));
		}
	}
	
	public void removeAll() {
		members.clear();
		deadMembers.clear();
		leader = null;
	}
	
	public void addDeadMember(UUID uuid) {
		deadMembers.add(uuid);
	}
	
	public void removeMember(UUID uuid) {
		if(members.size() == 2) {
			Main.getInstance().getRaidMethods().disbandRaidParty(Bukkit.getPlayer(uuid));
		}
		else {
			members.remove(uuid);
			if(leader.equals(uuid)) {
				
				for(int i = 0; i < members.size(); i++) {
					if(Bukkit.getPlayer(members.get(i)) != null) {
						setLeader(members.get(i));
						break;
					}
				}
				
			}	
		}
	}
	
	public void addMember(UUID uuid) {
		members.add(uuid);
	}
	
	public List<UUID> getMembers() {
		return members;
	}
	
	public List<UUID> getDeadMembers() {
		return deadMembers;
	}
	
	public UUID getLeader() {
		return leader;
	}
	
	public int getMemberListSize() {
		return members.size();
	}
	
    public List<UUID> getOnlineMembers() {
    	List<UUID> onlineMembers = new ArrayList<UUID>();
    	for(UUID member : members) {
    		if(Bukkit.getPlayer(member) != null) {
    			onlineMembers.add(member);
    		}
    	}
    	return onlineMembers;
    }
}
