package me.ResurrectAjax.Commands.RaidHistory;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;

public class StolenItems extends CommandInterface{
	public static String NAME = "stolenItems";
	
	 private Main main;
	 public StolenItems(Main main) {
		 this.main = main;
	 }
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getArguments(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CommandInterface> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void perform(Player player, String[] args) {
		int raidID = main.getGuiManager().getSelectedRaid().get(player.getUniqueId());
		
		main.getGuiManager().historyStolenItemsGUI(player, main.getRaidHistoryMap(), raidID, 0);
		
	}

}
