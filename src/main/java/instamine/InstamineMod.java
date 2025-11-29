package instamine;

import necesse.engine.commands.CommandsManager;
import necesse.engine.events.loot.ObjectLootTableDropsEvent;
import necesse.engine.events.loot.TileLootTableDropsEvent;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.Server;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;
import instamine.commands.InstamineToggleCommand;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.bytebuddy.asm.Advice;

@ModEntry
public class InstamineMod {

    private static final int TOOL_DPS = 999999;
    private static final int TOOL_HIT_DAMAGE = 999999;
    private static final int MIN_ANIM_TIME = 1;
    private static final int NO_COOLDOWN = 0;
    private static final float COMBAT_DAMAGE = 1000000f;
    private static final int PIXELS_PER_BLOCK = 32;
    private static final int DEFAULT_RANGE_BLOCKS = 16;
    private static final int MAX_RANGE_BLOCKS = 100;
    private static final int ORE_DROP_MULTIPLIER = 10;
    private static final Set<String> SPECIAL_MINABLE_ITEM_IDS;

    static {
        HashSet<String> ids = new HashSet<String>();
        ids.add("upgradeshard");
        ids.add("alchemyshard");
        SPECIAL_MINABLE_ITEM_IDS = Collections.unmodifiableSet(ids);
    }

    public enum FeatureToggle {
        MINING("Instant mining"),
        RANGE("Extended range"),
        COMBAT("Combat damage"),
        ORE("Ore multiplier");

        private final String displayName;

        FeatureToggle(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String describeWithState() {
            StringBuilder state = new StringBuilder(InstamineMod.isFeatureEnabled(this) ? "ON" : "OFF");
            if (this == RANGE) {
                state.append(" (").append(InstamineMod.formatRangeValue()).append(")");
            }
            return getDisplayName() + ": " + state;
        }
    }

    // All features enabled by default
    public static boolean instamineEnabled = true;
    public static boolean extendedRangeEnabled = true;
    public static boolean combatBoostEnabled = true;
    public static boolean oreMultiplierEnabled = true;
    public static int extendedRangePixels = DEFAULT_RANGE_BLOCKS * PIXELS_PER_BLOCK;

    public void initResources() {
        System.out.println("[Zen Instamine] Resources initialized");
    }

    public void postInit() {
        CommandsManager.registerServerCommand(new InstamineToggleCommand());
        CommandsManager.registerClientCommand(new InstamineToggleCommand());
    }

    public static boolean isFeatureEnabled(FeatureToggle feature) {
        switch (feature) {
            case MINING:
                return instamineEnabled;
            case RANGE:
                return extendedRangeEnabled;
            case COMBAT:
                return combatBoostEnabled;
            case ORE:
                return oreMultiplierEnabled;
            default:
                throw new IllegalArgumentException("Unhandled feature: " + feature);
        }
    }

    public static void setFeatureEnabled(FeatureToggle feature, boolean enabled) {
        switch (feature) {
            case MINING:
                instamineEnabled = enabled;
                break;
            case RANGE:
                extendedRangeEnabled = enabled;
                break;
            case COMBAT:
                combatBoostEnabled = enabled;
                break;
            case ORE:
                oreMultiplierEnabled = enabled;
                break;
            default:
                throw new IllegalArgumentException("Unhandled feature: " + feature);
        }
    }

    public static void setFeaturesEnabled(EnumSet<FeatureToggle> features, boolean enabled) {
        for (FeatureToggle feature : features) {
            setFeatureEnabled(feature, enabled);
        }
    }

    public static void broadcast(Server server, String message) {
        if (server != null && message != null && !message.isEmpty()) {
            server.network.sendToAllClients(new PacketChatMessage("[Instamine] " + message));
        }
    }

    public static String formatFeatureStateMessage(FeatureToggle feature, boolean enabled) {
        StringBuilder message = new StringBuilder(feature.getDisplayName()).append(" ")
            .append(enabled ? "enabled" : "disabled");
        if (feature == FeatureToggle.RANGE) {
            message.append(" (").append(formatRangeValue()).append(")");
        }
        return message.toString();
    }

    public static int getExtendedRangePixels() {
        return extendedRangePixels;
    }

    public static int getExtendedRangeBlocks() {
        return extendedRangePixels / PIXELS_PER_BLOCK;
    }

    public static int setExtendedRangeBlocks(int blocks) {
        int clamped = Math.max(0, Math.min(blocks, MAX_RANGE_BLOCKS));
        extendedRangePixels = clamped * PIXELS_PER_BLOCK;
        return clamped;
    }

    public static int getMaxRangeBlocks() {
        return MAX_RANGE_BLOCKS;
    }

    public static String formatRangeValue() {
        return getExtendedRangeBlocks() + " blocks / " + getExtendedRangePixels() + " px";
    }

    // Multiplies ore-category drops safely in place.
    public static void multiplyOreDrops(List<InventoryItem> drops) {
        if (drops == null || drops.isEmpty()) {
            return;
        }

        for (InventoryItem drop : drops) {
            if (!isOreItem(drop)) {
                continue;
            }

            int amount = drop.getAmount();
            if (amount <= 0) {
                continue;
            }

            long multiplied = (long) amount * ORE_DROP_MULTIPLIER;
            drop.setAmount(multiplied > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) multiplied);
        }
    }

