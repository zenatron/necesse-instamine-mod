package instamine;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import net.bytebuddy.asm.Advice;

/**
 * Instamine Mod for Necesse
 * Provides instant mining and massive damage boost for all tools and weapons
 * 
 * @author zenatron
 * @version 1.0
 */
@ModEntry
public class InstamineMod {

    private static final int TOOL_DPS = 999999;
    private static final int TOOL_HIT_DAMAGE = 999999;
    private static final int MIN_ANIM_TIME = 1;
    private static final int NO_COOLDOWN = 0;
    private static final float COMBAT_DAMAGE = 1000000f;

    public void init() {
        System.out.println("=== Instamine Mod Loaded ===");
        System.out.println("All tools now mine instantly with massive damage!");
    }

    /**
     * Boosts the base tool DPS (damage per second) for mining
     * This affects the tooltip display and mining damage calculations
     */
    @ModMethodPatch(target = ToolDamageItem.class, name = "getToolDps", arguments = {InventoryItem.class, Mob.class})
    public static class ToolDpsPatch {
        @Advice.OnMethodExit
        public static void boostToolDps(@Advice.Return(readOnly = false) int dps) {
            dps = TOOL_DPS;
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
            damage = TOOL_HIT_DAMAGE;
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
            time = MIN_ANIM_TIME;
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
            cooldown = NO_COOLDOWN;
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
            damage = new GameDamage(COMBAT_DAMAGE);
        }
    }
}