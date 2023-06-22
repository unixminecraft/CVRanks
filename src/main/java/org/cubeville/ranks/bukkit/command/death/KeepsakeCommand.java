package org.cubeville.ranks.bukkit.command.death;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.cubeville.ranks.bukkit.command.PlayerCommand;
import org.jetbrains.annotations.NotNull;

public final class KeepsakeCommand extends PlayerCommand {
    
    private final CVRanksPlugin plugin;
    
    public KeepsakeCommand(@NotNull final CVRanksPlugin plugin) {
        super("keepsake");
        
        this.plugin = plugin;
    }
    
    @Override
    protected boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        if (!sender.hasPermission("cvranks.death.ks") && !sender.hasPermission("cvranks.death.ks.admin")) {
            sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
            return true;
        }
        
        if (!args.isEmpty()) {
            
            final String time = args.remove(0);
            if (!args.isEmpty() || !time.equalsIgnoreCase("time")) {
                sender.sendMessage("§cSyntax: /ks [time]");
                return true;
            }
        }
        
        final long waitTime = this.plugin.getKeepsakeWaitTime(sender.getUniqueId());
        if (waitTime > 0L) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Wait Times:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§fKeepsake:");
            sender.sendMessage(" §f- In-game:§r §c" + this.plugin.formatWaitTime(waitTime));
            sender.sendMessage(" §f- Real-Time:§r §c" + this.plugin.formatRealWaitTime(waitTime));
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage("§bRank Wait Times:");
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage("§fKeepsake:§r §aREADY");
        sender.sendMessage("§8--------------------------------");
        return true;
    }
    
    @Override
    @NotNull
    protected List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
        if (!sender.hasPermission("cvranks.death.ks") && !sender.hasPermission("cvranks.death.ks.admin")) {
            return Collections.emptyList();
        }
        
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
