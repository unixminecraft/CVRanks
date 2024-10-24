package org.cubeville.ranks.bukkit.listener;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class EntityListener implements Listener {
    
    private final CVRanksPlugin plugin;
    
    public EntityListener(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(@NotNull final EntityDeathEvent event) {
        
        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();
        if (killer == null || !killer.hasPermission("cvranks.leatherworker")) {
            return;
        }
        
        switch (entity.getType()) {
            case COW:
            case MOOSHROOM:
            case HORSE:
            case DONKEY:
            case MULE:
            case LLAMA:
            case TRADER_LLAMA:
            case HOGLIN:
                break;
            default:
                return;
        }
        
        entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.LEATHER));
        if (!this.plugin.isNotifyLeatherDisabled(killer.getUniqueId())) {
            killer.sendMessage("§aYou got an extra piece of leather.");
        }
    }
}
