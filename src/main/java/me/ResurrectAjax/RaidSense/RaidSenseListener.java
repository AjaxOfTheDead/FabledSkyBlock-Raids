package me.ResurrectAjax.RaidSense;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.ResurrectAjax.Main.Main;

public class RaidSenseListener implements Listener{
	private Main main;
	public RaidSenseListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		RaidSenseTime islandTime = main.getIslandTime();
		islandTime.putPlayerLogTime(player.getUniqueId());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		main.getIslandTime().savePlayerTime(player);
		
	}
}
