package me.ResurrectAjax.Mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import me.ResurrectAjax.Commands.RaidHistory.RaidHistoryMap;
import me.ResurrectAjax.Main.Main;
import me.ResurrectAjax.Raid.RaidParty;


public abstract class Database {
    Main plugin;
    Connection connection;
    // The name of the table we created back in SQLite class.
    public String ctable = "Chunks";
    public int tokens = 0;
    public Database(Main instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM SpawnZones");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
   
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    //get all the players with an island and their spawnzones
    public List<List<String>> getSpawnZones() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        List<List<String>> zones = new ArrayList<List<String>>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM SpawnZones;");
   
            rs = ps.executeQuery();
            while(rs.next()){
                List<String> zone = new ArrayList<String>();
                
                zone.add(rs.getString("uuid"));
                zone.add(rs.getString("pos1"));
                zone.add(rs.getString("pos2"));
                zone.add(rs.getString("spawnpos"));
                zone.add(rs.getString("world"));
                
                zones.add(zone);
            }
        	return zones;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return zones;
    }
    
    public List<List<String>> getStructures() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        List<List<String>> zones = new ArrayList<List<String>>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Structures;");
   
            rs = ps.executeQuery();
            while(rs.next()){
                List<String> zone = new ArrayList<String>();
                
                zone.add(rs.getString("name"));
                zone.add(rs.getString("pos1"));
                zone.add(rs.getString("pos2"));
                
                zones.add(zone);
            }
        	return zones;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return zones;
    }
    
    public String deleteValues(String table, String query, String string) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM " + table + " WHERE " + query + " = '"+string+"';");
   
            try {
                ps.executeUpdate();
            }
            catch(Exception e) {
            	
            }
            return "";
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    // Exact same method here, Except as mentioned above i am looking for total!

