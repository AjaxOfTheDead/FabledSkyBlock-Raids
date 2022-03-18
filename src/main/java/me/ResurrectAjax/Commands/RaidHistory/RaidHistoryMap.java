package me.ResurrectAjax.Commands.RaidHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Mysql.Database;

/**
 * Class for loading in raid history data from the database
 * @author ResurrectAjax
 * */
public class RaidHistoryMap {
	private Database db;
	private Main main;
	
	private List<String[]> parties = new ArrayList<String[]>();
	private HashMap<Integer, String[]> raids = new HashMap<Integer, String[]>();
	private HashMap<Integer, String[]> blocks = new HashMap<Integer, String[]>();
	private HashMap<Integer, String[]> containerItems = new HashMap<Integer, String[]>();
	
	/**
	 * Constructor of RaidHistoryMap class<br>
	 * @param main instance of the {@link me.ResurrectAjax.Main.Main} class
	 * */
	public RaidHistoryMap(Main main) {
		this.main = main;
		db = main.getRDatabase();
		
		deleteRaidsOlderThan();
		parties = db.getParties();
		raids = db.getRaids();
		blocks = db.getBlocks();
		containerItems = db.getItems();	
	}
	
	/**
	 * Adds party values to the list of parties
	 * @param party array containing the partyID, playerUUID and leaderUUID
	 * */
	public void addParties(String[] party) {
		this.parties.add(party);
	}

	/**
	 * Adds raid values to the hashmap of raids
	 * @param raidID identification number of the added raid
	 * @param raid array containing the partyID, Date and islandUUID of the raided island
	 * */
	public void addRaid(Integer raidID, String[] raid) {
		this.raids.put(raidID, raid);
	}

	/**
	 * Adds block values to the hashmap of blocks
	 * @param blockID identification number of the added block
	 * @param block array containing the raidID, type, amount and isContainer boolean
	 * */
	public void addBlock(Integer blockID, String[] block) {
		this.blocks.put(blockID, block);
	}

	/**
	 * Adds item values to the hashmap of items
	 * @param itemID identification number of the added item
	 * @param block array containing the blockID, itemType, name, lore, enchantments and amount
	 * */
	public void addContainerItems(Integer itemID, String[] containerItem) {
		this.containerItems.put(itemID, containerItem);
	}

	/**
	 * Gets the hashmap of a party by raidID
	 * @param ID identification number of the raid
	 * @return hashmap containing the leader and members of the party
	 * */
	public HashMap<UUID, List<UUID>> getPartyByID(int ID) {
		HashMap<UUID, List<UUID>> partyList = new HashMap<UUID, List<UUID>>();
		List<UUID> members = new ArrayList<UUID>();
		for(String[] party : parties) {
			if(Integer.parseInt(party[0]) != ID) continue;
			members.add(UUID.fromString(party[1]));
			partyList.put(UUID.fromString(party[2]), members);
			
		}
		return partyList;
	}
	
	/**
	 * Gets the array of values of a raid by identification number
	 * @param ID identification number of the raid
	 * @return array containing the partyID, Date and islandUUID of the raided island
	 * */
	public String[] getRaidByID(int ID) {
		return raids.get(ID);
	}
	
	/**
	 * Gets the array of values of a block by the blockID
	 * @param ID identification number of the block
	 * @return array containing the raidID, type, amount and isContainer boolean
	 * */
	public String[] getBlocksByID(int ID) {
		return blocks.get(ID);
	}
	
	/**
	 * Gets the array of values of an item by the itemID
	 * @param ID identification number of the item
	 * @return array containing the raidID, type, amount and isContainer boolean
	 * */
	public String[] getContainerItemsByID(int ID) {
		return containerItems.get(ID);
	}
	
	public LinkedHashMap<Integer, ItemStack> getBlocksByRaidID(int raidID) {
		HashMap<Integer, ItemStack> blockHash = new HashMap<Integer, ItemStack>();
		for(int key : blocks.keySet()) {
			if(Integer.parseInt(blocks.get(key)[0]) != raidID) continue;
			
			ItemStack block = new ItemStack(Material.getMaterial(blocks.get(key)[1]), Integer.parseInt(blocks.get(key)[2]));
			blockHash.put(key, block);
		}
		List<Entry<Integer, ItemStack>> list = new LinkedList<Entry<Integer, ItemStack>>(blockHash.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<Integer, ItemStack>>()
        {

			@Override
			public int compare(Entry<Integer, ItemStack> o1, Entry<Integer, ItemStack> o2) {
				return o1.getValue().getI18NDisplayName().compareTo(o2.getValue().getI18NDisplayName());
			}
        });
        
        LinkedHashMap<Integer, ItemStack> sortedMap = new LinkedHashMap<Integer, ItemStack>();
        for(Entry<Integer, ItemStack> entry : list) {
        	sortedMap.put(entry.getKey(), entry.getValue());
        }
		
		return sortedMap;
	}
	
