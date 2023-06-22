package org.cubeville.ranks.bukkit.command.mining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.cubeville.ranks.bukkit.command.PlayerCommand;
import org.jetbrains.annotations.NotNull;

public final class ProspectorCommand extends PlayerCommand {
    
    private final CVRanksPlugin plugin;
    private final Set<String> types;
    private final Set<String> toggles;
    
    public ProspectorCommand(@NotNull final CVRanksPlugin plugin) {
        super("prospector");
        
        this.plugin = plugin;
        this.types = new HashSet<String>(Arrays.asList("coal", "quartz", "diamond", "flint", "all"));
        this.toggles = new HashSet<String>(Arrays.asList("on", "off"));
    }
    
    @Override
    protected boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final boolean hasAll = sender.hasPermission("cvranks.mining.ps");
        final boolean hasOre = sender.hasPermission("cvranks.mining.ps.ore");
        final boolean hasFlint = sender.hasPermission("cvranks.mining.ps.flint");
        
        if (!hasAll && !hasOre && !hasFlint) {
            sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
            return true;
        }
        
        final UUID senderId = sender.getUniqueId();
        if (args.isEmpty()) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            if (hasAll || hasOre) {
                sender.sendMessage(" §f- Coal:§r " + (this.plugin.isNotifyCoalDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage(" §f- Quartz:§r " + (this.plugin.isNotifyQuartzDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage(" §f- Diamond:§r " + (this.plugin.isNotifyDiamondDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            }
            if (hasAll || hasFlint) {
                sender.sendMessage(" §f- Flint:§r " + (this.plugin.isNotifyFlintDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            }
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bAvailable Prospector commands:");
            sender.sendMessage("§8--------------------------------");
            if (hasAll || hasOre) {
                sender.sendMessage(" §f-§r §a/ps notify coal [" + (this.plugin.isNotifyCoalDisabled(senderId) ? "on" : "off") + "]");
                sender.sendMessage(" §f-§r §a/ps notify quartz [" + (this.plugin.isNotifyQuartzDisabled(senderId) ? "on" : "off") + "]");
                sender.sendMessage(" §f-§r §a/ps notify diamond [" + (this.plugin.isNotifyDiamondDisabled(senderId) ? "on" : "off") + "]");
            }
            if (hasAll || hasFlint) {
                sender.sendMessage(" §f-§r §a/ps notify flint [" + (this.plugin.isNotifyFlintDisabled(senderId) ? "on" : "off") + "]");
            }
            sender.sendMessage(" §f-§r §a/ps notify all [on|off]");
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        final String notify = args.remove(0);
        if (!notify.equalsIgnoreCase("notify")) {
            this.sendUsageMessage(sender, hasAll, hasOre, hasFlint);
            return true;
        }
        
        if (args.isEmpty()) {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            if (hasAll || hasOre) {
                sender.sendMessage(" §f- Coal:§r " + (this.plugin.isNotifyCoalDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage(" §f- Quartz:§r " + (this.plugin.isNotifyQuartzDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage(" §f- Diamond:§r " + (this.plugin.isNotifyDiamondDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            }
            if (hasAll || hasFlint) {
                sender.sendMessage(" §f- Flint:§r " + (this.plugin.isNotifyFlintDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            }
            sender.sendMessage("§8--------------------------------");
            return true;
        }
        
        final String type = args.remove(0);
        if (!this.types.contains(type.toLowerCase())) {
            this.sendUsageMessage(sender, hasAll, hasOre, hasFlint);
            return true;
        }
        
        if (args.isEmpty()) {
            
            if (type.equalsIgnoreCase("coal")) {
                
                if (!hasAll && !hasOre) {
                    sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                    return true;
                }
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Notification Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- Coal:§r " + (this.plugin.isNotifyCoalDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            } else if (type.equalsIgnoreCase("quartz")) {
                
                if (!hasAll && !hasOre) {
                    sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                    return true;
                }
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Notification Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- Quartz:§r " + (this.plugin.isNotifyQuartzDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            } else if (type.equalsIgnoreCase("diamond")) {
                
                if (!hasAll && !hasOre) {
                    sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                    return true;
                }
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Notification Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- Diamond:§r " + (this.plugin.isNotifyDiamondDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            } else if (type.equalsIgnoreCase("flint")) {
                
                if (!hasAll && !hasFlint) {
                    sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                    return true;
                }
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Notification Statuses:");
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage(" §f- Flint:§r " + (this.plugin.isNotifyFlintDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage("§8--------------------------------");
                return true;
                
            } else {
                
                sender.sendMessage("§8--------------------------------");
                sender.sendMessage("§bRank Notification Statuses:");
                sender.sendMessage("§8--------------------------------");
                if (hasAll || hasOre) {
                    sender.sendMessage(" §f- Coal:§r " + (this.plugin.isNotifyCoalDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                    sender.sendMessage(" §f- Quartz:§r " + (this.plugin.isNotifyQuartzDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                    sender.sendMessage(" §f- Diamond:§r " + (this.plugin.isNotifyDiamondDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                }
                if (hasAll || hasFlint) {
                    sender.sendMessage(" §f- Flint:§r " + (this.plugin.isNotifyFlintDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                }
                sender.sendMessage("§8--------------------------------");
                return true;
                
            }
        }
        
        final String toggle = args.remove(0);
        if (!args.isEmpty() || !this.toggles.contains(toggle.toLowerCase())) {
            this.sendUsageMessage(sender, hasAll, hasOre, hasFlint);
            return true;
        }
        
        if (type.equalsIgnoreCase("coal")) {
            
            if (!hasAll && !hasOre) {
                sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                return true;
            }
            
            final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyCoal(senderId) : this.plugin.disableNotifyCoal(senderId);
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Coal:§r " + (this.plugin.isNotifyCoalDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
            sender.sendMessage(" §f- Quartz:§r " + (this.plugin.isNotifyQuartzDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage(" §f- Diamond:§r " + (this.plugin.isNotifyDiamondDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            if (hasAll || hasFlint) {
                sender.sendMessage(" §f- Flint:§r " + (this.plugin.isNotifyFlintDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            }
            sender.sendMessage("§8--------------------------------");
            return true;
            
        } else if (type.equalsIgnoreCase("quartz")) {
            
            if (!hasAll && !hasOre) {
                sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                return true;
            }
            
            final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyQuartz(senderId) : this.plugin.disableNotifyQuartz(senderId);
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Coal:§r " + (this.plugin.isNotifyCoalDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage(" §f- Quartz:§r " + (this.plugin.isNotifyQuartzDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
            sender.sendMessage(" §f- Diamond:§r " + (this.plugin.isNotifyDiamondDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            if (hasAll || hasFlint) {
                sender.sendMessage(" §f- Flint:§r " + (this.plugin.isNotifyFlintDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            }
            sender.sendMessage("§8--------------------------------");
            return true;
            
        } else if (type.equalsIgnoreCase("diamond")) {
            
            if (!hasAll && !hasOre) {
                sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                return true;
            }
            
            final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyDiamond(senderId) : this.plugin.disableNotifyDiamond(senderId);
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage(" §f- Coal:§r " + (this.plugin.isNotifyCoalDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage(" §f- Quartz:§r " + (this.plugin.isNotifyQuartzDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            sender.sendMessage(" §f- Diamond:§r " + (this.plugin.isNotifyDiamondDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
            if (hasAll || hasFlint) {
                sender.sendMessage(" §f- Flint:§r " + (this.plugin.isNotifyFlintDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            }
            sender.sendMessage("§8--------------------------------");
            return true;
            
        } else if (type.equalsIgnoreCase("flint")) {
            
            if (!hasAll && !hasFlint) {
                sender.sendMessage(DEFAULT_PERMISSION_MESSAGE);
                return true;
            }
            
            final boolean changed = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyFlint(senderId) : this.plugin.disableNotifyFlint(senderId);
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            if (hasAll || hasOre) {
                sender.sendMessage(" §f- Coal:§r " + (this.plugin.isNotifyCoalDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage(" §f- Quartz:§r " + (this.plugin.isNotifyQuartzDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
                sender.sendMessage(" §f- Diamond:§r " + (this.plugin.isNotifyDiamondDisabled(senderId) ? "§cDisabled" : "§aEnabled"));
            }
            sender.sendMessage(" §f- Flint:§r " + (this.plugin.isNotifyFlintDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (changed ? "§a(Changed)" : "§c(Not Changed)"));
            sender.sendMessage("§8--------------------------------");
            return true;
            
        } else {
            
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRank Notification Statuses:");
            sender.sendMessage("§8--------------------------------");
            if (hasAll || hasOre) {
                final boolean coalChanged = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyCoal(senderId) : this.plugin.disableNotifyCoal(senderId);
                final boolean quartzChanged = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyQuartz(senderId) : this.plugin.disableNotifyQuartz(senderId);
                final boolean diamondChanged = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyDiamond(senderId) : this.plugin.disableNotifyDiamond(senderId);
                sender.sendMessage(" §f- Coal:§r " + (this.plugin.isNotifyCoalDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (coalChanged ? "§a(Changed)" : "§c(Not Changed)"));
                sender.sendMessage(" §f- Quartz:§r " + (this.plugin.isNotifyQuartzDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (quartzChanged ? "§a(Changed)" : "§c(Not Changed)"));
                sender.sendMessage(" §f- Diamond:§r " + (this.plugin.isNotifyDiamondDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (diamondChanged ? "§a(Changed)" : "§c(Not Changed)"));
            }
            if (hasAll || hasFlint) {
                final boolean flintChanged = toggle.equalsIgnoreCase("on") ? this.plugin.enableNotifyFlint(senderId) : this.plugin.disableNotifyFlint(senderId);
                sender.sendMessage(" §f- Flint:§r " + (this.plugin.isNotifyFlintDisabled(senderId) ? "§cDisabled" : "§aEnabled") + "§r " + (flintChanged ? "§a(Changed)" : "§c(Not Changed)"));
            }
            sender.sendMessage("§8--------------------------------");
            return true;
            
        }
    }
    
    private void sendUsageMessage(@NotNull final Player sender, final boolean hasAll, final boolean hasOre, final boolean hasFlint) {
        
        final StringBuilder builder = new StringBuilder();
        builder.append("§cSyntax: ");
        builder.append("/ps notify [");
        if (hasAll || (hasOre && hasFlint)) {
            builder.append("coal|quartz|diamond|flint|all");
        } else if (hasOre) {
            builder.append("coal|quartz|diamond|all");
        } else {
            builder.append("flint|all");
        }
        builder.append("] [on|off]");
        
        sender.sendMessage(builder.toString());
    }
    
    @Override
    @NotNull
    protected List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final boolean hasAll = sender.hasPermission("cvranks.mining.ps");
        final boolean hasOre = sender.hasPermission("cvranks.mining.ps.ore");
        final boolean hasFlint = sender.hasPermission("cvranks.mining.ps.flint");
        
        if (!hasAll && !hasOre && !hasFlint) {
            return Collections.emptyList();
        }
        
        final List<String> completions = new ArrayList<String>();
        completions.add("notify");
        
        if (args.isEmpty()) {
            return completions;
        }
        
        final String notify = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(notify.toLowerCase()));
            return completions;
        }
        
        if (!notify.equalsIgnoreCase("notify")) {
            return Collections.emptyList();
        }
        
        completions.clear();
        if (hasAll || hasOre) {
            completions.add("coal");
            completions.add("quartz");
            completions.add("diamond");
        }
        if (hasAll || hasFlint) {
            completions.add("flint");
        }
        completions.add("all");
        
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
        if (type.equalsIgnoreCase("coal")) {
            
            if (!hasAll && !hasOre) {
                return Collections.emptyList();
            }
            completions.add(this.plugin.isNotifyCoalDisabled(senderId) ? "on" : "off");
            
        } else if (type.equalsIgnoreCase("quartz")) {
            
            if (!hasAll && !hasOre) {
                return Collections.emptyList();
            }
            completions.add(this.plugin.isNotifyQuartzDisabled(senderId) ? "on" : "off");
            
        } else if (type.equalsIgnoreCase("diamond")) {
            
            if (!hasAll && !hasOre) {
                return Collections.emptyList();
            }
            completions.add(this.plugin.isNotifyDiamondDisabled(senderId) ? "on" : "off");
            
        } else if (type.equalsIgnoreCase("flint")) {
            
            if (!hasAll && !hasFlint) {
                return Collections.emptyList();
            }
            completions.add(this.plugin.isNotifyFlintDisabled(senderId) ? "on" : "off");
            
        } else {
            completions.addAll(this.toggles);
        }
        
        final String toggle = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(toggle.toLowerCase()));
            return completions;
        }
        
        return Collections.emptyList();
    }
}
