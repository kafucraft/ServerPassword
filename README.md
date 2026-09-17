<p align="center">
  <img src="https://cdn.modrinth.com/data/cached_images/c60e22c01d7482b3849d6747b8378175fbe823bb.jpeg" width="340" alt="ServerPassword Logo">
</p>

<h1 align="center">ServerPassword</h1>

<p align="center">
  <strong>The Ultimate Lightweight Master Password Gatekeeper for Minecraft Servers</strong><br>
  Built for private communities and content creators (Streamer-Friendly)<br>
  Supports both <b>Java Edition</b> and <b>Bedrock Edition (GeyserMC / Floodgate)</b>
</p>

<p align="center">
  <a href="https://github.com/kafucraft/ServerPassword">
    <img src="https://img.shields.io/badge/GitHub-Repository-181717?style=flat-square&logo=github&logoColor=white" alt="GitHub">
  </a>
  <a href="https://discord.com/users/435412527548203028">
    <img src="https://img.shields.io/badge/Discord-kafu__craft-5865F2?style=flat-square&logo=discord&logoColor=white" alt="Discord">
  </a>
  <a href="https://www.youtube.com/@Kafu_Craft">
    <img src="https://img.shields.io/badge/YouTube-KaFu%20Craft-FF0000?style=flat-square&logo=youtube&logoColor=white" alt="YouTube">
  </a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.17--1.21+-2ea44f.svg?style=flat-square" alt="Minecraft Version">
  <img src="https://img.shields.io/badge/Server-Paper%20%7C%20Purpur%20%7C%20Spigot-f38b3c.svg?style=flat-square" alt="Server Platform">
  <img src="https://img.shields.io/badge/Bedrock-GeyserMC%20Ready-0969da.svg?style=flat-square" alt="Bedrock Supported">
  <img src="https://img.shields.io/badge/License-MIT-8250df.svg?style=flat-square" alt="License">
</p>

---

## 📖 Overview

**ServerPassword** is a robust, lightweight Minecraft server security plugin that enforces a centralized master password before allowing players into your world. Designed from the ground up for content creators and private communities, it features a persistent **Remember Player** mechanism so verified players never have to re-enter the password on stream, eliminating on-screen credential leaks completely.

🔗 **GitHub Repository:** [https://github.com/kafucraft/ServerPassword](https://github.com/kafucraft/ServerPassword)

---

## ✨ Key Features

* 🛡️ **Master Server Password:** Set a single 1-8 character password for the server in `config.yml` or live in-game.
* ⚡ **Remember Player (Streamer-Friendly):** Players verify once; verified UUIDs are remembered permanently across restarts. No chat bar leaks or UI popups while live streaming!
* 🤫 **Zero-Leak Stealth Chat:** Type the password directly in chat (`T` ➔ Password ➔ `Enter`). Messages are intercepted before broadcast and the chat screen is wiped instantly.
* 📱 **Crossplay Compatibility:** 100% compatible with Java Edition and Bedrock Edition players via GeyserMC.
* 🔒 **Anti-Bypass Restraints:** Freezes player movement, blocks damage, disables jumping, and prevents block break/place until authenticated.
* ⏱️ **Automated Safeguards:** Configurable timeout kick and maximum failed attempts protection.
* 🧰 **Virtual PIN Pad GUI (Optional):** Built-in Chest keypad interface for players who prefer clicking buttons.

---

## 📦 Installation

1. Download the latest release: [ServerPassword-1.0.0.jar](https://github.com/kafucraft/ServerPassword/releases)
2. Place the `.jar` file into your server's `plugins/` directory (Paper / Purpur / Spigot 1.17 - 1.21+).
3. Start or restart the server.
4. Customize settings in `plugins/ServerPassword/config.yml` (Default password: `12345678`).

---

## 🎮 Commands & Permissions

### Player Commands
| Command | Description |
| :--- | :--- |
| Direct Chat Input | Type the password directly in chat to verify |
| `/serverpass submit <password>` | Alternative command to submit password (alias: `/pass`) |
| `/serverpass gui` | Open the virtual Keypad PIN Pad |

### Admin Commands (Permission: `serverpassword.admin`)
| Command | Description |
| :--- | :--- |
| `/serverpass set <password>` | Change master password (automatically resets player sessions) |
| `/serverpass reset <player>` | Force a specific player to re-authenticate |
| `/serverpass resetall` | Force all players to re-authenticate |
| `/serverpass list` | View total count of authenticated players |
| `/serverpass reload` | Reload configuration from `config.yml` |

---

## ⚙️ Configuration (`config.yml`)

```yaml
# Master server password (1-8 alphanumeric characters)
server-password: "12345678"

# Remember player mechanism (verify once, never prompted again)
remember-player:
  enabled: true
  reset-on-password-change: true

# Input mode: "CHAT" (recommended) or "GUI"
input-mode: "CHAT"

# Stealth settings for content creators
stealth:
  clear-chat-on-submit: true  # Wipes chat screen after submission
  blindness: false             # Normal bright screen for recordings
  freeze: true                 # Freeze player position
  prompt-type: "ACTIONBAR"     # "ACTIONBAR", "TITLE", "CHAT", "NONE"

max-attempts: 3
timeout-seconds: 60
```

---

<details>
<summary><b>🇹🇭 คำอธิบายภาษาไทย (Click to expand Thai description)</b></summary>

### 📖 ภาพรวม (ภาษาไทย)
**ServerPassword** คือปลั๊กอินควบคุมการเข้าถึงเซิร์ฟเวอร์ Minecraft ด้วยรหัสผ่านกลาง (Master Password) ออกแบบมาเพื่อสตรีมเมอร์โดยเฉพาะ ด้วยระบบ **Remember Player** ที่ช่วยจดจำผู้เล่นที่ผ่านการยืนยันแล้ว ทำให้เข้าเล่นในครั้งถัดไปได้ทันทีโดยไม่ต้องกรอกรหัสซ้ำ ป้องกันปัญหารหัสผ่านรั่วไหลระหว่างการถ่ายทอดสด 100%

* 🛡️ **รหัสผ่านกลาง:** กำหนดรหัสผ่าน 1-8 ตัวอักษร
* ⚡ **Remember Player:** ใส่ครั้งเดียวจบ ครั้งต่อไปเข้าได้ทันที
* 🤫 **Zero-Leak:** พิมพ์ในแชตได้โดยตรง ข้อความไม่รั่วไหล และล้างแชตอัตโนมัติ
* 📱 **Crossplay:** รองรับทั้ง Java และ Bedrock (GeyserMC)

</details>

---

## 👨‍💻 Developer & Support

* **Lead Developer:** **KaFulnwza007** (KaFuCraft)
* **GitHub Repository:** [github.com/kafucraft/ServerPassword](https://github.com/kafucraft/ServerPassword)
* **Discord:** [kafu_craft](https://discord.com/users/435412527548203028)
* **YouTube:** [KaFu Craft](https://www.youtube.com/@Kafu_Craft)

---

## 📄 License

This project is licensed under the [MIT License](https://github.com/kafucraft/ServerPassword/blob/main/LICENSE).
