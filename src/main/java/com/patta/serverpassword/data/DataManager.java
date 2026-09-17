package com.patta.serverpassword.data;

import com.patta.serverpassword.ServerPasswordPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class DataManager {

    private final ServerPasswordPlugin plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Set<UUID> rememberedPlayers = new HashSet<>();

    public DataManager(ServerPasswordPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "authenticated_players.yml");
        load();
    }

    public synchronized void load() {
        if (!dataFile.getParentFile().exists()) {
            dataFile.getParentFile().mkdirs();
        }
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create authenticated_players.yml", e);
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        rememberedPlayers.clear();

        List<String> list = dataConfig.getStringList("remembered-players");
        for (String s : list) {
            try {
                rememberedPlayers.add(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public synchronized void save() {
        try {
            List<String> list = rememberedPlayers.stream().map(UUID::toString).toList();
            dataConfig.set("remembered-players", list);
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save authenticated_players.yml", e);
        }
    }

    public synchronized boolean isRemembered(UUID uuid) {
        return rememberedPlayers.contains(uuid);
    }

    public synchronized void rememberPlayer(UUID uuid) {
        if (rememberedPlayers.add(uuid)) {
            save();
        }
    }

    public synchronized boolean forgetPlayer(UUID uuid) {
        if (rememberedPlayers.remove(uuid)) {
            save();
            return true;
        }
        return false;
    }

    public synchronized void clearAll() {
        rememberedPlayers.clear();
        save();
    }

    public synchronized int getRememberedCount() {
        return rememberedPlayers.size();
    }
}
