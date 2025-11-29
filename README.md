# Instamine Mod for Necesse
A quality-of-life mod that enables instant mining and massive damage for all tools and weapons in Necesse.

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

## Author
Created by [zenatron](https://github.com/zenatron)