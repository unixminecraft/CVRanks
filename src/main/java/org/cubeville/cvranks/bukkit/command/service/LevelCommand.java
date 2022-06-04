package org.cubeville.cvranks.bukkit.command.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.cubeville.cvranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class LevelCommand implements TabExecutor {
    
    ///////////////////////////////////////////////////////////////
    //             EXTENDED ENCHANTMENT INNER CLASS              //
    //                                                           //
    // Used for determining the XP cost of leveling a particular //
    //   enchantment, as well as the (expanded) maximum level.   //
    ///////////////////////////////////////////////////////////////
    
    private static final class ExtendedEnchantment {
        
        private final Enchantment enchantment;
        private final int baseCost;
        private final int levelModifier;
        private final int maxLevel;
        private final List<String> names;
        
        private ExtendedEnchantment(@NotNull final Enchantment enchantment, @NotNull final ConfigurationSection config) throws IllegalArgumentException {
            
            if (!config.isSet("base-cost") || !config.isInt("base-cost")) {
                throw new IllegalArgumentException("Missing base-cost for enchantment " + enchantment.getKey().getKey() + ", skipping.");
            }
            if (!config.isSet("level-modifier") || !config.isInt("level-modifier")) {
                throw new IllegalArgumentException("Missing level-modifier for enchantment " + enchantment.getKey().getKey() + ", skipping.");
            }
            if (!config.isSet("max-level") || !config.isInt("max-level")) {
                throw new IllegalArgumentException("Missing max-level for enchantment " + enchantment.getKey().getKey() + ", skipping.");
            }
            
            this.enchantment = enchantment;
            this.baseCost = config.getInt("base-cost");
            this.levelModifier = config.getInt("level-modifier");
            this.maxLevel = config.getInt("max-level");
            this.names = config.getStringList("names");
        }
        
        @Override
        @NotNull
        public String toString() {
            
            final Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            
            data.put("enchantment", this.enchantment.getKey().toString());
            data.put("base-cost", this.baseCost);
            data.put("level-modifier", this.levelModifier);
            data.put("max-level", this.maxLevel);
            data.put("names", this.names.toString());
            
            return data.toString();
        }
    }
    
    /////////////////////////
    // LEVEL COMMAND LOGIC //
    /////////////////////////
    
    private final Map<Enchantment, ExtendedEnchantment> byEnchantment;
    private final Map<String, ExtendedEnchantment> byName;
    
    public LevelCommand(@NotNull final CVRanksPlugin plugin) throws IllegalArgumentException {
        
        final Logger logger = plugin.getLogger();
        this.byEnchantment = new ConcurrentHashMap<Enchantment, ExtendedEnchantment>();
        this.byName = new ConcurrentHashMap<String, ExtendedEnchantment>();
        
        final YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException | InvalidConfigurationException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to load the main config.", e);
        }
        
        final ConfigurationSection enchantmentsConfig = config.getConfigurationSection("enchantments");
        if (enchantmentsConfig == null) {
            throw new IllegalArgumentException("Enchantments section does not exist in CVRanks config.");
        }
        
        logger.log(Level.INFO, "LOADING ENCHANTMENTS FOR LEVELING STARTING");
        for (final String enchantmentName : enchantmentsConfig.getKeys(false)) {
            
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentName.toLowerCase()));
            if (enchantment == null) {
                // Backwards compatibility
                enchantment = Enchantment.getByName(enchantmentName);
            }
            if (enchantment == null) {
                logger.log(Level.WARNING, "Unable to find enchantment with the name " + enchantmentName + ", skipping.");
                continue;
            }
            
            final ConfigurationSection enchantmentConfig = enchantmentsConfig.getConfigurationSection(enchantmentName);
            if (enchantmentConfig == null) {
                logger.log(Level.WARNING, "No cost configuration defined for enchantment " + enchantmentName + ", skipping.");
                continue;
            }
            
            final ExtendedEnchantment extendedEnchantment;
            try {
                extendedEnchantment = new ExtendedEnchantment(enchantment, enchantmentConfig);
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, e.getMessage());
                continue;
            }
            
            ExtendedEnchantment check = this.byEnchantment.put(enchantment, extendedEnchantment);
            if (check != null && !check.enchantment.getKey().getKey().equals(enchantment.getKey().getKey())) {
                logger.log(Level.WARNING, "Duplicate registered by enchantment for " + enchantmentName + ", overwriting with the new one.");
                logger.log(Level.WARNING, "Already-registered: " + check.toString());
                logger.log(Level.WARNING, "Newly-registered: " + extendedEnchantment.toString());
            }
            
            check = this.byName.put(enchantment.getKey().getKey(), extendedEnchantment);
            if (check != null && !check.enchantment.getKey().getKey().equals(enchantment.getKey().getKey())) {
                logger.log(Level.WARNING, "Duplicate registered by name for " + enchantmentName + ", overwriting with the new one.");
                logger.log(Level.WARNING, "Already-registered: " + check.toString());
                logger.log(Level.WARNING, "Newly-registered: " + extendedEnchantment.toString());
            }
            
            check = this.byName.put(enchantment.getName(), extendedEnchantment);
            if (check != null && !check.enchantment.getKey().getKey().equals(enchantment.getKey().getKey())) {
                logger.log(Level.WARNING, "Duplicate registered by name for " + enchantmentName + ", overwriting with the new one.");
                logger.log(Level.WARNING, "Already-registered: " + check.toString());
                logger.log(Level.WARNING, "Newly-registered: " + extendedEnchantment.toString());
            }
            
            for (final String name : extendedEnchantment.names) {
                check = this.byName.put(name, extendedEnchantment);
                if (check != null && !check.enchantment.getKey().getKey().equals(enchantment.getKey().getKey())) {
                    logger.log(Level.WARNING, "Duplicate registered by name for " + enchantmentName + ", overwriting with the new one.");
                    logger.log(Level.WARNING, "Already-registered: " + check.toString());
                    logger.log(Level.WARNING, "Newly-registered: " + extendedEnchantment.toString());
                }
            }
            
            logger.log(Level.INFO, "Successfully registered enchantment " + enchantmentName + ".");
        }
    
        logger.log(Level.INFO, "LOADING ENCHANTMENTS FOR LEVELING FINISHED");
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThe level command can only be used by a player.");
            return true;
        }
        
        final Iterator<String> argsIterator = new ArrayList<String>(Arrays.asList(args)).iterator();
        if (!argsIterator.hasNext()) {
            return false;
        }
        
        final Player sender = (Player) commandSender;
        final ItemStack item = sender.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            sender.sendMessage("§cPlease hold the item you want to level up in your main hand.");
            return true;
        }
        
        if (!item.hasItemMeta()) {
            sender.sendMessage("§cThat item is not enchanted.");
            return true;
        }
        
        final ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasEnchants()) {
            sender.sendMessage("§cThat item is not enchanted.");
            return true;
        }
        
        final Map<Enchantment, Integer> enchantments = meta.getEnchants();
        final String subCommand = argsIterator.next();
        if (subCommand.equalsIgnoreCase("cost")) {
            
            if (argsIterator.hasNext()) {
                return false;
            }
            
            for (final Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
    
                final ExtendedEnchantment extendedEnchantment = this.byEnchantment.get(enchantment.getKey());
                if (extendedEnchantment == null) {
                    continue;
                }
                
                final StringBuilder builder = new StringBuilder();
                builder.append("§a").append(extendedEnchantment.names.get(0)).append("§r §f-§r ");
    
                final int cost = this.getCost(extendedEnchantment, enchantment.getValue(), sender);
                if (cost == -1) {
                    builder.append("§cCannot be leveled further");
                } else {
                    builder.append("§a").append(cost).append(" levels");
                }
                
                sender.sendMessage(builder.toString());
            }
            
            return true;
        }
        
        final StringBuilder enchantmentNameBuilder = new StringBuilder();
        enchantmentNameBuilder.append(subCommand);
        while (argsIterator.hasNext()) {
            enchantmentNameBuilder.append(" ").append(argsIterator.next());
        }
        
        final String enchantmentName = enchantmentNameBuilder.toString();
        final ExtendedEnchantment extendedEnchantment = this.byName.get(enchantmentName);
        if (extendedEnchantment == null) {
            sender.sendMessage("§6" + enchantmentName + "§r §cis not a valid enchantment.");
            return true;
        }
        
        final Enchantment enchantment = extendedEnchantment.enchantment;
        if (!enchantments.containsKey(enchantment)) {
            sender.sendMessage("§cYour item does not have the§r §6" + enchantmentName + "§r §cenchantment.");
            return true;
        }
        
        final int currentLevel = enchantments.get(enchantment);
        if (currentLevel == 0) {
            sender.sendMessage("§cYour item does not have the§r §6" + enchantmentName + "§r §cenchantment.");
            return true;
        }
        
        final int cost = this.getCost(extendedEnchantment, currentLevel, sender);
        if (cost == -1) {
            sender.sendMessage("§cThe enchantment§r §6" + enchantmentName + "§r §ccannot be leveled any further.");
            return true;
        }
        
        final int senderXPLevel = sender.getLevel();
        if (senderXPLevel < cost) {
            sender.sendMessage("§cYou do not have enough XP levels to level up the§r §6" + enchantmentName + "§r §cenchantment.");
            sender.sendMessage("§cYou need at least§r §6" + cost + "§r §cXP levels (you currently have§r §6" + sender.getLevel() + "§r §clevels).");
            return true;
        }
        
        sender.setLevel(senderXPLevel - cost);
        meta.addEnchant(enchantment, currentLevel + 1, true);
        item.setItemMeta(meta);
        sender.sendMessage("§aYou have leveled up the§r §6" + enchantmentName + "§r §aenchantment.");
        return true;
    }
    
    @Override
    @NotNull
    public List<String> onTabComplete(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            return Collections.emptyList();
        }
        
        final Player sender = (Player) commandSender;
        final ItemStack item = sender.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            return Collections.emptyList();
        }
        
        if (!item.hasItemMeta()) {
            return Collections.emptyList();
        }
        
        final ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasEnchants()) {
            return Collections.emptyList();
        }
        
        final List<String> completions = new ArrayList<String>();
        final Iterator<String> argsIterator = new ArrayList<String>(Arrays.asList(args)).iterator();
        final Map<Enchantment, Integer> enchantments = meta.getEnchants();
        for (final Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            
            final ExtendedEnchantment extendedEnchantment = this.byEnchantment.get(enchantment.getKey());
            if (extendedEnchantment == null || extendedEnchantment.maxLevel <= enchantment.getValue()) {
                continue;
            }
            
            completions.add(extendedEnchantment.enchantment.getKey().getKey());
            completions.addAll(extendedEnchantment.names);
        }
        
        completions.add("cost");
        
        if (!argsIterator.hasNext()) {
            return Collections.unmodifiableList(completions);
        }
        
        final StringBuilder subCommandBuilder = new StringBuilder();
        subCommandBuilder.append(argsIterator.next());
        while (argsIterator.hasNext()) {
            subCommandBuilder.append(" ").append(argsIterator.next());
        }
        
        final String subCommand = subCommandBuilder.toString();
        completions.removeIf(completion -> !completion.toLowerCase().startsWith(subCommand.toLowerCase()));
        return Collections.unmodifiableList(completions);
    }
    
    private int getCost(@NotNull final ExtendedEnchantment extendedEnchantment, final int currentLevel, @NotNull final Player sender) {
        
        if (currentLevel >= extendedEnchantment.maxLevel) {
            return -1;
        }
        
        double cost = extendedEnchantment.baseCost;
        final double modifier = (double) extendedEnchantment.levelModifier / 100.0D;
        cost *= Math.pow(modifier, currentLevel - 1);
        
        if (sender.hasPermission("cvranks.service.repairman.master")) {
            cost *= 0.80D;
        }
        
        int levelCost = (int) cost;
        return levelCost == 0 ? 1 : levelCost;
    }
}
