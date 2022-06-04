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

public final class BrickLayerCommand implements TabExecutor {
    
    private final CVRanksPlugin plugin;
    
    public BrickLayerCommand(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThe bricklayer command can only be used by a player.");
            return true;
        }
        if (args.length > 1) {
            return false;
        }
        
        final Player sender = (Player) commandSender;
        final UUID senderId = sender.getUniqueId();
        if (args.length == 0) {
            
            final boolean enabled = this.plugin.isBrickLayerEnabled(senderId);
            sender.sendMessage("§bYour bricklayer ability is currently§r " + (enabled ? "§aenabled" : "§cnot enabled") + "§r§b.");
            sender.sendMessage("§bYou can turn it§r " + (enabled ? "§coff" : "§aon") + "§r §bwith§r §a/brick " + (enabled ? "off" : "on") + "§r§b.");
            return true;
        }
        
        final String toggle = args[0];
        if (toggle.equalsIgnoreCase("on")) {
            
            if (this.plugin.enableBrickLayer(senderId)) {
                sender.sendMessage("§aYour bricklayer ability has been turned on.");
            } else {
                sender.sendMessage("§cYour bricklayer ability is already on. To turn it off, use§r §a/brick off§r§c.");
            }
            return true;
            
        } else if (toggle.equalsIgnoreCase("off")) {
            
            if (this.plugin.disableBrickLayer(senderId)) {
                sender.sendMessage("§aYour bricklayer ability has been turned off.");
            } else {
                sender.sendMessage("§cYour bricklayer ability is already off. To turn it on, use§r §a/brick on§r§c.");
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
        completions.add(this.plugin.isBrickLayerEnabled(sender.getUniqueId()) ? "off" : "on");
        
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
