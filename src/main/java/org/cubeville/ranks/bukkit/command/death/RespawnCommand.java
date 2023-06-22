package org.cubeville.ranks.bukkit.command.death;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.cubeville.ranks.bukkit.command.PlayerCommand;
import org.jetbrains.annotations.NotNull;

public final class RespawnCommand extends PlayerCommand {
    
    private final CVRanksPlugin plugin;
    
    public RespawnCommand(@NotNull final CVRanksPlugin plugin) {
        super("respawn");
        
        this.plugin = plugin;
    }
    
    @Override
    protected boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final UUID senderId = sender.getUniqueId();
        final long waitTime = this.plugin.getRespawnWaitTime(senderId);
        
        if (!args.isEmpty()) {
            
            final String time = args.remove(0);
            if (!args.isEmpty() || !time.equalsIgnoreCase("time")) {
                sender.sendMessage("§cSyntax: /respawn [time]");
                return true;
            }
            
            if (waitTime > 0L) {
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Wait Times:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§fRespawn:");
                sender.sendMessage(" §f- In-game:§r §c" + this.plugin.formatWaitTime(waitTime));
                sender.sendMessage(" §f- Real-Time:§r §c" + this.plugin.formatRealWaitTime(waitTime));
                sender.sendMessage("§8--------------------------------");
                return true;
            }
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Wait Times:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§fRespawn:§r §aREADY");
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        if (waitTime > 0L) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Wait Times:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§fRespawn:");
            sender.sendMessage(" §f- In-game:§r §c" + this.plugin.formatWaitTime(waitTime));
            sender.sendMessage(" §f- Real-Time:§r §c" + this.plugin.formatRealWaitTime(waitTime));
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        if (sender.isDead()) {
            sender.sendMessage("§6Not sure how you managed to send that command, but you cannot use your respawn ability while dead.");
            return true;
        }
        
        final Location deathLocation = this.plugin.getDeathLocation(senderId);
        if (deathLocation == null) {
            sender.sendMessage("§cYou have no previous death locations that you can teleport to.");
            return true;
        }
        
        sender.teleport(deathLocation);
        this.plugin.respawnUsed(senderId);
        this.plugin.removePendingDeathHoundNotification(senderId);
        this.plugin.removeDeathLocation(senderId);
        
        sender.sendMessage("§aYou have been returned to the point of your latest death.");
        
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            
            final UUID playerId = player.getUniqueId();
            if (!player.hasPermission("cvranks.death.hound") || this.plugin.isNotifyDeathHoundDisabled(playerId) || playerId.equals(senderId) || !player.canSee(sender)) {
                continue;
            }
            
            player.sendMessage("§6" + sender.getName() + "§r §ahas sent themselves back to their previous death location.");
        }
        
        return true;
    }
    
    @Override
    @NotNull
    protected List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
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
