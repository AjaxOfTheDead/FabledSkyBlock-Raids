package me.ResurrectAjax.Commands.Raid;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.FastDataAccess;
import me.ResurrectAjax.Raid.RaidParty;
import me.ResurrectAjax.Raid.ItemStorage.ItemStorage;
import net.md_5.bungee.api.ChatColor;

public class RaidCommand extends CommandInterface{
	private Main main;
	private RaidMethods raidMethods;
	private FastDataAccess fdb;
	
	public RaidCommand(Main main, Player player) {
		this.main = main;
		this.raidMethods = main.getRaidMethods();
		fdb = main.getFastDataAccess();
		perform(player);
	}
	
	public String getName() {
		return "raid";
	}
	
	public void startRaid(Player player, Location spectateLocation) {
		for(Player players : Bukkit.getOnlinePlayers()) {
			if(players != player) {
				players.hidePlayer(main, player);
			}
		}
		player.setAllowFlight(true);
		player.setInvulnerable(true);
		
		player.teleport(spectateLocation);
		
	}

	public void perform(Player player) {
		if(main.getIslandPositions().size() > 1) {
			Location location =  raidMethods.pickIsland(player.getUniqueId());
			Location spectateLocation = new Location(location.getWorld(), location.getX(), 72, location.getZ());
			
			ItemStorage storage = main.getStorage();
			storage.saveToStorage(player);
			
			if(main.getRaidParties().get(player.getUniqueId()) == null) {
				main.addRaidParty(player.getUniqueId(), new RaidParty(player));
			}
			
			RaidParty party = main.getRaidParties().get(player.getUniqueId());
			main.addRaidBar(player.getUniqueId(), new RaidBar(main, player, "spectate"));
			for(Player playere : party.getMembers()) {
				startRaid(playere, spectateLocation);
				main.getBossBar().get(player.getUniqueId()).addPlayer(playere);
			}
			
			OfflinePlayer owner = Bukkit.getOfflinePlayer(fdb.getOwnerByLocation(location));
			main.getSkyBlock().getIslandManager().loadIsland(owner);
			startRaid(player, spectateLocation);
			
		}
		else {
			FileConfiguration language = main.getLanguage();
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', language.getString("Raid.Error.NotEnoughIslands.Message")));
		}
	}

}
