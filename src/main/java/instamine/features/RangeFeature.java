package instamine.features;

import instamine.config.ModSettings;
import instamine.config.ModSettings.FeatureToggle;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolItem;
import net.bytebuddy.asm.Advice;

/**
 * Extends mining and attack interaction range
 */
public final class RangeFeature {

    private RangeFeature() {}

    public static void bootstrap() {
        // Class load hook for annotations.
    }

    private static boolean enabled() {
        return ModSettings.isFeatureEnabled(FeatureToggle.RANGE);
    }

    private static int extendedRange() {
        return ModSettings.getExtendedRangePixels();
    }

    @ModMethodPatch(target = ToolDamageItem.class, name = "getMiningRange", arguments = {InventoryItem.class, ItemAttackerMob.class})
    public static class MiningRangePatch {
        @Advice.OnMethodExit
        public static void extendRange(@Advice.Return(readOnly = false) int range) {
            if (enabled()) {
                range = extendedRange();
            }
        }
    }

    @ModMethodPatch(target = ToolItem.class, name = "getAttackRange", arguments = {InventoryItem.class})
    public static class AttackRangePatch {
        @Advice.OnMethodExit
        public static void extendAttackRange(@Advice.Return(readOnly = false) int range) {
            if (enabled()) {
                range = extendedRange();
            }
        }
    }
}
