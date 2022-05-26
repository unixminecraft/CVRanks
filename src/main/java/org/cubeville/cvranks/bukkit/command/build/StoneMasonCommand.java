package org.cubeville.cvranks.bukkit.command.build;

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

public final class StoneMasonCommand implements TabExecutor {
    
    private final CVRanksPlugin plugin;
    
    public StoneMasonCommand(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThe stonemason command can only be used by a player.");
            return true;
        }
        if (args.length > 1) {
            return false;
        }
        
        final Player sender = (Player) commandSender;
        final UUID senderId = sender.getUniqueId();
        if (args.length == 0) {
            
            final boolean enabled = this.plugin.isStonemasonEnabled(senderId);
            sender.sendMessage("§bYour stonemason ability is currently§r " + (enabled ? "§aenabled" : "§cnot enabled") + "§r§b.");
            sender.sendMessage("§bYou can turn it§r " + (enabled ? "§coff" : "§aon") + "§r §bwith§r §a/mason " + (enabled ? "off" : "on") + "§r§b.");
            return true;
        }
        
        final String toggle = args[0];
        if (toggle.equalsIgnoreCase("on")) {
            
            if (this.plugin.enableStonemason(senderId)) {
                sender.sendMessage("§aYour stonemason ability has been turned on.");
            } else {
                sender.sendMessage("§cYour stonemason ability is already on. To turn it off, use§r §a/mason off§r§c.");
            }
            return true;
            
        } else if (toggle.equalsIgnoreCase("off")) {
            
            if (this.plugin.disableStonemason(senderId)) {
                sender.sendMessage("§aYour stonemason ability has been turned off.");
            } else {
                sender.sendMessage("§cYour stonemason ability is already off. To turn it on, use§r §a/mason on§r§c.");
            }
            return true;
            
        } else {
            return false;
        }
    }
    
    @Override
    @NotNull
    public List<String> onTabComplete(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            return Collections.emptyList();
        }
        
        final Player sender = (Player) commandSender;
        final List<String> completions = new ArrayList<String>();
        final Iterator<String> argsIterator = new ArrayList<String>(Arrays.asList(args)).iterator();
        completions.add(this.plugin.isStonemasonEnabled(sender.getUniqueId()) ? "off" : "on");
        
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