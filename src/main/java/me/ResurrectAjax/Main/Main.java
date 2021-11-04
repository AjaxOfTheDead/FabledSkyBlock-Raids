package me.ResurrectAjax.Main;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;

import me.ResurrectAjax.Commands.Managers.CommandInterface;
import me.ResurrectAjax.Commands.Managers.CommandManager;
import me.ResurrectAjax.Commands.Managers.FileManager;
import me.ResurrectAjax.Listeners.BlockListeners;
import me.ResurrectAjax.Listeners.CommandListener;
import me.ResurrectAjax.Listeners.InteractListeners;
import me.ResurrectAjax.Listeners.IslandListener;
import me.ResurrectAjax.Listeners.JoinListeners;
import me.ResurrectAjax.Listeners.MoveListeners;
import me.ResurrectAjax.Listeners.RaidListener;
import me.ResurrectAjax.Mysql.Database;
import me.ResurrectAjax.Mysql.FastDataAccess;
import me.ResurrectAjax.Mysql.MysqlMain;
import me.ResurrectAjax.Playerdata.PlayerDataManager;
import me.ResurrectAjax.Raid.RaidManager;
import me.ResurrectAjax.Raid.RaidMethods;
import me.ResurrectAjax.Raid.ItemStorage.ItemStorage;
import me.ResurrectAjax.RaidGUI.GuiManager;

public class Main extends JavaPlugin{
	private static Main INSTANCE;
	
	private Database db;
	private SkyBlock skyblock;
	private FastDataAccess fdb;
	
	private PlayerDataManager playerDataManager;
	private FileManager fileManager;
	
	private FileConfiguration language;
	private FileConfiguration config;
	private FileConfiguration guiConfig;
	
	private ItemStorage storage;

	private RaidManager raidManager;
	
	private GuiManager guiManager;
	
	private RaidMethods raidMethods;
	
	private CommandManager commandManager;
	
	private TabCompletion tabCompleter;
	
	
	public static Main getInstance() {
        return INSTANCE;
    }
	
	public void onEnable() {
		
		//create config.yml
		saveDefaultConfig();
		
		if(!hookFabledSkyBlock()) {
			getServer().getConsoleSender().sendMessage("[" + ChatColor.GRAY + "FabledSkyBlock-RezAjax" + ChatColor.GRAY + "] " + ChatColor.RED + "Couldn't find the plugin:" + ChatColor.AQUA + " FabledSkyBlock");
		}
		
		//load database
		this.db = new MysqlMain(this);
		this.db.load();
		
		loadFiles();

		getServer().getPluginManager().registerEvents(new CommandListener(skyblock, this), this);
		getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
		getServer().getPluginManager().registerEvents(new InteractListeners(this), this);
		getServer().getPluginManager().registerEvents(new IslandListener(this), this);
		getServer().getPluginManager().registerEvents(new RaidListener(this), this);
		getServer().getPluginManager().registerEvents(new JoinListeners(this), this);
		getServer().getPluginManager().registerEvents(new MoveListeners(skyblock), this);
		
		//database
		
		
		//load data for fast access
		//
		//
		//
		//Listeners
		
		//unregister listeners
		PlayerJoinEvent.getHandlerList().unregister(skyblock);
		PlayerDeathEvent.getHandlerList().unregister(skyblock);
		PlayerMoveEvent.getHandlerList();
		for(RegisteredListener regis : HandlerList.getRegisteredListeners(skyblock)) {
			if(regis.getListener().getClass() == com.songoda.skyblock.listeners.MoveListeners.class) {
				PlayerMoveEvent.getHandlerList().unregister(regis);
			}
		}
		
		getCommand("raidparty").setTabCompleter(tabCompleter);
		
	
	}
	
	public boolean hookFabledSkyBlock() {
		try {
			for(Plugin plugin : getServer().getPluginManager().getPlugins()) {
				if(plugin instanceof SkyBlock) {
					this.skyblock = (SkyBlock)plugin;
					return true;
				}
			}
		}
		catch(NoClassDefFoundError e) {
			return false;
		}
		return false;
	}
	
	public void onDisable() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			raidMethods.onRaiderQuit(player);
			raidMethods.onSpectatorQuit(player);	
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			
			
			for(CommandInterface command : commandManager.getCommands()) {
				if(label.equalsIgnoreCase(command.getName())) {
					if(skyblock.getIslandManager().getIsland(player) != null) {
						if(command.getArguments().length > 0) {
							switch(args.length) {
								case 1:
									command.perform(player, args);
									break;
								case 2:
								case 3:
									if(command.getSubCommands() != null) {
										for(CommandInterface subcommands : command.getSubCommands()) {
											for(int i = 0; i < args.length; i++) {
												if(subcommands.getName().equalsIgnoreCase(args[i])) {
													command.perform(player, args);
												}
											}
										}	
									}
									else {
										command.perform(player, args);
									}
									break;
								default:
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', language.getString("Command.Execute.NotExist.Message")));
									break;
							}
						}
						else {
							command.perform(player, args);
						}
						
					}
					else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', skyblock.getLanguage().getString("Command.Island.Bans.Owner.Message")));
					}
				}
			}
		}
		else {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', language.getString("Command.Execute.ByConsole.Message")));
		}
		return true;
	}
	
    //ResurrectAjax getters
	public SkyBlock getSkyBlock() {
		return skyblock;
	}
	
	public FileManager getFileManager() {
        return fileManager;
    }
	
	public PlayerDataManager getPlayerDataManager() {
		return playerDataManager;
	}
	
    public Database getRDatabase() {
		  return this.db;
	  }
    
    public FastDataAccess getFastDataAccess() {
    	return fdb;
    }
    
    public FileConfiguration getLanguage() {
    	return language;
    }
    
    public GuiManager getGuiManager() {
    	return guiManager;
    }
    
    public FileConfiguration getConfiguration() {
        return config;
    }
    
    public ItemStorage getStorage() {
    	return storage;
    }
 
    
    public RaidManager getRaidManager() {
    	return raidManager;
    }
    
    
    public FileConfiguration getGuiConfig() {
    	return guiConfig;
    }

    
    public HashMap<UUID, Location> getIslandPositions() {
    	return fdb.getSpawnPositions();
    }
    
    public RaidMethods getRaidMethods() {
    	return raidMethods;
    }
    
    public Location getSpawnLocation() {
    	Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "locations.yml"));
    	return skyblock.getFileManager().getLocation(config, "Location.Spawn", true);
    }
    
    public CommandManager getCommandManager() {
    	return commandManager;
    }
    //ResurrectAjax getters
	
	
	
	public void loadFiles() {
		INSTANCE = this;
		
        fdb = new FastDataAccess(db);
        fdb.putAllSpawnZones();
        fdb.putAllStructures();
        
        fileManager = new FileManager(this);
        config = this.getFileManager().getConfig(new File(this.getDataFolder(), "config.yml")).getFileConfiguration();
        language = this.getFileManager().getConfig(new File(this.getDataFolder(), "language.yml")).getFileConfiguration();
        guiConfig = this.getFileManager().getConfig(new File(this.getDataFolder(), "gui.yml")).getFileConfiguration();
        
        playerDataManager = new PlayerDataManager(skyblock);
        
        storage = new ItemStorage(this);
        
        raidManager = new RaidManager(this);
        
        raidMethods = new RaidMethods(this);
        
        commandManager = new CommandManager(this);
        
        tabCompleter = new TabCompletion(this);
        
        guiManager = new GuiManager(this);
	}

}
