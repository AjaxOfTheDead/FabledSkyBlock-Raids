package me.ResurrectAjax.Commands.Managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.ByteStreams;

import me.ResurrectAjax.Main.Main;
/**
 * Class for managing files (adjusted copy of songoda FileManager)
 * 
 * @author ResurrectAjax
 * */
public class FileManager {

    private final Main plugin;
    private final Map<String, Config> loadedConfigs = new HashMap<>();

    public FileManager(Main plugin) {
        this.plugin = plugin;

        backupIfNeeded();

        loadConfigs();
    }

    public void loadConfigs() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        
        Map<String, File> configFiles = new LinkedHashMap<>();
        configFiles.put("language.yml", new File(plugin.getDataFolder(), "language.yml"));
        configFiles.put("gui.yml", new File(plugin.getDataFolder(), "gui.yml"));

        for (Entry<String, File> configEntry : configFiles.entrySet()) {

            String fileName = configEntry.getKey();
            File configFile = configEntry.getValue();

            if (configFile.exists()) {
                if (fileName.equals("config.yml") ||
                        fileName.equals("language.yml") || fileName.equals("gui.yml")) {
                    FileChecker fileChecker;

                    if (fileName.equals("config.yml")) {
                        fileChecker = new FileChecker(plugin, this, fileName, true);
                    } else {
                        fileChecker = new FileChecker(plugin, this, fileName, false);
                    }

                    fileChecker.loadSections();
                    fileChecker.compareFiles();
                    fileChecker.saveChanges();
                }
            } else {
                try {
                    configFile.createNewFile();
                    try (InputStream is = plugin.getResource(fileName); OutputStream os = new FileOutputStream(configFile)) {
                        if(is != null){
                            ByteStreams.copy(is, os);
                        }
                    }
                    
                } catch (IOException ex) {
                    Bukkit.getServer().getLogger().log(Level.WARNING, "SkyBlock | Error: Unable to create configuration file.");
                }
            }
        }
    }

    public void setLocation(Config config, String path, Location location, boolean direction) {

        final ConfigurationSection section = config.getFileConfiguration().createSection(path);

        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());

        if (direction) {
            section.set("yaw", location.getYaw());
            section.set("pitch", location.getPitch());
        }

        try {
            config.getFileConfiguration().save(config.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void backupIfNeeded() {
        File languageFile = new File(plugin.getDataFolder(), "language.yml");
        if(languageFile.exists()) {
            File backupDir = new File(plugin.getDataFolder().toString() + "/backup");
            if(!backupDir.exists()) {
                backupDir.mkdir();
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
            LocalDateTime now = LocalDateTime.now();
    
            
            Path oldLanguagePath = languageFile.toPath();
            Path newLanguagePath = new File(plugin.getDataFolder().toString() + "/backup", "language" + dtf.format(now) + ".yml").toPath();
    
            CopyOption[] options = new CopyOption[]{
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES
            };

            try {
                Files.copy(oldLanguagePath, newLanguagePath, options);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Location getLocation(Config config, String path, boolean direction) {

        ConfigurationSection section = config.getFileConfiguration().getConfigurationSection(path);

        if (section == null) return null;

        String world = section.getString("world");
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");

        double yaw = 0.0D;
        double pitch = 0.0D;

        if (direction) {
            yaw = section.getDouble("yaw");
            pitch = section.getDouble("pitch");
        }

        return new org.bukkit.Location(Bukkit.getWorld(world), x, y, z, (short) yaw, (short) pitch);
    }

    public boolean isFileExist(File configPath) {
        return configPath.exists();
    }

    public void unloadConfig(File configPath) {
        loadedConfigs.remove(configPath.getPath());
    }

    public void deleteConfig(File configPath) {
        Config config = getConfig(configPath);
        config.getFile().delete();
        loadedConfigs.remove(configPath.getPath());
    }

    public Config getConfig(File configPath) {
        Config cached = loadedConfigs.get(configPath.getPath());

        if (cached != null) return cached;

        Config config = new Config(this, configPath);
        loadedConfigs.put(configPath.getPath(), config);

        return config;
    }

    public Map<String, Config> getConfigs() {
        return loadedConfigs;
    }

    public boolean isConfigLoaded(java.io.File configPath) {
        return loadedConfigs.containsKey(configPath.getPath());
    }

    public InputStream getConfigContent(Reader reader) {
        try {
            String addLine, currentLine, pluginName = plugin.getDescription().getName();
            int commentNum = 0;

            StringBuilder whole = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(reader);

            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.contains("#")) {
                    addLine = currentLine.replace("[!]", "IMPORTANT").replace(":", "-").replaceFirst("#", pluginName + "_COMMENT_" + commentNum + ":");
                    whole.append(addLine).append("\n");
                    commentNum++;
                } else {
                    whole.append(currentLine).append("\n");
                }
            }

            String config = whole.toString();
            InputStream configStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
            bufferedReader.close();

            return configStream;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public InputStream getConfigContent(File configFile) {
        if (!configFile.exists()) {
            return null;
        }

        try {
            return getConfigContent(new FileReader(configFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String prepareConfigString(String configString) {
        String[] lines = configString.split("\n");
        StringBuilder config = new StringBuilder();

        for (String line : lines) {
            if (line.contains(plugin.getDescription().getName() + "_COMMENT")) {
                config.append(line.replace("IMPORTANT", "[!]").replace("\n", "").replace(plugin.getDescription().getName() + "_COMMENT_", "#").replaceAll("[0-9]+:", "")).append("\n");
            } else if (line.contains(":")) {
                config.append(line).append("\n");
            }
        }

        return config.toString();
    }

    public void saveConfig(String configString, File configFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            writer.write(prepareConfigString(configString));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Config {

        private final File configFile;
        private FileConfiguration configLoad;

        public Config(FileManager fileManager, java.io.File configPath) {
            configFile = configPath;

            if (configPath.getName().equals("config.yml")) {
                configLoad = YamlConfiguration.loadConfiguration(new InputStreamReader(fileManager.getConfigContent(configFile)));
            } else {
                configLoad = YamlConfiguration.loadConfiguration(configPath);
            }
        }

        public File getFile() {
            return configFile;
        }

        public FileConfiguration getFileConfiguration() {
            return configLoad;
        }

        public FileConfiguration loadFile() {
            configLoad = YamlConfiguration.loadConfiguration(configFile);

            return configLoad;
        }
    }
}
