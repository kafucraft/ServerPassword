package com.patta.serverpassword.auth;

import com.patta.serverpassword.ServerPasswordPlugin;
import com.patta.serverpassword.config.ConfigManager;
import com.patta.serverpassword.data.DataManager;
import com.patta.serverpassword.gui.KeypadGUI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthManager {

    private final ServerPasswordPlugin plugin;
    private final ConfigManager configManager;
    private final DataManager dataManager;
    private final KeypadGUI keypadGUI;
    private final Map<UUID, AuthSession> sessions = new ConcurrentHashMap<>();

    private final PotionEffectType blindnessType;
    private final PotionEffectType slownessType;
    private final PotionEffectType jumpType;

    public AuthManager(ServerPasswordPlugin plugin, ConfigManager configManager, DataManager dataManager, KeypadGUI keypadGUI) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.dataManager = dataManager;
        this.keypadGUI = keypadGUI;

        this.blindnessType = resolvePotionEffectType("BLINDNESS", "BLINDNESS");
        this.slownessType = resolvePotionEffectType("SLOWNESS", "SLOW");
        this.jumpType = resolvePotionEffectType("JUMP_BOOST", "JUMP");
    }

    private PotionEffectType resolvePotionEffectType(String modernName, String legacyName) {
        PotionEffectType type = PotionEffectType.getByName(modernName);
        if (type == null) {
            type = PotionEffectType.getByName(legacyName);
        }
        return type;
    }

    public boolean isUnauthenticated(UUID uuid) {
        return sessions.containsKey(uuid);
    }

    public boolean isUnauthenticated(Player player) {
        return player != null && isUnauthenticated(player.getUniqueId());
    }

    public AuthSession getSession(Player player) {
        if (player == null) return null;
        return sessions.get(player.getUniqueId());
    }

    public void startAuth(Player player) {
        UUID uuid = player.getUniqueId();
        AuthSession session = new AuthSession(uuid);
        sessions.put(uuid, session);

        // Apply restrictions & effects
        applyRestrictionEffects(player);

        // Notify player based on prompt-type
        setupPromptNotification(player, session);

        // Schedule timeout kick
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (isUnauthenticated(player) && player.isOnline()) {
                player.kickPlayer(configManager.getRawMessage("kick-timeout"));
            }
        }, configManager.getTimeoutSeconds() * 20L);
        session.setTimeoutTask(timeoutTask);

        // Open GUI ONLY if input-mode is GUI
        if (!configManager.isChatMode() && configManager.isGuiEnabled()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (isUnauthenticated(player) && player.isOnline()) {
                    keypadGUI.open(player, session);
                }
            }, 3L);
        }
    }

    private void setupPromptNotification(Player player, AuthSession session) {
        String promptType = configManager.getPromptType();

        switch (promptType) {
            case "ACTIONBAR" -> {
                String barText = configManager.getRawMessage("actionbar-prompt");
                sendActionBar(player, barText);

                // Keep refreshing action bar every second while unauthenticated
                BukkitTask promptTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    if (isUnauthenticated(player) && player.isOnline()) {
                        sendActionBar(player, barText);
                    }
                }, 20L, 20L);
                session.setPromptTask(promptTask);
            }
            case "TITLE" -> {
                player.sendTitle("§6§lใส่รหัสผ่านเซิร์ฟเวอร์", "§eกรุณาพิมพ์รหัส 1-8 หลักในช่องแชต", 10, 70, 20);
            }
            case "CHAT" -> {
                player.sendMessage(configManager.getMessage("prompt-chat"));
            }
            case "NONE" -> {
                // Completely stealth, no messages
            }
        }
    }

    public void applyRestrictionEffects(Player player) {
        if (configManager.isBlindnessEnabled() && blindnessType != null) {
            player.addPotionEffect(new PotionEffect(blindnessType, 999999, 1, false, false, false));
        }
        if (configManager.isFreezeEnabled()) {
            if (slownessType != null) {
                player.addPotionEffect(new PotionEffect(slownessType, 999999, 255, false, false, false));
            }
            if (jumpType != null) {
                player.addPotionEffect(new PotionEffect(jumpType, 999999, 200, false, false, false));
            }
        }
    }

    public void removeRestrictionEffects(Player player) {
        if (blindnessType != null) player.removePotionEffect(blindnessType);
        if (slownessType != null) player.removePotionEffect(slownessType);
        if (jumpType != null) player.removePotionEffect(jumpType);
    }

    public void completeAuth(Player player, AuthSession session) {
        if (session.getTimeoutTask() != null) {
            session.getTimeoutTask().cancel();
        }
        if (session.getPromptTask() != null) {
            session.getPromptTask().cancel();
        }
        sessions.remove(player.getUniqueId());

        // Remember player if enabled
        if (configManager.isRememberPlayerEnabled()) {
            dataManager.rememberPlayer(player.getUniqueId());
        }

        removeRestrictionEffects(player);
        player.closeInventory();

        // Sound effect
        try {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } catch (Exception ignored) {}

        // Send notifications
        if (configManager.getPromptType().equals("ACTIONBAR")) {
            sendActionBar(player, configManager.getRawMessage("actionbar-success"));
        }
        player.sendTitle(
                configManager.getRawMessage("title-success"),
                configManager.getRawMessage("subtitle-success"),
                10, 40, 15
        );
        player.sendMessage(configManager.getMessage("success"));
    }

    public void submitPassword(Player player, String rawInput) {
        AuthSession session = getSession(player);
        if (session == null) return;

        if (session.isProcessingSubmission()) return;
        session.setProcessingSubmission(true);

        // Stealth clear chat to prevent streamers' chat history from exposing password
        if (configManager.isClearChatOnSubmit()) {
            clearPlayerChatScreen(player);
        }

        String correctPassword = configManager.getServerPassword();
        if (rawInput != null && rawInput.trim().equals(correctPassword)) {
            completeAuth(player, session);
        } else {
            session.incrementFailedAttempts();
            session.clear();
            session.setProcessingSubmission(false);

            int remaining = configManager.getMaxAttempts() - session.getFailedAttempts();
            if (remaining <= 0) {
                if (session.getTimeoutTask() != null) {
                    session.getTimeoutTask().cancel();
                }
                if (session.getPromptTask() != null) {
                    session.getPromptTask().cancel();
                }
                sessions.remove(player.getUniqueId());
                player.kickPlayer(configManager.getRawMessage("kick-wrong"));
            } else {
                try {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                } catch (Exception ignored) {}

                String wrongMsg = configManager.getMessage("wrong-password")
                        .replace("{remaining}", String.valueOf(remaining));
                player.sendMessage(wrongMsg);

                if (configManager.getPromptType().equals("ACTIONBAR")) {
                    sendActionBar(player, "§cรหัสผ่านไม่ถูกต้อง! (เหลืออีก " + remaining + " ครั้ง)");
                } else {
                    player.sendTitle("§cรหัสผ่านไม่ถูกต้อง!", "§7เหลือโอกาสอีก §e" + remaining + " §7ครั้ง", 5, 40, 10);
                }

                if (!configManager.isChatMode() && player.getOpenInventory() != null) {
                    keypadGUI.update(player.getOpenInventory().getTopInventory(), session);
                }
            }
        }
    }

    public void clearPlayerChatScreen(Player player) {
        for (int i = 0; i < 100; i++) {
            player.sendMessage("");
        }
    }

    public void sendActionBar(Player player, String message) {
        try {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        } catch (Exception ignored) {}
    }

    public void handleKeypadClick(Player player, ItemStack item) {
        AuthSession session = getSession(player);
        if (session == null) return;

        String action = keypadGUI.getAction(item);
        if (action == null) return;

        try {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1.2f);
        } catch (Exception ignored) {}

        switch (action) {
            case KeypadGUI.ACTION_INPUT -> {
                String val = keypadGUI.getValue(item);
                if (val != null && !val.isEmpty()) {
                    session.appendChar(val.charAt(0));
                    keypadGUI.update(player.getOpenInventory().getTopInventory(), session);
                }
            }
            case KeypadGUI.ACTION_BACKSPACE -> {
                session.backspace();
                keypadGUI.update(player.getOpenInventory().getTopInventory(), session);
            }
            case KeypadGUI.ACTION_CLEAR -> {
                session.clear();
                keypadGUI.update(player.getOpenInventory().getTopInventory(), session);
            }
            case KeypadGUI.ACTION_TOGGLE_MODE -> {
                session.toggleAlphabetMode();
                keypadGUI.update(player.getOpenInventory().getTopInventory(), session);
            }
            case KeypadGUI.ACTION_SUBMIT -> {
                submitPassword(player, session.getInput());
            }
        }
    }

    public void cleanupPlayer(UUID uuid) {
        AuthSession session = sessions.remove(uuid);
        if (session != null) {
            if (session.getTimeoutTask() != null) {
                session.getTimeoutTask().cancel();
            }
            if (session.getPromptTask() != null) {
                session.getPromptTask().cancel();
            }
        }
    }

    public void reOpenGuiDelayed(Player player) {
        if (configManager.isChatMode() || !configManager.isGuiEnabled()) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (isUnauthenticated(player) && player.isOnline()) {
                AuthSession session = getSession(player);
                if (session != null) {
                    keypadGUI.open(player, session);
                }
            }
        }, 2L);
    }
}