// Now we need methods to save things to the database
    public void setSpawnZones(String uuid, String pos1, String pos2, String spawnpos, String world) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO SpawnZones (uuid,pos1,pos2,spawnpos,world) VALUES(?,?,?,?,?)");
            ps.setString(1, uuid); 
            
            
                                                                                               
            ps.setString(2, pos1); 
            
            
            
            ps.setString(3, pos2);

            
            
            ps.setString(4, spawnpos);
            
            
            
            ps.setString(5, world);
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;      
    }
    
    public void updateOwnership(String owner, String newOwner) {
    	Connection conn = null;
    	PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            
            
            ps = conn.prepareStatement("UPDATE SpawnZones SET uuid = ? WHERE uuid = ?");// IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            
            ps.setString(1, owner);
            ps.setString(2, newOwner);
            ps.executeUpdate();
            
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        
        try {
            conn = getSQLConnection();
            
            
            ps = conn.prepareStatement("UPDATE Islands SET uuid = ? WHERE uuid = ?");// IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            
            ps.setString(1, owner);
            ps.setString(2, newOwner);
            ps.executeUpdate();
            
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;   
        
        
        
    }

    public void setStructure(String name, String pos1, String pos2) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO Structures (name,pos1,pos2) VALUES(?,?,?)");
            ps.setString(1, name); 
            
            
                                                                                               
            ps.setString(2, pos1); 
            
            
            
            ps.setString(3, pos2);
            
            
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;      
    }
    
    public void setIsland(String uuid, double raidSense) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO Islands (uuid,time,raidsense) VALUES(?,?,?)");
            ps.setString(1, uuid); 
            
            ps.setInt(2, 0); 
                                                                                               
            ps.setDouble(3, raidSense); 
            
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;      
    }

    public Integer insertRaidParty(RaidParty party) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            
            String stmt = "insert into Parties (partyID, playerUUID, leaderUUID) values";
            String selectLastID = "(SELECT COALESCE(MAX(partyID)+1, 1) FROM Parties)";
            
            for(UUID playerUUID : party.getMembers()) {
            	if(playerUUID.equals(party.getMembers().get(party.getMembers().size()-1))) {
                	stmt += "(" + selectLastID + ", '" + playerUUID + "', '" + party.getLeader() +"')";	
            	}
            	else {
            		stmt += "(" + selectLastID + ", '" + playerUUID + "', '" + party.getLeader() +"'),";
            	}
            }
            stmt += ";";
            
            ps = conn.prepareStatement(stmt);
            ps.executeUpdate();
            ps.close();
            
            int[] raidPartyID = insertRaid(party.getLeader());
            ps = conn.prepareStatement("select * from Parties "
            		+ "where partyID = ?");
            ps.setInt(1, raidPartyID[1]);
            rs = ps.executeQuery();
            
        	RaidHistoryMap map = Main.getInstance().getRaidHistoryMap();
            while(rs.next()) {
            	map.addParties(new String[] {rs.getInt(1) + "", rs.getString(2), rs.getString(3)});
            }
            
            return raidPartyID[0];
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;      
    }
    
    public int[] insertRaid(UUID leaderUUID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	int raidID = 0;
            conn = getSQLConnection();
            
            ps = conn.prepareStatement("insert into Raids (partyID, dateTimeStart, islandUUID) values(("
            		+ "(SELECT partyID FROM Parties "
            		+ "where partyID in ("
            		+ "select max(partyID) from Parties"
            		+ ") "
            		+ "group by partyID)"
            		+ "),datetime('now'),?);", Statement.RETURN_GENERATED_KEYS);
            
            Location location = plugin.getRaidMethods().getIslandSpectator().get(leaderUUID);
            ps.setString(1, plugin.getRaidMethods().getIslandUUIDByLocation(location).toString());
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
            	raidID = rs.getInt(1);
            }
            ps.close();
            
            ps = conn.prepareStatement("select * from Raids "
            		+ "where raidID = ?");
            ps.setInt(1, raidID);
            rs = ps.executeQuery();
            if(rs.next()) {
            	RaidHistoryMap map = Main.getInstance().getRaidHistoryMap();
            	map.addRaid(rs.getInt(1), new String[] {rs.getInt(2) + "", rs.getString(3), rs.getString(4)});
            	
            	return new int[] {rs.getInt(1), rs.getInt(2)};
            }
            
            
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;    
    }
    
    public void insertBlocks(List<HashMap<ItemStack, Boolean>> blocks, int raidID) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            
            String stmt = "insert into Blocks (raidID, type, amount, isContainer) values";
            int count = 0;
            for(HashMap<ItemStack, Boolean> block : blocks) {
            	for(ItemStack item : block.keySet()) {
            		int boolVal = 0;
            		if(block.get(item)) {
            			boolVal = 1;
            		}
                	if(count == blocks.size()-1) {
                    	stmt += "(" + raidID + ", '" + item.getType() + "', " + item.getAmount() + ", '" + boolVal +"')";	
                	}
                	else {
                		stmt += "(" + raidID + ", '" + item.getType() + "', " + item.getAmount() + ", '" + boolVal +"'),";
                	}	
            	}
            	count++;
            }
            stmt += ";";
            
            ps = conn.prepareStatement(stmt);
            ps.executeUpdate();
            ps.close();
            
            ps = conn.prepareStatement("select * from Blocks "
            		+ "where raidID = ?");
            ps.setInt(1, raidID);
            rs = ps.executeQuery();
            
        	RaidHistoryMap map = Main.getInstance().getRaidHistoryMap();
            while(rs.next()) {
            	map.addBlock(rs.getInt(1), new String[] {rs.getInt(2) + "", rs.getString(3), rs.getInt(4) + "", (rs.getInt(5) > 0) + ""});
            }
            
            
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;   
    }
    
    public void insertItems(LinkedHashMap<Integer, ItemStack[]> items) {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            
            String stmt = "insert into Items (blockID, itemType, name, lore, enchantments, amount) values";
            int count = 0;
            
            List<Integer> IDList = new ArrayList<Integer>();
            for(int blockID : items.keySet()) {
        		boolean hasItem = false;
            	for(ItemStack stack : items.get(blockID)) {
            		if(stack != null) {
            			hasItem = true;
            		}
            	}
            	if(hasItem) {
            		IDList.add(blockID);
            	}
            }
            
            for(int blockID : IDList) {
            	List<ItemStack> itemList = new ArrayList<ItemStack>(Arrays.asList(items.get(blockID)));
            	itemList.removeAll(Collections.singleton(new ItemStack(Material.AIR)));
            	itemList.removeAll(Collections.singleton(null));
            	
            	int itemCount = 0;
                for(ItemStack item : itemList) {
                	if(item != null) {
                    	String lore = null;
                    	if(item.getLore() != null) {
                    		lore = "";
                        	for(String line : item.getLore()) {
                        		if(line.equals(item.getLore().get(item.getLore().size()-1))) {
                        			lore += line;
                        		}
                        		else {
                        			lore += line + ";";
                        		}
                        	}	
                    	}
                    	
                    	String enchantments = null;
                    	int countEnchants = 0;
                    	if(item.getEnchantments().keySet().size() > 0) {
                    		enchantments = "";
                        	for(Enchantment enchantment : item.getEnchantments().keySet()) {
                        		String enchantStr = enchantment.getKey().getKey() + "=" + item.getEnchantmentLevel(enchantment);
                        		
                    			if(countEnchants == item.getEnchantments().size()) {
                    				enchantments += enchantStr;
                    			}
                    			else {
                    				enchantments += enchantStr + ";";
                    			}
                    			countEnchants++;
                        	}	
                    	}
                    	
                    	String name = item.getItemMeta() != null ? item.getItemMeta().getDisplayName() : item.getI18NDisplayName();
                    	
                    	if(itemCount == itemList.size()-1 && count == IDList.size()-1) {
                        	stmt += "(" + blockID + ", '" + item.getType() + "', '" + name + "', '" + lore + "', '" + enchantments + "', " + item.getAmount() +")";	
                    	}
                    	else {
                    		stmt += "(" + blockID + ", '" + item.getType() + "', '" + name + "', '" + lore + "', '" + enchantments + "', " + item.getAmount() +"),";
                    	}
                        itemCount++;
                	}	
                }
                count++;
            }
            stmt += ";";
            
            ps = conn.prepareStatement(stmt);
            
            ps.executeUpdate();
            ps.close();
            
            stmt = "select * from Items "
            		+ "where blockID = ";
            int countSelect = 1;
            for(int blockID : items.keySet()) {	
            	stmt += blockID;
                if(countSelect != items.size()) {
                	stmt += " OR blockID = ";
                }
                countSelect++;
            }
            ps = conn.prepareStatement(stmt);
            rs = ps.executeQuery();
            
        	RaidHistoryMap map = Main.getInstance().getRaidHistoryMap();
            while(rs.next()) {
            	map.addContainerItems(rs.getInt(1), new String[] {rs.getInt(2) + "", rs.getString(3), rs.getString(4), !rs.getString(5).equals("null") ? rs.getString(5) : null, !rs.getString(6).equals("null") ? rs.getString(6) : null, rs.getInt(7) + ""});
            }
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;   
    }
    
    
    
    public Double getRaidSense(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Double raidsense = null;
        
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT raidsense FROM Islands WHERE uuid = ?;");
            ps.setString(0, uuid.toString());
   
            rs = ps.executeQuery();
            rs.next();
            raidsense = rs.getDouble("raidsense");
            
        	return raidsense;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return raidsense;
    }
    
    public HashMap<UUID, Double> getAllRaidSense() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        HashMap<UUID, Double> raidSenses = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT uuid,raidsense FROM Islands;");
   
            rs = ps.executeQuery();
            
        	raidSenses = new HashMap<UUID, Double>();
            while(rs.next()){
                raidSenses.put(UUID.fromString(rs.getString("uuid")), rs.getDouble("raidsense"));
            }
        	return raidSenses;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return raidSenses;
    }
    
    public void updateRaidSense(UUID uuid, Double raidSense) {
    	Connection conn = null;
    	PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            
            
            ps = conn.prepareStatement("UPDATE Islands SET raidsense = ? WHERE uuid = ?");// IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            
            ps.setDouble(1, raidSense);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
            
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;  
        
    }
    
    public Integer getIslandTime(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer time = null;
        
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT time FROM Islands WHERE uuid = ?;");
            ps.setString(0, uuid.toString());
   
            rs = ps.executeQuery();
            rs.next();
            time = rs.getInt("time");
            
        	return time;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return time;
    }
    
    public HashMap<UUID, Integer> getAllIslandTime() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<UUID, Integer> time = null;
        
        try {
        	conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT uuid,raidsense,time FROM Islands;");
   
            rs = ps.executeQuery();
            
        	time = new HashMap<UUID, Integer>();
            while(rs.next()){
                time.put(UUID.fromString(rs.getString("uuid")), rs.getInt("time"));
            }
        	return time;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return time;
    }
    
    public void updateIslandTime(UUID uuid, Integer time) {
    	Connection conn = null;
    	PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            
            
            ps = conn.prepareStatement("UPDATE Islands SET time = ? WHERE uuid = ?");// IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            
            ps.setInt(1, time);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
            
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;  
        
    }
    
    public List<String[]> getParties() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        List<String[]> parties = new ArrayList<String[]>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Parties;");
   
            rs = ps.executeQuery();
            while(rs.next()){
                String[] party = new String[3];
                
                party[0] = rs.getInt("partyID") + "";
                party[1] = rs.getString("playerUUID");
                party[2] = rs.getString("leaderUUID");
                
                parties.add(party);
            }
        	return parties;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return parties;
    }
    
    public HashMap<Integer, String[]> getRaids() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        HashMap<Integer, String[]> raids = new HashMap<Integer, String[]>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Raids "
            		+ "order by dateTimeStart desc;");
   
            rs = ps.executeQuery();
            while(rs.next()){
                String[] raid = new String[3];
                
                raid[0] = rs.getInt("partyID") + "";
                raid[1] = rs.getString("dateTimeStart");
                raid[2] = rs.getString("islandUUID");
                
                raids.put(rs.getInt("raidID"), raid);
            }
        	return raids;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return raids;
    }
    
    public HashMap<Integer, String[]> getBlocks() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        HashMap<Integer, String[]> blocks = new HashMap<Integer, String[]>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Blocks;");
   
            rs = ps.executeQuery();
            while(rs.next()){
                String[] block = new String[4];
                
                block[0] = rs.getInt("raidID") + "";
                block[1] = rs.getString("type");
                block[2] = rs.getInt("amount") + "";
                block[3] = (rs.getInt("isContainer") > 0) + "";
                
                blocks.put(rs.getInt("blockID"), block);
            }
        	return blocks;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return blocks;
    }
    
    public HashMap<Integer, String[]> getItems() {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        HashMap<Integer, String[]> items = new HashMap<Integer, String[]>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM Items;");
   
            rs = ps.executeQuery();
            while(rs.next()){
                String[] item = new String[6];
                
                item[0] = rs.getInt("blockID") + "";
                item[1] = rs.getString("itemType");
                item[2] = !rs.getString("name").equals("null") ? rs.getString("name") : null;
                item[3] = !rs.getString("lore").equals("null") ? rs.getString("lore") : null;
                item[4] = !rs.getString("enchantments").equals("null") ? rs.getString("enchantments") : null;
                item[5] = rs.getInt("amount") + "";
                
                items.put(rs.getInt("itemID"), item);
            }
        	return items;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return items;
    }
    
    public boolean deleteRaidOlderThan(String date) {
        Connection conn = null;
        PreparedStatement ps = null;
        Statement stmt = null;
        try {
        	
            conn = getSQLConnection();
            stmt = conn.createStatement();
            
            stmt.addBatch(
            		  "DELETE FROM Parties WHERE partyID in ("
            		+ "select partyID from Raids "
            		+ "where dateTimeStart < '" + date + "'"
            		+ ");");
            
            stmt.addBatch(
            		  "DELETE FROM Raids "
					+ "where dateTimeStart < '" + date + "'"
					+ ";");

            int[] success = stmt.executeBatch();
            
            if(success[0] > 0 && success[1] > 0) {
            	return true;
            }
            
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;
    }
    
    public void deleteIsland(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM Islands WHERE uuid = ?;");
   
            ps.setString(1, uuid.toString());
            try {
                ps.executeUpdate();
            }
            catch(Exception e) {
            	
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}
