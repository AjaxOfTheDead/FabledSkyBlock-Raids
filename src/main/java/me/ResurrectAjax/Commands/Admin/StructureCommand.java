package me.ResurrectAjax.Commands.Admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.ChatComponent;
import com.songoda.skyblock.utils.Compression;
import com.songoda.skyblock.utils.world.LocationUtil;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.Database;
import me.ResurrectAjax.Mysql.FastDataAccess;
import me.ResurrectAjax.Playerdata.PlayerData;
import me.ResurrectAjax.Utils.Structure.StructureUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

/**
 * Class for creating structures (adjusted copy of songoda StructureCommand)
 * 
 * @author ResurrectAjax
 * */
public class StructureCommand extends SubCommand {
	private Main main;
	
	/**
	 * Constructor of the class
	 * @param player player who sent the command
	 * @param args arguments of the command
	 * @param main instance of the {@link me.ResurrectAjax.Main.Main} class
	 * */
	public StructureCommand(Player player, String[] args, Main main) {
		this.main = main;
		
		onCommandByPlayer(player, args);
	}
	
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = plugin.getMessageManager();
        SoundManager soundManager = plugin.getSoundManager();
        
        Database db = main.getRDatabase();
        FastDataAccess fdb = main.getFastDataAccess();

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
        me.ResurrectAjax.Commands.Managers.FileManager.Config configSpawn = main.getFileManager().getConfig(new File(main.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        FileConfiguration configLoadSpawn = configSpawn.getFileConfiguration();

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            for (String helpLines : configLoad.getStringList("Command.Island.Help.Lines")) {
                if (helpLines.contains("%type")) {
                    helpLines = helpLines.replace("%type", "Structure");
                }

                if (helpLines.contains("%commands")) {
                    String[] sections = helpLines.split("%commands");
                    String prefix = "", suffix = "";

                    if (sections.length >= 1) {
                        prefix = ChatColor.translateAlternateColorCodes('&', sections[0]);
                    }

                    if (sections.length == 2) {
                        suffix = ChatColor.translateAlternateColorCodes('&', sections[1]);
                    }

                    player.spigot()
                            .sendMessage(
                                    new ChatComponent(
                                            prefix.replace("%info",
                                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                            "Command.Island.Admin.Structure.Tool.Info.Message")))
                                                    + "/island admin structure tool"
                                                    + suffix.replace("%info", ChatColor.translateAlternateColorCodes(
                                                    '&',
                                                    configLoad.getString(
                                                            "Command.Island.Admin.Structure.Tool.Info.Message"))),
                                            false, null, null,
                                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                            "Command.Island.Admin.Structure.Tool.Info.Message")))
                                                    .create())).getTextComponent());
                    player.spigot()
                            .sendMessage(
                                    new ChatComponent(
                                            prefix.replace("%info",
                                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                            "Command.Island.Admin.Structure.Save.Info.Message")))
                                                    + "/island admin structure save"
                                                    + suffix.replace("%info", ChatColor.translateAlternateColorCodes(
                                                    '&',
                                                    configLoad.getString(
                                                            "Command.Island.Admin.Structure.Save.Info.Message"))),
                                            false, null, null,
                                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                            "Command.Island.Admin.Structure.Save.Info.Message")))
                                                    .create())).getTextComponent());
                    player.spigot()
                        .sendMessage(
                            new ChatComponent(
                                prefix.replace("%info",
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                        "Command.Island.Admin.Structure.Convert.Info.Message")))
                                    + "/island admin structure convert"
                                    + suffix.replace("%info", ChatColor.translateAlternateColorCodes(
                                    '&',
                                    configLoad.getString(
                                        "Command.Island.Admin.Structure.Convert.Info.Message"))),
                                false, null, null,
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                        "Command.Island.Admin.Structure.Convert.Info.Message")))
                                    .create())).getTextComponent());
                } else {
                    messageManager.sendMessage(player, helpLines);
                }
            }

            soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);
        } else {
            if (args[0].equalsIgnoreCase("tool")) {
                try {
                    ItemStack is = com.songoda.skyblock.utils.structure.StructureUtil.getTool(), szt = StructureUtil.getSpawnZoneTool();

                    if(!player.getInventory().contains(szt) || !player.getInventory().contains(is)) {
                    	if(!player.getInventory().contains(is)) {
                    		player.getInventory().addItem(is);
                    		messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Admin.Structure.Tool.Equiped.Message"));
                    	}
                    	if(!player.getInventory().contains(szt)) {
                    		player.getInventory().addItem(szt);
                    		messageManager.sendMessage(player,
                                    configLoadSpawn.getString("Command.Island.Admin.Structure.SpawnZoneTool.Equiped.Message"));
                    	}
                    	soundManager.playSound(player, CompatibleSound.ENTITY_CHICKEN_EGG.getSound(), 1.0F, 1.0F);
                    }
                    else {
                		messageManager.sendMessage(player, configLoadSpawn
                                .getString("Command.Island.Admin.Structure.SpawnZoneTool.Inventory.Message"));
                    	
                    	soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    }

                } catch (Exception e) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Structure.Tool.Material.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getLogger().log(Level.WARNING,
                            "SkyBlock | Error: The defined material in the configuration file for the Structure selection tool could not be found.");
                    
                    e.printStackTrace();
                }

                return;
            } else if (args[0].equalsIgnoreCase("save")) {
                if (args.length == 2) {
                    com.songoda.skyblock.playerdata.PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

                    Location position1Location = playerData.getArea().getPosition(1);
                    Location position2Location = playerData.getArea().getPosition(2);
                    
                    
                    PlayerData playerDataSpawn = main.getPlayerDataManager().getPlayerData(player);
                    Location spawnPosition1Location = playerDataSpawn.getSpawnZoneArea().getPosition(1);
                    Location spawnPosition2Location = playerDataSpawn.getSpawnZoneArea().getPosition(2);

                    if (position1Location == null && position2Location == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Admin.Structure.Save.Position.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if ((position1Location == null && position2Location != null)
                            || (position1Location != null && position2Location == null)) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Admin.Structure.Save.Complete.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if (!position1Location.getWorld().getName()
                            .equals(position2Location.getWorld().getName())) {
                        messageManager.sendMessage(player, configLoad
                                .getString("Command.Island.Admin.Structure.Save.Selection.World.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if (!player.getWorld().getName().equals(position1Location.getWorld().getName())) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Admin.Structure.Save.Player.World.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if (!LocationUtil.isInsideArea(player.getLocation(), position1Location,
                            position2Location)) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Admin.Structure.Save.Player.Area.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                        
                    }
                    
                    if (spawnPosition1Location == null && spawnPosition2Location == null) {
                        messageManager.sendMessage(player,
                                configLoadSpawn.getString("Command.Island.Admin.Structure.Save.SpawnPosition.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if ((spawnPosition1Location == null && spawnPosition2Location != null)
                            || (spawnPosition1Location != null && spawnPosition2Location == null)) {
                        messageManager.sendMessage(player,
                                configLoadSpawn.getString("Command.Island.Admin.Structure.Save.SpawnComplete.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if (!spawnPosition1Location.getWorld().getName()
                            .equals(spawnPosition2Location.getWorld().getName())) {
                        messageManager.sendMessage(player, configLoad
                                .getString("Command.Island.Admin.Structure.Save.Selection.World.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if (!player.getWorld().getName().equals(spawnPosition1Location.getWorld().getName())) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Admin.Structure.Save.Player.World.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F); 
                    } else if(!position1Location.getWorld().getName().equals(spawnPosition1Location.getWorld().getName())) {
                    	messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Admin.Structure.Save.Player.World.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    }
                    
                    if(position1Location != null && position2Location != null && position1Location.getWorld().getName().equals(position2Location.getWorld().getName()) &&
                    		player.getWorld().getName().equals(position2Location.getWorld().getName()) && LocationUtil.isInsideArea(player.getLocation(), position1Location, position2Location) &&
                    		spawnPosition1Location != null && spawnPosition2Location != null && spawnPosition1Location.getWorld().getName().equals(spawnPosition2Location.getWorld().getName()) &&
                    		spawnPosition1Location.getWorld().getName().equals(position1Location.getWorld().getName())) {
                    	try {
                            File configFile = new File(
                                    plugin.getDataFolder().toString() + "/structures/" + args[1] + ".structure");
                            com.songoda.skyblock.utils.structure.StructureUtil.saveStructure(configFile, player.getLocation(),
                            		com.songoda.skyblock.utils.structure.StructureUtil.getFixedLocations(position1Location, position2Location));
                            
                            HashMap<String, Integer> relPositions = new HashMap<>();
                            relPositions = StructureUtil.getRelativePosition(new Location[] {spawnPosition1Location, spawnPosition2Location}, player.getLocation());
                            db.setStructure(args[1], relPositions.get("pos1X") + ":" + relPositions.get("pos1Z"), relPositions.get("pos2X") + ":" + relPositions.get("pos2Z"));
                            fdb.putStructure(args[1], relPositions.get("pos1X"), relPositions.get("pos1Z"), relPositions.get("pos2X"), relPositions.get("pos2Z"));

                            messageManager.sendMessage(player,
                                    configLoad
                                            .getString(
                                                    "Command.Island.Admin.Structure.Save.Saved.Successful.Message")
                                            .replace("%name", args[1]));
                            soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_YES.getSound(), 1.0F, 1.0F);
                            return;
                        } catch (Exception e) {
                            messageManager.sendMessage(player, configLoad
                                    .getString("Command.Island.Admin.Structure.Save.Saved.Failed.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            e.printStackTrace();
                        }
                    }
                } else {
                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Admin.Structure.Save.Invalid.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                }


            } else if (args[0].equalsIgnoreCase("convert")) {
                if (args.length == 2) {
                    File structureFile = new File(new File(plugin.getDataFolder().toString() + "/structures"), args[1]);
                    if (!structureFile.exists()) {
                        messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Admin.Structure.Convert.Invalid.Message")
                                .replace("%name", args[1]));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                        return;
                    }
                    byte[] content = new byte[(int) structureFile.length()];
                    try {
                        FileInputStream fileInputStream = new FileInputStream(structureFile);
                        fileInputStream.read(content);
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(structureFile, false);
                        fileOutputStream.write(Base64.getEncoder().encode(Compression.decompress(content).getBytes()));
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Admin.Structure.Convert.Converted.Failed.Message")
                                .replace("%name", args[1]));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                        e.printStackTrace();
                    }

                    messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Admin.Structure.Convert.Converted.Successful.Message")
                            .replace("%name", args[1]));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_YES.getSound(), 1.0F, 1.0F);

                } else {
                    messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Admin.Structure.Convert.Invalid.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                }

                return;
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Argument.Unrecognised.Message"));
                soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "structure";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.Structure.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getArguments() {
        return new String[]{"tool", "save", "convert"};
    }

}
