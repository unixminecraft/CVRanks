package org.cubeville.cvranks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.block.Block;

public class CVRanks extends JavaPlugin implements Listener
{
    private int uptime;
    private Map<UUID, Integer> lastHeals;
    private Map<UUID, Integer> lastRespawns;
    private Map<UUID, Integer> lastDeathHound;
    private Map<UUID, Integer> lastKeepsake;
    private Map<UUID, Integer> lastKeepXP;
    
    private Map<UUID, Location> deathLocation;
    private Set<UUID> pendingDeathHoundNotification;

    private Set<UUID> stonemasonActive;
    private Set<UUID> mossgardenerActive;
    private Set<UUID> bricklayerActive;
    private Set<UUID> carpenterActive;
    private Set<UUID> mrGlassActive;
    private Set<UUID> mrObsidianActive;
    private Set<UUID> mrMyceliumActive;
    private Set<UUID> scubaActive;
    private Set<UUID> nightstalkerActive;
    private Set<UUID> smeltActive;
    private Map<UUID, Integer> lastRepairs;

    private LevelCommand levelCommand;
    private RepairCommand repairCommand;
    
    public void onEnable() {
        stonemasonActive = new HashSet<>();
        mossgardenerActive = new HashSet<>();
        bricklayerActive = new HashSet<>();
        carpenterActive = new HashSet<>();
        scubaActive = new HashSet<>();
        nightstalkerActive = new HashSet<>();
        smeltActive = new HashSet<>();
        mrGlassActive = new HashSet<>();
        mrObsidianActive = new HashSet<>();
        mrMyceliumActive = new HashSet<>();
        
        deathLocation = new HashMap<>();
        pendingDeathHoundNotification = new HashSet<>();
        
        lastHeals = new HashMap<>();
        lastRespawns = new HashMap<>();
        lastDeathHound = new HashMap<>();
        lastKeepsake = new HashMap<>();
        lastKeepXP = new HashMap<>();
        
        uptime = 0;
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                public void run() {
                    uptime += 10;
                    for(UUID player: lastHeals.keySet()) {
                        Player p = getServer().getPlayer(player);
                        if(p == null || docTime(p) <= 0) {
                            if(p != null) p.sendMessage("Your doctor ability is ready to use.");
                            lastHeals.remove(player);
                        }
                    }

                    for(UUID player: lastRespawns.keySet()) {
                        Player p = getServer().getPlayer(player);
                        if(p == null || respawnTime(p) <= 0) {
                            if(p != null) p.sendMessage("Your respawn ability is ready to use.");
                            lastRespawns.remove(player);
                        }
                    }

                    for(UUID player: lastDeathHound.keySet()) {
                        Player p = getServer().getPlayer(player);
                        if(p == null || deathHoundTime(p) <= 0) {
                            if(p != null) p.sendMessage("Your death hound ability is ready to use.");
                            lastDeathHound.remove(player);
                        }                        
                    }
                }
            }, 200, 200);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        File dataFolder = getDataFolder();
        if(!dataFolder.exists()) dataFolder.mkdirs();

        getServer().addRecipe(new ShapedRecipe(new ItemStack(Material.SADDLE)).shape(new String[] { "XXX", "XXX" }).setIngredient('X', Material.LEATHER));

        repairCommand = new RepairCommand(this);
        if(getConfig().getConfigurationSection("enchantments") != null) {
            levelCommand = new LevelCommand(getConfig().getConfigurationSection("enchantments"), this);
        }

    }

    public int getUptime() {
        return uptime;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player senderPlayer = null;
        UUID senderId = null;
        if(sender instanceof Player) {
            senderPlayer = (Player) sender;
            senderId = senderPlayer.getUniqueId();
        }

        if(command.getName().equals("level")) {
            levelCommand.onLevelCommand(sender, args);
            return true;
        }

        else if(command.getName().equals("rp")) {
            repairCommand.onRepairCommand(sender, args);
            return true;
        }
        
        else if (command.getName().equals("mason") || command.getName().equals("mg") || command.getName().equals("brick") || command.getName().equals("carp") || command.getName().equals("ns") || command.getName().equals("scuba") || command.getName().equals("smelt") || command.getName().equals("mr")) {

            Set<UUID> typeSet;
            String typeName;
            String typeCommand = command.getName();
            String viewCommand = typeCommand;
            int argOffset = 0;
            if(typeCommand.equals("mason")) {
                typeSet = stonemasonActive;
                typeName = "stonemason";
            }
            else if(typeCommand.equals("mg")) {
                typeSet = mossgardenerActive;
                typeName = "moss gardener";
            }
            else if(typeCommand.equals("brick")) {
                typeSet = bricklayerActive;
                typeName = "bricklayer";
            }
            else if(typeCommand.equals("carp")) {
                typeSet = carpenterActive;
                typeName = "master carpenter";
            }
            else if(typeCommand.equals("scuba")) {
                typeSet = scubaActive;
                typeName = "scuba";
            }
            else if(typeCommand.equals("ns")) {
                typeSet = nightstalkerActive;
                typeName = "nightstalker";
            }
            else if(typeCommand.equals("smelt")) {
                typeSet = smeltActive;
                typeName = "instasmelt";
            }
            else if(typeCommand.equals("mr")) {
                argOffset = 1;
                if(args.length < 1) {
                    sender.sendMessage("§c/mr <type> [on|off]");
                    return true;
                }
                String permissionCheck = null;
                if(args[0].equals("glass")) {
                    typeSet = mrGlassActive;
                    typeName = "glass minirank";
                    permissionCheck = "cvranks.mr.glass";
                }
                else if(args[0].equals("obsidian")) {
                    typeSet = mrObsidianActive;
                    typeName = "obsidian minirank";
                    permissionCheck = "cvranks.mr.obsidian";
                }
                else if(args[0].equals("mycelium")) {
                    typeSet = mrMyceliumActive;
                    typeName = "mycelium minirank";
                    permissionCheck = "cvranks.mr.mycelium";
                }
                else {
                    sender.sendMessage("§cUnknown minirank, can be one of mycelium, glass, obsidian.");
                    return true;
                }
                viewCommand = "mr " + args[0];
                if(!sender.hasPermission(permissionCheck)) {
                    sender.sendMessage("§cNo permission.");
                    return true;
                }
            }
            else {
                sender.sendMessage("§cUnknown command.");
                return true;
            }

            if(args.length - argOffset > 1) {
                sender.sendMessage("§cToo many arguments.");
                sender.sendMessage("§c/" + viewCommand + " [on|off]");
                return true;
            }
            
            if(args.length == argOffset) {
                if(typeSet.contains(senderId)) {
                    sender.sendMessage("§aYour " + typeName + " ability is currently enabled.");
                    sender.sendMessage("§aYou can toggle it with /" + viewCommand + " off");
                }
                else {
                    sender.sendMessage("§cYour " + typeName + " ability is currently disabled.");
                    sender.sendMessage("§cYou may toggle it with /" + viewCommand + " on");
                }
                return true;
            }

            if(args[argOffset].equals("on")) {
                if(typeSet.contains(senderId)) {
                    sender.sendMessage("§cYour " + typeName + " ability is already enabled.");
                }
                else {
                    typeSet.add(senderId);
                    sender.sendMessage("§aYour " + typeName + " ability has been enabled.");
                }
                if(typeName.equals("nightstalker")) activateNightstalker(senderPlayer, true);
                if(typeName.equals("scuba")) activateScuba(senderPlayer, true);
            }
            else if(args[argOffset].equals("off")) {
                if(typeSet.contains(senderId)) {
                    typeSet.remove(senderId);
                    sender.sendMessage("§aYour " + typeName + " ability has been disabled.");
                }
                else {
                    sender.sendMessage("§cYour " + typeName + " ability is already disabled.");
                }
                if(typeName.equals("nightstalker")) activateNightstalker(senderPlayer, false);
                if(typeName.equals("scuba")) activateScuba(senderPlayer, false);
            }
            else {
                sender.sendMessage("§c/" + viewCommand + " [on|off]");
            }
            return true;
        }

        else if (command.getName().equals("dh")) {
            if(args.length == 0) {
                sender.sendMessage("§2Death Hound Command List");
                sender.sendMessage("§2----------------------"); // ^
                sender.sendMessage("§a/dh list - Lists online death hounds and their recharge timers");
                sender.sendMessage("§a/dh me - Send death coordinates to yourself");
                sender.sendMessage("§a/dh <player> - Send death coordinates to another player");
            }
            else if(args.length > 1) {
                sender.sendMessage("§cToo many arguments.");
                sender.sendMessage("§c/dh <list|me|player>");
            }
            else {
                if(args[0].equals("list")) {
                    boolean found = false;
                    for (Player p : getServer().getOnlinePlayers()) {
                        if(p.hasPermission("cvranks.death.ks") && !p.hasPermission("cvranks.death.ks.hidefromlist") && senderPlayer.canSee(p)) {
                            found = true;
                            UUID playerId = p.getUniqueId();
                            String dhName = p.getDisplayName();
                            if(lastDeathHound.containsKey(playerId)) {
                                int t = deathHoundTime(p) / 50;
                                sender.sendMessage("§c" + dhName + " - " + t + " hours left.");
                            } else {
                                sender.sendMessage("§a" + dhName + " - ready");
                            }
                        }
                    }
                    if(!found) { sender.sendMessage("§cNo Death Hounds online."); }
                }
                else {
                    if(!senderPlayer.hasPermission("cvranks.death.ks")) {
                        sender.sendMessage("§cNo permission.");
                    }
                    else if(deathHoundTime(senderPlayer) > 0) {
                        sender.sendMessage("§cYou cannot use your Death Hound abilities for another " + deathHoundTime(senderPlayer) + " seconds.");
                    }
                    else {
                        Player target;
                        if(args[0].equals("me")) { target = senderPlayer; }
                        else { target = getServer().getPlayerExact(args[0]); }
                        
                        if(target == null || !target.isOnline() || !senderPlayer.canSee(target)) {
                            sender.sendMessage("§cPlayer is not online or is not on survival.");
                        }
                        else if (!deathLocation.containsKey(target.getUniqueId())) {
                            sender.sendMessage("§cPlayer has not died recently.");
                        }
                        else if (!pendingDeathHoundNotification.contains(target.getUniqueId())) {
                            sender.sendMessage("§cPlayer has already had their death coordinates sent or has used /respawn.");
                        }
                        else {
                            resetDeathHoundTime(senderPlayer);
                            pendingDeathHoundNotification.remove(target.getUniqueId());
                            Location dl = deathLocation.get(target.getUniqueId());
                            
                            if(target.getUniqueId().equals(senderPlayer.getUniqueId())) {
                                target.sendMessage("§aYour last death coordinates:");
                            }
                            else {
                                sender.sendMessage("§aYou have sent §6" + target.getName() + "§a's death coordinates to them.");
                                target.sendMessage("§6" + senderPlayer.getName() + " §ais sending you your last death coordinates:");
                            }
                            
                            target.sendMessage("§aX: §6" + dl.getBlockX() + "§a, Y: §6" + dl.getBlockY() + "§a, Z: §6" + dl.getBlockZ());
                        }
                    }
                }
            }
        }
        
        else if (command.getName().equals("doc")) {
            if(args.length == 0) {
                sender.sendMessage("§2Doctor Command List");
                sender.sendMessage("§2----------------------"); // ^
                sender.sendMessage("§a/doc list - Lists online doctors and their recharge timers");
                sender.sendMessage("§a/doc me - Heals yourself");
                sender.sendMessage("§a/doc <player> - Heals another player");
            }
            else if(args.length > 1) {
                sender.sendMessage("§cToo many arguments.");
                sender.sendMessage("§c/doc <list|me|player>");
            }
            else {
                if(args[0].equals("list")) {
                    boolean found = false;
                    for (Player p : getServer().getOnlinePlayers()) {
                        if(p.hasPermission("cvranks.service.dr") == true && p.hasPermission("cvranks.service.dr.hidefromlist") == false && senderPlayer.canSee(p) == true) {
                            found = true;
                            UUID playerId = p.getUniqueId();
                            String docName = p.getDisplayName();
                            if (lastHeals.containsKey(playerId)) {
                                int t = docTime(p) / 50;
                                sender.sendMessage("§c" + docName + " - " + t + " hours left.");
                            } else {
                                sender.sendMessage("§a " + docName + " - ready");
                            }
                        }
                    }
                    if(found == false) sender.sendMessage("§cNo doctors online.");
                }
                else if(args[0].equals("me")) {
                    if(senderPlayer != null) {
                        if(!senderPlayer.hasPermission("cvranks.service.dr")) {
                            sender.sendMessage("§cNo permission.");
                        }
                        else {
                            docPlayer(sender, senderPlayer);
                        }
                    }
                }
                else {
                    if(!senderPlayer.hasPermission("cvranks.service.dr")) {
                        sender.sendMessage("§cNo permission.");
                    }
                    else {
                        String playerName = args[0];
                        Player player = getServer().getPlayer(playerName);
                        if(player == null || senderPlayer.canSee(player) == false) {
                            sender.sendMessage("§cPlayer not found.");
                        }
                        else {
                            docPlayer(sender, player);
                        }
                    }
                }
            }
            return true;
        }

        else if (command.getName().equals("respawn")) {
            if(senderPlayer != null && deathLocation.containsKey(senderId)) {
                if(respawnTime(senderPlayer) > 0) {
                    sender.sendMessage("§cYour respawn cooldown is still at " + respawnTime(senderPlayer) + " seconds.");
                }
                else {
                    senderPlayer.teleport(deathLocation.get(senderId));
                    deathLocation.remove(senderId);
                    pendingDeathHoundNotification.remove(senderId);
                    sender.sendMessage("§aYou have been returned to the point of your last death!");
                    resetRespawnTime(senderPlayer);
                }
            }
            else {
                sender.sendMessage("§cYou don't have a death point to return to.");
            }
        }
        
        return false;
    }

    public void activateScuba(Player player, boolean status) {
        if(status) {
            PotionEffect scuba = new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1);
            player.addPotionEffect(scuba);
            
        }
        else {
            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
        }
    }

    public void activateNightstalker(Player player, boolean status) {
        if(status) {
            PotionEffect ns = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1);
            player.addPotionEffect(ns);
        }
        else {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        scubaActive.remove(uuid);
        nightstalkerActive.remove(uuid);
    }

    public int docTime(Player player) {
        if(lastHeals.get(player.getUniqueId()) == null) return 0;
        // player's doc interval
        int rtime = 1200;
        if(player.hasPermission("cvranks.service.dr.master")) rtime = 600;
        // time since last heal
        int htime = uptime - lastHeals.get(player.getUniqueId());
        // time until next available heal
        return rtime - htime;
    }

    public int respawnTime(Player player) {
        if(lastRespawns.get(player.getUniqueId()) == null) return 0;
        int rtime = 3600;
        int htime = uptime - lastRespawns.get(player.getUniqueId());
        return rtime - htime;
    }

    public void resetRespawnTime(Player player) {
        lastRespawns.put(player.getUniqueId(), uptime);
    }

    public int deathHoundTime(Player player) {
        if(lastDeathHound.get(player.getUniqueId()) == null) return 0;
        int rtime = 1200;
        int htime = uptime - lastDeathHound.get(player.getUniqueId());
        return rtime - htime;
    }

    public void resetDeathHoundTime(Player player) {
        lastDeathHound.put(player.getUniqueId(), uptime);
    }

    public int keepsakeTime(Player player) {
        if(lastKeepsake.get(player.getUniqueId()) == null) return 0;
        int rtime = 1200;
        int htime = uptime - lastKeepsake.get(player.getUniqueId());
        return rtime - htime;
    }

    public void resetKeepsakeTime(Player player) {
        lastKeepsake.put(player.getUniqueId(), uptime);
    }

    public int keepXPTime(Player player) {
        if(lastKeepXP.get(player.getUniqueId()) == null) return 0;
        int rtime = 1200;
        int htime = uptime - lastKeepXP.get(player.getUniqueId());
        return rtime - htime;
    }

    public void resetKeepXPTime(Player player) {
        lastKeepXP.put(player.getUniqueId(), uptime);
    }

    public void docPlayer(CommandSender sender, Player target) {
        boolean used = false;

        String senderName;

        if(sender instanceof Player) {
            Player senderPlayer = (Player) sender;
            if(lastHeals.containsKey(senderPlayer.getUniqueId())) {
                sender.sendMessage("§cYour doctor ability is not ready to use yet.");
                return;
            }
            if(senderPlayer.getUniqueId().equals(target.getUniqueId())) {
                senderName = "yourself";
            }
            else {
                senderName = senderPlayer.getDisplayName();
            }
        }
        else {
            senderName = "Console";
        }

        if (target.isDead()) {
            target.sendMessage("§a" + senderName + " tried to doc you, but it was too late.");
            sender.sendMessage("§cToo late...");
            return;
        }
        
        if (target.getHealth() < target.getMaxHealth()) {
            used = true;
            target.setHealth(target.getMaxHealth());
            target.sendMessage("§aYou have been healed by " + senderName + ".");
        }

        if (target.getFoodLevel() < 20) {
            used = true;
            target.setFoodLevel(20);
            target.setExhaustion(1.0F);
            target.setSaturation(5.0F);
            target.sendMessage("§aYour hunger has been refilled by " + senderName + ".");
        }

        if(sender.hasPermission("cvranks.service.svc")) {
            if (target.getFireTicks() > 0) {
                used = true;
                target.setFireTicks(0);
                target.sendMessage("§aYou have been extinguished by " + senderName + ".");
            }
            if (target.getRemainingAir() < target.getMaximumAir()) {
                used = true;
                target.setRemainingAir(target.getMaximumAir());
                target.sendMessage("§aYour air has been refilled by " + senderName + ".");
            }
        }

        if(used) {
            sender.sendMessage(target.getDisplayName() + " has been healed.");
            if(sender instanceof Player) {
                Player p = (Player) sender;
                lastHeals.put(p.getUniqueId(), uptime);
            }
        }
        else {
            sender.sendMessage(target.getDisplayName() + " does not need to be healed!");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.isCancelled()) return;

        Material material = event.getBlockPlaced().getType();
        UUID playerId = event.getPlayer().getUniqueId();
        
        if(material == Material.COBBLESTONE) {
            if(stonemasonActive.contains(playerId))
                event.getBlockPlaced().setType(Material.STONE);
        }
        else if(material == Material.CLAY) {
            if(bricklayerActive.contains(playerId))
                event.getBlockPlaced().setType(Material.BRICKS);
        }
        else if(material == Material.SAND) {
            if(carpenterActive.contains(playerId) || mrGlassActive.contains(playerId))
                event.getBlockPlaced().setType(Material.GLASS);
        }
        else if(material == Material.SOUL_SAND) {
            if(carpenterActive.contains(playerId) || mrObsidianActive.contains(playerId))
                event.getBlockPlaced().setType(Material.OBSIDIAN);
        }
        else if(material == Material.DIRT) {
            if(mrMyceliumActive.contains(playerId)) {
                event.getBlockPlaced().setType(Material.MYCELIUM);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        boolean keepInventory;
        if(player.hasPermission("cvranks.death.ks.admin")) { keepInventory = true; }
        else if(player.hasPermission("cvranks.death.ks") && keepsakeTime(player) <= 0) {
            resetKeepsakeTime(player);
            keepInventory = true;
        }
        else { keepInventory = false; }
        
        if(keepInventory) {
            event.setKeepInventory(true);
            event.getDrops().clear();
            Inventory inventory = player.getInventory();
            for(int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if(item != null) {
                    if(item.getEnchantmentLevel(Enchantment.VANISHING_CURSE) > 0) {
                        inventory.clear(i);
                    }
                }
            }
        }
        
        boolean keepXP;
        if(player.hasPermission("cvranks.death.te.admin")) { keepXP = true; }
        else if(player.hasPermission("cvranks.death.te") && keepXPTime(player) <= 0) {
            resetKeepXPTime(player);
            keepXP = true;
        }
        else { keepXP = false; }
        
        if(keepXP) {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
        
        pendingDeathHoundNotification.add(player.getUniqueId());
        deathLocation.put(player.getUniqueId(), player.getLocation());
    }

//    @EventHandler
//    public void onCraftItem(CraftItemEvent event)
//    {
//        if(event.isCancelled()) return;
//        if (!(event.getRecipe() instanceof ShapedRecipe)) return;
//        if (event.getRecipe().getResult().getType() != Material.SADDLE) return;
//        if (!(event.getView().getPlayer() instanceof Player)) return;
//        Player player = (Player)event.getView().getPlayer();
//        if (!player.hasPermission("cvranks.leatherworker")) event.setCancelled(true);
//    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled()) return;
        Player player = event.getPlayer();
        boolean smelt = smeltActive.contains(player.getUniqueId());
        Block target = event.getBlock();

        boolean permPs = player.hasPermission("cvranks.mining.ps");
        boolean permPsExtraOre = player.hasPermission("cvranks.mining.ps.ore");
        boolean permPsExtraLogs = player.hasPermission("cvranks.mining.ps.logs");
        boolean permPsExtraFlint = player.hasPermission("cvranks.mining.ps.flint");

        if(permPs || permPsExtraOre || permPsExtraLogs || permPsExtraFlint) { // TODO: ugh, lag
            ItemStack tool = player.getInventory().getItemInMainHand();
            if(tool == null || (tool.containsEnchantment(Enchantment.SILK_TOUCH) && (target.getType() != Material.ACACIA_LOG && target.getType() != Material.BIRCH_LOG && target.getType() != Material.DARK_OAK_LOG && target.getType() != Material.JUNGLE_LOG && target.getType() != Material.OAK_LOG && target.getType() != Material.SPRUCE_LOG))) return;
            
            int rand = (int)Math.floor(100.0D * Math.random()) + 1;
            ItemStack drop = null;
            int chance = -1;
            String message = "";
            
            if(target.getType() == Material.DIAMOND_ORE) {
                if(permPs || permPsExtraOre) {
                    if(tool.getType() == Material.DIAMOND_PICKAXE || tool.getType() == Material.NETHERITE_PICKAXE) chance = 8;
                    drop = new ItemStack(Material.DIAMOND);
                    message = "§aYou found an extra diamond.";
                }
            }
            else if(target.getType() == Material.COAL_ORE) {
                if(permPs || permPsExtraOre) {
                    if(tool.getType() == Material.STONE_PICKAXE) chance = 4;
                    else if(tool.getType() == Material.IRON_PICKAXE) chance = 8;
                    else if(tool.getType() == Material.DIAMOND_PICKAXE) chance = 16;
                    else if(tool.getType() == Material.NETHERITE_PICKAXE) chance = 20;
                    else if(tool.getType() == Material.GOLDEN_PICKAXE) chance = 24;
                    drop = new ItemStack(Material.COAL);
                    message = "§aYou found extra coal.";
                }
            }
            else if(target.getType() == Material.NETHER_QUARTZ_ORE) {
                if(permPs || permPsExtraOre) {
                    if(tool.getType() == Material.STONE_PICKAXE) chance = 4;
                    else if(tool.getType() == Material.IRON_PICKAXE) chance = 8;
                    else if(tool.getType() == Material.DIAMOND_PICKAXE) chance = 16;
                    else if(tool.getType() == Material.NETHERITE_PICKAXE) chance = 20;
                    else if(tool.getType() == Material.GOLDEN_PICKAXE) chance = 24;
                    drop = new ItemStack(Material.QUARTZ);
                    message = "§aYou found extra quartz";
                }
            } 
            else if(target.getType() == Material.GRAVEL) {
                if(permPs || permPsExtraFlint) {
                    if(tool.getType() == Material.STONE_SHOVEL) chance = 4;
                    else if(tool.getType() == Material.IRON_SHOVEL) chance = 8;
                    else if(tool.getType() == Material.DIAMOND_SHOVEL) chance = 16;
                    else if(tool.getType() == Material.NETHERITE_SHOVEL) chance = 20;
                    else if(tool.getType() == Material.GOLDEN_SHOVEL) chance = 24;
                    drop = new ItemStack(Material.FLINT);
                    message = "§aYou found extra flint";
                }
            }
            else if(target.getType() == Material.ACACIA_LOG || target.getType() == Material.BIRCH_LOG ||
                    target.getType() == Material.DARK_OAK_LOG || target.getType() == Material.JUNGLE_LOG ||
                    target.getType() == Material.OAK_LOG || target.getType() == Material.SPRUCE_LOG) {
                if(permPs || permPsExtraLogs) {
                    if(tool.getType() == Material.STONE_AXE) chance = 4;
                    else if(tool.getType() == Material.IRON_AXE) chance = 8;
                    else if(tool.getType() == Material.DIAMOND_AXE) chance = 16;
                    else if(tool.getType() == Material.NETHERITE_AXE) chance = 20;
                    else if(tool.getType() == Material.GOLDEN_AXE) chance = 24;
                    drop = new ItemStack(target.getType());
                    message = "§aYou found extra wood";
                }
            }
            if(drop != null && rand <= chance) {
                player.sendMessage("" + message);
                player.getWorld().dropItemNaturally(target.getLocation(), drop);
            }
        }
        if(smelt) {
            ItemStack drop = null;
            if(target.getType() == Material.IRON_ORE) {
                drop = new ItemStack(Material.IRON_INGOT);
            }
            else if(target.getType() == Material.GOLD_ORE) {
                drop = new ItemStack(Material.GOLD_INGOT);
            }
            if(drop != null) {
                event.setCancelled(true);
                target.setType(Material.AIR);
                int exp = 1;
                if(player.hasPermission("cvranks.mining.mp") && Math.random() < 0.15) {
                    exp = 2;
                    drop.setAmount(2);
                    player.sendMessage("§aYou found an extra ingot.");
                }
                player.giveExp(exp);
                player.getWorld().dropItemNaturally(target.getLocation(), drop);
            }
        }
        if(target.getType() == Material.COAL_ORE && Math.random() < 0.02 && player.hasPermission("cvranks.mining.mp")) {
            ItemStack drop = new ItemStack(Material.DIAMOND);
            player.getWorld().dropItemNaturally(target.getLocation(), drop);
            player.sendMessage("§aYou found a diamond.");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller().hasPermission("cvranks.leatherworker")) {

            List<EntityType> leatherAnimals = new ArrayList<>();
            leatherAnimals.add(EntityType.COW);
            leatherAnimals.add(EntityType.MUSHROOM_COW);
            leatherAnimals.add(EntityType.HORSE);
            leatherAnimals.add(EntityType.DONKEY);
            leatherAnimals.add(EntityType.MULE);
            leatherAnimals.add(EntityType.LLAMA);

            if (leatherAnimals.contains(event.getEntity().getType())) {
                Player killer = event.getEntity().getKiller();
                if(killer != null) {
                    ItemStack drop = new ItemStack(Material.LEATHER);
                    killer.getWorld().dropItemNaturally(event.getEntity().getLocation(), drop);
                    killer.sendMessage("§aYou got extra leather.");
                }
            }
        }
    }
}
