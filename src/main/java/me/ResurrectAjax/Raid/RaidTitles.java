package me.ResurrectAjax.Raid;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Main.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class RaidTitles {
	private Player player;
	private String title, subtitle, actionbar;
	private Integer timeLeft;
	private FileConfiguration configLoad;
	public RaidTitles(Main main, Player player) {
		this.player = player;
		configLoad = main.getConfiguration();
	}
	
	public RaidTitles(String title, String subtitle, String actionbar, Player player, Integer timeLeft, Main main) {
		this.player = player;
		configLoad = main.getConfiguration();
		this.timeLeft = timeLeft;
		
		setTitle(convertTimeLeft(title));
		setSubtitle(convertTimeLeft(subtitle));
		setActionBar(convertTimeLeft(actionbar));
		
		sendRaidTitle();
	}
	
	public void setTitle(String string) {
		title = checkStringForColor(string);
	}
	
	public void setSubtitle(String string) {
		subtitle = checkStringForColor(string);
	}
	
	public void setActionBar(String string) {
		actionbar = checkStringForColor(string);
	}
	
	public void sendRaidTitle() {
		player.sendTitle(this.title, this.subtitle, 10, 20, 10);
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
	}
	
	public String checkStringForColor(String string) {
		String colorString = "";
		if(string.contains("&")) {
			colorString = ChatColor.translateAlternateColorCodes('&', string);
		}
		else {
			colorString = string;	
		}
		return colorString;
	}
	
	public String convertTimeLeft(String string) {
		int time = 0;
		if(timeLeft == null) {
			time = configLoad.getInt("Raid.RaidFinder.ScoutTime");	
		}
		else {
			time = timeLeft;
		}
		String newstr = string;
		
		if(newstr.contains("%TimeLeft%")) {
			newstr = string.replace("%TimeLeft%", time + "");
		}
		return newstr;
	}
}
