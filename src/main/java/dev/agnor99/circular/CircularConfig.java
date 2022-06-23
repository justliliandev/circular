package dev.agnor99.circular;

import net.minecraftforge.common.ForgeConfigSpec;

public class CircularConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec.ConfigValue<Double> CHANCE_FOR_SCREEN;
    public static final ForgeConfigSpec.ConfigValue<Double> CHANCE_FOR_EASTER_EGG;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        CHANCE_FOR_SCREEN = configBuilder.comment("Chance for the round screen to be seen").defineInRange("chanceForScreen", 1D, 0D, 1D);
        CHANCE_FOR_EASTER_EGG = configBuilder.comment("Chance for the easter egg").defineInRange("chanceForEasterEgg", 0.10D, 0D, 1D);
        CLIENT_SPEC = configBuilder.build();
    }
}
