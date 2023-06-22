package org.cubeville.ranks.bukkit.command.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.cubeville.ranks.bukkit.command.PlayerCommand;
import org.jetbrains.annotations.NotNull;

public final class ScubaCommand extends PlayerCommand {
    
    private final CVRanksPlugin plugin;
    private final Set<String> toggles;
    
    public ScubaCommand(@NotNull final CVRanksPlugin plugin) {
        super("scuba");
        
        this.plugin = plugin;
        this.toggles = new HashSet<String>(Arrays.asList("on", "off"));
    }
    
    @Override
    protected boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final UUID senderId = sender.getUniqueId();
        if (args.isEmpty()) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Scuba:§r " + (this.plugin.isScubaEnabled(senderId) ? "§aEnabled" : "§cDisabled"));
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bAvailable Scuba commands:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f-§r §a/scuba [" + (this.plugin.isScubaEnabled(senderId) ? "off" : "on") + "]");
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        final String toggle = args.remove(0);
        if (!args.isEmpty() || !this.toggles.contains(toggle.toLowerCase())) {
            sender.sendMessage("§cSyntax: /scuba [on|off]");
            return true;
        }
        
        final boolean changed;
        if (toggle.equalsIgnoreCase("on")) {
            changed = this.plugin.enableScuba(senderId);
            if (changed) {
                sender.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1, false, false));
            }
        } else {
            changed = this.plugin.disableScuba(senderId);
            if (changed) {
                sender.removePotionEffect(PotionEffectType.WATER_BREATHING);
            }
        }
        
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage("§bRank Statuses:");
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage(" §f- Scuba:§r " + (this.plugin.isScubaEnabled(senderId) ? "§aEnabled" : "§cDisabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
        sender.sendMessage("§8--------------------------------");
        return true;
    }
    
    @Override
    @NotNull
    protected List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final UUID senderId = sender.getUniqueId();
        
        final List<String> completions = new ArrayList<String>();
        completions.add(this.plugin.isScubaEnabled(senderId) ? "off" : "on");
        
        if (args.isEmpty()) {
            return completions;
        }
        
        final String toggle = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(toggle.toLowerCase()));
            return completions;
        }
        
        return Collections.emptyList();
    }
}
