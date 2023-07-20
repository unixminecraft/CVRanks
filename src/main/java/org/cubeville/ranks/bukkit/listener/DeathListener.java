package org.cubeville.ranks.bukkit.listener;

import java.util.Iterator;
import java.util.UUID;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class DeathListener implements Listener {
    
    private final CVRanksPlugin plugin;
    
    public DeathListener(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(@NotNull final PlayerDeathEvent event) {
        
        final Player player = event.getEntity();
        final UUID playerId = player.getUniqueId();
        
        final boolean keepExperience;
        if (player.hasPermission("cvranks.death.te.admin")) {
            keepExperience = true;
        } else if (player.hasPermission("cvranks.death.te") && this.plugin.getXpertWaitTime(playerId) == 0L) {
            keepExperience = true;
            this.plugin.xpertUsed(playerId);
        } else {
            keepExperience = false;
        }
        
        if (keepExperience) {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
            player.sendMessage("§aYour Xpert ability has been triggered, and you have kept your XP.");
        }
        
        final boolean keepInventory;
        if (player.hasPermission("cvranks.death.ks.admin")) {
            keepInventory = true;
        } else if (player.hasPermission("cvranks.death.ks") && this.plugin.getKeepsakeWaitTime(playerId) == 0L) {
            keepInventory = true;
            this.plugin.keepsakeUsed(playerId);
        } else {
            keepInventory = false;
        }
        
        if (keepInventory) {
            event.setKeepInventory(true);
            event.getDrops().clear();
            
            final Iterator<ItemStack> iterator = player.getInventory().iterator();
            while (iterator.hasNext()) {
                final ItemStack item = iterator.next();
                if (item != null && item.getEnchantmentLevel(Enchantment.VANISHING_CURSE) > 0) {
                    iterator.remove();
                }
            }
            
            player.sendMessage("§aYour Keepsake ability has been triggered, and you have kept your inventory.");
        }
        
        this.plugin.addPendingDeathHoundNotification(playerId);
        this.plugin.addDeathLocation(playerId, player.getLocation());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(@NotNull final PlayerRespawnEvent event) {
        
        final UUID respawnPlayerId = event.getPlayer().getUniqueId();
        this.plugin.disableNightStalker(respawnPlayerId);
        this.plugin.disableScuba(respawnPlayerId);
    }
}
