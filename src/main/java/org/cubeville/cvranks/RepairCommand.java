package org.cubeville.cvranks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RepairCommand
{
    CVRanks plugin;

    Map<UUID, Integer> lastUsage;
    
    public RepairCommand(CVRanks plugin) {
        this.plugin = plugin;
        lastUsage = new HashMap<>();
    }

    public int getTime(Player player) {
        if(lastUsage.get(player.getUniqueId()) == null) return 0;
        int rtime = 2400;
        if(player.hasPermission("cvranks.service.repairman.master")) rtime = 1800;
        int htime = plugin.getUptime() - lastUsage.get(player.getUniqueId());
        return rtime - htime;
    }
    
    public void onRepairCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;

        if(args.length > 1 || (args.length == 1 && args[0].equals("cost") == false && args[0].equals("time") == false)) {
            player.sendMessage("§cWrong number of arguments.");
            player.sendMessage("§c/rp [cost|time]");
            return;
        }

        if(args.length == 1 && args[0].equals("time")) {
            int time = getTime(player);
            if(time <= 0) {
                player.sendMessage("§aYour repair ability is ready to use.");
            }
            else {
                time /= 50;
                time += 1;
                player.sendMessage("§eYour repair ability will recharge in §b" + time + "§e hours.");
            }
            return;
        }
        
        ItemStack item = player.getItemInHand();
        if(item == null || item.getType() == Material.AIR) {
            player.sendMessage("§cPlease hold the item that you want to repair.");
            return;
        }

        if(!isRepairable(item)) {
            player.sendMessage("§cThat item is not repairable.");
            return;
        }
        
        if(args.length == 1 && args[0].equals("cost")) {
            player.sendMessage("You need §b" + getRepairCost(item) + "§r to repair this item.");
            return;
        }
        else if(args.length == 0) {
            if(getTime(player) > 0) {
                player.sendMessage("§cYour repair ability is not ready yet.");
                return;
            }
            if(!(removeReagents(item, player.getInventory()))) {
                player.sendMessage("You need §b" + getRepairCost(item) + "§r to repair this item.");
                return;
            }
            lastUsage.put(player.getUniqueId(), plugin.getUptime());
            repair(item);
        }
    }
    
    public static boolean isRepairable(ItemStack item)
    {
        return (item != null) && (getRepairCost(item) != null) && (item.getDurability() > 0);
    }

    public static String getRepairCost(ItemStack item)
    {
        switch (item.getType()) {
        case DIAMOND_AXE: 
        case DIAMOND_HOE: 
        case DIAMOND_PICKAXE: 
        case DIAMOND_SHOVEL: 
        case DIAMOND_SWORD: 
            return "1 diamond";
     
        case IRON_AXE: 
        case IRON_HOE: 
        case IRON_PICKAXE: 
        case IRON_SHOVEL: 
        case IRON_SWORD: 
            return "1 iron ingot";
    
        case GOLDEN_AXE: 
        case GOLDEN_HOE: 
        case GOLDEN_PICKAXE: 
        case GOLDEN_SHOVEL: 
        case GOLDEN_SWORD: 
            return "1 gold ingot";
     
        case STONE_AXE: 
        case STONE_HOE: 
        case STONE_PICKAXE: 
        case STONE_SHOVEL: 
        case STONE_SWORD: 
            return "1 cobblestone";
     
        case WOODEN_AXE: 
        case WOODEN_HOE: 
        case WOODEN_PICKAXE: 
        case WOODEN_SHOVEL: 
        case WOODEN_SWORD: 
            return "1 wood plank";
    
        case DIAMOND_CHESTPLATE: 
            return "4 diamonds";
    
        case DIAMOND_LEGGINGS: 
            return "3 diamonds";
     
        case DIAMOND_BOOTS: 
        case DIAMOND_HELMET: 
            return "2 diamonds";
     
        case IRON_CHESTPLATE: 
        case CHAINMAIL_CHESTPLATE: 
            return "4 iron ingots";
     
        case IRON_LEGGINGS: 
        case CHAINMAIL_LEGGINGS: 
            return "3 iron ingots";
     
        case IRON_BOOTS: 
        case IRON_HELMET: 
        case CHAINMAIL_BOOTS: 
        case CHAINMAIL_HELMET: 
            return "2 iron ingots";
    
        case GOLDEN_CHESTPLATE: 
            return "4 gold ingots";
     
        case GOLDEN_LEGGINGS: 
            return "3 gold ingots";
    
        case GOLDEN_BOOTS: 
        case GOLDEN_HELMET: 
            return "2 gold ingots";
     
        case LEATHER_CHESTPLATE: 
            return "4 leather";
     
        case LEATHER_LEGGINGS: 
            return "3 leather";
     
        case LEATHER_BOOTS: 
        case LEATHER_HELMET: 
            return "2 leather";
     
        case SHEARS: 
            return "1 iron ingot";
     
        case BOW: 
            return "1 log";
     
        case FISHING_ROD: 
            return "1 log";
        }
     
        return null;
    }

    public static boolean removeReagents(ItemStack repair, Inventory inv)
    {
        ItemStack[] inventory = inv.getContents();
        
        String cost = getRepairCost(repair);
        Material material;
        if (cost.contains("diamond"))
            material = Material.DIAMOND;
        else if (cost.contains("iron ingot"))
            material = Material.IRON_INGOT;
        else if (cost.contains("gold ingot"))
            material = Material.GOLD_INGOT;
        else if (cost.contains("cobblestone"))
            material = Material.COBBLESTONE;
        else if (cost.contains("wood plank"))
            material = Material.OAK_WOOD;
        else if (cost.contains("leather"))
            material = Material.LEATHER;
        else if (cost.contains("log"))
            material = Material.OAK_LOG;
        else return false;

        int amount;
        try { amount = Integer.parseInt(cost.substring(0, 1));
        } catch (NumberFormatException ex) {
            return false;
        }
        
        if (amount <= 0) {
            return false;
        }
        
        int has = 0;
        
        for (ItemStack item : inventory) {
            if ((item != null) && (item.getType() == material) && (item.getAmount() > 0)) {
                has += item.getAmount();
            }
        }
        
        if (has < amount) {
            return false;
        }
        
        for (int slot = inv.getSize() - 1; (slot >= 0) && (amount > 0); slot--) {
            ItemStack item = inventory[slot];
            
            if ((item != null) && (item.getType() == material))
                {
                    
                    
                    if (item.getAmount() <= amount) {
                        amount -= item.getAmount();
                        inv.clear(slot);
                    } else {
                        item.setAmount(item.getAmount() - amount);
                        break;
                    }
                }
        }
        return true;
    }
    
    public static void repair(ItemStack item)
    {
        item.setDurability((short)0);
        
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasEnchants()) return;

        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            Enchantment e = (Enchantment)entry.getKey();
            if(!e.isCursed()) {
                int level = ((Integer)entry.getValue()).intValue();
                
                meta.removeEnchant(e);
                if (level > 1) meta.addEnchant(e, level - 1, true);
            }
        }
        item.setItemMeta(meta);
    }

}
