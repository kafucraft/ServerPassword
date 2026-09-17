<p align="center">
  <img src="assets/logo.png" width="320" alt="ServerPassword Logo">
</p>

<h1 align="center">ServerPassword</h1>

<p align="center">
  <strong>ระบบรหัสผ่านกลางสำหรับปกป้องเซิร์ฟเวอร์ Minecraft</strong><br>
  ออกแบบเพื่อความปลอดภัย ความเรียบง่าย และเป็นมิตรต่อสตรีมเมอร์<br>
  รองรับทั้ง <b>Java Edition</b> และ <b>Bedrock Edition (Geyser / Floodgate)</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.17--1.21+-2ea44f.svg?style=flat-square" alt="Minecraft Version">
  <img src="https://img.shields.io/badge/Server-Paper%20%7C%20Purpur%20%7C%20Spigot-f38b3c.svg?style=flat-square" alt="Server Platform">
  <img src="https://img.shields.io/badge/Bedrock-GeyserMC%20Ready-0969da.svg?style=flat-square" alt="Bedrock Supported">
  <img src="https://img.shields.io/badge/License-MIT-8250df.svg?style=flat-square" alt="License">
</p>

<p align="center">
  <a href="https://discord.com/users/435412527548203028">
    <img src="https://img.shields.io/badge/Discord-kafu__craft-5865F2?style=flat-square&logo=discord&logoColor=white" alt="Discord">
  </a>
  <a href="https://www.youtube.com/@Kafu_Craft">
    <img src="https://img.shields.io/badge/YouTube-KaFu%20Craft-FF0000?style=flat-square&logo=youtube&logoColor=white" alt="YouTube">
  </a>
</p>

---

## 📖 ภาพรวม (Overview)

**ServerPassword** คือปลั๊กอินควบคุมการเข้าถึงเซิร์ฟเวอร์ Minecraft ด้วยรหัสผ่านกลาง (Master Password) ผู้เล่นทุกคนต้องยืนยันรหัสผ่านที่ถูกต้องก่อนเข้าสู่โลกเกม โดยมีระบบ **Remember Player** ที่ช่วยจดจำผู้เล่นที่ผ่านการยืนยันแล้ว ทำให้เข้าเล่นในครั้งถัดไปได้ทันทีโดยไม่ต้องกรอกรหัสซ้ำ ป้องกันปัญหารหัสผ่านรั่วไหลระหว่างการถ่ายทอดสด (Live Stream) ได้อย่างสมบูรณ์แบบ

---

## ✨ คุณสมบัติเด่น (Features)

* 🛡️ **Master Password:** กำหนดรหัสผ่านประจำเซิร์ฟเวอร์ความยาว 1-8 ตัวอักษรหรือตัวเลขผ่าน `config.yml` หรือคำสั่งในเกม
* ⚡ **Remember Player:** จดจำผู้เล่นที่ยืนยันรหัสผ่านแล้วถาวรผ่าน UUID ไม่ต้องกรอกรหัสซ้ำในครั้งถัดไป
* 🤫 **Zero-Leak Protection:** ดักจับข้อความรหัสผ่านในแชตไม่ให้แสดงสู่สาธารณะ พร้อมระบบล้างหน้าจอแชตอัตโนมัติทันทีหลังส่ง
* 📱 **Crossplay Compatibility:** ใช้งานได้สมบูรณ์ทั้ง Java Edition และ Bedrock Edition (ผ่าน GeyserMC)
* 🔒 **Anti-Bypass Restriction:** ระงับการเคลื่อนที่, การกระโดด, การโต้ตอบกับบล็อก/ไอเทม และป้องกันความเสียหายทุกรูปแบบระหว่างรอการยืนยัน
* ⏱️ **Security Safeguards:** ระบบเตะผู้เล่นอัตโนมัติเมื่อกรอกรหัสผิดเกินจำนวนครั้ง หรือหมดเวลาที่กำหนด

---

## 📦 การติดตั้ง (Installation)

