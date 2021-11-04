package me.ResurrectAjax.Utils.Structure;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.Gson;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.utils.structure.Location;
import com.songoda.skyblock.utils.structure.SelectionLocation;
import com.songoda.skyblock.utils.structure.Storage;
import com.songoda.skyblock.utils.world.block.BlockData;
import com.songoda.skyblock.utils.world.block.BlockUtil;
import com.songoda.skyblock.utils.world.entity.EntityData;
import com.songoda.skyblock.utils.world.entity.EntityUtil;

import me.ResurrectAjax.Main.Main;

public final class StructureUtil {
	
    public static void saveStructure(File configFile, org.bukkit.Location originLocation, org.bukkit.Location[] positions) throws Exception {
        if (!configFile.exists()) {
            configFile.createNewFile();
        }
        
        LinkedHashMap<Block, Location> blocks = SelectionLocation.getBlocks(originLocation, positions[0], positions[1]);
        LinkedHashMap<Entity, Location> entities = SelectionLocation.getEntities(originLocation, positions[0], positions[1]);

        List<BlockData> blockData = new ArrayList<>();
        List<EntityData> entityData = new ArrayList<>();

        String originBlockLocation = "";

        for (Block block : blocks.keySet()) {
            Location location = blocks.get(block);
            CompatibleMaterial material = CompatibleMaterial.getMaterial(block);

            if (location.isOriginLocation()) {
                originBlockLocation = location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + positions[0].getWorld().getName();
                
                if (material == CompatibleMaterial.AIR) {
                    blockData.add(BlockUtil.convertBlockToBlockData(block, location.getX(), location.getY(), location.getZ()));
                }
            }

            if (material == CompatibleMaterial.AIR) {
                continue;
            }

            blockData.add(BlockUtil.convertBlockToBlockData(block, location.getX(), location.getY(), location.getZ()));
        }

        for (Entity entityList : entities.keySet()) {
            if (entityList.getType() == EntityType.PLAYER) {
                continue;
            }

            Location location = entities.get(entityList);
            entityData.add(EntityUtil.convertEntityToEntityData(entityList, location.getX(), location.getY(), location.getZ()));
        }

        if (!originBlockLocation.isEmpty()) {
            originBlockLocation = originBlockLocation + ":" + originLocation.getYaw() + ":" + originLocation.getPitch();
        }
        
        HashMap<String, Integer> spawnZone = getRelativePosition(positions, originLocation);
        
        originBlockLocation += ":" + spawnZone.get("pos1X") + ":" + spawnZone.get("pos1Z") + ":" + spawnZone.get("pos2X") + ":" + spawnZone.get("pos2Z");

        String JSONString = new Gson().toJson(new Storage(new Gson().toJson(blockData), new Gson().toJson(entityData), originBlockLocation, System.currentTimeMillis(), Integer.parseInt(ServerVersion.getVersionReleaseNumber())), Storage.class);

        FileOutputStream fileOutputStream = new FileOutputStream(configFile, false);
        fileOutputStream.write(Base64.getEncoder().encode(JSONString.getBytes(StandardCharsets.UTF_8)));
        fileOutputStream.flush();
        fileOutputStream.close();
        
    }
    
    public static HashMap<String, Integer> getRelativePosition(org.bukkit.Location[] positions, org.bukkit.Location originLocation) {
    	HashMap<String, Integer> relPositions = new HashMap<>();
    	
    	int temp = 0;
    	
    	temp = originLocation.getBlockX() - positions[0].getBlockX();
    	if(originLocation.getBlockX() > positions[0].getBlockX()) {
    		relPositions.put("pos1X", -Math.abs(temp));
    	}
    	else {
    		relPositions.put("pos1X", Math.abs(temp));
    	}
    	temp = originLocation.getBlockX() - positions[1].getBlockX();
    	if(originLocation.getBlockX() > positions[1].getBlockX()) {
    		relPositions.put("pos2X", -Math.abs(temp));
    	}
    	else {
    		relPositions.put("pos2X", Math.abs(temp));
    	}
    	
    	
    	temp = originLocation.getBlockZ() - positions[0].getBlockZ();
    	if(originLocation.getBlockZ() > positions[0].getBlockZ()) {
    		relPositions.put("pos1Z", -Math.abs(temp));
    	}
    	else {
    		relPositions.put("pos1Z", Math.abs(temp));
    	}
    	temp = originLocation.getBlockZ() - positions[1].getBlockZ();
    	if(originLocation.getBlockZ() > positions[1].getBlockZ()) {
    		relPositions.put("pos2Z", -Math.abs(temp));
    	}
    	else {
    		relPositions.put("pos2Z", Math.abs(temp));
    	}
    	
    	return relPositions;
    	
    }
    
    public static ItemStack getSpawnZoneTool() throws Exception {
        Main plugin = Main.getInstance();

        FileConfiguration configLoad = plugin.getLanguage();

        ItemStack is = new ItemStack(Material.valueOf(plugin.getConfiguration().getString("Island.Admin.Structure.SpawnZoneSelector")));
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Island.Structure.SpawnZoneTool.Item.Displayname")));

        List<String> itemLore = new ArrayList<>();

        for (String itemLoreList : configLoad.getStringList("Island.Structure.SpawnZoneTool.Item.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
        }

        im.setLore(itemLore);
        is.setItemMeta(im);

        return is;
    }

}
