# Medicine Reminder / 药不能停

> *"让每一次服药，都被温柔以待。"*  
> *"Every dose, gently reminded."*

---

<div align="center">

[![Android](https://img.shields.io/badge/Android-7.0%2B-brightgreen?logo=android)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-8%2B-orange?logo=java)](https://www.java.com)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)
[![API](https://img.shields.io/badge/API-24%2B-yellow?logo=android)](https://developer.android.com/about/versions/nougat)
[![GitHub stars](https://img.shields.io/github/stars/RIVICV/MedicineReminder?style=social)](https://github.com/RIVICV/MedicineReminder)

</div>

---

<div align="center">
  <img src="https://img.shields.io/badge/MVVM-Architecture-FF6B6B?style=for-the-badge" alt="MVVM"/>
  <img src="https://img.shields.io/badge/Room-Database-4ECDC4?style=for-the-badge" alt="Room"/>
  <img src="https://img.shields.io/badge/Material-Design-3-FFE66D?style=for-the-badge" alt="Material Design"/>
  <img src="https://img.shields.io/badge/4_Components-Covered-292F36?style=for-the-badge" alt="4 Components"/>
</div>

---

## 📖 Table of Contents / 目录

| English | 中文 |
| :--- | :--- |
| [Overview](#-overview) | [项目概述](#-项目概述) |
| [Screenshots](#-screenshots) | [应用截图](#-应用截图) |
| [Core Features](#-core-features) | [核心功能](#-核心功能) |
| [Architecture](#️-architecture) | [技术架构](#️-技术架构) |
| [Tech Stack](#-tech-stack) | [技术栈](#-技术栈) |
| [Quick Start](#-quick-start) | [快速开始](#-快速开始) |
| [Project Structure](#-project-structure) | [项目结构](#-项目结构) |
| [Database Design](#-database-design) | [数据库设计](#-数据库设计) |
| [Key Highlights](#-key-highlights) | [技术亮点](#-技术亮点) |
| [License](#-license) | [许可证](#-许可证) |

---

## 🌟 Overview

<div align="center">
  <h3>English</h3>
</div>

**Medicine Reminder** is a beautifully designed Android application that transforms medication management from a cold, clinical task into a warm, caring experience. Built with a soothing peach-and-mint color palette, it combines powerful technical capabilities with thoughtful UX design.

> 📊 **The Problem**: Over 60% of elderly chronic disease patients in China exhibit poor medication adherence, leading to worsened health outcomes and preventable hospitalizations.

> 💡 **Our Solution**: An intelligent reminder system that requires minimal user interaction — simply shake your phone to confirm taking medication, with no need to unlock, tap, or read small text.

<div align="center">
  <h3>中文</h3>
</div>

**药不能停** 是一款设计精美、温馨治愈的 Android 服药提醒应用。它采用蜜桃粉与薄荷绿的治愈系配色，将冰冷的“吃药任务”转变为一种被关怀的温暖体验。

> 📊 **痛点数据**：全国超 60% 的老年慢性病患者存在用药依从性差的问题，直接影响治疗效果与生活质量。

> 💡 **解决方案**：通过极简的交互设计——用户只需**摇一摇手机**即可确认服药，无需解锁屏幕、无需点击按钮、无需阅读小字。

---

## 📱 Screenshots

<div align="center">
  <table>
    <tr>
      <td align="center"><strong>🏠 Home Page</strong><br><em>首页</em></td>
      <td align="center"><strong>⏰ Reminder Alert</strong><br><em>全屏提醒</em></td>
      <td align="center"><strong>📋 History</strong><br><em>服药记录</em></td>
      <td align="center"><strong>🔍 Search</strong><br><em>药品搜索</em></td>
    </tr>
    <tr>
      <td>
        <ul>
          <li>Time-based greeting</li>
          <li>Daily statistics card</li>
          <li>Medicine list</li>
          <li>FAB quick add</li>
        </ul>
      </td>
      <td>
        <ul>
          <li>Full-screen immersive</li>
          <li>48sp large font</li>
          <li>Shake to confirm</li>
          <li>Foreground ringtone</li>
        </ul>
      </td>
      <td>
        <ul>
          <li>Date-based filtering</li>
          <li>Tap to toggle status</li>
          <li>Long press to delete</li>
        </ul>
      </td>
      <td>
        <ul>
          <li>Online API query</li>
          <li>HTML instructions</li>
          <li>Real-time results</li>
        </ul>
      </td>
    </tr>
  </table>
</div>

---

## 🎯 Core Features

<div align="center">
  <h3>English</h3>
</div>

| Feature | Description | Technical Implementation |
| :--- | :--- | :--- |
| 🎯 **Smart Alarm** | System-level alarm clock, triggers even when app is killed | `AlarmManager.setAlarmClock()` + `setExactAndAllowWhileIdle()` |
| 🤝 **Shake to Confirm** | Shake phone to acknowledge medication — no tapping required | `Accelerometer Sensor` + `Vibrator` feedback |
| 🔄 **Flexible Scheduling** | Daily, specific weekdays, or one-time only | Room DB with repeat mode logic |
| 📊 **Real-time Stats** | Today's taken/pending count auto-calculated | `LiveData` + dynamic calculation algorithm |
| 📝 **History Tracking** | View, edit, delete medication records | Room CRUD + `RecyclerView` |
| 🔍 **Drug Search** | Online drug information lookup with full instructions | TianAPI integration + `HttpURLConnection` |
| 🔔 **Foreground Service** | Ringtone plays reliably even in background | `ForegroundService` + `AudioFocus` + `WakeLock` |
| 🔄 **Boot Recovery** | Auto-restore all alarms after device reboot | `BootReceiver` + `BOOT_COMPLETED` broadcast |
| 📤 **Data Sharing** | Expose medication records to other health apps | `ContentProvider` implementation |
| 🌙 **Dark Mode** | Auto-adapts to system theme | `DayNight` theme |

<div align="center">
  <h3>中文</h3>
</div>

| 功能 | 说明 | 技术实现 |
| :--- | :--- | :--- |
| 🎯 **智能闹钟** | 系统级闹钟，应用被清理后仍能准时触发 | `AlarmManager.setAlarmClock()` + `setExactAndAllowWhileIdle()` |
| 🤝 **摇一摇确认** | 摇动手机即可确认服药，无需点击屏幕 | 加速度传感器 + 振动反馈 |
| 🔄 **灵活排程** | 支持每天、指定星期、仅一次三种模式 | Room数据库 + 重复逻辑算法 |
| 📊 **实时统计** | 今日待服/已服次数自动计算 | `LiveData` + 动态统计算法 |
| 📝 **记录追踪** | 查看、编辑、删除服药历史 | Room增删改查 + `RecyclerView` |
| 🔍 **药品搜索** | 在线查询药品完整说明书 | 天聚数行API + `HttpURLConnection` |
| 🔔 **前台服务** | 后台铃声稳定播放，不被系统清理 | `ForegroundService` + `AudioFocus` + `WakeLock` |
| 🔄 **开机恢复** | 重启后自动恢复所有闹钟 | `BootReceiver` + `BOOT_COMPLETED` |
| 📤 **数据共享** | 向其他健康应用暴露服药数据 | `ContentProvider` |
| 🌙 **暗色模式** | 自动跟随系统主题切换 | `DayNight`主题 |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   📱 VIEW LAYER (UI)                    │
│  Activities / Fragments / Adapters                       │
│  • MainActivity          • AddMedicineActivity           │
│  • ReminderActivity      • HistoryActivity               │
│  • DrugSearchActivity    • DrugDetailActivity            │
│                                                          │
│  Responsibility: UI rendering & user interaction         │
│  Principle: No business logic, only display & delegate   │
└──────────────────────┬──────────────────────────────────┘
                       │ Observes LiveData
                       │ Calls ViewModel methods
                       ▼
┌─────────────────────────────────────────────────────────┐
│                   🧠 VIEWMODEL LAYER                    │
│  MedicineViewModel extends AndroidViewModel              │
│                                                          │
│  • Exposes LiveData for View observation                 │
│  • Handles business logic (CRUD, statistics calculation) │
│  • Manages AlarmManager scheduling lifecycle             │
│                                                          │
│  Principle: No View reference, lifecycle-aware           │
└──────────────────────┬──────────────────────────────────┘
                       │ Holds Repository reference
                       │ Calls Repository methods
                       ▼
┌─────────────────────────────────────────────────────────┐
│                   🗄️ REPOSITORY LAYER                   │
│  MedicineRepository                                      │
│                                                          │
│  • Single source of truth for all data operations        │
│  • Coordinates Room DB (local) & Network API (remote)    │
│  • All DB writes on background thread pool (4 threads)   │
│                                                          │
│  Principle: Abstract data source differences             │
└──────────────┬───────────────────┬──────────────────────┘
               │                   │
               ▼                   ▼
┌──────────────────────┐  ┌───────────────────────────────┐
│  🗃️ Room Database     │  │  🌐 Network API              │
│  (Local Storage)      │  │  (Remote Data)                │
│                       │  │                               │
│  • MedicineDao        │  │  • TianAPI Drug Search        │
│  • RecordDao          │  │  • HttpURLConnection          │
│  • LiveData queries   │  │  • JSON parsing               │
└──────────────────────┘  └───────────────────────────────┘
```

---

## 💻 Tech Stack

<div align="center">
  <h3>English</h3>
</div>

| Category | Technology | Purpose |
| :--- | :--- | :--- |
| **Language** | Java 8+ | Primary development language |
| **Architecture** | MVVM + Repository | Layered architecture, separation of concerns |
| **Database** | Room (SQLite ORM) | Local persistent storage |
| **Async** | LiveData + ExecutorService | Reactive data flow + background threading |
| **UI Framework** | Material Design 3 | Modern, consistent visual language |
| **Alarm** | AlarmManager + AlarmClockInfo | System-level precise wake-up |
| **Background** | ForegroundService (mediaPlayback) | Reliable background ringtone playback |
| **Sensor** | Accelerometer (TYPE_ACCELEROMETER) | Shake gesture detection |
| **Media** | MediaPlayer + RingtoneManager | Alarm ringtone loop playback |
| **Network** | HttpURLConnection | Online drug information query |
| **Permissions** | SCHEDULE_EXACT_ALARM, POST_NOTIFICATIONS | Android 12/13+ permission adaptation |
| **Components** | All 4 Android components used | Activity, BroadcastReceiver, Service, ContentProvider |

<div align="center">
  <h3>中文</h3>
</div>

| 类别 | 技术 | 用途 |
| :--- | :--- | :--- |
| **语言** | Java 8+ | 主要开发语言 |
| **架构** | MVVM + Repository | 分层架构，关注点分离 |
| **数据库** | Room (SQLite ORM) | 本地持久化存储 |
| **异步处理** | LiveData + ExecutorService | 响应式数据流 + 后台线程 |
| **UI框架** | Material Design 3 | 现代、一致的视觉语言 |
| **闹钟** | AlarmManager + AlarmClockInfo | 系统级精准唤醒 |
| **后台任务** | ForegroundService (mediaPlayback) | 可靠的后台铃声播放 |
| **传感器** | 加速度传感器 (TYPE_ACCELEROMETER) | 摇动手势检测 |
| **多媒体** | MediaPlayer + RingtoneManager | 闹钟铃声循环播放 |
| **网络** | HttpURLConnection | 在线药品信息查询 |
| **权限适配** | SCHEDULE_EXACT_ALARM, POST_NOTIFICATIONS | Android 12/13+权限适配 |
| **四大组件** | 全部覆盖 | Activity, BroadcastReceiver, Service, ContentProvider |

---

## 🚀 Quick Start

<div align="center">
  <h3>English</h3>
</div>

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK** 17 or higher
- **Android SDK** 34
- **Gradle** 8.9
- A physical Android device (recommended) or emulator

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/RIVICV/MedicineReminder.git

# 2. Open in Android Studio
# File → Open → Select the project directory

# 3. Wait for Gradle sync to complete

# 4. Configure API Key
# Copy app/src/main/res/values/secrets.xml.example
# Rename to secrets.xml
# Fill in your TianAPI key

# 5. Connect your device & Run!
```

### API Key Configuration

1. Visit [TianAPI](https://www.tianapi.com) and register
2. Apply for the "Drug Package Insert" (药品说明书) API
3. Copy your API Key from the dashboard
4. Paste it into `secrets.xml`

<div align="center">
  <h3>中文</h3>
</div>

### 环境要求

- **Android Studio** Hedgehog (2023.1.1) 或更高版本
- **JDK** 17 或更高版本
- **Android SDK** 34
- **Gradle** 8.9
- 一台 Android 真机（推荐）或模拟器

### 安装步骤

```bash
# 1. 克隆仓库
git clone https://github.com/RIVICV/MedicineReminder.git

# 2. 用 Android Studio 打开
# File → Open → 选择项目目录

# 3. 等待 Gradle 同步完成

# 4. 配置 API Key
# 复制 app/src/main/res/values/secrets.xml.example
# 重命名为 secrets.xml
# 填入你的天聚数行 API Key

# 5. 连接手机，运行！
```

### API Key 配置

1. 前往 [天聚数行](https://www.tianapi.com) 注册
2. 申请"药品说明书"接口
3. 在控制台复制你的 API Key
4. 粘贴到 `secrets.xml` 中

---

## 📁 Project Structure

```
MedicineReminder/
│
├── app/
│   ├── src/main/
│   │   ├── java/com/example/medicinereminder/
│   │   │   ├── MyApplication.java              # Global Application class
│   │   │   ├── Medicine.java                   # Medicine entity (Room)
│   │   │   ├── Record.java                     # Record entity (Room)
│   │   │   ├── MedicineDao.java                # Data Access Object
│   │   │   ├── AppDatabase.java                # Room Database manager
│   │   │   ├── MedicineRepository.java         # Repository layer
│   │   │   ├── MedicineViewModel.java          # ViewModel layer
│   │   │   ├── ReminderScheduler.java          # Alarm scheduler
│   │   │   ├── AlarmReceiver.java              # Broadcast receiver
│   │   │   ├── BootReceiver.java               # Boot complete receiver
│   │   │   ├── ReminderForegroundService.java  # Foreground service
│   │   │   ├── ShakeDetector.java              # Accelerometer sensor
│   │   │   ├── MedicineReminderData.java       # Parcelable data class
│   │   │   ├── MedicineContentProvider.java    # ContentProvider
│   │   │   ├── MainActivity.java               # Home page
│   │   │   ├── AddMedicineActivity.java        # Add/Edit medicine
│   │   │   ├── MedicineAdapter.java            # RecyclerView adapter
│   │   │   ├── ReminderActivity.java           # Full-screen reminder
│   │   │   ├── HistoryActivity.java            # Medication history
│   │   │   ├── DrugSearchActivity.java         # Drug search
│   │   │   └── DrugDetailActivity.java         # Drug detail
│   │   │
│   │   ├── res/
│   │   │   ├── layout/                         # XML layout files
│   │   │   ├── drawable/                       # Icons & graphics
│   │   │   ├── values/                         # Colors, strings, themes
│   │   │   └── menu/                           # Bottom navigation menu
│   │   │
│   │   └── AndroidManifest.xml                 # App configuration
│   │
│   └── build.gradle                            # Module build config
│
├── gradle/wrapper/                             # Gradle wrapper
├── build.gradle                                # Project build config
├── settings.gradle                             # Project settings
├── gradle.properties                           # Gradle properties
├── .gitignore                                  # Git ignore rules
├── LICENSE                                     # MIT License
└── README.md                                   # This file
```

---

## 🗃️ Database Design

### Entity Relationship Diagram

```
┌────────────────────——─┐         ┌───────────────────————───┐
│   medicine_table      │         │    record_table          │
├─────────────────────——┤         ├──────────────────────————┤
│ PK │ id (INT)         │──1:N──—→│ FK │ medicine_id (INT)   │
│    │ name (TEXT)      │         │    │ medicine_name (TXT) │
│    │ dosage (TEXT)    │         │    │ scheduled_time (L)  │
│    │ instruction (TXT)│         │    │ actual_time (LONG)  │
│    │ remind_times (T) │         │    │ status (INT)        │
│    │ repeat_mode (I)  │         │    │ created_at (LONG)   │
│    │ repeat_days (T)  │         └────────────────────————──┘
│    │ is_repeating (I) │
│    │ is_active (INT)  │
│    │ created_at (L)   │
└────────────────────——─┘
```

### Table Details

| Table | Fields | Primary Key | Description |
| :--- | :--- | :--- | :--- |
| `medicine_table` | 11 | `id` (auto-increment) | Stores all registered medicines |
| `record_table` | 7 | `id` (auto-increment) | Stores each medication event |

**Relationship**: One medicine → Many records (1:N via `medicine_id` foreign key)

---

## 🔑 Key Highlights

<div align="center">
  <h3>English</h3>
</div>

### 1. Intelligent Shake Detection Algorithm

```
Pseudo-code:
1. Register TYPE_ACCELEROMETER sensor (SENSOR_DELAY_NORMAL)
2. On each sensor event:
   - Calculate vector magnitude: g = sqrt(x² + y² + z²)
   - IF g > 15 m/s² AND (now - lastShakeTime) > 500ms:
     - Vibrate 100ms for haptic feedback
     - Trigger confirmation callback
     - Update lastShakeTime
```

### 2. Dual-Layer Alarm Strategy

| Layer | Method | Priority | Purpose |
| :--- | :--- | :--- | :--- |
| **Layer 1** | `setAlarmClock()` | System-level (highest) | Survives app termination |
| **Layer 2** | `setExactAndAllowWhileIdle()` | Precision wake-up | Works in Doze mode |

### 3. Foreground Service + Audio Focus + WakeLock

Ensures the alarm ringtone keeps playing even when the app is in background:
- `ForegroundService` → Persistent notification, high process priority
- `AudioFocus` → Prevents other apps from interrupting
- `PARTIAL_WAKE_LOCK` → Keeps CPU running during playback

### 4. Four Major Android Components Covered

✅ **Activity** — 6 activities for all UI pages  
✅ **BroadcastReceiver** — AlarmReceiver + BootReceiver  
✅ **Service** — ReminderForegroundService with mediaPlayback type  
✅ **ContentProvider** — MedicineContentProvider for cross-app data sharing

<div align="center">
  <h3>中文</h3>
</div>

### 1. 智能摇动检测算法

```
伪代码：
1. 注册加速度传感器 (SENSOR_DELAY_NORMAL)
2. 每次传感器事件：
   - 计算向量模长：g = sqrt(x² + y² + z²)
   - 如果 g > 15 且 距上次触发 > 500ms：
     - 振动 100ms 提供触感反馈
     - 触发确认回调
     - 更新上次触发时间
```

### 2. 双层闹钟策略

| 层级 | 方法 | 优先级 | 用途 |
| :--- | :--- | :--- | :--- |
| **第一层** | `setAlarmClock()` | 系统级（最高） | 即使应用被杀也能触发 |
| **第二层** | `setExactAndAllowWhileIdle()` | 精准唤醒 | 在Doze模式下也能工作 |

### 3. 前台服务 + 音频焦点 + 唤醒锁

确保应用在后台时铃声持续播放：
- `ForegroundService` → 常驻通知，高进程优先级
- `AudioFocus` → 防止其他应用打断
- `PARTIAL_WAKE_LOCK` → 播放期间保持CPU运行

### 4. 安卓四大组件全覆盖

✅ **Activity** — 6个页面承载全部UI  
✅ **BroadcastReceiver** — 闹钟接收器 + 开机接收器  
✅ **Service** — 前台服务（mediaPlayback类型）  
✅ **ContentProvider** — 跨应用数据共享

---

## 📄 License

<div align="center">

```
MIT License

Copyright (c) 2026 RIVICV

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
```

</div>

---

<div align="center">

### ⭐ If you find this project helpful, please give it a Star!

### 📧 Contact: Open an Issue on GitHub

### 🏫 Course Project for *Mobile Terminal Programming Technology B*

---

**[⬆ Back to Top](#medicine-reminder--药不能停)**

</div>
