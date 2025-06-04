package com.ycelaschi.cobblemonspawncustom.config;

public class SpeciesConfig {
    public final int ivValue;
    public final int ivQuantity;
    public final double shinyChance;
    public final double haChance;

    public SpeciesConfig(int ivValue, int ivQuantity, double shinyChance, double haChance) {
        this.ivValue = ivValue;
        this.ivQuantity = Math.max(1, Math.min(ivQuantity, 6));
        this.shinyChance = shinyChance;
        this.haChance = haChance;
    }
}
