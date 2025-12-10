package instamine.config;

import instamine.features.CraftingFeature;
import java.util.EnumSet;
import java.util.Locale;

/**
 * Holds mutable configuration and runtime state for all mod features.
 */
public final class ModSettings {

    public enum FeatureToggle {
        MINING("Instant mining"),
        RANGE("Extended range"),
        CRAFT("Storage crafting range"),
        MOVEMENT("Movement speed"),
        PICKUP("Pickup range"),
        COMBAT("Combat damage"),
        BUILD("Building speed"),
        ORE("Ore multiplier");

        private final String displayName;

        FeatureToggle(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static boolean instamineEnabled = true;
    private static boolean extendedRangeEnabled = true;
    private static boolean craftRangeEnabled = true;
    private static boolean movementSpeedEnabled = true;
    private static boolean pickupRangeEnabled = true;
    private static boolean combatBoostEnabled = true;
    private static boolean buildSpeedEnabled = true;
    private static boolean oreMultiplierEnabled = true;

    private static int extendedRangePixels = ModConstants.Range.DEFAULT_BLOCKS * ModConstants.PIXELS_PER_BLOCK;
    private static int craftRangeBlocks = ModConstants.Crafting.DEFAULT_RANGE_BLOCKS;
    private static int movementSpeedPercent = ModConstants.Movement.DEFAULT_PERCENT;
    private static int pickupRangeBlocks = ModConstants.Pickup.DEFAULT_RANGE_BLOCKS;
    private static int buildSpeedPercent = ModConstants.Building.DEFAULT_PERCENT;
    private static int combatDamagePercent = ModConstants.Combat.DEFAULT_PERCENT;
    private static int oreMultiplier = ModConstants.Ore.DEFAULT_MULTIPLIER;

    private ModSettings() {}

    public static boolean isFeatureEnabled(FeatureToggle feature) {
        switch (feature) {
            case MINING:
                return instamineEnabled;
            case RANGE:
                return extendedRangeEnabled;
            case CRAFT:
                return craftRangeEnabled;
            case MOVEMENT:
                return movementSpeedEnabled;
            case PICKUP:
                return pickupRangeEnabled;
            case COMBAT:
                return combatBoostEnabled;
            case BUILD:
                return buildSpeedEnabled;
            case ORE:
                return oreMultiplierEnabled;
            default:
                throw new IllegalArgumentException("Unhandled feature: " + feature);
        }
    }

    public static void setFeatureEnabled(FeatureToggle feature, boolean enabled) {
        switch (feature) {
            case MINING:
                instamineEnabled = enabled;
                break;
            case RANGE:
                extendedRangeEnabled = enabled;
                break;
            case CRAFT:
                craftRangeEnabled = enabled;
                CraftingFeature.applyCurrentRange();
                break;
            case MOVEMENT:
                movementSpeedEnabled = enabled;
                break;
            case PICKUP:
                pickupRangeEnabled = enabled;
                break;
            case COMBAT:
                combatBoostEnabled = enabled;
                break;
            case BUILD:
                buildSpeedEnabled = enabled;
                break;
            case ORE:
                oreMultiplierEnabled = enabled;
                break;
            default:
                throw new IllegalArgumentException("Unhandled feature: " + feature);
        }
    }

    public static void setFeaturesEnabled(EnumSet<FeatureToggle> features, boolean enabled) {
        for (FeatureToggle feature : features) {
            setFeatureEnabled(feature, enabled);
        }
    }

    public static int getExtendedRangePixels() {
        return extendedRangePixels;
    }

    public static int getExtendedRangeBlocks() {
        return extendedRangePixels / ModConstants.PIXELS_PER_BLOCK;
    }

    public static int setExtendedRangeBlocks(int blocks) {
        int clamped = clamp(blocks, 0, ModConstants.Range.MAX_BLOCKS);
        extendedRangePixels = clamped * ModConstants.PIXELS_PER_BLOCK;
        return clamped;
    }

    public static int getMaxRangeBlocks() {
        return ModConstants.Range.MAX_BLOCKS;
    }

    public static String formatRangeValue() {
        return getExtendedRangeBlocks() + " blocks / " + getExtendedRangePixels() + " px";
    }

    public static void refreshCraftRange() {
        CraftingFeature.applyCurrentRange();
    }

    public static int getCraftRangeBlocks() {
        return craftRangeBlocks;
    }

    public static int getCraftRangePixels() {
        return craftRangeBlocks * ModConstants.PIXELS_PER_BLOCK;
    }

    public static int getMinCraftRangeBlocks() {
        return ModConstants.Crafting.MIN_RANGE_BLOCKS;
    }

    public static int getMaxCraftRangeBlocks() {
        return ModConstants.Crafting.MAX_RANGE_BLOCKS;
    }

    public static int setCraftRangeBlocks(int blocks) {
        int clamped = clamp(blocks, ModConstants.Crafting.MIN_RANGE_BLOCKS, ModConstants.Crafting.MAX_RANGE_BLOCKS);
        craftRangeBlocks = clamped;
        CraftingFeature.applyCurrentRange();
        return clamped;
    }

    public static int getMovementSpeedPercent() {
        return movementSpeedPercent;
    }

    public static float getMovementSpeedMultiplier() {
        return movementSpeedEnabled ? movementSpeedPercent / 100.0f : 1.0f;
    }

    public static int getMinSpeedPercent() {
        return ModConstants.Movement.MIN_PERCENT;
    }

    public static int getMaxSpeedPercent() {
        return ModConstants.Movement.MAX_PERCENT;
    }

    public static int setMovementSpeedPercent(int percent) {
        int clamped = clamp(percent, ModConstants.Movement.MIN_PERCENT, ModConstants.Movement.MAX_PERCENT);
        movementSpeedPercent = clamped;
        return clamped;
    }

    public static int getBuildSpeedPercent() {
        return buildSpeedPercent;
    }

    public static float getBuildSpeedMultiplier() {
        return buildSpeedEnabled ? buildSpeedPercent / 100.0f : 1.0f;
    }

    public static int getMinBuildSpeedPercent() {
        return ModConstants.Building.MIN_PERCENT;
    }

    public static int getMaxBuildSpeedPercent() {
        return ModConstants.Building.MAX_PERCENT;
    }

    public static int setBuildSpeedPercent(int percent) {
        int clamped = clamp(percent, ModConstants.Building.MIN_PERCENT, ModConstants.Building.MAX_PERCENT);
        buildSpeedPercent = clamped;
        return clamped;
    }

    public static int getPickupRangeBlocks() {
        return pickupRangeBlocks;
    }

    public static float getPickupRangePixels() {
        return pickupRangeBlocks * ModConstants.PIXELS_PER_BLOCK;
    }

    public static int getMinPickupRangeBlocks() {
        return ModConstants.Pickup.MIN_RANGE_BLOCKS;
    }

    public static int getMaxPickupRangeBlocks() {
        return ModConstants.Pickup.MAX_RANGE_BLOCKS;
    }

    public static float getActivePickupRangePixels() {
        return pickupRangeEnabled ? getPickupRangePixels() : ModConstants.Pickup.VANILLA_RANGE_PIXELS;
    }

    public static int setPickupRangeBlocks(int blocks) {
        int clamped = clamp(blocks, ModConstants.Pickup.MIN_RANGE_BLOCKS, ModConstants.Pickup.MAX_RANGE_BLOCKS);
        pickupRangeBlocks = clamped;
        return clamped;
    }

    public static float applyPickupRange(float baseRange) {
        if (!pickupRangeEnabled) return baseRange;
        return Math.max(baseRange, getPickupRangePixels());
    }

    public static String formatCraftRangeValue() {
        return getCraftRangeBlocks() + " blocks / " + getCraftRangePixels() + " px";
    }

    public static String formatMovementSpeedValue() {
        return movementSpeedPercent + "% (" + String.format(Locale.ENGLISH, "%.2fx", movementSpeedPercent / 100.0f) + ")";
    }

    public static String formatBuildSpeedValue() {
        return buildSpeedPercent + "% (" + String.format(Locale.ENGLISH, "%.2fx", buildSpeedPercent / 100.0f) + ")";
    }

    public static String formatPickupRangeValue() {
        return getPickupRangeBlocks() + " blocks / " + Math.round(getPickupRangePixels()) + " px";
    }

    public static String describeFeatureState(FeatureToggle feature) {
        StringBuilder state = new StringBuilder(isFeatureEnabled(feature) ? "ON" : "OFF");
        switch (feature) {
            case RANGE:
                state.append(" (").append(formatRangeValue()).append(")");
                break;
            case CRAFT:
                state.append(" (").append(formatCraftRangeValue()).append(")");
                break;
            case MOVEMENT:
                state.append(" (").append(formatMovementSpeedValue()).append(")");
                break;
            case BUILD:
                state.append(" (").append(formatBuildSpeedValue()).append(")");
                break;
            case PICKUP:
                state.append(" (").append(formatPickupRangeValue()).append(")");
                break;
            case COMBAT:
                state.append(" (").append(formatCombatDamageValue()).append(")");
                break;
            case ORE:
                state.append(" (").append(formatOreMultiplierValue()).append(")");
                break;
            default:
                break;
        }
        return feature.getDisplayName() + ": " + state;
    }

    public static String formatFeatureStateMessage(FeatureToggle feature, boolean enabled) {
        StringBuilder message = new StringBuilder(feature.getDisplayName()).append(" ")
            .append(enabled ? "enabled" : "disabled");
        switch (feature) {
            case RANGE:
                message.append(" (").append(formatRangeValue()).append(")");
                break;
            case CRAFT:
                message.append(" (").append(formatCraftRangeValue()).append(")");
                break;
            case MOVEMENT:
                message.append(" (").append(formatMovementSpeedValue()).append(")");
                break;
            case BUILD:
                message.append(" (").append(formatBuildSpeedValue()).append(")");
                break;
            case PICKUP:
                message.append(" (").append(formatPickupRangeValue()).append(")");
                break;
            case COMBAT:
                message.append(" (").append(formatCombatDamageValue()).append(")");
                break;
            case ORE:
                message.append(" (").append(formatOreMultiplierValue()).append(")");
                break;
            default:
                break;
        }
        return message.toString();
    }

    // Combat damage settings
    public static int getCombatDamageMultiplierRaw() {
        return combatDamagePercent;
    }

    public static float getCombatDamageMultiplier() {
        return combatBoostEnabled ? (float) combatDamagePercent : 1.0f;
    }

    public static int getMinCombatDamagePercent() {
        return ModConstants.Combat.MIN_PERCENT;
    }

    public static int getMaxCombatDamagePercent() {
        return ModConstants.Combat.MAX_PERCENT;
    }

    public static int setCombatDamagePercent(int multiplier) {
        int clamped = clamp(multiplier, ModConstants.Combat.MIN_PERCENT, ModConstants.Combat.MAX_PERCENT);
        combatDamagePercent = clamped;
        return clamped;
    }

    public static String formatCombatDamageValue() {
        return combatDamagePercent + "x";
    }

    // Ore multiplier settings
    public static int getOreMultiplier() {
        return oreMultiplierEnabled ? oreMultiplier : 1;
    }

    public static int getOreMultiplierRaw() {
        return oreMultiplier;
    }

    public static int getMinOreMultiplier() {
        return ModConstants.Ore.MIN_MULTIPLIER;
    }

    public static int getMaxOreMultiplier() {
        return ModConstants.Ore.MAX_MULTIPLIER;
    }

    public static int setOreMultiplier(int multiplier) {
        int clamped = clamp(multiplier, ModConstants.Ore.MIN_MULTIPLIER, ModConstants.Ore.MAX_MULTIPLIER);
        oreMultiplier = clamped;
        return clamped;
    }

    public static String formatOreMultiplierValue() {
        return oreMultiplier + "x";
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}
