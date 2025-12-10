package instamine.features;

import instamine.config.ModSettings;
import instamine.config.ModSettings.FeatureToggle;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import necesse.engine.events.loot.ObjectLootTableDropsEvent;
import necesse.engine.events.loot.TileLootTableDropsEvent;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.level.maps.Level;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import net.bytebuddy.asm.Advice;

/**
 * Multiplies ore drops from tiles and objects
 */
public final class OreFeature {

    private static final Set<String> SPECIAL_MINABLE_ITEM_IDS;
    private static final Set<String> CRYSTAL_GEM_IDS;

    static {
        HashSet<String> ids = new HashSet<>();
        ids.add("upgradeshard");
        ids.add("alchemyshard");
        SPECIAL_MINABLE_ITEM_IDS = Collections.unmodifiableSet(ids);

        HashSet<String> gems = new HashSet<>();
        gems.add("sapphire");
        gems.add("emerald");
        gems.add("ruby");
        gems.add("amethyst");
        gems.add("topaz");
        gems.add("quartz");
        gems.add("lifequartz");
        gems.add("obsidian");
        gems.add("amber");
        gems.add("frostshard");
        gems.add("runestone");
        gems.add("slimeum");
        CRYSTAL_GEM_IDS = Collections.unmodifiableSet(gems);
    }

    private OreFeature() {}

    public static void bootstrap() {
        // Class load hook for annotations.
    }

    @ModMethodPatch(target = Level.class, name = "onTileLootTableDropped", arguments = {TileLootTableDropsEvent.class})
    public static class OreDropMultiplierPatch {
        @Advice.OnMethodEnter
        public static void multiplyDrops(@Advice.Argument(0) TileLootTableDropsEvent event) {
            if (!ModSettings.isFeatureEnabled(FeatureToggle.ORE) || event == null || event.level == null || !event.level.isServer()) {
                return;
            }

            multiplyOreDrops(event.drops);
        }
    }

    @ModMethodPatch(target = Level.class, name = "onObjectLootTableDropped", arguments = {ObjectLootTableDropsEvent.class})
    public static class OreObjectDropMultiplierPatch {
        @Advice.OnMethodEnter
        public static void multiplyDrops(@Advice.Argument(0) ObjectLootTableDropsEvent event) {
            if (!ModSettings.isFeatureEnabled(FeatureToggle.ORE) || event == null || event.level == null || !event.level.isServer()) {
                return;
            }

            if (event.object != null && event.object.isOre) {
                multiplyOreDrops(event.objectDrops);
                multiplyOreDrops(event.entityDrops);
            }
        }
    }

    public static void multiplyOreDrops(List<InventoryItem> drops) {
        if (drops == null || drops.isEmpty()) return;

        int multiplier = ModSettings.getOreMultiplier();
        if (multiplier <= 1) return;

        for (InventoryItem drop : drops) {
            if (!isOreItem(drop)) continue;

            int amount = drop.getAmount();
            if (amount <= 0) continue;

            long multiplied = (long) amount * multiplier;
            drop.setAmount(multiplied > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) multiplied);
        }
    }

    private static boolean isOreItem(InventoryItem drop) {
        if (drop == null || drop.item == null) return false;

        String stringID = drop.item.getStringID();
        
        // Check for crystal gems first (quick lookup)
        if (stringID != null && CRYSTAL_GEM_IDS.contains(stringID)) {
            return true;
        }

        ItemCategory category = ItemCategory.getItemsCategory(drop.item);
        if (category != null && (category.isOrHasParent("ore") || category.isOrHasParent("minerals"))) {
            return true;
        }

        return stringID != null && SPECIAL_MINABLE_ITEM_IDS.contains(stringID);
    }
}
