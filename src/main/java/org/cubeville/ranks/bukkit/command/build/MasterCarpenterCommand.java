package org.cubeville.ranks.bukkit.command.build;

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

public final class MasterCarpenterCommand extends PlayerCommand {
    
    private final CVRanksPlugin plugin;
    private final Set<String> toggles;
    
    public MasterCarpenterCommand(@NotNull final CVRanksPlugin plugin) {
        super("master carpenter");
        
        this.plugin = plugin;
        this.toggles = new HashSet<String>(Arrays.asList("on", "off"));
    }
    
    @Override
    protected boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final UUID senderId = sender.getUniqueId();
        if (args.isEmpty()) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Master Carpenter:§r " + (this.plugin.isMasterCarpenterEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bAvailable Master Carpenter commands:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f-§r §a/carp [" + (this.plugin.isMasterCarpenterEnabled(senderId) ? "off" : "on") + "]");
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        final String toggle = args.remove(0);
        if (!args.isEmpty() || !this.toggles.contains(toggle.toLowerCase())) {
            sender.sendMessage("§cSyntax: /carp [on|off]");
            return true;
        }
        
        final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableMasterCarpenter(senderId) : this.plugin.disableMasterCarpenter(senderId);
        
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage("§bRank Statuses:");
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage(" §f- Master Carpenter:§r " + (this.plugin.isMasterCarpenterEnabled(senderId) ? "§aEnabled" : "§cDisabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
        sender.sendMessage("§8--------------------------------");
        return true;
    }
    
    @Override
    @NotNull
    protected List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final UUID senderId = sender.getUniqueId();
        
        final List<String> completions = new ArrayList<String>();
        completions.add(this.plugin.isMasterCarpenterEnabled(senderId) ? "off" : "on");
        
        if (args.isEmpty()) {
            return completions;
        }
        
        final String toggle = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(toggle.toLowerCase()));
            return completions;
        }
        
        return Collections.emptyList();
    }
}
