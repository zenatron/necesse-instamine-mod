package instamine.config;

/**
 * Central store for immutable tuning constants used by the mod
 */
public final class ModConstants {

    public static final int PIXELS_PER_BLOCK = 32;

    public static final class Mining {
        public static final int TOOL_DPS = 999_999;
        public static final int TOOL_HIT_DAMAGE = 999_999;
        public static final int MIN_ANIM_TIME = 1;
        public static final int NO_COOLDOWN = 0;

        private Mining() {}
    }

    public static final class Combat {
        public static final float DAMAGE = 1_000_000f;

        private Combat() {}
    }

    public static final class Range {
        public static final int DEFAULT_BLOCKS = 16;
        public static final int MAX_BLOCKS = 100;

        private Range() {}
    }

    public static final class Crafting {
        public static final int VANILLA_RANGE_BLOCKS = 9;
        public static final int DEFAULT_RANGE_BLOCKS = 50;
        public static final int MIN_RANGE_BLOCKS = 0;
        public static final int MAX_RANGE_BLOCKS = 200;

        private Crafting() {}
    }

    public static final class Movement {
        public static final int DEFAULT_PERCENT = 200;
        public static final int MIN_PERCENT = 10;
        public static final int MAX_PERCENT = 1_000;

        private Movement() {}
    }

    public static final class Pickup {
        public static final float VANILLA_RANGE_PIXELS = 60f;
        public static final int DEFAULT_RANGE_BLOCKS = 10;
        public static final int MIN_RANGE_BLOCKS = 1;
        public static final int MAX_RANGE_BLOCKS = 200;

        private Pickup() {}
    }

    public static final class Ore {
        public static final int DROP_MULTIPLIER = 10;

        private Ore() {}
    }

    private ModConstants() {}
}
