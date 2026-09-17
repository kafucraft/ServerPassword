package com.patta.serverpassword.command;

import com.patta.serverpassword.ServerPasswordPlugin;
import com.patta.serverpassword.auth.AuthManager;
import com.patta.serverpassword.auth.AuthSession;
import com.patta.serverpassword.config.ConfigManager;
import com.patta.serverpassword.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerPasswordCommand implements CommandExecutor, TabCompleter {

    private final ServerPasswordPlugin plugin;
    private final AuthManager authManager;
    private final ConfigManager configManager;
    private final DataManager dataManager;

    public ServerPasswordCommand(ServerPasswordPlugin plugin, AuthManager authManager, ConfigManager configManager, DataManager dataManager) {
        this.plugin = plugin;
        this.authManager = authManager;
        this.configManager = configManager;
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player && authManager.isUnauthenticated(player)) {
                AuthSession session = authManager.getSession(player);
                if (session != null) {
                    plugin.getKeypadGUI().open(player, session);
                    return true;
                }
            }
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "reload" -> {
                if (!sender.hasPermission("serverpassword.admin")) {
                    sender.sendMessage("§cคุณไม่มีสิทธิ์ใช้งานคำสั่งนี้!");
                    return true;
                }
                configManager.load();
                dataManager.load();
                sender.sendMessage(configManager.getMessage("admin-reload"));
                return true;
            }

            case "set" -> {
                if (!sender.hasPermission("serverpassword.admin")) {
                    sender.sendMessage("§cคุณไม่มีสิทธิ์ใช้งานคำสั่งนี้!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cวิธีใช้: /serverpass set <รหัสผ่าน 1-8 ตัว>");
                    return true;
                }
                String newPass = args[1];
                if (newPass.length() < 1 || newPass.length() > 8) {
                    sender.sendMessage(configManager.getMessage("admin-invalid-length"));
                    return true;
                }
                boolean success = configManager.setServerPassword(newPass);
                if (success) {
                    if (configManager.isResetOnPasswordChange()) {
                        dataManager.clearAll();
                        sender.sendMessage("§e[แจ้งเตือน] ทำการรีเซ็ตรายชื่อผู้เล่นที่เคยจำไว้ทั้งหมด เนื่องจากมีการเปลี่ยนรหัสผ่านใหม่");
                    }
                    String msg = configManager.getMessage("admin-set-success").replace("{password}", newPass);
                    sender.sendMessage(msg);
                } else {
                    sender.sendMessage("§cเกิดข้อผิดพลาดในการบันทึกรหัสผ่านใหม่!");
                }
                return true;
            }

            case "reset" -> {
                if (!sender.hasPermission("serverpassword.admin")) {
                    sender.sendMessage("§cคุณไม่มีสิทธิ์ใช้งานคำสั่งนี้!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cวิธีใช้: /serverpass reset <ชื่อผู้เล่น>");
                    return true;
                }
                String targetName = args[1];
                Player onlineTarget = Bukkit.getPlayer(targetName);
                if (onlineTarget != null) {
                    dataManager.forgetPlayer(onlineTarget.getUniqueId());
                    authManager.startAuth(onlineTarget);
                    sender.sendMessage(configManager.getMessage("admin-reset-player").replace("{player}", onlineTarget.getName()));
                } else {
                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
                    if (offlineTarget.hasPlayedBefore() || offlineTarget.isOnline()) {
                        dataManager.forgetPlayer(offlineTarget.getUniqueId());
                        sender.sendMessage(configManager.getMessage("admin-reset-player").replace("{player}", targetName));
                    } else {
                        sender.sendMessage("§cไม่พบข้อมูลผู้เล่น: " + targetName);
                    }
                }
                return true;
            }

            case "resetall" -> {
                if (!sender.hasPermission("serverpassword.admin")) {
                    sender.sendMessage("§cคุณไม่มีสิทธิ์ใช้งานคำสั่งนี้!");
                    return true;
                }
                dataManager.clearAll();
                sender.sendMessage(configManager.getMessage("admin-reset-all"));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    authManager.startAuth(p);
                }
                return true;
            }

            case "list" -> {
                if (!sender.hasPermission("serverpassword.admin")) {
                    sender.sendMessage("§cคุณไม่มีสิทธิ์ใช้งานคำสั่งนี้!");
                    return true;
                }
                sender.sendMessage("§6§l=== รายชื่อผู้เล่นที่ผ่านการใส่รหัสแล้ว ===");
                sender.sendMessage("§7จำนวนผู้เล่นที่จำไว้: §e" + dataManager.getRememberedCount() + " §7คน");
                return true;
            }

            case "submit" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cคำสั่งนี้ใช้ได้เฉพาะผู้เล่นในเกมเท่านั้น!");
                    return true;
                }
                if (!authManager.isUnauthenticated(player)) {
                    player.sendMessage(configManager.getMessage("already-authenticated"));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§cวิธีใช้: /serverpass submit <รหัสผ่าน>");
                    return true;
                }
                authManager.submitPassword(player, args[1]);
                return true;
            }

            case "gui" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cคำสั่งนี้ใช้ได้เฉพาะผู้เล่นในเกมเท่านั้น!");
                    return true;
                }
                if (!authManager.isUnauthenticated(player)) {
                    player.sendMessage(configManager.getMessage("already-authenticated"));
                    return true;
                }
                AuthSession session = authManager.getSession(player);
                if (session != null) {
                    plugin.getKeypadGUI().open(player, session);
                }
                return true;
            }

            default -> {
                if (sender instanceof Player player && authManager.isUnauthenticated(player)) {
                    authManager.submitPassword(player, args[0]);
                    return true;
                }
                sendHelp(sender);
                return true;
            }
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l=== ระบบรหัสผ่านเซิร์ฟเวอร์ (ServerPassword) ===");
        if (sender.hasPermission("serverpassword.admin")) {
            sender.sendMessage("§e/serverpass set <รหัส> §7- ตั้งรหัสผ่านเซิร์ฟเวอร์ใหม่ (1-8 ตัว)");
            sender.sendMessage("§e/serverpass reload §7- โหลดไฟล์การตั้งค่า config.yml ใหม่");
            sender.sendMessage("§e/serverpass reset <ชื่อ> §7- ลบสิทธิ์ผู้เล่นคนนั้นเพื่อให้ใส่รหัสใหม่");
            sender.sendMessage("§e/serverpass resetall §7- รีเซ็ตผู้เล่นทุกคนเพื่อให้ใส่รหัสใหม่");
            sender.sendMessage("§e/serverpass list §7- ดูจำนวนผู้เล่นที่ยืนยันแล้ว");
        }
        sender.sendMessage("§e/serverpass submit <รหัส> §7- ใส่รหัสผ่านเพื่อเข้าเซิร์ฟเวอร์");
        sender.sendMessage("§e/serverpass gui §7- เปิดหน้าต่างตู้เซฟกดรหัสผ่าน");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("serverpassword.admin")) {
                completions.add("reload");
                completions.add("set");
                completions.add("reset");
                completions.add("resetall");
                completions.add("list");
            }
            if (sender instanceof Player player && authManager.isUnauthenticated(player)) {
                completions.add("gui");
                completions.add("submit");
            }
            return completions;
        }
        return Collections.emptyList();
    }
}
