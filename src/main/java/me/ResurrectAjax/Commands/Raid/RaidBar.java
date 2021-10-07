package me.ResurrectAjax.Commands.Raid;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;

import me.ResurrectAjax.Main.Main;
import net.md_5.bungee.api.ChatColor;

public class RaidBar {
	private Main main;
	private BossBar bar;
	FileConfiguration language, configLoad;
	private String title, color;
	private Player player;
	
	private int taskID;
	
	public RaidBar(Main main, Player player, String type) {
		this.main = main;
		language = main.getLanguage();
		configLoad = main.getConfiguration();
		this.player = player;
		createBar(type);
	}
	
	public void addPlayer(Player player) {
		bar.addPlayer(player);
	}
	
	public BossBar getBar() {
		return bar;
	}
	
	public void createBar(String type) {
		if(type.equalsIgnoreCase("spectate")) {
			title = language.getString("Raid.RaidFinder.BossBar.Title");
			color = configLoad.getString("Raid.RaidFinder.BossBar.Color");
		}
		else if(type.equalsIgnoreCase("raid")) {
			title = language.getString("Raid.Raid.BossBar.Title");
			color = configLoad.getString("Raid.Raid.BossBar.Color");
		}
		bar = Bukkit.createBossBar(format(title), BarColor.valueOf(color), BarStyle.SOLID);
		bar.setVisible(true);
		bar.addPlayer(player);
		cast(type);
	}
	
	public void cast(String type) {
		if(type.equalsIgnoreCase("spectate")) {
			taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
				
				double progress = 1.0;
				double configTime = configLoad.getInt("Raid.RaidFinder.ScoutTime");
				int totalTime;
				double time = 1.0 / (configTime*20);
				
				@Override
				public void run() {
					bar.setProgress(progress);
					
					progress = progress - time;
					if(title.contains("%TimeLeft%")) {
						String newstr;
						totalTime = (int) Math.round(progress*configTime);
						newstr = title.replaceAll("%TimeLeft%", Integer.toString(totalTime));
						bar.setTitle(format(newstr));
					}
					if(progress <= 0) {
						Bukkit.getScheduler().cancelTask(taskID);
						for(Player player : bar.getPlayers()) {
							main.getRaidMethods().exitRaidSpectator(player);
						}
						bar.removeAll();
					}
				}
			}, 0, 0);
		}
		else if(type.equalsIgnoreCase("raid")) {
			taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
				
				double progress = 1.0;
				double configTime = configLoad.getInt("Raid.Raid.RaidTime");
				int totalTime;
				double time = 1.0 / (configTime*20);
				
				@Override
				public void run() {
					bar.setProgress(progress);
					
					progress = progress - time;
					if(title.contains("%TimeLeft%")) {
						String newstr;
						totalTime = (int) Math.round(progress*configTime);
						newstr = title.replaceAll("%TimeLeft%", Integer.toString(totalTime));
						bar.setTitle(format(newstr));
					}
					if(progress <= 0) {
						Bukkit.getScheduler().cancelTask(taskID);
						for(Player player : bar.getPlayers()) {
							Location playerIsland = main.getSkyBlock().getIslandManager().getIsland(player).getLocation(IslandWorld.Normal, IslandEnvironment.Main);
							player.teleport(playerIsland);
							if(main.getRaidMethods().getIslandRaider().containsKey(player.getUniqueId())) {
								main.getRaidMethods().removeRaider(player.getUniqueId());	
							}
						}
						bar.removeAll();
					}
				}
			}, 0, 0);
		}
	}
	
	public void cancelTask() {
		Bukkit.getScheduler().cancelTask(taskID);
	}
	
	private String format(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public int getTaskID() {
		return taskID;
	}
}
