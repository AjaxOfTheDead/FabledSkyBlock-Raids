package me.ResurrectAjax.Commands.RaidSense;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.songoda.skyblock.island.Island;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Playerdata.PlayerManager;
import me.ResurrectAjax.Raid.RaidMethods;

public class RaidSenseCommand extends CommandInterface{
	
	private Main main;
	public RaidSenseCommand(Main main) {
		this.main = main;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "raidsense";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/raidsense <player>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Get the RaidSense balance of a player's island";
	}

	@Override
	public String[] getArguments(UUID uuid) {
		String[] playerNames = new String[Bukkit.getOnlinePlayers().size()-1];
		int count = 0;
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getUniqueId().equals(uuid)) continue;
			playerNames[count] = player.getName();	
			count++;
		}
		return playerNames;
	}

	@Override
	public List<CommandInterface> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Player player, String[] args) {
		FileConfiguration language = main.getLanguage();
		String message = language.getString("RaidSense.GetBalance.Message");
		switch(args.length) {
		case 0:
			player.sendMessage(format(message, player.getUniqueId()));
			break;
		case 1:
			if(Bukkit.getOfflinePlayer(args[0]) != null && Bukkit.getPlayer(args[0]) != null) {
				player.sendMessage(RaidMethods.format(language.getString("RaidParty.Error.PlayerNotExist.Message")));
				break;
			}
			if(PlayerManager.getPlayersIsland(Bukkit.getOfflinePlayer(args[0]).getUniqueId()) == null || PlayerManager.getPlayersIsland(Bukkit.getPlayer(args[0]).getUniqueId()) == null) {
				player.sendMessage(RaidMethods.format(language.getString("RaidSense.NoIsland.Message")));
				return;
			}
			UUID player2UUID = Bukkit.getPlayer(args[0]) != null ? Bukkit.getPlayer(args[0]).getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId();
			player.sendMessage(format(message, player2UUID));
			break;
		default:
			player.sendMessage(RaidMethods.format(RaidMethods.convertSyntax(getSyntax())));
			break;
		}
		
	}
	
	private String format(String str, UUID player) {
		String nStr = str;
		for(String format : RaidMethods.FORMATS) {
			if(!str.contains(format)) continue;
			switch(format) {
			case "%RaidSense%":
				UUID islandUUID = PlayerManager.getIslandUUIDByMember(player);
				Island island = main.getSkyBlock().getIslandManager().getIslandByUUID(islandUUID);
				nStr = RaidMethods.format(str, main.getFastDataAccess().getRaidSense(island.getOwnerUUID()) + "");
				break;
			}
		}
		return RaidMethods.format(nStr);
	}
	
}
