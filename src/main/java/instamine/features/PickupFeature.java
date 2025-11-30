package instamine.features;

import instamine.config.ModSettings;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.entity.pickup.PickupEntity;
import net.bytebuddy.asm.Advice;

/**
 * Extends how far away dropped items can magnetise to the player.
 */
public final class PickupFeature {

    private PickupFeature() {}

    public static void bootstrap() {
        // Class load hook for annotations.
    }

    @ModMethodPatch(target = PickupEntity.class, name = "getTargetRange", arguments = {ServerClient.class})
    public static class PickupTargetRangePatch {
        @Advice.OnMethodExit
        public static void apply(@Advice.Return(readOnly = false) float range) {
            range = ModSettings.applyPickupRange(range);
        }
    }

    @ModMethodPatch(target = PickupEntity.class, name = "getTargetStreamRange", arguments = {})
    public static class PickupTargetStreamRangePatch {
        @Advice.OnMethodExit
        public static void apply(@Advice.Return(readOnly = false) float range) {
            range = ModSettings.applyPickupRange(range);
        }
    }
}
