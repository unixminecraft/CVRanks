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
    
    /////////////////////////
    // LEVEL COMMAND LOGIC //
    /////////////////////////
    
    private final Map<Enchantment, ExtendedEnchantment> byEnchantment;
    private final Map<String, ExtendedEnchantment> byName;
    
    public LevelCommand(@NotNull final CVRanksPlugin plugin) throws IllegalArgumentException {
        
        
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
