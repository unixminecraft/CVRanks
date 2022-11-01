package org.cubeville.cvranks.bukkit.command.other;

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

public final class MiniRankCommand implements TabExecutor {
    
    private final CVRanksPlugin plugin;
    
    public MiniRankCommand(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThe minirank command can only be used by a player.");
            return true;
        }
        
        final Player sender = (Player) commandSender;
        final UUID senderId = sender.getUniqueId();
        final boolean permsMycelium = sender.hasPermission("cvranks.mr.mycelium");
        final boolean permsGlass = sender.hasPermission("cvranks.mr.glass");
        final boolean permsObsidian = sender.hasPermission("cvranks.mr.obsidian");
        
        final Iterator<String> argsIterator = new ArrayList<String>(Arrays.asList(args)).iterator();
        if (!argsIterator.hasNext()) {
            if (!permsMycelium && !permsGlass && !permsObsidian) {
                sender.sendMessage("§cYou have no minirank commands available for you to use.");
                return true;
            }
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bAvailable Miniranks commands:");
            sender.sendMessage("§8--------------------------------");
            if (permsMycelium) {
                final boolean enabled = this.plugin.isMiniRankMyceliumEnabled(senderId);
                sender.sendMessage(" §f-§r §a/mr mycelium [on|off]§r §f-§r §bCurrently§r " + (enabled ? "§aEnabled" : "§cDisabled"));
            }
            if (permsGlass) {
                final boolean enabled = this.plugin.isMiniRankGlassEnabled(senderId);
                sender.sendMessage(" §f-§r §a/mr glass [on|off]§r §f-§r §bCurrently§r " + (enabled ? "§aEnabled" : "§cDisabled"));
            }
            if (permsObsidian) {
                final boolean enabled = this.plugin.isMiniRankObsidianEnabled(senderId);
                sender.sendMessage(" §f-§r §a/mr obsidian [on|off]§r §f-§r §bCurrently§r " + (enabled ? "§aEnabled" : "§cDisabled"));
            }
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        if (!permsMycelium && !permsGlass && !permsObsidian) {
            sender.sendMessage(CVRanksPlugin.DEFAULT_PERMISSION_MESSAGE);
            return true;
        }
        
        final String rank = argsIterator.next();
        if (rank.equalsIgnoreCase("mycelium")) {
            if (!permsMycelium) {
                sender.sendMessage(CVRanksPlugin.DEFAULT_PERMISSION_MESSAGE);
                return true;
            }
        } else if (rank.equalsIgnoreCase("glass")) {
            if (!permsGlass) {
                sender.sendMessage(CVRanksPlugin.DEFAULT_PERMISSION_MESSAGE);
                return true;
            }
        } else if (rank.equalsIgnoreCase("obsidian")) {
            if (!permsObsidian) {
                sender.sendMessage(CVRanksPlugin.DEFAULT_PERMISSION_MESSAGE);
                return true;
            }
        } else {
            return false;
        }
        
        if (!argsIterator.hasNext()) {
            if (rank.equalsIgnoreCase("mycelium")) {
                final boolean enabled = this.plugin.isMiniRankMyceliumEnabled(senderId);
                sender.sendMessage("§bYour mycelium minirank ability is currently§r " + (enabled ? "§aenabled" : "§cnot enabled") + "§r§b.");
                sender.sendMessage("§bYou can turn it§r " + (enabled ? "§coff" : "§aon") + "§r §bwith§r §a/mr mycelium " + (enabled ? "off" : "on") + "§r§b.");
                return true;
                
            } else if (rank.equalsIgnoreCase("glass")) {
                final boolean enabled = this.plugin.isMiniRankGlassEnabled(senderId);
                sender.sendMessage("§bYour glass minirank ability is currently§r " + (enabled ? "§aenabled" : "§cnot enabled") + "§r§b.");
                sender.sendMessage("§bYou can turn it§r " + (enabled ? "§coff" : "§aon") + "§r §bwith§r §a/mr glass " + (enabled ? "off" : "on") + "§r§b.");
                return true;
                
            } else if (rank.equalsIgnoreCase("obsidian")) {
                final boolean enabled = this.plugin.isMiniRankObsidianEnabled(senderId);
                sender.sendMessage("§bYour obsidian minirank ability is currently§r " + (enabled ? "§aenabled" : "§cnot enabled") + "§r§b.");
                sender.sendMessage("§bYou can turn it§r " + (enabled ? "§coff" : "§aon") + "§r §bwith§r §a/mr obsidian " + (enabled ? "off" : "on") + "§r§b.");
                return true;
                
            } else {
                return false;
            }
        }
        
        final String toggle = argsIterator.next();
        if (argsIterator.hasNext()) {
            return false;
        }
        
        if (toggle.equalsIgnoreCase("on")) {
            
            final boolean enabled;
            if (rank.equalsIgnoreCase("mycelium")){
                enabled = this.plugin.enableMiniRankMycelium(senderId);
            } else if (rank.equalsIgnoreCase("glass")) {
                enabled = this.plugin.enableMiniRankGlass(senderId);
            } else if (rank.equalsIgnoreCase("obsidian")) {
                enabled = this.plugin.enableMiniRankObsidian(senderId);
            } else {
                return false;
            }
            
            if (enabled) {
                sender.sendMessage("§aYour " + rank.toLowerCase() + "minirank ability has been turned on.");
            } else {
                sender.sendMessage("§cYour " + rank.toLowerCase() + "minirank ability is already on. To turn it off, use§r §a/mr " + rank.toLowerCase() + " off§r§c.");
            }
            return true;
            
        } else if (toggle.equalsIgnoreCase("off")) {
            
            final boolean disabled;
            if (rank.equalsIgnoreCase("mycelium")){
                disabled = this.plugin.disableMiniRankMycelium(senderId);
            } else if (rank.equalsIgnoreCase("glass")) {
                disabled = this.plugin.disableMiniRankGlass(senderId);
            } else if (rank.equalsIgnoreCase("obsidian")) {
                disabled = this.plugin.disableMiniRankObsidian(senderId);
            } else {
                return false;
            }
            
            if (disabled) {
                sender.sendMessage("§aYour " + rank.toLowerCase() + "minirank ability has been turned off.");
            } else {
                sender.sendMessage("§cYour " + rank.toLowerCase() + "minirank ability is already off. To turn it on, use§r §a/mr " + rank.toLowerCase() + " on§r§c.");
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
        final UUID senderId = sender.getUniqueId();
        final List<String> completions = new ArrayList<String>();
        final Iterator<String> argsIterator = new ArrayList<String>(Arrays.asList(args)).iterator();
        
        final boolean permsMycelium = sender.hasPermission("cvranks.mr.mycelium");
        final boolean permsGlass = sender.hasPermission("cvranks.mr.glass");
        final boolean permsObsidian = sender.hasPermission("cvranks.mr.obsidian");
        
        if (permsMycelium) {
            completions.add("mycelium");
        }
        if (permsGlass) {
            completions.add("glass");
        }
        if (permsObsidian) {
            completions.add("obsidian");
        }
        
        if (!argsIterator.hasNext()) {
            return Collections.unmodifiableList(completions);
        }
        
        final String rank = argsIterator.next();
        if (!argsIterator.hasNext()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(rank.toLowerCase()));
            return Collections.unmodifiableList(completions);
        }
        
        completions.clear();
        final String toggle = argsIterator.next();
        if (rank.equalsIgnoreCase("mycelium")) {
            
            if (!permsMycelium) {
                return Collections.emptyList();
            }
            
            completions.add(this.plugin.isMiniRankMyceliumEnabled(senderId) ? "off" : "on");
            if (!argsIterator.hasNext()) {
                completions.removeIf(completion -> !completion.toLowerCase().startsWith(toggle.toLowerCase()));
                return Collections.unmodifiableList(completions);
            }
            
            return Collections.emptyList();
            
        } else if (rank.equalsIgnoreCase("glass")) {
            
            if (!permsGlass) {
                return Collections.emptyList();
            }
            
            completions.add(this.plugin.isMiniRankGlassEnabled(senderId) ? "off" : "on");
            if (!argsIterator.hasNext()) {
                completions.removeIf(completion -> !completion.toLowerCase().startsWith(toggle.toLowerCase()));
                return Collections.unmodifiableList(completions);
            }
            
            return Collections.emptyList();
            
        } else if (rank.equalsIgnoreCase("obsidian")) {
            
            if (!permsObsidian) {
                return Collections.emptyList();
            }
            
            completions.add(this.plugin.isMiniRankObsidianEnabled(senderId) ? "off" : "on");
            if (!argsIterator.hasNext()) {
                completions.removeIf(completion -> !completion.toLowerCase().startsWith(toggle.toLowerCase()));
                return Collections.unmodifiableList(completions);
            }
            
            return Collections.emptyList();
            
        } else {
            return Collections.emptyList();
        }
    }
}