    public static boolean isOreItem(InventoryItem drop) {
        if (drop == null || drop.item == null) {
            return false;
        }

        ItemCategory category = ItemCategory.getItemsCategory(drop.item);
        if (category != null) {
            if (category.isOrHasParent("ore") || category.isOrHasParent("minerals")) {
                return true;
            }
        }

        String stringID = drop.item.getStringID();
        return stringID != null && SPECIAL_MINABLE_ITEM_IDS.contains(stringID);
    }

    /**
     * Boosts the base tool DPS (damage per second) for mining
     * This affects the tooltip display and mining damage calculations
     */
    @ModMethodPatch(target = ToolDamageItem.class, name = "getToolDps", arguments = {InventoryItem.class, Mob.class})
    public static class ToolDpsPatch {
        @Advice.OnMethodExit
        public static void boostToolDps(@Advice.Return(readOnly = false) int dps) {
            if (instamineEnabled) {
                dps = TOOL_DPS;
            }
        }
    }

    /**
     * Boosts the actual damage dealt per hit to tiles and objects
     * This ensures one-shot mining for all blocks
     */
    @ModMethodPatch(target = ToolDamageItem.class, name = "getToolHitDamage", arguments = {InventoryItem.class, int.class, ItemAttackerMob.class})
    public static class ToolHitDamagePatch {
        @Advice.OnMethodExit
        public static void boostHitDamage(@Advice.Return(readOnly = false) int damage) {
            if (instamineEnabled) {
                damage = TOOL_HIT_DAMAGE;
            }
        }
    }

    /**
     * Minimizes attack animation time for instant swings
     * This makes mining feel instantaneous
     */
    @ModMethodPatch(target = ToolDamageItem.class, name = "getAttackAnimTime", arguments = {InventoryItem.class, ItemAttackerMob.class})
    public static class AttackAnimTimePatch {
        @Advice.OnMethodExit
        public static void minimizeAnimTime(@Advice.Return(readOnly = false) int time) {
            if (instamineEnabled) {
                time = MIN_ANIM_TIME;
            }
        }
    }

    /**
     * Removes cooldown between damage applications
     * Allows continuous mining without delays
     */
    @ModMethodPatch(target = ToolDamageItem.class, name = "getAttackHandlerDamageCooldown", arguments = {InventoryItem.class, ItemAttackerMob.class})
    public static class DamageCooldownPatch {
        @Advice.OnMethodExit
        public static void removeCooldown(@Advice.Return(readOnly = false) int cooldown) {
            if (instamineEnabled) {
                cooldown = NO_COOLDOWN;
            }
        }
    }

    /**
     * Boosts combat damage for weapons
     * Makes all weapons deal massive damage to enemies
     */
    @ModMethodPatch(target = ToolItem.class, name = "getAttackDamage", arguments = {InventoryItem.class})
    public static class AttackDamagePatch {
        @Advice.OnMethodExit
        public static void boostDamage(@Advice.Return(readOnly = false) GameDamage damage) {
            if (combatBoostEnabled) {
                damage = new GameDamage(COMBAT_DAMAGE);
            }
        }
    }

    /**
     * Extends mining range significantly
     * Allows mining from much further away
     */
    @ModMethodPatch(target = ToolDamageItem.class, name = "getMiningRange", arguments = {InventoryItem.class, ItemAttackerMob.class})
    public static class MiningRangePatch {
        @Advice.OnMethodExit
        public static void extendRange(@Advice.Return(readOnly = false) int range) {
            if (extendedRangeEnabled) {
                range = extendedRangePixels;
            }
        }
    }

    /**
     * Extends attack range to match mining range
     * Keeps attack and mining range consistent
     */
    @ModMethodPatch(target = ToolItem.class, name = "getAttackRange", arguments = {InventoryItem.class})
    public static class AttackRangePatch {
        @Advice.OnMethodExit
        public static void extendAttackRange(@Advice.Return(readOnly = false) int range) {
            if (extendedRangeEnabled) {
                range = extendedRangePixels;
            }
        }
    }

    /**
     * Multiplies ore drops by 10x when enabled
     * Patches onTileLootTableDropped to modify the event's drops list before items spawn
     */
    @ModMethodPatch(target = Level.class, name = "onTileLootTableDropped", arguments = {TileLootTableDropsEvent.class})
    public static class OreDropMultiplierPatch {
        @Advice.OnMethodEnter
        public static void multiplyDrops(@Advice.Argument(0) TileLootTableDropsEvent event) {
            if (!oreMultiplierEnabled || event == null || event.level == null || !event.level.isServer()) {
                return;
            }

            multiplyOreDrops(event.drops);
        }
    }

    /**
     * Multiplies ore drops for ore objects (e.g., veins) pulled from loot tables.
     */
    @ModMethodPatch(target = Level.class, name = "onObjectLootTableDropped", arguments = {ObjectLootTableDropsEvent.class})
    public static class OreObjectDropMultiplierPatch {
        @Advice.OnMethodEnter
        public static void multiplyDrops(@Advice.Argument(0) ObjectLootTableDropsEvent event) {
            if (!oreMultiplierEnabled || event == null || event.level == null || !event.level.isServer()) {
                return;
            }

            if (event.object != null && event.object.isOre) {
                multiplyOreDrops(event.objectDrops);
                multiplyOreDrops(event.entityDrops);
            }
        }
    }
}