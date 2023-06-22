package org.cubeville.ranks.bukkit.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerCommand extends CVRanksCommand {
    
    protected PlayerCommand(@NotNull final String name) {
        super(name);
    }
    
    @Override
    protected final boolean onCommand(@NotNull final CommandSender sender, @NotNull final List<String> args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThe " + this.getName() + " command can only be used by a player.");
            return true;
        }
        
        return this.execute((Player) sender, args);
    }
    
    @Override
    @NotNull
    protected final List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final List<String> args) {
        
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        
        return this.tabComplete((Player) sender, args);
    }
    
    protected abstract boolean execute(@NotNull final Player sender, @NotNull final List<String> args);
    
    @NotNull
    protected abstract List<String> tabComplete(@NotNull final Player player, @NotNull final List<String> args);
}
