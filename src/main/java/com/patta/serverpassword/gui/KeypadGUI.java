package com.patta.serverpassword.gui;

import com.patta.serverpassword.ServerPasswordPlugin;
import com.patta.serverpassword.auth.AuthSession;
import com.patta.serverpassword.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class KeypadGUI {

    public static final String ACTION_INPUT = "input";
    public static final String ACTION_BACKSPACE = "backspace";
    public static final String ACTION_CLEAR = "clear";
    public static final String ACTION_SUBMIT = "submit";
    public static final String ACTION_TOGGLE_MODE = "toggle_mode";

    private final ServerPasswordPlugin plugin;
    private final NamespacedKey actionKey;
    private final NamespacedKey valueKey;

    public KeypadGUI(ServerPasswordPlugin plugin) {
        this.plugin = plugin;
        this.actionKey = new NamespacedKey(plugin, "keypad_action");
        this.valueKey = new NamespacedKey(plugin, "keypad_value");
    }

    public void open(Player player, AuthSession session) {
        Inventory inv = Bukkit.createInventory(player, 54, plugin.getConfigManager().getGuiTitle());
        render(inv, session);
        player.openInventory(inv);
    }

    public void update(Inventory inv, AuthSession session) {
        render(inv, session);
    }

    private void render(Inventory inv, AuthSession session) {
        inv.clear();

        // 1. Fill background with border glass
        ItemStack borderGlass = createItem(Material.GRAY_STAINED_GLASS_PANE, "§7", null);
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, borderGlass);
        }

        // 2. Mode toggle button (Slot 0)
        boolean isAlphabet = session.isAlphabetMode();
        ItemStack modeBtn = createButton(
                Material.COMPASS,
                isAlphabet ? "§bสลับเป็นโหมด: §e[ ตัวเลข 0-9 ]" : "§bสลับเป็นโหมด: §e[ ตัวอักษร A-Z ]",
                List.of("§7คลิกเพื่อสลับแป้นพิมพ์"),
                ACTION_TOGGLE_MODE,
                "toggle"
        );
        inv.setItem(0, modeBtn);

        // 3. Instruction Info (Slot 8)
        ItemStack infoBtn = createItem(
                Material.BOOK,
                "§6คำแนะนำการใช้งาน",
                List.of(
                        "§7• กดปุ่มบนหน้าจอเพื่อใส่รหัสผ่านเซิร์ฟเวอร์",
                        "§7• ความยาวรหัสผ่าน: §f1 - 8 ตัวอักษร",
                        "§7• รองรับทั้งผู้เล่น §bJava §7และ §aBedrock",
                        "§7• รหัสจะถูกซ่อนเพื่อป้องกันการมองเห็น"
                )
        );
        inv.setItem(8, infoBtn);

        // 4. Screen Display (Slot 4)
        String maskChar = plugin.getConfigManager().getMaskChar();
        String masked = session.getMaskedInput(maskChar);
        if (masked.isEmpty()) {
            masked = "§8(ยังไม่ได้ใส่รหัส)";
        } else {
            masked = "§a" + masked;
        }

        List<String> screenLore = new ArrayList<>();
        screenLore.add("§7จำนวนตัวอักษร: §f" + session.getInputLength() + "§7/8");
        screenLore.add("§8[ระบบจะปิดบังรหัสผ่านจริงด้วยเครื่องหมาย *]");
        screenLore.add("");
        screenLore.add("§eกดปุ่ม §a[ ยืนยัน ] §eเมื่อใส่รหัสครบแล้ว");

        ItemStack screen = createItem(
                Material.NAME_TAG,
                "§eรหัสผ่าน: §7[ " + masked + " §7]",
                screenLore
        );
        inv.setItem(4, screen);

        // 5. Render Buttons depending on Mode
        if (!isAlphabet) {
            renderNumericPad(inv);
        } else {
            renderAlphabetPad(inv);
        }

        // 6. Action buttons (Clear, Backspace, Submit)
        ItemStack clearBtn = createButton(
                Material.BARRIER,
                "§c✖ ล้างทั้งหมด (Clear)",
                List.of("§7ล้างรหัสผ่านที่กรอกไว้ทั้งหมด"),
                ACTION_CLEAR,
                "clear"
        );
        inv.setItem(45, clearBtn);

        ItemStack backspaceBtn = createButton(
                Material.REDSTONE_BLOCK,
                "§c⌫ ลบ (Backspace)",
                List.of("§7ลบตัวอักษรตัวสุดท้าย"),
                ACTION_BACKSPACE,
                "backspace"
        );
        inv.setItem(47, backspaceBtn);

        ItemStack submitBtn = createButton(
                Material.EMERALD_BLOCK,
                "§a✔ ยืนยันรหัสผ่าน (Submit)",
                List.of("§7ตรวจสอบรหัสผ่านเพื่อเข้าสู่เซิร์ฟเวอร์"),
                ACTION_SUBMIT,
                "submit"
        );
        inv.setItem(49, submitBtn);
    }

    private void renderNumericPad(Inventory inv) {
        // Numeric 3x4 Layout:
        // Row 1: [1:20] [2:21] [3:22]
        // Row 2: [4:29] [5:30] [6:31]
        // Row 3: [7:38] [8:39] [9:40]
        // Row 4:        [0:48]
        int[] numSlots = {20, 21, 22, 29, 30, 31, 38, 39, 40};
        for (int i = 1; i <= 9; i++) {
            char ch = (char) ('0' + i);
            ItemStack btn = createButton(
                    Material.WHITE_CONCRETE,
                    "§f§l" + ch,
                    List.of("§7คลิกเพื่อใส่: §e" + ch),
                    ACTION_INPUT,
                    String.valueOf(ch)
            );
            inv.setItem(numSlots[i - 1], btn);
        }

        // Slot for 0
        ItemStack zeroBtn = createButton(
                Material.WHITE_CONCRETE,
                "§f§l0",
                List.of("§7คลิกเพื่อใส่: §e0"),
                ACTION_INPUT,
                "0"
        );
        inv.setItem(48, zeroBtn);
    }

    private void renderAlphabetPad(Inventory inv) {
        // 26 Letters Layout:
        // Row 1 (7 slots): 10, 11, 12, 13, 14, 15, 16 -> A, B, C, D, E, F, G
        // Row 2 (7 slots): 19, 20, 21, 22, 23, 24, 25 -> H, I, J, K, L, M, N
        // Row 3 (7 slots): 28, 29, 30, 31, 32, 33, 34 -> O, P, Q, R, S, T, U
        // Row 4 (5 slots): 38, 39, 40, 41, 42         -> V, W, X, Y, Z
        int[] alphaSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                38, 39, 40, 41, 42
        };

        for (int i = 0; i < 26; i++) {
            char ch = (char) ('A' + i);
            ItemStack btn = createButton(
                    Material.LIGHT_BLUE_CONCRETE,
                    "§b§l" + ch,
                    List.of("§7คลิกเพื่อใส่: §e" + ch),
                    ACTION_INPUT,
                    String.valueOf(ch)
            );
            inv.setItem(alphaSlots[i], btn);
        }
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createButton(Material material, String name, List<String> lore, String action, String value) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(lore);
            }
            meta.getPersistentDataContainer().set(actionKey, PersistentDataType.STRING, action);
            meta.getPersistentDataContainer().set(valueKey, PersistentDataType.STRING, value);
            item.setItemMeta(meta);
        }
        return item;
    }

    public String getAction(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(actionKey, PersistentDataType.STRING);
    }

    public String getValue(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(valueKey, PersistentDataType.STRING);
    }
}
