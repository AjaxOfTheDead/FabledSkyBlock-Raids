package me.ResurrectAjax.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.listeners.MoveListeners;

import me.ResurrectAjax.Commands.Managers.FileManager;
import me.ResurrectAjax.Commands.Raid.RaidBar;
import me.ResurrectAjax.Commands.Raid.RaidCommand;
import me.ResurrectAjax.Commands.Raid.RaidMethods;
import me.ResurrectAjax.Listeners.BlockListeners;
import me.ResurrectAjax.Listeners.CommandListener;
import me.ResurrectAjax.Listeners.InteractListeners;
import me.ResurrectAjax.Listeners.IslandListener;
import me.ResurrectAjax.Listeners.JoinListeners;
import me.ResurrectAjax.Listeners.RaidListener;
import me.ResurrectAjax.Mysql.Database;
import me.ResurrectAjax.Mysql.FastDataAccess;
import me.ResurrectAjax.Mysql.MysqlMain;
import me.ResurrectAjax.Playerdata.PlayerDataManager;
import me.ResurrectAjax.Raid.RaidParty;
import me.ResurrectAjax.Raid.ItemStorage.ItemStorage;

public class Main extends JavaPlugin{
	private static Main INSTANCE;
	
	private Database db;
	private SkyBlock skyblock;
	private FastDataAccess fdb;
	
	private PlayerDataManager playerDataManager;
	private FileManager fileManager;
	
	private FileConfiguration language;
	private FileConfiguration config;
	
	private ItemStorage storage;
	
	private HashMap<UUID, RaidBar> raidBars = new HashMap<UUID, RaidBar>();
	private HashMap<UUID, RaidParty> raidParty = new HashMap<UUID, RaidParty>();
	
	private RaidMethods raidmethods;
	
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
		
		//database
		
		
		//load data for fast access
		//
		//
		//
		//Listeners
		
		//unregister listeners
		PlayerJoinEvent.getHandlerList().unregister(skyblock);
		
	
	}
	
	public void onDisable() {
		for(UUID uuid : storage.getItemStorage().keySet()) {
			raidmethods.exitRaidSpectator(Bukkit.getPlayer(uuid));	
		}
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
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			
			if(label.equalsIgnoreCase("raid")) {
				if(skyblock.getIslandManager().getIsland(player) != null) {
					new RaidCommand(this, player);
				}
				else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', skyblock.getLanguage().getString("Command.Island.Bans.Owner.Message")));
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
    
    public FileConfiguration getConfiguration() {
        return config;
    }
    
    public ItemStorage getStorage() {
    	return storage;
    }
    
    public HashMap<UUID, RaidBar> getBossBar() {
    	return raidBars;
    }
    
    public HashMap<UUID, RaidParty> getRaidParties() {
    	return raidParty;
    }
    
    public RaidMethods getRaidMethods() {
    	return raidmethods;
    }
    
    public void addRaidBar(UUID uuid, RaidBar bar) {
    	raidBars.put(uuid, bar);
    }
    
    public void addRaidParty(UUID uuid, RaidParty party) {
    	raidParty.put(uuid, party);
    }
    
    public HashMap<UUID, Location> getIslandPositions() {
    	return fdb.getSpawnPositions();
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
        
        playerDataManager = new PlayerDataManager(skyblock);
        
        storage = new ItemStorage(this);
        
        raidmethods = new RaidMethods(this);
        
	}

}
