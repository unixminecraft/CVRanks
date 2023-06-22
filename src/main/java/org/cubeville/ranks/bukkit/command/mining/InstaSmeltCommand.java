package org.cubeville.ranks.bukkit.command.mining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.cubeville.ranks.bukkit.command.PlayerCommand;
import org.jetbrains.annotations.NotNull;

public final class InstaSmeltCommand extends PlayerCommand {
    
    private final CVRanksPlugin plugin;
    private final Set<String> types;
    private final Set<String> toggles;
    
    public InstaSmeltCommand(@NotNull final CVRanksPlugin plugin) {
        super("instasmelt");
        
        this.plugin = plugin;
        this.types = new HashSet<String>(Arrays.asList("iron", "gold", "copper", "all"));
        this.toggles = new HashSet<String>(Arrays.asList("on", "off"));
    }
    
    @Override
    protected boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final UUID senderId = sender.getUniqueId();
        if (args.isEmpty()) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- InstaSmelt:§r " + (this.plugin.isInstaSmeltEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Iron:§r " + (this.plugin.isNotifyIronDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage(" §f- Gold:§r " + (this.plugin.isNotifyGoldDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage(" §f- Copper:§r " + (this.plugin.isNotifyCopperDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bAvailable InstaSmelt commands:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f-§r §a/smelt [" + (this.plugin.isInstaSmeltEnabled(senderId) ? "off" : "on") + "]");
            sender.sendMessage(" §f-§r §a/smelt notify iron [" + (this.plugin.isNotifyIronDisabled(senderId) ? "on" : "off") + "]");
            sender.sendMessage(" §f-§r §a/smelt notify gold [" + (this.plugin.isNotifyGoldDisabled(senderId) ? "on" : "off") + "]");
            sender.sendMessage(" §f-§r §a/smelt notify copper [" + (this.plugin.isNotifyCopperDisabled(senderId) ? "on" : "off") + "]");
            sender.sendMessage(" §f-§r §a/smelt notify all [on|off]");
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        final String subCommand = args.remove(0);
        if (args.isEmpty()) {
            
            if (subCommand.equalsIgnoreCase("on")) {
                
                final boolean changed = this.plugin.enableInstaSmelt(senderId);
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- InstaSmelt:§r " + (this.plugin.isInstaSmeltEnabled(senderId) ? "§aEnabled" : "§cDisabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            } else if (subCommand.equalsIgnoreCase("off")) {
                
                final boolean changed = this.plugin.disableInstaSmelt(senderId);
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- InstaSmelt:§r " + (this.plugin.isInstaSmeltEnabled(senderId) ? "§aEnabled" : "§cDisabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            } else if (subCommand.equalsIgnoreCase("notify")) {
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Notification Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- Iron:§r " + (this.plugin.isNotifyIronDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage(" §f- Gold:§r " + (this.plugin.isNotifyGoldDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage(" §f- Copper:§r " + (this.plugin.isNotifyCopperDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            } else {
                sender.sendMessage("§cSyntax: /smelt [on|off|notify [iron|gold|copper|all] [on|off]]");
                return true;
            }
        }
        
        if (!subCommand.equalsIgnoreCase("notify")) {
            sender.sendMessage("§cSyntax: /smelt [on|off|notify [iron|gold|copper|all] [on|off]]");
            return true;
        }
        
        final String type = args.remove(0);
        if (!this.types.contains(type.toLowerCase())) {
            sender.sendMessage("§cSyntax: /smelt [on|off|notify [iron|gold|copper|all] [on|off]]");
            return true;
        }
        
        if (args.isEmpty()) {
            
            if (type.equalsIgnoreCase("iron")) {
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Notification Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- Iron:§r " + (this.plugin.isNotifyIronDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            } else if (type.equalsIgnoreCase("gold")) {
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Notification Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- Gold:§r " + (this.plugin.isNotifyGoldDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            } else if (type.equalsIgnoreCase("copper")) {
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Notification Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- Copper:§r " + (this.plugin.isNotifyCopperDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            } else {
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Notification Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- Iron:§r " + (this.plugin.isNotifyIronDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage(" §f- Gold:§r " + (this.plugin.isNotifyGoldDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage(" §f- Copper:§r " + (this.plugin.isNotifyCopperDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            }
        }
        
        final String toggle = args.remove(0);
        if (!args.isEmpty() || !this.toggles.contains(toggle.toLowerCase())) {
            sender.sendMessage("§cSyntax: /smelt [on|off|notify [iron|gold|copper|all] [on|off]]");
            return true;
        }
        
        if (type.equalsIgnoreCase("iron")) {
            
            final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyIron(senderId) : this.plugin.disableNotifyIron(senderId);
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Iron:§r " + (this.plugin.isNotifyIronDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
            sender.sendMessage(" §f- Gold:§r " + (this.plugin.isNotifyGoldDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage(" §f- Copper:§r " + (this.plugin.isNotifyCopperDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage("§8--------------------------------");
            return true;
            
        } else if (type.equalsIgnoreCase("gold")) {
            
            final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyGold(senderId) : this.plugin.disableNotifyGold(senderId);
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Iron:§r " + (this.plugin.isNotifyIronDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage(" §f- Gold:§r " + (this.plugin.isNotifyGoldDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
            sender.sendMessage(" §f- Copper:§r " + (this.plugin.isNotifyCopperDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage("§8--------------------------------");
            return true;
            
        } else if (type.equalsIgnoreCase("copper")) {
            
            final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyCopper(senderId) : this.plugin.disableNotifyCopper(senderId);
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Iron:§r " + (this.plugin.isNotifyIronDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage(" §f- Gold:§r " + (this.plugin.isNotifyGoldDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage(" §f- Copper:§r " + (this.plugin.isNotifyCopperDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
            sender.sendMessage("§8--------------------------------");
            return true;
            
        } else {
            
            final boolean ironChanged = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyIron(senderId) : this.plugin.disableNotifyIron(senderId);
            final boolean goldChanged = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyGold(senderId) : this.plugin.disableNotifyGold(senderId);
            final boolean copperChanged = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyCopper(senderId) : this.plugin.disableNotifyCopper(senderId);
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Iron:§r " + (this.plugin.isNotifyIronDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (ironChanged ? "§a(Changed)" : "§c(Not Changed)"));
            sender.sendMessage(" §f- Gold:§r " + (this.plugin.isNotifyGoldDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (goldChanged ? "§a(Changed)" : "§c(Not Changed)"));
            sender.sendMessage(" §f- Copper:§r " + (this.plugin.isNotifyCopperDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (copperChanged ? "§a(Changed)" : "§c(Not Changed)"));
            sender.sendMessage("§8--------------------------------");
            return true;
            
        }
    }
    
    @Override
    @NotNull
    protected List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final UUID senderId = sender.getUniqueId();
        
        final List<String> completions = new ArrayList<String>();
        completions.add(this.plugin.isInstaSmeltEnabled(senderId) ? "off" : "on");
        completions.add("notify");
        
        if (args.isEmpty()) {
            return completions;
        }
        
        final String subCommand = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(subCommand.toLowerCase()));
            return completions;
        }
        
        if (!subCommand.equalsIgnoreCase("notify")) {
            return Collections.emptyList();
        }
        
        completions.clear();
        completions.addAll(this.types);
        
        final String type = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(type.toLowerCase()));
            return completions;
        }
        
        if (!this.types.contains(type.toLowerCase())) {
            return Collections.emptyList();
        }
        
        completions.clear();
        if (type.equalsIgnoreCase("iron")) {
            completions.add(this.plugin.isNotifyIronDisabled(senderId) ? "on" : "off");
        } else if (type.equalsIgnoreCase("gold")) {
            completions.add(this.plugin.isNotifyGoldDisabled(senderId) ? "on" : "off");
        } else if (type.equalsIgnoreCase("copper")) {
            completions.add(this.plugin.isNotifyCopperDisabled(senderId) ? "on" : "off");
        } else {
            completions.addAll(this.toggles);
        }
        
        final String toggle = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(toggle.toLowerCase()));
            return completions;
        }
        
        return Collections.emptyList();
    }
}
