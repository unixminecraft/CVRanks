package org.cubeville.ranks.bukkit.command.service;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.cubeville.ranks.bukkit.command.PlayerCommand;
import org.jetbrains.annotations.NotNull;

public final class ShopkeeperCommand extends PlayerCommand {
    
    private final CVRanksPlugin plugin;
    
    public ShopkeeperCommand(@NotNull final CVRanksPlugin plugin) {
        super("shopkeeper");
        
        this.plugin = plugin;
    }
    
    @Override
    protected boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        if (args.isEmpty()) {
            sender.sendMessage("§cSyntax: /shopkeeper list [all]");
            return true;
        }
        
        final String list = args.remove(0);
        if (!list.equalsIgnoreCase("list")) {
            sender.sendMessage("§cSyntax: /shopkeeper list [all]");
            return true;
        }
        
        final boolean all = !args.isEmpty() && args.remove(0).equalsIgnoreCase("all");
        if (!args.isEmpty()) {
            sender.sendMessage("§cSyntax: /shopkeeper list [all]");
            return true;
        }
        
        boolean more = false;
        final List<String> names = new ArrayList<String>();
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            
            if (!player.hasPermission("group.rank_service_shopkeeper") || player.hasPermission("cvranks.service.shopkeeper.hidefromlist") || !sender.canSee(player)) {
                continue;
            }
            
            names.add(player.getName());
            
            if (!all && names.size() >= 10) {
                more = true;
                break;
            }
        }
        
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage("§bOnline Shopkeepers");
        sender.sendMessage("§8--------------------------------");
        for (final String name : names) {
            sender.sendMessage(" §f-§r §b" + name);
        }
        if (more) {
            sender.sendMessage("§bAnd more...");
        }
        sender.sendMessage("§8--------------------------------");
        
        return true;
    }
    
    @Override
    @NotNull
    protected List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final List<String> completions = new ArrayList<String>();
        completions.add("list");
        
        if (args.isEmpty()) {
            return completions;
        }
        
        final String list = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(list.toLowerCase()));
            return completions;
        }
        
        completions.clear();
        if (!list.equalsIgnoreCase("list")) {
            return completions;
        }
        
        completions.add("all");
        
        final String all = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(all.toLowerCase()));
            return completions;
        }
        
        completions.clear();
        return completions;
    }
}
