package org.cubeville.cvranks.bukkit.listener;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.cubeville.cvranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class CVRanksEventListener implements Listener {
    
    private final CVRanksPlugin plugin;
    
    public CVRanksEventListener(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        
        final Player respawnPlayer = event.getPlayer();
        final UUID respawnPlayerId = respawnPlayer.getUniqueId();
        
        this.plugin.disableNightStalker(respawnPlayerId);
        this.plugin.disableScuba(respawnPlayerId);
        
        // This might not stay around.
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            
            final UUID playerId = player.getUniqueId();
            if (!player.hasPermission("cvranks.death.hound") || playerId.equals(respawnPlayerId) || !player.canSee(respawnPlayer)) {
                continue;
            }
            
            player.sendMessage("§6" + respawnPlayer.getName() + "§r §ahas respawned, their death coordinates may be sent to them.");
        }
    }
}
