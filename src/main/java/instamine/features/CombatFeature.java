package instamine.features;

import instamine.config.ModConstants;
import instamine.config.ModSettings;
import instamine.config.ModSettings.FeatureToggle;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.GameDamage;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import net.bytebuddy.asm.Advice;

/**
 * Provides high combat damage output when enabled
 */
public final class CombatFeature {

    private CombatFeature() {}

    public static void bootstrap() {
        // Class load hook for annotations.
    }

    @ModMethodPatch(target = ToolItem.class, name = "getAttackDamage", arguments = {InventoryItem.class})
    public static class AttackDamagePatch {
        @Advice.OnMethodExit
        public static void boostDamage(@Advice.Return(readOnly = false) GameDamage damage) {
            if (ModSettings.isFeatureEnabled(FeatureToggle.COMBAT)) {
                damage = new GameDamage(ModConstants.Combat.DAMAGE);
            }
        }
    }
}
