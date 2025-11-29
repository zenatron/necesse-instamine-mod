# Instamine Mod for Necesse
A quality-of-life mod that enables instant mining, extended reach, optional combat boosts, and 10× ore drops in Necesse.

## Installation
1. Download the latest `ZenInstamine-X.X.X-X.X.jar` from the releases page
2. Place the jar file in your Necesse mods folder:
   - **Windows**: `%APPDATA%/Necesse/mods/`
   - **macOS**: `~/Library/Application Support/Necesse/mods/`
   - **Linux**: `~/.config/Necesse/mods/`
3. Launch Necesse and enable the mod from the mods menu

## Building from Source
```bash
./gradlew clean buildModJar
```
The compiled mod will be in `build/jar/`

## Features
- Instant tile breaking
- Extended mining range
- Optional combat damage boost
- 10× ore, shard, and mineral drops

## In-Game Controls
Use the `/instamine` chat command (available to all players):
- `/instamine toggle <mining|range|combat|ore>` switches a feature
- `/instamine on|off <feature>` forces a state
- `/instamine range set <blocks>` adjusts the extended reach (1 block = 32 px, max 100 blocks)
- `/instamine status` shows the current configuration

## Compatibility
- **Game Version**: Necesse 1.0.2+
- **Client/Server**: Client-side only (works in single-player and multiplayer)

## Technical Details
This mod uses ByteBuddy method patching to modify:
- `ToolDamageItem.getToolDps()` - Tool damage per second
- `ToolDamageItem.getToolHitDamage()` - Actual damage applied
- `ToolDamageItem.getAttackAnimTime()` - Animation timing
- `ToolDamageItem.getAttackHandlerDamageCooldown()` - Cooldown between hits
- `ToolItem.getAttackDamage()` - Combat damage to enemies
- `Level.onTileLootTableDropped()` / `Level.onObjectLootTableDropped()` - Ore and shard drop counts

## Notes
- Range values are configured in blocks (`/instamine range set <blocks>`); 1 block equals 32 pixels and values above 100 blocks are clamped.

## Author
Created by [zenatron](https://github.com/zenatron)