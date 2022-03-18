package me.ResurrectAjax.Commands.RaidParty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Commands.Raid.ExitGui;
import me.ResurrectAjax.Commands.RaidHelp.RaidPartyHelp;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;
import me.ResurrectAjax.Raid.RaidParty;
import me.ResurrectAjax.RaidGUI.GuiManager;

public class RaidPartyCommands extends CommandInterface{

	private Main main;
	private RaidMethods methods;
	private RaidManager raidManager;
	private GuiManager guiManager;
	private List<CommandInterface> subcommands = new ArrayList<CommandInterface>();
	
	public RaidPartyCommands(Main main) {
		this.main = main;
		raidManager = main.getRaidManager();
		methods = main.getRaidMethods();
		guiManager = main.getGuiManager();
		
		loadCommands();
	}
	
	public String getName() {
		return "raidparty";
	}

	public String getSyntax() {
		return "/raidparty";
	}

	public String getDescription() {
		return "Open the RaidParty GUI";
	}

	public String[] getArguments(UUID uuid) {
		String[] arguments = new String[subcommands.size()];
		for(int i = 0; i < subcommands.size(); i++) {
			arguments[i] = subcommands.get(i).getName();
		}
		return arguments;
	}

	public void perform(Player player, String[] args) {
		FileConfiguration configLoad = main.getLanguage();
		String cmdName = "";
		if(args.length <= 0) {
			for(CommandInterface command : subcommands) {
				if(!command.getName().equalsIgnoreCase(args[0])) continue;
				command.perform(player, args);
				cmdName = command.getName();
			}
		}
		if(cmdName.equalsIgnoreCase("") && args.length > 0) player.sendMessage(RaidMethods.format(RaidMethods.convertSyntax(getSyntax())));
		else if(args.length != 0) return;
		if(raidManager.getMembersParty(player.getUniqueId()) != null) guiManager.partyLeaderGui1(player, 0);	
		else player.sendMessage(RaidMethods.format(configLoad.getString("RaidParty.Error.NoParty.Message")));
		
	}
	
	private void loadCommands() {
		
		subcommands = Arrays.asList(
				new RaidPartyInvite(main, this),
				new RaidPartyAccept(main),
				new RaidPartyDeny(main),
				new RaidPartyLeave(main),
				new RaidPartyKick(main),
				new RaidPartyCancelInvite(main),
				new RaidPartyHelp(main),
				new ExitGui()
				);
	}

	public List<CommandInterface> getSubCommands() {
		
		return subcommands;
	}

}
