package instamine.features;

/**
 * Load feature classes to apply their patches and initialize them
 */
public final class FeatureRegistry {

    private FeatureRegistry() {}

    public static void initialize() {
        MiningFeature.bootstrap();
        RangeFeature.bootstrap();
        CraftingFeature.bootstrap();
        MovementFeature.bootstrap();
        PickupFeature.bootstrap();
        CombatFeature.bootstrap();
        BuildingFeature.bootstrap();
        OreFeature.bootstrap();
    }
}
