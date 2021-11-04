package me.ResurrectAjax.Raid;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.sound.SoundManager;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.FastDataAccess;
import net.md_5.bungee.api.ChatColor;

public class RaidBar {
	private Main main;
	private RaidMethods raidMethods;
	private RaidManager raidManager;
	private BossBar bar;
	FileConfiguration language, configLoad;
	private String title, color;
	private UUID player;
	
	private int taskID;
	
	public RaidBar(Main main, Player player, String type) {
		this.main = main;
		this.raidMethods = main.getRaidMethods();
		this.raidManager = main.getRaidManager();
		
		language = main.getLanguage();
		configLoad = main.getConfiguration();
		this.player = player.getUniqueId();
		createBar(type);
	}
	
	public RaidBar addPlayer(Player player) {
		bar.addPlayer(player);
		return this;
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
		else if(type.equalsIgnoreCase("prepare")) {
			title = language.getString("Raid.Prepare.BossBar.Title");
			color = configLoad.getString("Raid.Prepare.BossBar.Color");
		}
		bar = Bukkit.createBossBar(format(title), BarColor.valueOf(color), BarStyle.SOLID);
		bar.setVisible(true);
		bar.addPlayer(Bukkit.getPlayer(player));
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
							Location enemy = raidMethods.getIslandSpectator().get(player.getUniqueId());
							if(!raidMethods.getIslands().contains(enemy)) {
								raidMethods.getIslands().add(enemy);	
							}
							player.teleport(raidManager.getStartPositions().get(player.getUniqueId()));
							raidMethods.exitRaidSpectator(player);
							main.getStorage().restoreItems(Bukkit.getPlayer(player.getUniqueId()));
							raidManager.getCalledRaidCommands().remove(player.getUniqueId());
						}
						raidMethods.checkLeader(raidManager.getMembersParty(player).getOnlineMembers().get(0));
						bar.removeAll();
						cancelTask();
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
							player.teleport(raidManager.getStartPositions().get(player.getUniqueId()));
							if(raidMethods.getIslandRaider().containsKey(player.getUniqueId())) {
								raidMethods.removeRaider(player.getUniqueId());
								raidManager.getCalledRaidCommands().remove(player.getUniqueId());
							}
						}
						raidMethods.checkLeader(raidManager.getMembersParty(player).getOnlineMembers().get(0));
						bar.removeAll();
						cancelTask();
					}
				}
			}, 0, 0);
		}
		else if(type.equalsIgnoreCase("prepare")) {
			bar.setProgress(1.0);
			taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
				
				double progress = 1.0;
				double configTime = configLoad.getInt("Raid.Prepare.PrepareTime");
				int totalTime;
				double time = 1.0 / (configTime);
				IslandManager islandManager = main.getSkyBlock().getIslandManager();
				FastDataAccess fdb = main.getFastDataAccess();
				
				SoundManager soundManager = main.getSkyBlock().getSoundManager();
				
				@Override
				public void run() {
					if(title.contains("%TimeLeft%")) {
						String newstr;
						totalTime = (int) Math.round(progress*configTime);
						newstr = title.replaceAll("%TimeLeft%", Integer.toString(totalTime));
						bar.setTitle(format(newstr));
						
						String title = language.getString("Raid.PrepareTime.PrepareTitles.Title"), subtitle = language.getString("Raid.PrepareTime.PrepareTitles.Subtitle")
								, actionbar = language.getString("Raid.PrepareTime.PrepareTitles.Actionbar");
						for(UUID uuid : raidManager.getMembersParty(player).getMembers()) {
							
							if(Bukkit.getPlayer(uuid) != null && totalTime > 0) {
								if(totalTime >= configTime) {
									new RaidTitles(title, subtitle, actionbar, Bukkit.getPlayer(uuid), totalTime, main);	
									soundManager.playSound(Bukkit.getPlayer(uuid), Sound.EVENT_RAID_HORN, 100.0F, 100.0F);
								}
								if(raidMethods.getIslandSpectator().containsKey(uuid)) {
									raidMethods.exitRaidSpectator(Bukkit.getPlayer(uuid));	
								}
							}
						}
						
						
						Location tempIsland = raidMethods.getIslandSpectator().get(player);
						
						OfflinePlayer owner = Bukkit.getOfflinePlayer(fdb.getOwnerByLocation(tempIsland));
						islandManager.loadIsland(owner);
						
						for(IslandRole ir : IslandRole.getRoles()) {
							for(UUID member : islandManager.getIslandByOwner(owner).getRole(ir)) {
								if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(member))) {
									bar.addPlayer(Bukkit.getPlayer(member));
									if(totalTime >= configTime) {
										new RaidTitles(title, subtitle, actionbar, Bukkit.getPlayer(member), totalTime, main);	
										soundManager.playSound(Bukkit.getPlayer(member), Sound.EVENT_RAID_HORN, 100.0F, 100.0F);
									}
								}
							}
						}
					}
					if(progress < 0) {
						bar.removeAll();
						raidMethods.raidIslandFromSpectator(Bukkit.getPlayer(player));
						cancelTask();
					}
					else {
						bar.setProgress(progress);
						progress -= time;
					}
				}
			}, 0, 20);
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
