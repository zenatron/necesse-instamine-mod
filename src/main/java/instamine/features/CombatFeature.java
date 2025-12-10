package instamine.features;

import instamine.config.ModSettings;
import instamine.config.ModSettings.FeatureToggle;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import net.bytebuddy.asm.Advice;

/**
 * Provides combat damage multiplier when enabled.
 * This mod only runs on machines where it's installed, so the multiplier
 * only applies to players who have the mod.
 * 
 * Only patches getBuffedDamage which is used for both tooltip display and
 * runtime damage calculation, avoiding double-application issues.
 */
public final class CombatFeature {

    private CombatFeature() {}

    public static void bootstrap() {
        // Class load hook for annotations
    }

    /**
     * Patches damage calculation used for both tooltips and runtime damage.
     */
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

            // Only boost player damage, not NPC/mob damage
            Mob attackOwner = attacker.getAttackOwner();
            if (!(attackOwner instanceof PlayerMob)) {
                return;
            }

            float multiplier = ModSettings.getCombatDamageMultiplier();
            if (multiplier == 1.0f) {
                return;
            }

            damage *= multiplier;
        }
    }
}
