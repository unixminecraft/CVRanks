package org.cubeville.cvranks.bukkit.command.service;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.cubeville.cvranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class RepairCommand implements TabExecutor {
    
    private final CVRanksPlugin plugin;
    
    public RepairCommand(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThe repair command can only be used by a player.");
            return true;
        }
        
        commandSender.sendMessage("§cThe repair command is currently non-functional as we rework it.");
        commandSender.sendMessage("§6If you need to repair an item, it's highly recommended to enchant it with mending instead, it costs way less");
        return true;
    }
    
    @Override
    @NotNull
    public List<String> onTabComplete(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        return Collections.emptyList();
    }
}
