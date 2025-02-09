# ForceBattle

### Description

ForceBattle is a multiplayer gamemode for Minecraft spigot and paper servers.
The goal of the game is for every player to collect an item, kill a mob, find
a biome, finish and advancement or read a height that is given by the plugin.

Each completed task awards the player one point. The player with the most points
wins the game after the time has run out.

### Features

- Commands:
    - /menu - open the settings menu of the plugin.
    - /backpack - open the backpack.
    - /joker - adjust the joker amount (skips, swaps).
    - /timer - timer commands (toggle, set, duration).
    - /team - team related commands (create, join, etc.).
    - /skip - skip a player's objective.
    - /result - show the results of a player at the end of the game.
    - /reset - reset the challenge.
    - /language - set the language of the plugin (english, german).
    - /displayresults - show the leaderboard at the end of the game.
    - /exclude - exclude a player from the game.
- Timer
- Teams
- Bossbar

### Contributing

If you are interested in adding new features or fixing bugs, follow these instructions:

1. Fork the repository.
2. Create a new branch. Name the branch after the feature you are working on. For example: 'feature/new-feature'.
3. Make your changes. Make sure to follow the .editorconfig and the general java conventions.
4. Test your changes. Make sure your changes don't break other features.
5. Push your changes to your fork and make a pull request describing you feature/fix.

### Building

To build the plugin from source, you need to have gradle installed. Run the following command to build the plugin:

```shell
gradle build
```

The plugin jar file will be located in the `build/libs` directory in the Spigot module.
