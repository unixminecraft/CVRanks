package org.cubeville.ranks.bukkit.listener;

import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class GeneralListener implements Listener {
    
    private final CVRanksPlugin plugin;
    
    public GeneralListener(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    ////////////////////////////////////
    // COMMAND TAB-COMPLETION UPDATES //
    ////////////////////////////////////
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandSend(@NotNull final PlayerCommandSendEvent event) {
        event.getCommands().removeAll(this.plugin.onPlayerCommandSend(event.getPlayer()));
    }
    
    /////////////////////////////////////////
    // GENERAL ABILITY TIMER NOTIFICATIONS //
    /////////////////////////////////////////
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(@NotNull final PlayerJoinEvent event) {
        
        final UUID playerId = event.getPlayer().getUniqueId();
        
        this.plugin.checkDoctorResetNotification(playerId);
        this.plugin.checkRepairResetNotification(playerId);
        this.plugin.checkXpertResetNotification(playerId);
        this.plugin.checkKeepsakeResetNotification(playerId);
        this.plugin.checkDeathHoundResetNotification(playerId);
        this.plugin.checkRespawnResetNotification(playerId);
    }
}
