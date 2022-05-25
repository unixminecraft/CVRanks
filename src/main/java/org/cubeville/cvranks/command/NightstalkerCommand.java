package org.cubeville.cvranks.command;

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
import org.cubeville.cvranks.CVRanks;
import org.jetbrains.annotations.NotNull;

public final class NightstalkerCommand implements TabExecutor {
    
    private final CVRanks plugin;
    
    public NightstalkerCommand(@NotNull final CVRanks plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThe nightstalker command can only be used by a player.");
            return true;
        }
        if (args.length > 1) {
            return false;
        }
        
        final UUID senderId = ((Player) sender).getUniqueId();
        final boolean enabled = this.plugin.isNightstalkerEnabled(senderId);
        if (args.length == 0) {
            sender.sendMessage("§bYour nightstalker is currently§r " + (enabled ? "§aenabled§r" : "§cnot enabled§r") + "§b.");
            sender.sendMessage("§bYou can turn it§r " + (enabled ? "§coff§r" : "§aon§r") + " §bwith§r §a/ns " + (enabled ? "off" : "on") + "§r§b.");
            return true;
        }
        
        final String toggle = args[0];
        if (toggle.equalsIgnoreCase("on")) {
            
            if (enabled) {
                sender.sendMessage("§cYour nightstalker is already on. To turn it off, use§r §a/ns off§r§b.");
            } else {
                this.plugin.enableNightstalker(senderId);
                sender.sendMessage("§aYour nightstalker has been turned on.");
            }
            return true;
            
        } else if (toggle.equalsIgnoreCase("off")) {
            
            if (enabled) {
                this.plugin.disableNightstalker(senderId);
                sender.sendMessage("§aYour nightstalker has been turned off.");
            } else {
                sender.sendMessage("§cYour nightstalker is already off. To turn it on, use§r §a/ns on§r§b.");
            }
            return true;
            
        } else {
            return false;
        }
    }
    
    @NotNull
    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
    
        final Iterator<String> argsIterator = (new ArrayList<String>(Arrays.asList(args))).iterator();
        final List<String> completions = new ArrayList<String>();
        completions.add(this.plugin.isNightstalkerEnabled(((Player) sender).getUniqueId()) ? "off" : "on");
        
        if (!argsIterator.hasNext()) {
            return Collections.unmodifiableList(completions);
        }
        
        final String toggle = argsIterator.next();
        if (!argsIterator.hasNext()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(toggle.toLowerCase()));
            return Collections.unmodifiableList(completions);
        }
        
        return Collections.emptyList();
    }
}
