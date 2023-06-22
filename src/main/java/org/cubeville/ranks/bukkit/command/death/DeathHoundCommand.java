package org.cubeville.ranks.bukkit.command.death;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.cubeville.ranks.bukkit.command.PlayerCommand;
import org.jetbrains.annotations.NotNull;

public final class DeathHoundCommand extends PlayerCommand {
    
    private final CVRanksPlugin plugin;
    private final Set<String> toggles;
    
    public DeathHoundCommand(@NotNull final CVRanksPlugin plugin) {
        super("death hound");
        
        this.plugin = plugin;
        this.toggles = new HashSet<String>(Arrays.asList("on", "off"));
    }
    
    @Override
    protected boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final boolean hasDH = sender.hasPermission("cvranks.death.hound");
        
        final UUID senderId = sender.getUniqueId();
        
        if (args.isEmpty()) {
            
            sender.sendMessage("§8--------------------------------");
            if (hasDH) {
                final long waitTime = this.plugin.getDeathHoundWaitTime(senderId);
                sender.sendMessage("§bRank Wait Times:");
                sender.sendMessage("§8--------------------------------");
                if (waitTime > 0L) {
                    sender.sendMessage("§fDeath Hound:");
                    sender.sendMessage(" §f- In-game:§r §c" + this.plugin.formatWaitTime(waitTime));
                    sender.sendMessage(" §f- Real-Time:§r §c" + this.plugin.formatRealWaitTime(waitTime));
                } else {
                    sender.sendMessage("§fDeath Hound:§r §aREADY");
                }
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Notification Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- Death Hound:§r " + (this.plugin.isNotifyDeathHoundDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage("§8--------------------------------");
            }
            sender.sendMessage("§bAvailable Death Hound commands:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f-§r §a/dh list");
            if (hasDH) {
                sender.sendMessage(" §f-§r §a/dh time");
                sender.sendMessage(" §f-§r §a/dh me");
                sender.sendMessage(" §f-§r §a/dh <player>");
                sender.sendMessage(" §f-§r §a/dh notify [" + (this.plugin.isNotifyDeathHoundDisabled(senderId) ? "on" : "off") + "]");
            }
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        final String subCommand = args.remove(0);
        if (subCommand.equalsIgnoreCase("list")) {
            
            final List<String> messages = new ArrayList<String>();
            for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
                
                if (!player.hasPermission("cvranks.death.hound") || player.hasPermission("cvranks.death.hound.hidefromlist") || !sender.canSee(player)) {
                    continue;
                }
                
                final long waitTime = this.plugin.getDeathHoundWaitTime(player.getUniqueId());
                final String prefix = " §f-§r §b" + player.getName() + "§r ";
                if (waitTime == 0L) {
                    messages.add(prefix + "§ais ready.");
                } else {
                    messages.add(prefix + "§6has " + this.plugin.formatWaitTime(waitTime) + " left");
                }
            }
            
            if (messages.isEmpty()) {
                sender.sendMessage("§cNo death hounds online.");
                return true;
            }
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bOnline Death Hounds - (in-game hours)");
            sender.sendMessage("§8--------------------------------");
            for (final String message : messages) {
                sender.sendMessage(message);
            }
            sender.sendMessage("§8--------------------------------");
            
            return true;
        }
        
        if (!hasDH) {
            sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
            return true;
        }
        
