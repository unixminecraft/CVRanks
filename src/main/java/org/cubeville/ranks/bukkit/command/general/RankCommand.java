package org.cubeville.ranks.bukkit.command.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.entity.Player;
import org.cubeville.ranks.bukkit.CVRanksPlugin;
import org.cubeville.ranks.bukkit.command.PlayerCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RankCommand extends PlayerCommand {
    
    private static final String PERMISSION_LIST_OTHER = "cvranks.general.list.other";
    
    private final CVRanksPlugin plugin;
    
    public RankCommand(@NotNull final CVRanksPlugin plugin) {
        super("ranks");
        
        this.plugin = plugin;
    }
    
    @Override
    protected boolean execute(@NotNull final Player sender, @NotNull final List<String> args) {
        
        if (args.isEmpty()) {
            this.showHelp(sender);
            return true;
        }
        
        if (args.get(0).equalsIgnoreCase("help")) {
            
            if (args.size() > 1) {
                sender.sendMessage("§cSyntax: /rank help");
                return true;
            }
            
            this.showHelp(sender);
            return true;
        } else if (args.size() > 3) {
            this.showHelp(sender);
            return true;
        }
        
        args.replaceAll(String::toLowerCase);
        
        final int listIndex = args.indexOf("list");
        if (listIndex != -1) {
            args.remove(listIndex);
        } else if (args.size() == 3) {
            this.showHelp(sender);
            return true;
        }
        
        final boolean all;
        if (args.contains("all")) {
            args.remove("all");
            all = true;
        } else if (args.size() == 2) {
            this.showHelp(sender);
            return true;
        } else {
            all = false;
        }
        
        if (args.isEmpty()) {
            this.listRanks(sender, sender, all);
            return true;
        }
        
        if (!sender.hasPermission(PERMISSION_LIST_OTHER)) {
            this.showHelp(sender);
            return true;
        }
        
        final String targetName = args.remove(0);
        final Player target = this.plugin.getServer().getPlayer(targetName);
        
        if (target == null) {
            sender.sendMessage("§cPlayer§r §6" + targetName + "§r §cnot found.");
            return true;
        } else if (!target.isOnline()) {
            sender.sendMessage("§6" + target.getName() + "§r §cis not online.");
            return true;
        }
        
        this.listRanks(sender, target, all);
        return true;
    }
    
    private void showHelp(@NotNull final Player sender) {
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage("§bRank Commands:");
        sender.sendMessage("§8--------------------------------");
        sender.sendMessage(" §f-§r §a/rank help");
        sender.sendMessage(" §f-§r §a/rank list " + this.getListArgs(sender));
        sender.sendMessage("§8--------------------------------");
    }
    
    @NotNull
    private String getListArgs(@NotNull final Player sender) {
        return sender.hasPermission(PERMISSION_LIST_OTHER) ? "[[player] [all]]" : "[all]";
    }
    
    private void listRanks(@NotNull final Player sender, @NotNull final Player target, final boolean showAll) {
        
        final List<String> ranks = new ArrayList<String>();
        ranks.add(this.getRank(target, "Shopkeeper", "group.rank_service_shopkeeper", showAll));
        ranks.add(this.getRank(target, "Entrepreneur 1", "cvclaims.add.ep1", showAll));
        ranks.add(this.getRank(target, "Entrepreneur 2", "cvclaims.add.ep2", showAll));
        ranks.add(this.getRank(target, "Doctor", "cvranks.service.dr", showAll));
        ranks.add(this.getRank(target, "Doctor Plus", "cvranks.service.dr.master", showAll));
        ranks.add(this.getRank(target, "Repairman", "cvranks.service.repairman", showAll));
        ranks.add(this.getRank(target, "Repairman Plus", "cvranks.service.repairman.master", showAll));
        ranks.add(this.getRank(target, "Servicemaster", "group.rank_service_servicemaster", showAll));
        ranks.add(this.getRank(target, "Prospector", "cvranks.mining.ps", showAll));
        ranks.add(this.getRank(target, "Instasmelt", "cvranks.mining.instasmelt", showAll));
        ranks.add(this.getRank(target, "Night Stalker", "cvranks.mining.nightstalker", showAll));
        ranks.add(this.getRank(target, "Master Pick", "cvranks.mining.mp", showAll));
        ranks.add(this.getRank(target, "Stonemason", "cvranks.build.stonemason", showAll));
        ranks.add(this.getRank(target, "Mush Gardener", "cvranks.build.mushgardener", showAll));
        ranks.add(this.getRank(target, "Brick Layer", "cvranks.build.bricklayer", showAll));
        ranks.add(this.getRank(target, "Master Carpenter", "cvranks.build.carpenter", showAll));
        ranks.add(this.getRank(target, "Xpert", "cvranks.death.te", showAll));
        ranks.add(this.getRank(target, "Keepsake", "cvranks.death.ks", showAll));
        ranks.add(this.getRank(target, "Death Hound", "cvranks.death.hound", showAll));
        ranks.add(this.getRank(target, "Death Master", "cvranks.death.respawn", showAll));
        ranks.add(this.getRank(target, "Scuba", "cvranks.service.scuba", showAll));
        ranks.add(this.getRank(target, "Leatherworker", "cvranks.leatherworker", showAll));
        ranks.add(this.getRank(target, "Woodworker", "cvranks.mining.ps.logs", showAll));
        ranks.add(this.getRank(target, "Home 2", "cvhome.home2", showAll));
        ranks.add(this.getRank(target, "Home 3", "cvhome.home3", showAll));
        ranks.add(this.getRank(target, "Minirank Ore", "cvranks.mining.ps.ore", false));
        ranks.add(this.getRank(target, "Minirank Flint", "cvranks.mining.ps.flint", false));
        ranks.add(this.getRank(target, "Minirank Mycelium", "cvranks.mr.mycelium", false));
        ranks.add(this.getRank(target, "Minirank Glass", "cvranks.mr.glass", false));
        ranks.add(this.getRank(target, "Minirank Obsidian", "cvranks.mr.obsidian", false));
        
        ranks.removeIf(Objects::isNull);
        
        final boolean samePlayer = sender.getUniqueId().equals(target.getUniqueId());
        
        if (ranks.isEmpty()) {
            sender.sendMessage("§8--------------------------------");
            if (samePlayer) {
                sender.sendMessage("§cYou do not have any ranks currently.");
            } else {
                sender.sendMessage("§c" + target.getName() + " does not have any ranks currently.");
            }
            sender.sendMessage("§8--------------------------------");
            return;
        }
        
        if (showAll) {
            sender.sendMessage("§8--------------------------------");
            sender.sendMessage("§bAll Ranks:");
            sender.sendMessage("§8--------------------------------");
            if (samePlayer) {
                sender.sendMessage("§bRanks you have will be in§r §agreen§r§b.");
                sender.sendMessage("§bRanks you do not have yet will be in§r §cred§r§b.");
            } else {
                sender.sendMessage("§bRanks " + target.getName() + " has will be in §r §agreen§r§b.");
                sender.sendMessage("§bRanks " + target.getName() + " does not have will be in §r §cred§r§b.");
            }
            sender.sendMessage("§8--------------------------------");
        } else {
            sender.sendMessage("§8--------------------------------");
            if (samePlayer) {
                sender.sendMessage("§bYour Ranks:");
            } else {
                sender.sendMessage("§b" + target.getName() + "'s Ranks:");
            }
            sender.sendMessage("§8--------------------------------");
        }
        
        for (final String rank : ranks) {
            sender.sendMessage(" §f-§r " + rank);
        }
        
        sender.sendMessage("§8--------------------------------");
    }
    
    @Nullable
    private String getRank(@NotNull final Player target, @NotNull final String name, @NotNull final String permission, final boolean showAll) {
        
        if (target.hasPermission(permission)) {
            return "§a" + name;
        } else if (showAll) {
            return "§c" + name;
        } else {
            return null;
        }
    }
    
    @Override
    @NotNull
    protected List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        
        final List<String> completions = new ArrayList<String>();
        this.addAllPlayers(completions, sender);
        
        final List<String> players = new ArrayList<String>(completions);
        
        completions.add("help");
        completions.add("list");
        completions.add("all");
        
        if (args.isEmpty()) {
            return completions;
        }
        
        final String first = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(first.toLowerCase()));
            return completions;
        }
        
        if (first.equalsIgnoreCase("help")) {
            completions.clear();
            return completions;
        }
        
        boolean hasAll = first.equalsIgnoreCase("all");
        boolean hasList = first.equalsIgnoreCase("list");
        
        if (hasList) {
            completions.remove("list");
        } else if (hasAll) {
            completions.remove("all");
        } else {
            completions.removeAll(players);
        }
        
        final String second = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(second.toLowerCase()));
            return completions;
        }
        
        if (hasList) {
            if (second.equalsIgnoreCase("all")) {
                completions.remove("all");
            } else {
                completions.removeAll(players);
            }
        } else if (hasAll) {
            if (second.equalsIgnoreCase("list")) {
                completions.remove("list");
            } else {
                completions.removeAll(players);
            }
        } else {
            if (second.equalsIgnoreCase("all")) {
                completions.remove("all");
            } else if (second.equalsIgnoreCase("list")) {
                completions.remove("list");
            }
        }
        
        final String third = args.remove(0);
        if (args.isEmpty()) {
            completions.removeIf(completion -> !completion.toLowerCase().startsWith(third.toLowerCase()));
            return completions;
        }
        
        completions.clear();
        return completions;
    }
    
    private void addAllPlayers(@NotNull final List<String> completions, @NotNull final Player sender) {
        
        if (!sender.hasPermission(PERMISSION_LIST_OTHER)) {
            return;
        }
        
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            completions.add(player.getName());
        }
    }
}
