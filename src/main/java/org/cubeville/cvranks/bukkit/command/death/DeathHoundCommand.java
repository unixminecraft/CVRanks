package org.cubeville.cvranks.bukkit.command.death;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.cubeville.cvranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class DeathHoundCommand implements TabExecutor {
    
    private final CVRanksPlugin plugin;
    
    public DeathHoundCommand(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThe death hound command can only be used by a player.");
            return true;
        }
        if (args.length > 1) {
            return false;
        }
        
        final Player sender = (Player) commandSender;
        if (args.length == 0) {
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bAvailable Death Hound commands:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f-§r §a/dh list");
            if (sender.hasPermission("cvranks.death.hound")) {
                sender.sendMessage(" §f-§r §a/dh time");
                sender.sendMessage(" §f-§r §a/dh me");
                sender.sendMessage(" §f-§r §a/dh <player>");
            }
            sender.sendMessage("§8--------------------------------");
            
            return true;
        }
        
        final String subCommand = args[0];
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
        
        if (!sender.hasPermission("cvranks.death.hound")) {
            sender.sendMessage(CVRanksPlugin.DEFAULT_PERMISSION_MESSAGE);
            return true;
        }
        
        final UUID senderId = sender.getUniqueId();
        final long waitTime = this.plugin.getDeathHoundWaitTime(senderId);
        
        if (waitTime > 0L) {
            
            final StringBuilder builder = new StringBuilder();
            builder.append("§cYou must wait§r §6").append(this.plugin.formatWaitTime(waitTime)).append("§r §cin-game");
            builder.append("§r §b(").append(this.plugin.formatRealTimeWait(waitTime)).append(" in real-time)");
            builder.append("§r §cto use your death hound ability.");
            sender.sendMessage(builder.toString());
            return true;
        }
        
        if (subCommand.equalsIgnoreCase("time")) {
            sender.sendMessage(CVRanksPlugin.ABILITY_READY_DEATH_HOUND);
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
            if (!player.hasPermission("cvranks.death.hound") || player.hasPermission("cvranks.death.hound.notifyoptout") || playerId.equals(senderId) || playerId.equals(targetId) || !player.canSee(target)) {
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
    public List<String> onTabComplete(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            return Collections.emptyList();
        }
        
        final Player sender = (Player) commandSender;
        final List<String> completions = new ArrayList<String>();
        final Iterator<String> argsIterator = new ArrayList<String>(Arrays.asList(args)).iterator();
        
        completions.add("list");
        if (!sender.hasPermission("cvranks.death.hound") || this.plugin.getDeathHoundWaitTime(sender.getUniqueId()) > 0L) {
            
            if (!argsIterator.hasNext()) {
                return Collections.unmodifiableList(completions);
            }
            
            final String subCommand = argsIterator.next();
            if (!argsIterator.hasNext()) {
                completions.removeIf(completion -> !completion.toLowerCase().startsWith(subCommand.toLowerCase()));
                return Collections.unmodifiableList(completions);
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
        
        if (!argsIterator.hasNext()) {
            return Collections.unmodifiableList(completions);
        }
        
        final String subCommand = argsIterator.next();
        if (!argsIterator.hasNext()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(subCommand.toLowerCase()));
            return Collections.unmodifiableList(completions);
        }
        
        return Collections.emptyList();
    }
}
