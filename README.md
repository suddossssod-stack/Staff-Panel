# Staff Panel (Fabric)

A standalone client-side Minecraft Fabric mod for Minecraft 1.21.11 and Java 21.

## Features
- Press **K** to open the panel (change the key in Options → Controls → Staff Panel).
- Or type **`/staffpanel`** in chat.
- Maintain a local player watchlist.
- Save and delete private staff notes.
- Watchlist and notes are stored in `.minecraft/config/staff-panel.json`.

## Build
Use Java 21 and Gradle 8.14.3, then run `gradle build --no-daemon`. The JAR is created in `build/libs/`.

## Important limits
This is a client-side organizer only. It cannot see server-only information, inspect players invisibly, prove cheating, moderate other players, or grant staff permissions. The watchlist and notes stay on the local computer.
