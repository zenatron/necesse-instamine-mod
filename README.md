# Instamine Mod for Necesse
A QOL mod that enables instant mining, flexible reach values, movement and building buffs, item magnetism, optional combat boosts, and 10× ore drops in Necesse.

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
- Adjustable mining reach (up to 100 blocks)
- Toggleable storage-box crafting access radius
- Configurable movement-speed multiplier (10% – 1000%)
- Configurable building-speed multiplier (10% – 2000%)
- Item pickup "magnet" radius control
- Optional combat damage boost
- 10× ore, shard, and mineral drops

## In-Game Controls
Use the `/instamine` chat command (available to all players):
- `/instamine toggle <mining|range|craft|movement|pickup|combat|build|ore>` switches a feature
- `/instamine on|off <feature>` forces a state
- `/instamine range set <blocks>` changes mining reach (1 block = 32 px, max 100 blocks)
- `/instamine craft set <blocks>` adjusts storage crafting range (up to 200 blocks)
- `/instamine movement set <percent>` sets the movement multiplier (10–1000)
- `/instamine build set <percent>` sets the building/placement multiplier (10–2000)
- `/instamine pickup set <blocks>` sets the pickup radius (1–200 blocks)
- `/instamine status` shows the current configuration

## Compatibility
- **Game Version**: Necesse 1.0.2+
- **Client/Server**: Client-side only (works in single-player and multiplayer)

## Technical Details
This mod uses ByteBuddy method patching to modify:
- Tool mining performance (`ToolDamageItem` helpers)
- Player attack range and damage (`ToolItem`)
- Storage crafting radius (`CraftingStationContainer`)
- Player movement speed modifier (`Mob.getSpeedModifier`)
- Player building placement cooldown (`PlaceableItem.getAttackHandlerPlaceCooldown`)
- Item pickup targeting and streaming range (`PickupEntity`)
- Ore/shard loot tables (`Level.on*LootTableDropped`)

## Notes
- Range values are configured in blocks (`/instamine range set <blocks>`); 1 block equals 32 pixels and values above 100 blocks are clamped.

## Author
Created by [zenatron](https://github.com/zenatron)