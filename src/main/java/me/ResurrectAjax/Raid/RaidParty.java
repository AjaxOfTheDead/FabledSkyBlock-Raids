package me.ResurrectAjax.Raid;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class RaidParty {
	private Player leader;
	private List<Player> members = new ArrayList<Player>();
	private List<Player> deadMembers = new ArrayList<Player>();
	
	public RaidParty(Player leader) {
		this.leader = leader;
		addMember(leader);
	}
	
	public void removeDeadMembers() {
		for(Player member : deadMembers) {
			deadMembers.remove(member);
		}
	}
	
	public void addDeadMember(Player player) {
		deadMembers.add(player);
	}
	
	public void removeMember(Player player) {
		members.remove(player);
	}
	
	public void addMember(Player player) {
		members.add(player);
	}
	
	public List<Player> getMembers() {
		return members;
	}
	
	public List<Player> getDeadMembers() {
		return deadMembers;
	}
	
	public Player getLeader() {
		return leader;
	}
}
