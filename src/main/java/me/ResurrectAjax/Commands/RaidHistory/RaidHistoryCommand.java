package me.ResurrectAjax.Commands.RaidHistory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;

public class RaidHistoryCommand extends CommandInterface{
	private Main main;
	private List<CommandInterface> subcommands;
	
	public RaidHistoryCommand(Main main) {
		this.main = main;
		loadSubCommands();
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "raidhistory";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raidhistory";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Open the raidhistory gui";
	}

	@Override
	public String[] getArguments(UUID uuid) {
		String[] arguments = new String[subcommands.size()];
		for(int i = 0; i < subcommands.size(); i++) {
			arguments[i] = subcommands.get(i).getName();
		}
		return arguments;
	}

	@Override
	public List<CommandInterface> getSubCommands() {
		return subcommands;
	}

	@Override
	public void perform(Player player, String[] args) {
		switch(args.length) {
		case 1:
			boolean isCommand = false;
			for(CommandInterface command : subcommands) {
				if(args[0].equalsIgnoreCase(command.getName())) {
					command.perform(player, args);
					isCommand = true;
				}
			}
			if(!isCommand) {
				// main.getGuiManager().historySelectGui(player);
				main.getGuiManager().historyDefendingGUI(player, main.getRaidHistoryMap(), 0);
			}
			break;
		default:
			// main.getGuiManager().historySelectGui(player);
			main.getGuiManager().historyDefendingGUI(player, main.getRaidHistoryMap(), 0);
			break;
		}
		
	}
	
	private void loadSubCommands() {
		subcommands = Arrays.asList(
				new RaidHistoryDefend(main),
				new StolenItems(main),
				new SpecificHistory(main)
				);
	}
	
}
