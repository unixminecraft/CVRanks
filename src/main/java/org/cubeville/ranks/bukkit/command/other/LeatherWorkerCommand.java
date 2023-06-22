package org.cubeville.ranks.bukkit.command.other;

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

public final class LeatherWorkerCommand extends PlayerCommand {
    
    private final CVRanksPlugin plugin;
    private final Set<String> toggles;
    
    public LeatherWorkerCommand(@NotNull final CVRanksPlugin plugin) {
        super("leatherworker");
        
        this.plugin = plugin;
        this.toggles = new HashSet<String>(Arrays.asList("on", "off"));
    }
    
    @Override
    protected boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final UUID senderId = sender.getUniqueId();
        if (args.isEmpty()) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Leather:§r " + (this.plugin.isNotifyLeatherDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bAvailable LeatherWorker commands:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f-§r §a/leather notify [" + (this.plugin.isNotifyLeatherDisabled(senderId) ? "on" : "off") + "]");
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        final String notify = args.remove(0);
        if (!notify.equalsIgnoreCase("notify")) {
            sender.sendMessage("§cSyntax: /leather notify [on|off]");
            return true;
        }
        
        if (args.isEmpty()) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Leather:§r " + (this.plugin.isNotifyLeatherDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        final String toggle = args.remove(0);
        if (!args.isEmpty() || !this.toggles.contains(toggle.toLowerCase())) {
            sender.sendMessage("§cSyntax: /leather notify [on|off]");
            return true;
        }
        
        final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyLeather(senderId) : this.plugin.disableNotifyLeather(senderId);
        
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage("§bRank Notification Statuses:");
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage(" §f- Leather:§r " + (this.plugin.isNotifyLeatherDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
        sender.sendMessage("§8--------------------------------");
        return true;
    }
    
    @Override
    @NotNull
    protected List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final List<String> completions = new ArrayList<String>();
        completions.add("notify");
        
        if (args.isEmpty()) {
            return completions;
        }
        
        final String notify = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(notify.toLowerCase()));
            return completions;
        }
        
        if (!notify.equalsIgnoreCase("notify")) {
            return Collections.emptyList();
        }
        
        completions.clear();
        completions.add(this.plugin.isNotifyLeatherDisabled(sender.getUniqueId()) ? "on" : "off");
        
        final String toggle = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(toggle.toLowerCase()));
            return completions;
        }
        
        return Collections.emptyList();
    }
}