	public LinkedHashMap<Integer, ItemStack> getItemsByContainerBlockID(int blockID) {
		HashMap<Integer, ItemStack> itemHash = new HashMap<Integer, ItemStack>();
		for(int key : containerItems.keySet()) {
			if(Integer.parseInt(containerItems.get(key)[0]) != blockID) continue;
			ItemStack item = new ItemStack(Material.getMaterial(containerItems.get(key)[1]), Integer.parseInt(containerItems.get(key)[5]));
			
			ItemMeta meta = item.getItemMeta();
			
			if(containerItems.get(key)[2] != null) meta.setDisplayName(containerItems.get(key)[2]);
			
			if(containerItems.get(key)[3] != null) {
				List<String> lore = new ArrayList<String>();
				for(String line : containerItems.get(key)[3].split(";")) {
					lore.add(line);
				}
				meta.setLore(lore);	
			}
			
			if(containerItems.get(key)[4] != null) {
				for(String enchantString : containerItems.get(key)[4].split(";")) {
					String namedKey = enchantString.split("=")[0];
					int level = Integer.parseInt(enchantString.split("=")[1]);
					
					if(Enchantment.getByKey(NamespacedKey.minecraft(namedKey)) == null) continue;
					Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(namedKey));
					meta.addEnchant(enchant, level, false);
				}	
			}
			
			item.setItemMeta(meta);
			itemHash.put(key, item);
		}
		
		List<Entry<Integer, ItemStack>> list = new LinkedList<Entry<Integer, ItemStack>>(itemHash.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<Integer, ItemStack>>()
        {

			@Override
			public int compare(Entry<Integer, ItemStack> o1, Entry<Integer, ItemStack> o2) {
				return o1.getValue().getI18NDisplayName().compareTo(o2.getValue().getI18NDisplayName());
			}
        });
        
        LinkedHashMap<Integer, ItemStack> sortedMap = new LinkedHashMap<Integer, ItemStack>();
        for(Entry<Integer, ItemStack> entry : list) {
        	sortedMap.put(entry.getKey(), entry.getValue());
        }
		
		return sortedMap;
	}
	
	public Map<Integer, String[]> getRaidsByIslandUUID(UUID islandUUID) {
		HashMap<Integer, String[]> raidMap = new HashMap<Integer, String[]>();
		for(int raidID : raids.keySet()) {
			if(UUID.fromString(getRaidByID(raidID)[2]).equals(islandUUID)) raidMap.put(raidID, getRaidByID(raidID));
		}
		List<Entry<Integer, String[]>> list = new LinkedList<Entry<Integer, String[]>>(raidMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<Integer, String[]>>()
        {

			@Override
			public int compare(Entry<Integer, String[]> o1, Entry<Integer, String[]> o2) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				try {
					return sdf.parse(o2.getValue()[1]).compareTo(sdf.parse(o1.getValue()[1]));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return 0;
			}
        });

        
        Map<Integer, String[]> sortedMap = new LinkedHashMap<Integer, String[]>();
        for (Entry<Integer, String[]> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
	}
	
	public HashMap<UUID, List<UUID>> getPartyByRaidID(int raidID) {
		int partyID = Integer.parseInt(getRaidByID(raidID)[0]);
		return getPartyByID(partyID);
	}
	
	private boolean deleteRaidsOlderThan() {
		FileConfiguration configLoad = main.getConfiguration();
		if(!configLoad.getBoolean("Database.RemoveData.Enabled")) return false;
		
		String time = configLoad.getString("Database.RemoveData.RemoveAfter");
		
		HashMap<String, Integer> daysMonths = new HashMap<String, Integer>();
		String[] numbers = time.split("\\D");
		String[] letters = time.replaceAll("\\d", "").split("");
		for(int i = 0; i < letters.length; i++) {
			daysMonths.put(letters[i], Integer.parseInt(numbers[i]));
		}
		
		Integer days = daysMonths.get("d"), 
				months = daysMonths.get("M"),
				weeks = daysMonths.get("w");
		
		if(days == null && months == null && weeks == null) throw new IllegalArgumentException("Please use the right time formats(d|M): config.yml");
		else {
			LocalDate date = LocalDate.now();
			if(days != null) date = date.minusDays(days);
			if(weeks != null) date = date.minusWeeks(weeks);
			if(months != null) date = date.minusMonths(months);
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"), format2 = new SimpleDateFormat("HH:mm:ss");
			String dateStr = format.format(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())) + " " + format2.format(Calendar.getInstance().getTime());
			if(db.deleteRaidOlderThan(dateStr)) return true;
		}
		return false;
	}
}
