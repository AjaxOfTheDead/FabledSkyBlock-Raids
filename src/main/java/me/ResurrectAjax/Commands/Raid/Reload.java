package me.ResurrectAjax.Commands.Raid;

import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidMethods;

public class Reload extends CommandInterface{

	private Main main;
	public Reload(Main main) {
		this.main = main;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "reload";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raid reload";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Reload config files";
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
		FileConfiguration language = main.getLanguage();
		
		if(!player.hasPermission("raid.reload") && !player.isOp()) player.sendMessage(RaidMethods.format(language.getString("Command.Error.NoPermission.Message")));
		main.reload();	
		player.sendMessage(RaidMethods.format(language.getString("Reload.Message")));
	}

}
