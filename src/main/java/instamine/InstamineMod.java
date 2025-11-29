package instamine;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.pickaxeToolItem.PickaxeToolItem;
import necesse.entity.mobs.Mob;
import net.bytebuddy.asm.Advice;

@ModEntry
public class InstamineMod {

    // Patch 1: Override tool tier to bypass tier requirements
    @ModMethodPatch(target = PickaxeToolItem.class, name = "getToolTier", arguments = {InventoryItem.class, Mob.class})
    public static class PickaxeToolTierPatch {
        @Advice.OnMethodExit
        static void increaseToolTier(@Advice.Return(readOnly = false) float toolTier) {
            toolTier = Float.MAX_VALUE;
        }
    }

    // Patch 2: Override tool DPS for massive damage
    @ModMethodPatch(target = PickaxeToolItem.class, name = "getToolDps", arguments = {InventoryItem.class, Mob.class})
    public static class PickaxeToolDpsPatch {
        @Advice.OnMethodExit
        static void increaseToolDps(@Advice.Return(readOnly = false) int toolDps) {
            toolDps = Integer.MAX_VALUE;
        }
    }

    // Patch 3: Override mining speed modifier for instant mining
    @ModMethodPatch(target = PickaxeToolItem.class, name = "getMiningSpeedModifier", arguments = {InventoryItem.class, Mob.class})
    public static class PickaxeMiningSpeedPatch {
        @Advice.OnMethodExit
        static void increaseMiningSpeed(@Advice.Return(readOnly = false) float miningSpeed) {
            miningSpeed = Float.MAX_VALUE;
        }
    }
}