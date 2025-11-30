package instamine.features;

import instamine.config.ModConstants;
import instamine.config.ModSettings;
import instamine.config.ModSettings.FeatureToggle;
import necesse.inventory.container.object.CraftingStationContainer;

/**
 * Applies the configured storage crafting range to the base game
 */
public final class CraftingFeature {

    private CraftingFeature() {}

    public static void bootstrap() {
        applyCurrentRange();
    }

    public static void applyCurrentRange() {
        int activeBlocks = ModSettings.isFeatureEnabled(FeatureToggle.CRAFT)
            ? ModSettings.getCraftRangeBlocks()
            : ModConstants.Crafting.VANILLA_RANGE_BLOCKS;

        int clamped = Math.max(ModConstants.Crafting.MIN_RANGE_BLOCKS, activeBlocks);
        CraftingStationContainer.nearbyCraftTileRange = clamped;
    }
}
