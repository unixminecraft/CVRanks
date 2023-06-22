package org.cubeville.ranks.bukkit.command.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.cubeville.ranks.bukkit.command.PlayerCommand;
import org.jetbrains.annotations.NotNull;

public final class RepairCommand extends PlayerCommand {
    
    private final CVRanksPlugin plugin;
    
    public RepairCommand(@NotNull final CVRanksPlugin plugin) {
        super("repair");
        
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final UUID senderId = sender.getUniqueId();
        final long waitTime = this.plugin.getRepairWaitTime(senderId);
        
        if (!args.isEmpty()) {
            
            final String time = args.remove(0);
            if (!args.isEmpty() || !time.equalsIgnoreCase("time")) {
                sender.sendMessage("§cSyntax: /rp [time]");
                return true;
            }
            
            if (waitTime > 0L) {
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Wait Times:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§fRepair:");
                sender.sendMessage(" §f- In-game:§r §c" + this.plugin.formatWaitTime(waitTime));
                sender.sendMessage(" §f- Real-Time:§r §c" + this.plugin.formatRealWaitTime(waitTime));
                sender.sendMessage("§8--------------------------------");
                return true;
            }
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Wait Times:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§fRepair:§r §aREADY");
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        if (waitTime > 0L) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Wait Times:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§fRepair:");
            sender.sendMessage(" §f- In-game:§r §c" + this.plugin.formatWaitTime(waitTime));
            sender.sendMessage(" §f- Real-Time:§r §c" + this.plugin.formatRealWaitTime(waitTime));
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        final ItemStack item = sender.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            sender.sendMessage("§cPlease hold the item you wish to repair.");
            return true;
        }
        
        final String itemName = item.getType().name().replace("_", " ").toLowerCase();
        final ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals("§f\u231B §3\u0F3A §8§k|§r §3e§7x§fp§7e§3n§fd§3a§7b§fl§7e §3e§fl§3y§7t§fr§7a §8§k|§r §3\u0F3B §f\u231B")) {
            sender.sendMessage("§cYou may not repair the expendable elytra.");
            return true;
        }
        
        if (!(meta instanceof Damageable)) {
            sender.sendMessage("§cYour§r §6" + itemName + "§r §cis not repairable.");
            return true;
        }
        
        final Damageable damage = (Damageable) meta;
        if (damage.getDamage() == 0) {
            sender.sendMessage("§cYour§r §6" + itemName + "§r §cdoes not need to be repaired.");
            return true;
        }
        
        damage.setDamage(0);
        item.setItemMeta(damage);
        this.plugin.repairUsed(senderId);
        
        sender.sendMessage("§aYou have successfully repaired your§r §6" + itemName + "§r§c.");
        return true;
    }
    
    @Override
    @NotNull
    public List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final List<String> completions = new ArrayList<String>();
        completions.add("time");
        if (args.isEmpty()) {
            return completions;
        }
        
        final String time = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(time.toLowerCase()));
            return completions;
        }
        
        return Collections.emptyList();
    }
}
