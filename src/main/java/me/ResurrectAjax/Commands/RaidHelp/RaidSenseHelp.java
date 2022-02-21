package me.ResurrectAjax.Commands.RaidHelp;

import org.bukkit.entity.Player;

import me.ResurrectAjax.Main.Main;

public class RaidSenseHelp extends RaidHelpParent {

	public RaidSenseHelp(Main main) {
		super(main);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raidsense help";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Get a list of all the raidsense commands";
	}

	@Override
	public void perform(Player player, String[] args) {
		super.perform(player,  "raidsense", args);
		
	}

}
