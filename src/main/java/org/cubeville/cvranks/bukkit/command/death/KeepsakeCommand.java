package org.cubeville.cvranks.bukkit.command.death;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.cubeville.cvranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class KeepsakeCommand implements TabExecutor {
    
    private final CVRanksPlugin plugin;
    
    public KeepsakeCommand(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThe keepsake command can only be used by a player.");
            return true;
        }
        
        final Player sender = (Player) commandSender;
        if (!sender.hasPermission("cvranks.death.ks") && !sender.hasPermission("cvranks.death.ks.admin")) {
            sender.sendMessage(CVRanksPlugin.DEFAULT_PERMISSION_MESSAGE);
            return true;
        }
        
        if (args.length > 1) {
            return false;
        }
        if (args.length == 1 && !args[0].equalsIgnoreCase("time")) {
            return false;
        }
        
        final long waitTime = this.plugin.getKeepsakeWaitTime(sender.getUniqueId());
        if (waitTime > 0L) {
            final StringBuilder builder = new StringBuilder();
            builder.append("§cYou must wait§r §6").append(this.plugin.formatWaitTime(waitTime)).append("§r §cin-game");
            builder.append("§r §b(").append(this.plugin.formatRealTimeWait(waitTime)).append(" in real-time)");
            builder.append("§r §cto use your keepsake ability.");
            sender.sendMessage(builder.toString());
        } else {
            sender.sendMessage(CVRanksPlugin.ABILITY_READY_KEEPSAKE);
        }
        
        return true;
    }
    
    @Override
    @NotNull
    public List<String> onTabComplete(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        return commandSender instanceof Player ? List.of("time") : Collections.emptyList();
    }
}
