package me.ResurrectAjax.Mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.ResurrectAjax.Main.Main;


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
