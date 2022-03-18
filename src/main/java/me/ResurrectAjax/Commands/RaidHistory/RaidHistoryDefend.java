package me.ResurrectAjax.Commands.RaidHistory;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;

/**
 * Class for getting the defending raid history of an island
 * @author ResurrectAjax
 * */
public class RaidHistoryDefend extends CommandInterface{

	private Main main;
	
	/**
	 * Constructor of RaidHistoryDefend class<br>
	 * @param main instance of the {@link me.ResurrectAjax.Main.Main} class
	 * */
	public RaidHistoryDefend(Main main) {
		this.main = main;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "defending";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raidhistory <defending>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Open the raidhistory defending gui";
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
		main.getGuiManager().historyDefendingGUI(player, main.getRaidHistoryMap(), 0);
		
	}
	
}
