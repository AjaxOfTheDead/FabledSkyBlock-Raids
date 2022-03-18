package me.ResurrectAjax.Commands.RaidHelp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;

/**
 * Class for getting a list of raid commands
 * 
 * @author ResurrectAjax
 * */
public class RaidHelp extends RaidHelpParent{
	public RaidHelp(Main main) {
		super(main);
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raid help";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Get a list of all the raid commands";
	}

	@Override
	public void perform(Player player, String[] args) {
		player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Raid" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "raid");
		
		List<CommandInterface> commands = new ArrayList<CommandInterface>(main.getCommandManager().getCommands());
		commands.add(main.getCommandManager().getCommandByName("end"));
		commands.sort(new Comparator<CommandInterface>() {
			@Override
			public int compare(final CommandInterface cmi1, final CommandInterface cmi2) {
				return cmi1.getName().compareTo(cmi2.getName());
			}
		});
		
		for(CommandInterface helpCommand : commands) {
			String message = "";
			
			switch(helpCommand.getName().toLowerCase()) {
			case "raidparty":
				message = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "* " + ChatColor.RED + "/" + helpCommand.getName() + " help" + ChatColor.GRAY + " - " + ChatColor.WHITE + ChatColor.ITALIC + "Get all the " + helpCommand.getName() + " commands.";
				break;
			default:
				message = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "* " + ChatColor.RED + helpCommand.getSyntax() + ChatColor.GRAY + " - " + ChatColor.WHITE + ChatColor.ITALIC + helpCommand.getDescription();
				break;
			}
			
			
			player.sendMessage(message);
		}

		
	}
}
