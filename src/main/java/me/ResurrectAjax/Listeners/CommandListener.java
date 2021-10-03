package me.ResurrectAjax.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;

import me.ResurrectAjax.Commands.Admin.StructureCommand;
import me.ResurrectAjax.Main.Main;

public class CommandListener implements Listener{
	private SkyBlock skyblock;
	private Main main;
	
	private HashMap<Player, List<String>> playerCommand = new HashMap<Player, List<String>>();
	
	public CommandListener(SkyBlock skyblock, Main main) {
		this.skyblock = skyblock;
		this.main = main;
	}
	
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String cmd = event.getMessage();
		List<String> cmdArray = Arrays.asList(cmd.split(" "));
		
		playerCommand.put(event.getPlayer(), cmdArray);
	
		for(SubCommand subcmd : getSubCommands()) {
			List<String> subargs = new ArrayList<String>();
			if(subcmd.getArguments().length > 0) {
				for(int i = 0; i < subcmd.getArguments().length; i++) {
					if(cmd.contains(subcmd.getName() + " " + subcmd.getArguments()[i])) {
						if(cmdArray.indexOf(subcmd.getArguments()[i]) != cmdArray.size()-1) {
							
							for(int j = cmdArray.indexOf(subcmd.getArguments()[i]); j < cmdArray.size(); j++) {
								subargs.add(cmdArray.get(j));
							}
						}
						else {
							subargs.add(cmdArray.get(cmdArray.size()-1));
						}
					}
				}
			}
			else {
				if(!cmdArray.get(cmdArray.size()-1).equals(subcmd.getName()) || !cmdArray.get(cmdArray.size()-1).equals(cmdArray.get(0))) {
					subargs.add(cmdArray.get(cmdArray.size()-1));
				}
			}
			if(subargs.size() != 0) {
				if(cmdArray.get(1).equals(subcmd.getName())) {
					
				}
				else if(cmdArray.get(1).equalsIgnoreCase("admin")) {
					if(cmdArray.get(2).equals(subcmd.getName())) {
						event.setCancelled(true);
						
						
						switch(subcmd.getName()) {
							case "structure":
								new StructureCommand(event.getPlayer(), subargs.toArray(new String[subargs.size()]), main);
								break;
							default:
								break;
						}
					}
				}
			}
		}
	
	}
	
	public static String unTranslateAlternateColorCodes(String text) {
	    char[] array = text.toCharArray();
	    for (int i = 0; i < array.length - 1; i++) {
	        if (array[i] == ChatColor.COLOR_CHAR && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(array[i + 1]) != -1) {
	            array[i] = '&';
	            array[i + 1] = Character.toLowerCase(array[i + 1]);
	        }
	    }
	    return new String(array);
	}
	
	public List<SubCommand> getSubCommands() {
		List<SubCommand> subcommands = new ArrayList<SubCommand>();
		subcommands.add(skyblock.getCommandManager().getAdminSubCommand("structure"));
		
		return subcommands;
	}
}
