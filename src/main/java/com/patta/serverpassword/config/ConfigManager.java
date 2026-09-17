package com.patta.serverpassword.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private String serverPassword;
    private boolean rememberPlayerEnabled;
    private boolean resetOnPasswordChange;
    private String inputMode;
    private boolean clearChatOnSubmit;
    private boolean blindnessEnabled;
    private boolean freezeEnabled;
    private String promptType;
    private int maxAttempts;
    private int timeoutSeconds;
    private boolean guiEnabled;
    private String guiTitle;
    private String maskChar;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        this.serverPassword = config.getString("server-password", "12345678");
        this.rememberPlayerEnabled = config.getBoolean("remember-player.enabled", true);
        this.resetOnPasswordChange = config.getBoolean("remember-player.reset-on-password-change", true);

        this.inputMode = config.getString("input-mode", "CHAT").toUpperCase();
        this.clearChatOnSubmit = config.getBoolean("stealth.clear-chat-on-submit", true);
        this.blindnessEnabled = config.getBoolean("stealth.blindness", false);
        this.freezeEnabled = config.getBoolean("stealth.freeze", true);
        this.promptType = config.getString("stealth.prompt-type", "ACTIONBAR").toUpperCase();

        this.maxAttempts = config.getInt("max-attempts", 3);
        this.timeoutSeconds = config.getInt("timeout-seconds", 60);
        this.guiEnabled = config.getBoolean("gui.enabled", false);
        this.guiTitle = colorize(config.getString("gui.title", "&8» &6ใส่รหัสผ่านเซิร์ฟเวอร์ &8«"));
        this.maskChar = config.getString("gui.mask-char", "*");
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public boolean setServerPassword(String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty() || newPassword.length() > 8) {
            return false;
        }
        this.serverPassword = newPassword;
        plugin.getConfig().set("server-password", newPassword);
        plugin.saveConfig();
        return true;
    }

    public boolean isRememberPlayerEnabled() {
        return rememberPlayerEnabled;
    }

    public boolean isResetOnPasswordChange() {
        return resetOnPasswordChange;
    }

    public String getInputMode() {
        return inputMode;
    }

    public boolean isChatMode() {
        return "CHAT".equalsIgnoreCase(inputMode);
    }

    public boolean isClearChatOnSubmit() {
        return clearChatOnSubmit;
    }

    public boolean isBlindnessEnabled() {
        return blindnessEnabled;
    }

    public boolean isFreezeEnabled() {
        return freezeEnabled;
    }

    public String getPromptType() {
        return promptType;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public boolean isGuiEnabled() {
        return guiEnabled;
    }

    public String getGuiTitle() {
        return guiTitle;
    }

    public String getMaskChar() {
        return maskChar;
    }

    public String getMessage(String path) {
        String msg = plugin.getConfig().getString("messages." + path, "");
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&6ServerPass&8] ");
        return colorize(prefix + msg);
    }

    public String getRawMessage(String path) {
        String msg = plugin.getConfig().getString("messages." + path, "");
        return colorize(msg);
    }

    public static String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
