package instamine.features;

import instamine.config.ModSettings;
import instamine.config.ModSettings.FeatureToggle;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import net.bytebuddy.asm.Advice;

/**
 * Adjusts player movement speed based on configured multiplier.
 */
public final class MovementFeature {

    private MovementFeature() {}

    public static void bootstrap() {
        // Class load hook for annotations.
    }

    @ModMethodPatch(target = Mob.class, name = "getSpeedModifier", arguments = {})
    public static class MovementSpeedPatch {
        @Advice.OnMethodExit
        public static void apply(@Advice.This Mob mob, @Advice.Return(readOnly = false) float modifier) {
            if (!ModSettings.isFeatureEnabled(FeatureToggle.MOVEMENT)) {
                return;
            }

            if (!(mob instanceof PlayerMob)) return;

            float multiplier = ModSettings.getMovementSpeedMultiplier();
            if (Math.abs(multiplier - 1.0f) < 0.0001f) return;

            modifier *= multiplier;
        }
    }
}