1. ดาวน์โหลดไฟล์ [ServerPassword-1.0.0.jar](ServerPassword-1.0.0.jar)
2. นำไฟล์ไปวางในโฟลเดอร์ `plugins/` ของเซิร์ฟเวอร์ (รองรับ Paper / Purpur / Spigot 1.17 - 1.21+)
3. เริ่มต้นหรือรีสตาร์ตเซิร์ฟเวอร์
4. กำหนดรหัสผ่านในไฟล์ `plugins/ServerPassword/config.yml` (ค่าเริ่มต้น: `12345678`)

---

## 🎮 คำสั่งและสิทธิ์การใช้งาน (Commands & Permissions)

### สำหรับผู้เล่นทั่วไป
| คำสั่ง | คำอธิบาย |
| :--- | :--- |
| พิมพ์รหัสในช่องแชต | ยืนยันรหัสผ่านเพื่อเข้าสู่เซิร์ฟเวอร์ |
| `/serverpass submit <รหัส>` | คำสั่งสำรองสำหรับส่งรหัสผ่าน (หรือ `/pass <รหัส>`) |
| `/serverpass gui` | เปิดเมนูตู้เซฟ PIN Pad (กรณีต้องการกดปุ่ม) |

### สำหรับผู้ดูแลระบบ (Permission: `serverpassword.admin`)
| คำสั่ง | คำอธิบาย |
| :--- | :--- |
| `/serverpass set <รหัส>` | เปลี่ยนรหัสผ่านเซิร์ฟเวอร์ใหม่ (ระบบจะรีเซ็ตให้ทุกคนยืนยันใหม่) |
| `/serverpass reset <ชื่อผู้เล่น>` | สั่งให้ผู้เล่นที่ระบุต้องยืนยันรหัสผ่านใหม่อีกครั้ง |
| `/serverpass resetall` | สั่งให้ผู้เล่นทุกคนต้องยืนยันรหัสผ่านใหม่อีกครั้ง |
| `/serverpass list` | แสดงจำนวนผู้เล่นที่ผ่านการยืนยันรหัสผ่านแล้ว |
| `/serverpass reload` | โหลดการตั้งค่าจาก `config.yml` ใหม่ทันที |

---

## ⚙️ การตั้งค่า (`config.yml`)

```yaml
# รหัสผ่านเซิร์ฟเวอร์ (1-8 ตัวอักษรหรือตัวเลข)
server-password: "12345678"

# ระบบจดจำผู้เล่น (ยืนยันครั้งเดียว ไม่ต้องกรอกซ้ำ)
remember-player:
  enabled: true
  reset-on-password-change: true # รีเซ็ตสถานะทุกคนเมื่อมีการเปลี่ยนรหัสผ่าน

# รูปแบบการกรอกรหัส: "CHAT" หรือ "GUI"
input-mode: "CHAT"

# การตั้งค่าความปลอดภัยและความเป็นส่วนตัว
stealth:
  clear-chat-on-submit: true  # ล้างหน้าจอแชตทันทีหลังกดส่ง
  blindness: false             # ปิดหน้าจอมืดเพื่อให้ภาพในการสตรีมคมชัดปกติ
  freeze: true                 # ล็อกตำแหน่งผู้เล่นก่อนยืนยันรหัส
  prompt-type: "ACTIONBAR"     # การแจ้งเตือน ("ACTIONBAR", "TITLE", "CHAT", "NONE")

# จำนวนครั้งที่อนุญาตให้กรอกผิดก่อนเตะออกจากเซิร์ฟเวอร์
max-attempts: 3

# ระยะเวลาที่กำหนดให้กรอกรหัส (วินาที)
timeout-seconds: 60
```

---

## 👨‍💻 ข้อมูลผู้พัฒนา (Developer)

* **ผู้พัฒนา:** **KaFulnwza007** (KaFuCraft)
* **Discord:** [kafu_craft](https://discord.com/users/435412527548203028)
* **YouTube:** [KaFu Craft](https://www.youtube.com/@Kafu_Craft)

---

## 📄 สัญญาอนุญาต (License)

ซอฟต์แวร์นี้เผยแพร่ภายใต้สัญญาอนุญาต [MIT License](LICENSE)
