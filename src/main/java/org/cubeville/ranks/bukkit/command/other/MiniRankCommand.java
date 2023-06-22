package org.cubeville.ranks.bukkit.command.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.cubeville.ranks.bukkit.command.PlayerCommand;
import org.jetbrains.annotations.NotNull;

public final class MiniRankCommand extends PlayerCommand {
    
    private final CVRanksPlugin plugin;
    private final Set<String> types;
    private final Set<String> toggles;
    
    public MiniRankCommand(@NotNull final CVRanksPlugin plugin) {
        super("minirank");
        
        this.plugin = plugin;
        this.types = new HashSet<String>(Arrays.asList("mycelium", "glass", "obsidian"));
        this.toggles = new HashSet<String>(Arrays.asList("on", "off"));
    }
    
    @Override
    protected boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final boolean hasMycelium = sender.hasPermission("cvranks.mr.mycelium");
        final boolean hasGlass = sender.hasPermission("cvranks.mr.glass");
        final boolean hasObsidian = sender.hasPermission("cvranks.mr.obsidian");
        
        if (!hasMycelium && !hasGlass && !hasObsidian) {
            sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
            return true;
        }
        
        final UUID senderId = sender.getUniqueId();
        if (args.isEmpty()) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Statuses:");
            sender.sendMessage("§8--------------------------------");
            if (hasMycelium) {
                sender.sendMessage(" §f- MiniRank Mycelium:§r " + (this.plugin.isMiniRankMyceliumEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
            }
            if (hasGlass) {
                sender.sendMessage(" §f- MiniRank Glass:§r " + (this.plugin.isMiniRankGlassEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
            }
            if (hasObsidian) {
                sender.sendMessage(" §f- MiniRank Obsidian:§r " + (this.plugin.isMiniRankObsidianEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
            }
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bAvailable MiniRank commands:");
            sender.sendMessage("§8--------------------------------");
            if (hasMycelium) {
                sender.sendMessage(" §f-§r §a/mr mycelium [" + (this.plugin.isMiniRankMyceliumEnabled(senderId) ? "off" : "on") + "]");
            }
            if (hasGlass) {
                sender.sendMessage(" §f-§r §a/mr mycelium [" + (this.plugin.isMiniRankGlassEnabled(senderId) ? "off" : "on") + "]");
            }
            if (hasObsidian) {
                sender.sendMessage(" §f-§r §a/mr mycelium [" + (this.plugin.isMiniRankObsidianEnabled(senderId) ? "off" : "on") + "]");
            }
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        final String type = args.remove(0);
        if (!this.types.contains(type.toLowerCase())) {
            this.sendUsageMessage(sender, hasMycelium, hasGlass, hasObsidian);
            return true;
        }
        
        if (args.isEmpty()) {
            
            if (type.equalsIgnoreCase("mycelium")) {
                
                if (!hasMycelium) {
                    sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                    return true;
                }
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- MiniRank Mycelium:§r " + (this.plugin.isMiniRankMyceliumEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            } else if (type.equalsIgnoreCase("glass")) {
                
                if (!hasGlass) {
                    sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                    return true;
                }
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- MiniRank Glass:§r " + (this.plugin.isMiniRankGlassEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            } else {
                
                if (!hasObsidian) {
                    sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                    return true;
                }
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- MiniRank Obsidian:§r " + (this.plugin.isMiniRankObsidianEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            }
        }
        
        final String toggle = args.remove(0);
        if (!args.isEmpty() || !this.toggles.contains(toggle.toLowerCase())) {
            this.sendUsageMessage(sender, hasMycelium, hasGlass, hasObsidian);
            return true;
        }
        
        if (type.equalsIgnoreCase("mycelium")) {
            
            if (!hasMycelium) {
                sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                return true;
            }
            
            final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableMiniRankMycelium(senderId) : this.plugin.disableMiniRankMycelium(senderId);
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- MiniRank Mycelium:§r " + (this.plugin.isMiniRankMyceliumEnabled(senderId) ? "§aEnabled" : "§cDisabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
            if (hasGlass) {
                sender.sendMessage(" §f- MiniRank Glass:§r " + (this.plugin.isMiniRankGlassEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
            }
            if (hasObsidian) {
                sender.sendMessage(" §f- MiniRank Obsidian:§r " + (this.plugin.isMiniRankObsidianEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
            }
            sender.sendMessage("§8--------------------------------");
            return true;
            
        } else if (type.equalsIgnoreCase("glass")) {
            
            if (!hasGlass) {
                sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                return true;
            }
            
            final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableMiniRankGlass(senderId) : this.plugin.disableMiniRankGlass(senderId);
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Statuses:");
            sender.sendMessage("§8--------------------------------");
            if (hasMycelium) {
                sender.sendMessage(" §f- MiniRank Mycelium:§r " + (this.plugin.isMiniRankMyceliumEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
            }
            sender.sendMessage(" §f- MiniRank Glass:§r " + (this.plugin.isMiniRankGlassEnabled(senderId) ? "§aEnabled" : "§cDisabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
            if (hasObsidian) {
                sender.sendMessage(" §f- MiniRank Obsidian:§r " + (this.plugin.isMiniRankObsidianEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
            }
            sender.sendMessage("§8--------------------------------");
            return true;
            
        } else {
            
            if (!hasObsidian) {
                sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                return true;
            }
            
            final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableMiniRankObsidian(senderId) : this.plugin.disableMiniRankObsidian(senderId);
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Statuses:");
            sender.sendMessage("§8--------------------------------");
            if (hasMycelium) {
                sender.sendMessage(" §f- MiniRank Mycelium:§r " + (this.plugin.isMiniRankMyceliumEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
            }
            if (hasGlass) {
                sender.sendMessage(" §f- MiniRank Glass:§r " + (this.plugin.isMiniRankGlassEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
            }
            sender.sendMessage(" §f- MiniRank Obsidian:§r " + (this.plugin.isMiniRankObsidianEnabled(senderId) ? "§aEnabled" : "§cDisabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
            sender.sendMessage("§8--------------------------------");
            return true;
            
        }
    }
    
    private void sendUsageMessage(@NotNull final Player sender, final boolean hasMycelium, final boolean hasGlass, final boolean hasObsidian) {
        
        final List<String> miniranks = new ArrayList<String>();
        if (hasMycelium) {
            miniranks.add("mycelium");
        }
        if (hasGlass) {
            miniranks.add("glass");
        }
        if (hasObsidian) {
            miniranks.add("obsidian");
        }
        
        final StringBuilder builder = new StringBuilder();
        builder.append("§cSyntax: ");
        builder.append("/mr [");
        
        final Iterator<String> iterator = miniranks.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append("|");
            }
        }
        
        builder.append("] [on|off]");
        
        sender.sendMessage(builder.toString());
    }
    
    @Override
    @NotNull
    protected List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final boolean hasMycelium = sender.hasPermission("cvranks.mr.mycelium");
        final boolean hasGlass = sender.hasPermission("cvranks.mr.glass");
        final boolean hasObsidian = sender.hasPermission("cvranks.mr.obsidian");
        
        if (!hasMycelium && !hasGlass && !hasObsidian) {
            return Collections.emptyList();
        }
        
        final List<String> completions = new ArrayList<String>(0);
        
        if (hasMycelium) {
            completions.add("mycelium");
        }
        if (hasGlass) {
            completions.add("glass");
        }
        if (hasObsidian) {
            completions.add("obsidian");
        }
        
        if (args.isEmpty()) {
            return completions;
        }
        
        final String type = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(type.toLowerCase()));
            return completions;
        }
        
        if (!this.types.contains(type.toLowerCase())) {
            return Collections.emptyList();
        }
        
        final UUID senderId = sender.getUniqueId();
        completions.clear();
        if (type.equalsIgnoreCase("mycelium")) {
            
            if (!hasMycelium) {
                return Collections.emptyList();
            }
            completions.add(this.plugin.isMiniRankMyceliumEnabled(senderId) ? "off" : "on");
            
        } else if (type.equalsIgnoreCase("glass")) {
            
            if (!hasGlass) {
                return Collections.emptyList();
            }
            completions.add(this.plugin.isMiniRankGlassEnabled(senderId) ? "off" : "on");
            
        } else {
            
            if (!hasObsidian) {
                return Collections.emptyList();
            }
            completions.add(this.plugin.isMiniRankObsidianEnabled(senderId) ? "off" : "on");
            
        }
        
        final String toggle = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(toggle.toLowerCase()));
            return completions;
        }
        
        return Collections.emptyList();
    }
}
