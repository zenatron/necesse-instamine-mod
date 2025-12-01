package instamine.features;

import instamine.config.ModConstants;
import instamine.config.ModSettings;
import instamine.config.ModSettings.FeatureToggle;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import net.bytebuddy.asm.Advice;

/**
 * Provides high combat damage output when enabled
 */
public final class CombatFeature {

    private CombatFeature() {}

    public static void bootstrap() {
        // Class load hook for annotations
    }

    @ModMethodPatch(target = ToolItem.class, name = "getAttackDamageValue", arguments = {InventoryItem.class, Attacker.class})
    public static class AttackDamagePatch {
        @Advice.OnMethodExit
        public static void boostDamage(
            @Advice.Argument(1) Attacker attacker,
            @Advice.Return(readOnly = false) int damage
        ) {
            if (!ModSettings.isFeatureEnabled(FeatureToggle.COMBAT) || attacker == null) {
                return;
            }

            PlayerMob playerOwner = attacker.getFirstPlayerOwner();
            if (playerOwner != null) {
                damage = ModConstants.Combat.DAMAGE;
            }
        }
    }

    @ModMethodPatch(target = GameDamage.class, name = "getBuffedDamage", arguments = {Attacker.class})
    public static class PlayerBuffedDamagePatch {
        @Advice.OnMethodExit
        public static void boostDamage(
            @Advice.Argument(0) Attacker attacker,
            @Advice.Return(readOnly = false) float damage
        ) {
            if (!ModSettings.isFeatureEnabled(FeatureToggle.COMBAT) || attacker == null) {
                return;
            }

            Mob attackOwner = attacker.getAttackOwner();
            if (!(attackOwner instanceof PlayerMob)) {
                return;
            }

            PlayerMob playerOwner = attacker.getFirstPlayerOwner();
            if (playerOwner == null) {
                return;
            }

            damage = Math.max(damage, ModConstants.Combat.DAMAGE);
        }
    }
}
