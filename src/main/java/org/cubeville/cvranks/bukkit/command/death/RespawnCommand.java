package org.cubeville.cvranks.bukkit.command.death;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.cubeville.cvranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class RespawnCommand implements TabExecutor {
    
    private final CVRanksPlugin plugin;
    
    public RespawnCommand(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThe respawn command can only be used by a player.");
            return true;
        }
        if (args.length > 0) {
            return false;
        }
        
        final Player sender = (Player) commandSender;
        final UUID senderId = sender.getUniqueId();
        final long waitTime = this.plugin.getRespawnWaitTime(senderId);
        if (waitTime > 0L) {
            
            final StringBuilder builder = new StringBuilder();
            builder.append("§cYou must wait§r §6").append(this.plugin.formatWaitTime(waitTime)).append("§r §cin-game");
            builder.append("§r §b(").append(this.plugin.formatRealTimeWait(waitTime)).append(" in real-time)");
            builder.append("§r §cto use your respawn ability.");
            sender.sendMessage(builder.toString());
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
            
            if (!player.hasPermission("cvranks.death.hound") || player.getUniqueId().equals(senderId) || !player.canSee(sender)) {
                continue;
            }
            
            player.sendMessage("§6" + sender.getName() + "§r §ahas sent themselves back to their previous death location.");
        }
        
        return true;
    }
    
    @Override
    @NotNull
    public List<String> onTabComplete(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        return Collections.emptyList();
    }
}
