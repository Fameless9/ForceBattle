# ForceBattle

[![Build Status](https://github.com/Fameless9/ForceBattle/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/Fameless9/ForceBattle/actions/workflows/gradle.yml)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Code Quality](https://www.codefactor.io/repository/github/fameless9/forcebattle/badge)](https://www.codefactor.io/repository/github/fameless9/forcebattle)
[![Spigot Downloads](https://pluginbadges.glitch.me/api/v1/dl/Spigot%20Downloads-limegreen.svg?spigot=1-21-x-force-battle-item-mob-biome-advancement-height.112328&github=Fameless9%2FForceBattle&style=flat)](https://www.spigotmc.org/resources/1-21-x-force-battle-item-mob-biome-advancement-height.112328/)

---

## 📖 About

**ForceBattle** is a multiplayer gamemode where players compete to complete randomized objectives and earn points.  
The player with the most points after the timer ends wins the battle!

**Objective types include:**
- 🔹 Collect an item
- 🔹 Kill a mob
- 🔹 Discover a biome
- 🔹 Complete an advancement
- 🔹 Reach a certain height
- 🔹 Reach a certain coordinate
- 🔹 Discover a certain structure


Objective types can be enabled or disabled individually via the plugin's settings menu.

---

## ✨ Features

- Adjustable **battle timer**
- **Chain Mode** - each player has to complete the same chain of objectives
- **Team system**
- Integrated **bossbar** progress display
- Extensive **settings menu** (in-game GUI)
- Multi-language support (🇬🇧 English, 🇩🇪 German, 🇨🇳 Chinese)

### 📋 Commands

| Command                     | Description                                                |
|:----------------------------|:-----------------------------------------------------------|
| `/backpack <player>`        | Opens the personal or teammates backpack (can be disabled) |
| `/displayresults`           | Show the final leaderboard (player, team)                  |
| `/exclude <player>`         | Exclude a player from the battle                           |
| `/help`                     | Display a list of available commands                       |
| `/joker <player> <amount>`  | Adjust jokers for a player                                 |
| `/language`                 | Open the language selection menu                           |
| `/points <player> <amount>` | Modify a player's points                                   |
| `/randomteams <teamsize>`   | Creates random teams with a specific size                  |
| `/reset [player]`           | Reset the battle or a specific player                      |
| `/result <player>`          | Show detailed results for a player                         |
| `/settings`                 | Open the plugin's settings menu                            |
| `/skip <player>`            | Skip a player's current objective (for admins)             |
| `/team`                     | Manage teams                                               |
| `/timer`                    | Control the timer (start, stop, set duration)              |

---

## 📷 Screenshots

<details>
<summary>Click to show Screenshots</summary>
<img src="https://sss.feathermc.com/PM0rK84J.png" width="600">
<img src="https://sss.feathermc.com/riSvtKJ9.png" width="600">
<img src="https://sss.feathermc.com/kEXLII1q.png" width="600">
<img src="https://sss.feathermc.com/5hzkfB7V.png" width="600">
<img src="https://sss.feathermc.com/GOMuaczz.png" width="600">
<img src="https://sss.feathermc.com/8jVUlYKp.png" width="600">
</details>

---

## 🛠️ Building from Source

To build ForceBattle yourself, make sure you have **Gradle** installed.  
Then clone this repository and run:

```bash
gradle build
```

The compiled `.jar` file will be located under `build/libs` inside the `Spigot` module.

---

## 🤝 Contributing

Want to contribute? Awesome! Here's how you can help:

1. **Fork** this repository.
2. **Create a new branch** based on your feature or fix.  
   (Example: `feature/new-feature` or `fix/bug-description`)
3. **Implement your changes**, following `.editorconfig` and standard Java conventions.
4. **Test your changes** carefully to ensure nothing breaks.
5. **Push** to your fork and **open a pull request** with a clear description of what you changed.

---

## 📜 License

This project is licensed under the **GNU GPLv3**.  
See the [LICENSE](https://www.gnu.org/licenses/gpl-3.0) file for details.
