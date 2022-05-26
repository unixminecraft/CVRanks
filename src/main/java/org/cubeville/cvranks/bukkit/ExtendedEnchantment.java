package org.cubeville.cvranks.bukkit;

import java.util.Collections;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public final class ExtendedEnchantment {
    
    private final Enchantment enchantment;
    private final int baseCost;
    private final int levelModifier;
    private final int maxLevel;
    private final List<String> names;
    
    public ExtendedEnchantment(@NotNull final Enchantment enchantment, @NotNull final ConfigurationSection config) throws IllegalArgumentException {
        
        if (!config.isSet("base-cost") || !config.isInt("base-cost")) {
            throw new IllegalArgumentException("Missing base-cost for enchantment " + enchantment.getKey().getKey());
        }
        if (!config.isSet("level-modifier") || !config.isInt("level-modifier")) {
            throw new IllegalArgumentException("Missing level-modifier for enchantment " + enchantment.getKey().getKey());
        }
        if (!config.isSet("max-level") || !config.isInt("max-level")) {
            throw new IllegalArgumentException("Missing max-level for enchantment " + enchantment.getKey().getKey());
        }
        
        this.enchantment = enchantment;
        this.baseCost = config.getInt("base-cost");
        this.levelModifier = config.getInt("level-modifier");
        this.maxLevel = config.getInt("max-level");
        this.names = config.getStringList("names");
    }
    
    @NotNull
    public Enchantment getEnchantment() {
        return this.enchantment;
    }
    
    public int getBaseCost() {
        return this.baseCost;
    }
    
    public int getLevelModifier() {
        return this.levelModifier;
    }
    
    public int getMaxLevel() {
        return this.maxLevel;
    }
    
    @NotNull
    public List<String> getNames() {
        return Collections.unmodifiableList(this.names);
    }
}
