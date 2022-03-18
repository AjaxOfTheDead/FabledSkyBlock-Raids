package me.ResurrectAjax.Commands.Raid;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;

public class ExitGui extends CommandInterface{
	public static String NAME = "exitgui";
	
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/<command> exitgui";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "exit the gui";
	}

	public String[] getArguments(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<CommandInterface> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void perform(Player player, String[] args) {
		player.closeInventory();
		
	}
}
