package org.cubeville.cvranks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
public class LevelCommand
{
    CVRanks plugin;
    
    Map<Enchantment, Integer> baseCost;
    Map<Enchantment, Integer> levelModifier;
    Map<Enchantment, Integer> maxLevel;
    Map<Enchantment, List<String>> names;

    public LevelCommand(ConfigurationSection config, CVRanks plugin) {
        this.plugin = plugin;
        baseCost = new HashMap<>();
        levelModifier = new HashMap<>();
        maxLevel = new HashMap<>();
        names = new HashMap<>();
        for(String enchantmentName: config.getKeys(false)) {
            System.out.println("LOADING ENCHANTMENT: " + enchantmentName);
            ConfigurationSection ec = config.getConfigurationSection(enchantmentName);
            Enchantment enchantment = Enchantment.getByName(enchantmentName);
            System.out.println("Enchantment: " + enchantment.toString());
            baseCost.put(enchantment, ec.getInt("base-cost"));
            levelModifier.put(enchantment, ec.getInt("level-modifier"));
            maxLevel.put(enchantment, ec.getInt("max-level"));
            names.put(enchantment, ec.getStringList("names"));
        }
    }

    public void onLevelCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;

        if(args.length == 0) {
            sender.sendMessage("§cNot enough arguments.");
            sender.sendMessage("§c/level <cost|enchantment>");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if(item == null || item.getType() == Material.AIR) {
            sender.sendMessage("§cPlease hold the item you want to level up.");
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasEnchants()) {
            sender.sendMessage("§cThat item isn't enchanted.");
            return;
        }

        if(args[0].equals("cost") && args.length == 1) {
            for(Enchantment enchantment: meta.getEnchants().keySet()) {
                int level = meta.getEnchants().get(enchantment);
                int cost = getLevelCost(enchantment, level, player);
                if(cost != -2) {
                    String message = "§a" + names.get(enchantment).get(0) + " §6- ";
                    if(cost == -1) message += "§cCannot be leveled further";
                    else message += "§a" + cost + " levels";
                    player.sendMessage(message);
                }
            }
        }
        else {
            String enchantmentName = "";
            for(int i = 0; i < args.length; i++) {
                if(i > 0) enchantmentName += " ";
                enchantmentName += args[i];
            }
            Enchantment enchantment = getEnchantmentByName(enchantmentName);
            if(enchantment == null) {
                player.sendMessage("§c" + args[0] + " is no valid enchantment name.");
                return;
            }
            if(!meta.getEnchants().containsKey(enchantment)) {
                player.sendMessage("§cItem does not have this enchantment.");
                return;
            }
            int currentLevel = meta.getEnchants().get(enchantment);
            if(currentLevel == 0) {
                player.sendMessage("§cItem does not have this enchantment.");
                return;
            }

            int cost = getLevelCost(enchantment, currentLevel, player);
            if(cost == -1) {
                player.sendMessage("§cItem can't be leveled further.");
                return;
            }
            else if(cost == -2) {
                player.sendMessage("§cThis enchantment can't be leveled.");
                return;
            }
            
            if(player.getLevel() < cost) {
                player.sendMessage("§cYou need §b" + cost + " §cexperience to level that enchantment.");
                return;
            }

            player.setLevel(player.getLevel() - cost);
            meta.addEnchant(enchantment, currentLevel + 1, true);
            item.setItemMeta(meta);
            player.sendMessage("§aYour enchantment has been leveled up.");
        }
    }

    private Enchantment getEnchantmentByName(String enchantmentName) {
        for(Enchantment enchantment: names.keySet()) {
            for(String name: names.get(enchantment)) {
                if(enchantmentName.toLowerCase().equals(name.toLowerCase())) return enchantment;
            }
        }
        return null;
    }
    
    private int getLevelCost(Enchantment enchantment, int level, Player player) {
        if (maxLevel.get(enchantment) == null || baseCost.get(enchantment) == null || levelModifier.get(enchantment) == null || names.get(enchantment) == null) return -2;
        if (level >= maxLevel.get(enchantment)) return -1;
        
        double base = baseCost.get(enchantment);
        double modifier = levelModifier.get(enchantment) / 100.0;
        for(int i = 1; i < level; i++) base *= modifier;
        if(player.hasPermission("cvranks.service.repairman.master")) base *= .80;
        int ret = (int)base;
        if(ret == 0) ret = 1;
        return ret;
    }

}
