# Plugin Update: Version 2.0

Hello,

I am pleased to announce the release of the second version of this plugin, replacing the initial version. The updated plugin introduces several new features, enhancing your experience. Notable additions include the ability to select multiple challenges simultaneously, providing you with a random challenge from your chosen selection.

**Key Features:**

- *Multiple Challenge Selection*: Choose and experience random challenges from a selection.
- *Team Management*: Teams can now be set as private or public, with a designated team leader having exclusive management rights (e.g., kicking players, toggling public/private status).
- *Enhanced V1 Features*: All features from the previous version have been retained and improved.

Your input is crucial in refining this plugin further. Thank you in advance for your time and feedback.

***Discord:*** fameless9

***Version 1:*** https://github.com/Fameless9/ForceItemPlugin

### Detailed Features:

- /menu for all the important settings
- Exclude players from the challenge
- /result <player> command to see what objectives the player has completed
- Backpacks for both team and personal
- Points to keep track of completed objectives
- Teams (/team create) - synced points across team
- Reset menu to easily reset the challenge (/reset, /reset <player>)
- Jokers and swaps (adjustable amount)
- Joker menu to adjust the amount of jokers and swaps (/joker, /joker <player>)
- Timer and timer UI (/timer, /timer <toggle, set>)
- Points menu to easily adjust players points (/points <player>)
- Skip command to skip unobtainable objectives (most are excluded by default)
- For devs: Placeholder API - currently only featuring objective, objective type and points.
         Points: %points%
         Objective Type: %objective_type%
         Objective: %objective%

### Detailed Commands:

- /team: all the options for your team
- /skip: skip an objective for a player
- /exclude: exclude a player from the challenge (spectator)
- /backpack: open your personal or team backpack
- /points: adjust players points
- /timer: toggle or set the timer/open a timer UI
- /reset: menu to easily reset the challenge
- /joker: adjust the amount of jokers a player has
- /result: opens an inventory containing the completed objectives of a player

### Placeholder API:
When it comes to the placeholder API, I want to offer a lot more placeholders. For now, this plugin only features:
-     %points%
-     %type%
-     %objective%

What they do should be self-explanatory, the %points% return the points of a player, the %type% returns the type of objective the player is currently facing and the %objective% returns the objective itself. For more information on what is being returned, have a look at the placeholder classes in the source code: https://github.com/Fameless9/ForceBattle/tree/master/src/main/java/net/fameless/forcebattle/Placeholder

*Building requires [BuildTools](https://www.spigotmc.org/wiki/buildtools/) 1.20.1*
