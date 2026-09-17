package com.patta.serverpassword.listener;

import com.patta.serverpassword.ServerPasswordPlugin;
import com.patta.serverpassword.auth.AuthManager;
import com.patta.serverpassword.config.ConfigManager;
import com.patta.serverpassword.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;

public class PlayerRestrictionListener implements Listener {

    private final ServerPasswordPlugin plugin;
    private final AuthManager authManager;
    private final ConfigManager configManager;
    private final DataManager dataManager;

    public PlayerRestrictionListener(ServerPasswordPlugin plugin, AuthManager authManager, ConfigManager configManager, DataManager dataManager) {
        this.plugin = plugin;
        this.authManager = authManager;
        this.configManager = configManager;
        this.dataManager = dataManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // If player is already remembered (entered password in a previous session), skip auth completely!
        if (configManager.isRememberPlayerEnabled() && dataManager.isRemembered(player.getUniqueId())) {
            return;
        }

        authManager.startAuth(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        authManager.cleanupPlayer(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!authManager.isUnauthenticated(player)) return;
        if (!configManager.isFreezeEnabled()) return;

        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;

        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            Location locked = from.clone();
            locked.setPitch(to.getPitch());
            locked.setYaw(to.getYaw());
            event.setTo(locked);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!authManager.isUnauthenticated(player)) return;

        // Cancel chat broadcast immediately - zero leak!
        event.setCancelled(true);

        String message = event.getMessage().trim();
        Bukkit.getScheduler().runTask(plugin, () -> authManager.submitPassword(player, message));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!authManager.isUnauthenticated(player)) return;

        event.setCancelled(true);
        String raw = event.getMessage().trim();
        if (raw.length() <= 1) return;

        String cmd = raw.substring(1).trim();
        String lower = cmd.toLowerCase();

        if (lower.startsWith("serverpass submit ")) {
            String pass = cmd.substring(18).trim();
            authManager.submitPassword(player, pass);
        } else if (lower.startsWith("pass ")) {
            String pass = cmd.substring(5).trim();
            authManager.submitPassword(player, pass);
        } else if (lower.equals("serverpass gui")) {
            authManager.reOpenGuiDelayed(player);
        } else if (lower.equals("serverpass") || lower.equals("pass")) {
            player.sendMessage(configManager.getMessage("prompt-chat"));
        } else {
            // If they typed e.g. /12345678 (accidentally included slash), treat as password!
            authManager.submitPassword(player, cmd);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (authManager.isUnauthenticated(player)) {
            event.setCancelled(true);

            if (event.getClickedInventory() != null &&
                    event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
                if (event.getCurrentItem() != null) {
                    authManager.handleKeypadClick(player, event.getCurrentItem());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (authManager.isUnauthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            if (authManager.isUnauthenticated(player)) {
                authManager.reOpenGuiDelayed(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (authManager.isUnauthenticated(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (authManager.isUnauthenticated(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (authManager.isUnauthenticated(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (authManager.isUnauthenticated(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player && authManager.isUnauthenticated(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && authManager.isUnauthenticated(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && authManager.isUnauthenticated(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (authManager.isUnauthenticated(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        if (authManager.isUnauthenticated(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
