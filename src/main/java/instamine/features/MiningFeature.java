package instamine.features;

import instamine.config.ModConstants;
import instamine.config.ModSettings;
import instamine.config.ModSettings.FeatureToggle;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import net.bytebuddy.asm.Advice;

/**
 * Handles instant mining behavior
 */
public final class MiningFeature {

    private MiningFeature() {}

    public static void bootstrap() {
        // Class load hook for annotations.
    }

    private static boolean enabled() {
        return ModSettings.isFeatureEnabled(FeatureToggle.MINING);
    }

    @ModMethodPatch(target = ToolDamageItem.class, name = "getToolDps", arguments = {InventoryItem.class, Mob.class})
    public static class ToolDpsPatch {
        @Advice.OnMethodExit
        public static void boostToolDps(@Advice.Return(readOnly = false) int dps) {
            if (enabled()) {
                dps = ModConstants.Mining.TOOL_DPS;
            }
        }
    }

    @ModMethodPatch(target = ToolDamageItem.class, name = "getToolHitDamage", arguments = {InventoryItem.class, int.class, ItemAttackerMob.class})
    public static class ToolHitDamagePatch {
        @Advice.OnMethodExit
        public static void boostHitDamage(@Advice.Return(readOnly = false) int damage) {
            if (enabled()) {
                damage = ModConstants.Mining.TOOL_HIT_DAMAGE;
            }
        }
    }

    @ModMethodPatch(target = ToolDamageItem.class, name = "getAttackAnimTime", arguments = {InventoryItem.class, ItemAttackerMob.class})
    public static class AttackAnimTimePatch {
        @Advice.OnMethodExit
        public static void minimizeAnimTime(@Advice.Return(readOnly = false) int time) {
            if (enabled()) {
                time = ModConstants.Mining.MIN_ANIM_TIME;
            }
        }
    }

    @ModMethodPatch(target = ToolDamageItem.class, name = "getAttackHandlerDamageCooldown", arguments = {InventoryItem.class, ItemAttackerMob.class})
    public static class DamageCooldownPatch {
        @Advice.OnMethodExit
        public static void removeCooldown(@Advice.Return(readOnly = false) int cooldown) {
            if (enabled()) {
                cooldown = ModConstants.Mining.NO_COOLDOWN;
            }
        }
    }
}
