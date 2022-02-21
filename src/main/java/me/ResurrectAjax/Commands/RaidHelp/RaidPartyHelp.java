package me.ResurrectAjax.Commands.RaidHelp;

import org.bukkit.entity.Player;

import me.ResurrectAjax.Main.Main;

public class RaidPartyHelp extends RaidHelpParent{
	public RaidPartyHelp(Main main) {
		super(main);
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raidparty help <page>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Get a list of all the raidparty commands";
	}

	@Override
	public void perform(Player player, String[] args) {
		super.perform(player, "raidparty", args);

		
	}
}
