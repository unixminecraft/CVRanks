package org.cubeville.ranks.bukkit.command.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.entity.Player;
import org.cubeville.ranks.bukkit.command.PlayerCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RankCommand extends PlayerCommand {
    
    public RankCommand() {
        super("ranks");
    }
    
    @Override
    protected boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        if (args.isEmpty()) {
            this.showHelp(sender);
            return true;
        }
        
        final String command = args.remove(0);
        if (command.equalsIgnoreCase("help")) {
            
            if (!args.isEmpty()) {
                sender.sendMessage("§cSyntax: /rank help");
                return true;
            }
            
            this.showHelp(sender);
            return true;
        } else if (command.equalsIgnoreCase("list")) {
            
            if (args.isEmpty()) {
                this.listRanks(sender, false);
                return true;
            }
            
            final String all = args.remove(0);
            if (!all.equalsIgnoreCase("all") || !args.isEmpty()) {
                sender.sendMessage("§cSyntax: /rank list [all]");
                return true;
            }
            
            this.listRanks(sender, true);
            return true;
            
        } else {
            this.showHelp(sender);
            return true;
        }
    }
    
    @Override
    @NotNull
    protected List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final List<String> completions = new ArrayList<String>();
        completions.add("help");
        completions.add("list");
        
        if (args.isEmpty()) {
            return completions;
        }
        
        final String command = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(command.toLowerCase()));
            return completions;
        }
        
        completions.clear();
        if (!command.equalsIgnoreCase("list")) {
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
    
    private void showHelp(@NotNull final Player sender) {
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage("§bRank Commands:");
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage(" §f-§r §a/rank help");
        sender.sendMessage(" §f-§r §a/rank list [all]");
        sender.sendMessage("§8--------------------------------");
    }
    
    private void listRanks(@NotNull final Player sender, final boolean showAll) {
        
        final List<String> ranks = new ArrayList<String>();
        ranks.add(this.getRank(sender, "Shopkeeper", "group.rank_service_shopkeeper", showAll));
        ranks.add(this.getRank(sender, "Entrepreneur 1", "cvclaims.add.ep1", showAll));
        ranks.add(this.getRank(sender, "Entrepreneur 2", "cvclaims.add.ep2", showAll));
        ranks.add(this.getRank(sender, "Doctor", "cvranks.service.dr", showAll));
        ranks.add(this.getRank(sender, "Doctor Plus", "cvranks.service.dr.master", showAll));
        ranks.add(this.getRank(sender, "Repairman", "cvranks.service.repairman", showAll));
        ranks.add(this.getRank(sender, "Repairman Plus", "cvranks.service.repairman.master", showAll));
        ranks.add(this.getRank(sender, "Servicemaster", "group.rank_service_servicemaster", showAll));
        ranks.add(this.getRank(sender, "Prospector", "cvranks.mining.ps", showAll));
        ranks.add(this.getRank(sender, "Instasmelt", "cvranks.mining.instasmelt", showAll));
        ranks.add(this.getRank(sender, "Night Stalker", "cvranks.mining.nightstalker", showAll));
        ranks.add(this.getRank(sender, "Master Pick", "cvranks.mining.mp", showAll));
        ranks.add(this.getRank(sender, "Stonemason", "cvranks.build.stonemason", showAll));
        ranks.add(this.getRank(sender, "Mush Gardener", "cvranks.build.mushgardener", showAll));
        ranks.add(this.getRank(sender, "Brick Layer", "cvranks.build.bricklayer", showAll));
        ranks.add(this.getRank(sender, "Master Carpenter", "cvranks.build.carpenter", showAll));
        ranks.add(this.getRank(sender, "Xpert", "cvranks.death.te", showAll));
        ranks.add(this.getRank(sender, "Keepsake", "cvranks.death.ks", showAll));
        ranks.add(this.getRank(sender, "Death Hound", "cvranks.death.hound", showAll));
        ranks.add(this.getRank(sender, "Death Master", "cvranks.death.respawn", showAll));
        ranks.add(this.getRank(sender, "Scuba", "cvranks.service.scuba", showAll));
        ranks.add(this.getRank(sender, "Leatherworker", "cvranks.leatherworker", showAll));
        ranks.add(this.getRank(sender, "Woodworker", "cvranks.mining.ps.logs", showAll));
        ranks.add(this.getRank(sender, "Home 2", "cvhome.home2", showAll));
        ranks.add(this.getRank(sender, "Minirank Ore", "cvranks.mining.ps.ore", false));
        ranks.add(this.getRank(sender, "Minirank Flint", "cvranks.mining.ps.flint", false));
        ranks.add(this.getRank(sender, "Minirank Mycelium", "cvranks.mr.mycelium", false));
        ranks.add(this.getRank(sender, "Minirank Glass", "cvranks.mr.glass", false));
        ranks.add(this.getRank(sender, "Minirank Obsidian", "cvranks.mr.obsidian", false));
        
        ranks.removeIf(Objects::isNull);
        
        if (ranks.isEmpty()) {
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§cYou do not have any ranks currently.");
            sender.sendMessage("§8--------------------------------");
            return;
        }
        
        if (showAll) {
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bAll Ranks:");
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bRanks you have will be in§r §agreen§r§b.");
            sender.sendMessage("§bRanks you do not have yet will be in§r §cred§r§b.");
            sender.sendMessage("§8--------------------------------");
        } else {
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bYour Ranks:");
            sender.sendMessage("§8--------------------------------");
        }
        
        for (final String rank : ranks) {
            sender.sendMessage(" §f-§r " + rank);
        }
        
        sender.sendMessage("§8--------------------------------");
    }
    
    @Nullable
    private String getRank(@NotNull final Player sender, @NotNull final String name, @NotNull final String permission, final boolean showAll) {
        
        if (sender.hasPermission(permission)) {
            return "§a" + name;
        } else if (showAll) {
            return "§c" + name;
        } else {
            return null;
        }
    }
}
