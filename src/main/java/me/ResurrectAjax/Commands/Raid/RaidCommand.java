package me.ResurrectAjax.Commands.Raid;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.ItemStorage.ItemStorage;

public class RaidCommand extends CommandInterface{
	Main main;
	
	public RaidCommand(Main main, Player player) {
		this.main = main;
		perform(player);
	}
	
	public String getName() {
		return "raid";
	}

	public void perform(Player player) {
		ItemStorage storage = main.getStorage();
		storage.saveToStorage(player);
		
		for(Player players : Bukkit.getOnlinePlayers()) {
			if(players != player) {
				players.hidePlayer(main, player);
			}
		}
		
	}

}
