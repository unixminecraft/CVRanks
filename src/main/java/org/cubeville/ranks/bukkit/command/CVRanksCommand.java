package org.cubeville.ranks.bukkit.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

abstract class CVRanksCommand implements TabExecutor {
    
    protected static final String DEFAULT_PERMISSION_MESSAGE = "§cYou do not have permission to execute this command.";
    
    private final String name;
    
    protected CVRanksCommand(@NotNull final String name) {
        this.name = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name));
    }
    
    @NotNull
    protected final String getName() {
        return this.name;
    }
    
    @Override
    public final boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        return this.onCommand(sender, new ArrayList<String>(Arrays.asList(args)));
    }
    
    @Override
    @NotNull
    public final List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        return Collections.unmodifiableList(this.onTabComplete(sender, new ArrayList<String>(Arrays.asList(args))));
    }
    
    protected abstract boolean onCommand(@NotNull final CommandSender sender, @NotNull final List<String> args);
    
    @NotNull
    protected abstract List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final List<String> args);
}
