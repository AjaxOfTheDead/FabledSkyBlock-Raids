package me.ResurrectAjax.Commands.RaidHistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RaidHistory {
	private UUID islandOwner;
	private UUID partyLeader;
	private List<ItemStack> brokenBlocks;
	private List<UUID> islandRaiders;
	private Date date;
	
	public RaidHistory(UUID islandOwner, UUID partyLeader, Date date) {
		setOwner(islandOwner);
		setRaidPartyLeader(partyLeader);
		islandRaiders = new ArrayList<UUID>();
		addRaider(partyLeader);
		this.brokenBlocks = new ArrayList<ItemStack>();
		this.date = date;
	}
	public RaidHistory(UUID islandOwner, UUID partyLeader, List<UUID> islandRaiders, Date date) {
		setOwner(islandOwner);
		setRaidPartyLeader(partyLeader);
		this.islandRaiders = islandRaiders;
		addRaider(partyLeader);
		this.brokenBlocks = new ArrayList<ItemStack>();
		this.date = date;
	}
	public RaidHistory(UUID islandOwner, UUID partyLeader, String brokenBlocks, Date date) {
		setOwner(islandOwner);
		setRaidPartyLeader(partyLeader);
		islandRaiders = new ArrayList<UUID>();
		addRaider(partyLeader);
		this.brokenBlocks = convertBlocksFromString(brokenBlocks);
		this.date = date;
	}
	public RaidHistory(UUID islandOwner, UUID partyLeader, List<UUID> islandRaiders, String brokenBlocks, Date date) {
		setOwner(islandOwner);
		setRaidPartyLeader(partyLeader);
		this.islandRaiders = islandRaiders;
		addRaider(partyLeader);
		this.brokenBlocks = convertBlocksFromString(brokenBlocks);
		this.date = date;
	}
	
	
	public void setOwner(UUID owner) {
		islandOwner = owner;
	}
	public UUID getIslandOwner() {
		return islandOwner;
	}
	
	
	public void setRaidPartyLeader(UUID leader) {
		partyLeader = leader;
	}
	public UUID getRaidPartyLeader() {
		return partyLeader;
	}
	
	
	public void addRaider(UUID raider) {
		islandRaiders.add(raider);
	}
	public List<UUID> getRaiders() {
		return islandRaiders;
	}
	
	
	public void addBlock(ItemStack block) {
		if(brokenBlocks.size() > 0) {
			boolean containsBlock = false;
			
			for(ItemStack brokenBlock : brokenBlocks) {
				if(brokenBlock.getItemMeta().getLocalizedName().equalsIgnoreCase(block.getItemMeta().getLocalizedName())) {
					int amount = brokenBlock.getAmount()+1;
					brokenBlocks.remove(brokenBlock);
					brokenBlock.setAmount(amount);
					brokenBlocks.add(brokenBlock);
					containsBlock = true;
				}
			}
			
			if(!containsBlock) {
				brokenBlocks.add(block);
			}
		}
		else {
			brokenBlocks.add(block);
		}
	}
	public List<ItemStack> getBrokenBlocks() {
		return brokenBlocks;
	}
	
	
	public Date getDate() {
		return date;
	}
	
	
	private List<ItemStack> convertBlocksFromString(String string) {
		String[] splitString = string.split(",");
		for(String str : splitString) {
			Material block = Material.matchMaterial(str.split(":")[0]);
			int amount = Integer.parseInt(str.split(":")[1]);
			
			brokenBlocks.add(new ItemStack(block, amount));
		}
		return brokenBlocks;
	}
	public String convertBlocksToString() {
		String totalstr = "";
		for(ItemStack block : brokenBlocks) {
			if(brokenBlocks.indexOf(block) == brokenBlocks.size()-1) {
				totalstr += block.getItemMeta().getLocalizedName() + ":" + block.getAmount();	
			}
			else {
				totalstr += block.getItemMeta().getLocalizedName() + ":" + block.getAmount() + ",";
			}
		}
		return totalstr;
	}
}
