package com.patta.serverpassword;

import com.patta.serverpassword.auth.AuthManager;
import com.patta.serverpassword.command.ServerPasswordCommand;
import com.patta.serverpassword.config.ConfigManager;
import com.patta.serverpassword.data.DataManager;
import com.patta.serverpassword.gui.KeypadGUI;
import com.patta.serverpassword.listener.PlayerRestrictionListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerPasswordPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private DataManager dataManager;
    private KeypadGUI keypadGUI;
    private AuthManager authManager;

    @Override
    public void onEnable() {
        // 1. Initialize configuration and persistent data
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);

        // 2. Initialize GUI and Auth manager
        this.keypadGUI = new KeypadGUI(this);
        this.authManager = new AuthManager(this, configManager, dataManager, keypadGUI);

        // 3. Register listeners
        getServer().getPluginManager().registerEvents(
                new PlayerRestrictionListener(this, authManager, configManager, dataManager),
                this
        );

        // 4. Register commands
        ServerPasswordCommand commandHandler = new ServerPasswordCommand(this, authManager, configManager, dataManager);
        registerCommand("serverpass", commandHandler);

        // 5. If players are already online during reload, lock them down if not remembered
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!configManager.isRememberPlayerEnabled() || !dataManager.isRemembered(player.getUniqueId())) {
                authManager.startAuth(player);
            }
        }

        getLogger().info("==========================================================");
        getLogger().info("  🔐 ServerPassword v" + getDescription().getVersion() + " (Prassword Server)");
        getLogger().info("  👨‍💻 Developer : KaFulnwza007 (KaFuCraft)");
        getLogger().info("  💬 Discord   : kafu_craft (UID: 435412527548203028)");
        getLogger().info("  📺 YouTube   : Kafu Craft");
        getLogger().info("  ⚙️ Password  : " + configManager.getServerPassword());
        getLogger().info("  🛡️ Remember  : " + (configManager.isRememberPlayerEnabled() ? "Enabled (" + dataManager.getRememberedCount() + " players)" : "Disabled"));
        getLogger().info("  🚀 Status    : Successfully loaded and running!");
        getLogger().info("==========================================================");
    }

    @Override
    public void onDisable() {
        if (authManager != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (authManager.isUnauthenticated(player)) {
                    authManager.removeRestrictionEffects(player);
                    player.closeInventory();
                }
            }
        }
        if (dataManager != null) {
            dataManager.save();
        }
        Bukkit.getScheduler().cancelTasks(this);
        getLogger().info("ServerPassword by KaFulnwza007 disabled successfully.");
    }

    private void registerCommand(String name, ServerPasswordCommand handler) {
        PluginCommand cmd = getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(handler);
            cmd.setTabCompleter(handler);
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public KeypadGUI getKeypadGUI() {
        return keypadGUI;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }
}
