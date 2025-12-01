package instamine.features;

import instamine.config.ModSettings;
import instamine.config.ModSettings.FeatureToggle;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.PlaceableItem;
import net.bytebuddy.asm.Advice;

/**
 * Increases building speed when enabled
 */
public final class BuildingFeature {

    private BuildingFeature() {}

    public static void bootstrap() {
        // Class load hook for annotations.
    }

    @ModMethodPatch(target = PlaceableItem.class, name = "getAttackHandlerPlaceCooldown", arguments = {InventoryItem.class, ItemAttackerMob.class})
    public static class PlaceCooldownPatch {
        @Advice.OnMethodExit
        public static void adjustCooldown(
            @Advice.Argument(1) ItemAttackerMob attackerMob,
            @Advice.Return(readOnly = false) int cooldown
        ) {
            if (!ModSettings.isFeatureEnabled(FeatureToggle.BUILD) || !(attackerMob instanceof PlayerMob) || cooldown <= 0) {
                return;
            }

            float multiplier = ModSettings.getBuildSpeedMultiplier();
            if (multiplier <= 0f || multiplier == 1.0f) {
                return;
            }

            int adjusted = Math.max(0, Math.round(cooldown / multiplier));
            cooldown = adjusted;
        }
    }
}
