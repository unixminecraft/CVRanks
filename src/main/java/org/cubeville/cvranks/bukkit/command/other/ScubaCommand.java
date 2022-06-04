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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cubeville.cvranks.bukkit.CVRanksPlugin;
import org.jetbrains.annotations.NotNull;

public final class ScubaCommand implements TabExecutor {
    
    private final CVRanksPlugin plugin;
    
    public ScubaCommand(@NotNull final CVRanksPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThe scuba command can only be used by a player.");
            return true;
        }
        if (args.length > 1) {
            return false;
        }
        
        final Player sender = (Player) commandSender;
        final UUID senderId = sender.getUniqueId();
        if (args.length == 0) {
            
            final boolean enabled = this.plugin.isScubaEnabled(senderId);
            sender.sendMessage("§bYour scuba ability is currently§r " + (enabled ? "§aenabled" : "§cnot enabled") + "§r§b.");
            sender.sendMessage("§bYou can turn it§r " + (enabled ? "§coff" : "§aon") + "§r §bwith§r §a/scuba " + (enabled ? "off" : "on") + "§r§b.");
            return true;
        }
        
        final String toggle = args[0];
        if (toggle.equalsIgnoreCase("on")) {
            
            if (this.plugin.enableScuba(senderId)) {
                sender.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1));
                sender.sendMessage("§aYour scuba ability has been turned on.");
            } else {
                sender.sendMessage("§cYour scuba ability is already on. To turn it off, use§r §a/scuba off§r§c.");
            }
            return true;
            
        } else if (toggle.equalsIgnoreCase("off")) {
            
            if (this.plugin.disableScuba(senderId)) {
                sender.removePotionEffect(PotionEffectType.WATER_BREATHING);
                sender.sendMessage("§aYour scuba ability has been turned off.");
            } else {
                sender.sendMessage("§cYour scuba ability is already off. To turn it on, use§r §a/scuba on§r§c.");
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
        completions.add(this.plugin.isScubaEnabled(sender.getUniqueId()) ? "off" : "on");
        
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
