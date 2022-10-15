package org.cubeville.cvranks.bukkit.command.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.cubeville.cvranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class RepairCommand implements TabExecutor {
    
    private final CVRanksPlugin plugin;
    
    public RepairCommand(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThe repair command can only be used by a player.");
            return true;
        }
        if (args.length > 1) {
            return false;
        }
        if (args.length == 1 && !args[0].equalsIgnoreCase("time")) {
            return false;
        }
        
        final Player sender = (Player) commandSender;
        final UUID senderId = sender.getUniqueId();
        final long waitTime = this.plugin.getRepairWaitTime(senderId);
        if (waitTime > 0L) {
            
            final StringBuilder builder = new StringBuilder();
            builder.append("§cYou must wait§r §6").append(this.plugin.formatWaitTime(waitTime)).append("§r §cin-game");
            builder.append("§r §b(").append(this.plugin.formatRealTimeWait(waitTime)).append(" in real time)");
            builder.append("§r §cto use your repair ability.");
            sender.sendMessage(builder.toString());
            return true;
        }
        
        if (args.length == 1) {
            sender.sendMessage(CVRanksPlugin.ABILITY_READY_REPAIR);
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
    public List<String> onTabComplete(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        return commandSender instanceof Player ? List.of("time") : Collections.emptyList();
    }
}
