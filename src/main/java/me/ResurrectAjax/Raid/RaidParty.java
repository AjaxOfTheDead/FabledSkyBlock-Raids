package me.ResurrectAjax.Raid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RaidParty {
	private UUID leader;
	private List<UUID> members = new ArrayList<UUID>();
	private List<UUID> deadMembers = new ArrayList<UUID>();
	
	public RaidParty(Player leader) {
		this.leader = leader.getUniqueId();
		addMember(leader);
	}
	
	public void setLeader(Player player) {
		leader = player.getUniqueId();
	}
	
	public void removeDeadMembers() {
		for(UUID member : deadMembers) {
			deadMembers.remove(member);
		}
	}
	
	public void removeAll() {
		members.clear();
		deadMembers.clear();
		leader = null;
	}
	
	public void addDeadMember(Player player) {
		deadMembers.add(player.getUniqueId());
	}
	
	public void removeMember(Player player) {
		members.remove(player.getUniqueId());
	}
	
	public void addMember(Player player) {
		members.add(player.getUniqueId());
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