        if (subCommand.equalsIgnoreCase("notify")) {
            
            if (args.isEmpty()) {
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Notification Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- Death Hound:§r " + (this.plugin.isNotifyDeathHoundDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
            }
            
            final String toggle = args.remove(0);
            if (!args.isEmpty() || !this.toggles.contains(toggle.toLowerCase())) {
                sender.sendMessage("§cSyntax: /dh <list|time|me|[player]|notify [on|off]>");
                return true;
            }
            
            final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyDeathHound(senderId) : this.plugin.disableNotifyDeathHound(senderId);
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Death Hound:§r " + (this.plugin.isNotifyDeathHoundDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        final long waitTime = this.plugin.getDeathHoundWaitTime(senderId);
        if (waitTime > 0L) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Wait Times:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§fDeath Hound:");
            sender.sendMessage(" §f- In-game:§r §c" + this.plugin.formatWaitTime(waitTime));
            sender.sendMessage(" §f- Real-Time:§r §c" + this.plugin.formatRealWaitTime(waitTime));
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        if (subCommand.equalsIgnoreCase("time")) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Wait Times:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§fDeath Hound:§r §aREADY");
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        final Player target;
        if (subCommand.equalsIgnoreCase("me") || subCommand.equalsIgnoreCase(sender.getName())) {
            target = sender;
        } else {
            target = this.plugin.getServer().getPlayer(subCommand);
            
            if (target == null || !target.isOnline() || !sender.canSee(target)) {
                sender.sendMessage("§cPlayer§r §6" + subCommand + "§r §cnot found.");
                return true;
            }
        }
        
        final UUID targetId = target.getUniqueId();
        final boolean self = senderId.equals(targetId);
        
        if (target.isDead()) {
            if (self) {
                sender.sendMessage("§6Not sure how you managed to send that command, but you cannot send yourself death coordinates while dead.");
            } else {
                sender.sendMessage("§cPlease wait until§r §6" + target.getName() + "§r §chas respawned to use your death hound ability on them.");
                sender.sendMessage("§cYou will get a notification when they respawn.");
            }
            return true;
        }
        
        final Location deathLocation = this.plugin.getDeathLocation(targetId);
        if (deathLocation == null) {
            sender.sendMessage("§6" + target.getName() + "§r §chas not died recently.");
            return true;
        }
        
        if (!this.plugin.isPendingDeathHoundNotification(targetId)) {
            sender.sendMessage("§6" + target.getName() + "§r §chas already had their death coordinates sent to them, or they used /respawn.");
            return true;
        }
        
        this.plugin.deathHoundUsed(senderId);
        this.plugin.removePendingDeathHoundNotification(targetId);
        if (!target.hasPermission("cvranks.death.respawn")) {
            this.plugin.removeDeathLocation(targetId);
        }
        
        final String senderName;
        if (self) {
            senderName = "yourself";
        } else if (!target.canSee(sender)) {
            senderName = "someone mysterious";
        } else {
            senderName = sender.getName();
        }
        
        final StringBuilder locationBuilder = new StringBuilder();
        locationBuilder.append("§aX:§r §6").append(deathLocation.getBlockX()).append("§r§f,§r ");
        locationBuilder.append("§aY:§r §6").append(deathLocation.getBlockY()).append("§r§f,§r ");
        locationBuilder.append("§aZ:§r §6").append(deathLocation.getBlockZ());
        
        target.sendMessage("§aYour last death coordinates (sent by " + senderName + "):");
        target.sendMessage(locationBuilder.toString());
        if (!self) {
            sender.sendMessage("§aYou have sent§r §6" + target.getName() + "§r §atheir latest death coordinates.");
        }
        
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            
            final UUID playerId = player.getUniqueId();
            if (!player.hasPermission("cvranks.death.hound") || this.plugin.isNotifyDeathHoundDisabled(playerId) || playerId.equals(senderId) || playerId.equals(targetId) || !player.canSee(target)) {
                continue;
            }
            
            final StringBuilder builder = new StringBuilder();
            builder.append("§6").append(player.canSee(sender) ? sender.getName() : "Someone mysterious").append("§r ");
            builder.append("§ahas sent");
            if (self) {
                builder.append(" themselves their own ");
            } else {
                builder.append("§r §6").append(target.getName()).append("§r§a their ");
            }
            builder.append("death coordinates.");
            
            player.sendMessage(builder.toString());
        }
        
        return true;
    }
    
    @Override
    @NotNull
    protected List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final UUID senderId = sender.getUniqueId();
        final List<String> completions = new ArrayList<String>();
        completions.add("list");
        
        if (!sender.hasPermission("cvranks.death.hound")) {
            
            if (args.isEmpty()) {
                return completions;
            }
            
            final String subCommand = args.remove(0);
            if (args.isEmpty()) {
                completions.removeIf(completion -> !completion.toLowerCase().startsWith(subCommand.toLowerCase()));
                return completions;
            }
            
            return Collections.emptyList();
        }
        
        completions.add("notify");
        if (this.plugin.getDeathHoundWaitTime(senderId) > 0L) {
            
            if (args.isEmpty()) {
                return completions;
            }
            
            final String subCommand = args.remove(0);
            if (args.isEmpty()) {
                completions.removeIf(completion -> !completion.toLowerCase().startsWith(subCommand.toLowerCase()));
                return completions;
            }
            
            completions.clear();
            if (subCommand.equalsIgnoreCase("notify")) {
                
                completions.add(this.plugin.isNotifyDeathHoundDisabled(senderId) ? "on" : "off");
                
                final String toggle = args.remove(0);
                if (args.isEmpty()) {
                    completions.removeIf(completion -> !completion.toLowerCase().startsWith(toggle.toLowerCase()));
                    return completions;
                }
                
                return Collections.emptyList();
            }
            
            return Collections.emptyList();
        }
        
        completions.add("time");
        completions.add("me");
        for (final UUID playerId : this.plugin.getDeathLocationIds()) {
            
            if (!this.plugin.isPendingDeathHoundNotification(playerId)) {
                continue;
            }
            
            final Player player = this.plugin.getServer().getPlayer(playerId);
            if (player == null || !player.isOnline() || !sender.canSee(player) || player.isDead()) {
                continue;
            }
            
            completions.add(player.getName());
        }
        
        if (args.isEmpty()) {
            return completions;
        }
        
        final String subCommand = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(subCommand.toLowerCase()));
            return completions;
        }
        
        completions.clear();
        if (subCommand.equalsIgnoreCase("notify")) {
            
            completions.add(this.plugin.isNotifyDeathHoundDisabled(senderId) ? "on" : "off");
            
            final String toggle = args.remove(0);
            if (args.isEmpty()) {
                completions.removeIf(completion -> !completion.toLowerCase().startsWith(toggle.toLowerCase()));
                return completions;
            }
            
            return Collections.emptyList();
        }
        
        return Collections.emptyList();
    }
}
