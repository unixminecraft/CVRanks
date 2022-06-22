package org.cubeville.cvranks.bukkit.command.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.cubeville.cvranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class DoctorCommand implements TabExecutor {
    
    private final CVRanksPlugin plugin;
    
    public DoctorCommand(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThe doctor command can only be used by a player.");
            return true;
        }
        if (args.length > 1) {
            return false;
        }
        
        final Player sender = (Player) commandSender;
        if (args.length == 0) {
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bAvailable Doctor commands:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f-§r §a/doc list");
            if (sender.hasPermission("cvranks.service.dr")) {
                sender.sendMessage(" §f-§r §a/doc me");
                sender.sendMessage(" §f-§r §a/doc <player>");
            }
            sender.sendMessage("§8--------------------------------");
            
            return true;
        }
        
        final String subCommand = args[0];
        if (subCommand.equalsIgnoreCase("list")) {
            
            final List<String> messages = new ArrayList<String>();
            for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
                
                if (!player.hasPermission("cvranks.service.dr") || player.hasPermission("cvranks.service.dr.hidefromlist") || !sender.canSee(player)) {
                    continue;
                }
                
                final long waitTime = this.plugin.getDoctorWaitTime(player.getUniqueId());
                final String prefix = " §f-§r §b" + player.getName() + "§r ";
                if (waitTime == 0L) {
                    messages.add(prefix + "§ais ready.");
                } else {
                    messages.add(prefix + "§6has " + this.plugin.formatWaitTime(waitTime) + " left");
                }
            }
            
            if (messages.isEmpty()) {
                sender.sendMessage("§cNo doctors online.");
                return true;
            }
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bOnline Doctors - (in-game hours)");
            sender.sendMessage("§8--------------------------------");
            for (final String message : messages) {
                sender.sendMessage(message);
            }
            sender.sendMessage("§8--------------------------------");
            
            return true;
        }
        
        if (!sender.hasPermission("cvranks.service.dr")) {
            sender.sendMessage(CVRanksPlugin.DEFAULT_PERMISSION_MESSAGE);
            return true;
        }
        
        final UUID senderId = sender.getUniqueId();
        final long waitTime = this.plugin.getDoctorWaitTime(senderId);
        if (waitTime > 0L) {
            
            final StringBuilder builder = new StringBuilder();
            builder.append("§cYou must wait§r §6").append(this.plugin.formatWaitTime(waitTime)).append("§r §cin-game");
            builder.append("§r §b(").append(this.plugin.formatRealTimeWait(waitTime)).append(" in real-time)");
            builder.append("§r §cto use your doctor ability.");
            sender.sendMessage(builder.toString());
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
                sender.sendMessage("§6Not sure how you managed to send that command, but you cannot bring yourself back but through the \"Respawn\" button.");
            } else {
                target.sendMessage("§b" + sender.getName() + "§r §6tried to heal you, but it was too late...");
                sender.sendMessage("§6Too late, they died...");
            }
            return true;
        }
        
        if (!this.needsHealing(target)) {
            sender.sendMessage("§6" + (self ? "You§r §cdo" : target.getName() + "§r §cdoes") + " not need to be healed.");
            return true;
        }
        
        this.plugin.doctorUsed(senderId);
        
        final String senderName;
        if (self) {
            senderName = "yourself";
        } else if (!target.canSee(sender)) {
            senderName = "someone mysterious";
        } else {
            senderName = sender.getName();
        }
        
        // Eventually, #getMaxHealth() should probably be replaced with
        // #getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), but until
        // such a time, the deprecated method will be used
        if (target.getHealth() < target.getMaxHealth()) {
            target.setHealth(target.getMaxHealth());
            target.sendMessage("§aYou have been healed by " + senderName + ".");
        }
        
        // Apparently there's no max/min levels of these, so they get set as follows:
        // - Food level: MAX 20
        // - Saturation: MAX 5.0
        // - Exhaustion: MIN 0.0
        if (target.getFoodLevel() < 20) {
            target.setFoodLevel(20);
            target.setSaturation(5.0F);
            target.setExhaustion(0.0F);
            target.sendMessage("§aYour hunger has been refilled by " + senderName + ".");
        }
        
        if (target.getFireTicks() > 0) {
            target.setFireTicks(0);
            target.sendMessage("§aYou have been extinguished by " + senderName + ".");
        }
        
        if (target.getRemainingAir() < target.getMaximumAir()) {
            target.setRemainingAir(target.getMaximumAir());
            target.sendMessage("§aYour air has been refilled by " + senderName + ".");
        }
        
        sender.sendMessage("§aYou have healed§r §6" + (self ? "yourself" : target.getName()) + "§r §asuccessfully.");
        
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            
            final UUID playerId = player.getUniqueId();
            if (!player.hasPermission("cvranks.service.dr") || player.hasPermission("cvranks.service.dr.notifyoptout") || playerId.equals(senderId) || playerId.equals(targetId) || !player.canSee(target)) {
                continue;
            }
            
            final StringBuilder builder = new StringBuilder();
            builder.append("§6").append(player.canSee(sender) ? sender.getName() : "Someone mysterious").append("§r ");
            builder.append("§ahas healed");
            if (self) {
                builder.append(" themselves.");
            } else {
                builder.append("§r §6").append(target.getName()).append("§r§a.");
            }
            
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
        final Iterator<String> argsIterator = (new ArrayList<String>(Arrays.asList(args))).iterator();
        
        completions.add("list");
        if (!sender.hasPermission("cvranks.service.dr") || this.plugin.getDoctorWaitTime(sender.getUniqueId()) > 0L) {
            
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
        
        completions.add("me");
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (sender.canSee(player) && this.needsHealing(player)) {
                completions.add(player.getName());
            }
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
    
    private boolean needsHealing(@NotNull final Player player) {
        if (player.getHealth() < player.getMaxHealth()) {
            return true;
        } else if (player.getFoodLevel() < 20) {
            return true;
        } else if (player.getFireTicks() > 0) {
            return true;
        } else if (player.getRemainingAir() < player.getMaximumAir()) {
            return true;
        } else {
            return false;
        }
    }
}
