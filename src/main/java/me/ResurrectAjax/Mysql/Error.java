package me.ResurrectAjax.Mysql;

import java.util.logging.Level;

import com.songoda.skyblock.SkyBlock;

import me.ResurrectAjax.Main.Main;

public class Error {
    public static void execute(SkyBlock plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(Main plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
