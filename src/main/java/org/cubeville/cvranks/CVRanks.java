package org.cubeville.cvranks;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
import org.bukkit.material.MaterialData;
import org.bukkit.TreeSpecies;
import org.bukkit.material.Wood;
import org.bukkit.material.Tree;

public class CVRanks extends JavaPlugin implements Listener
{
    private int uptime;
    private Map<UUID, Integer> lastHeals;
    private Map<UUID, Location> deathLocation;
    private Set<UUID> stonemasonActive;
    private Set<UUID> mossgardenerActive;
    private Set<UUID> bricklayerActive;
    private Set<UUID> carpenterActive;
    private Set<UUID> scubaActive;
    private Set<UUID> concreteActive;
    private Set<UUID> nightstalkerActive;
    private Set<UUID> smeltActive;
    private Map<UUID, Inventory> ratpackInventories;
    private Map<UUID, Integer> lastRepairs;

    private LevelCommand levelCommand;
    private RepairCommand repairCommand;
    
    public void onEnable() {
        stonemasonActive = new HashSet<>();
        mossgardenerActive = new HashSet<>();
        bricklayerActive = new HashSet<>();
        carpenterActive = new HashSet<>();
        scubaActive = new HashSet<>();
        concreteActive = new HashSet<>();
        nightstalkerActive = new HashSet<>();
        smeltActive = new HashSet<>();
        
        deathLocation = new HashMap<>();
        
        lastHeals = new HashMap<>();
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
                }
            }, 200, 200);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        File dataFolder = getDataFolder();
        if(!dataFolder.exists()) dataFolder.mkdirs();

        ratpackInventories = new HashMap<>();
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for(Player p: players) loadInventory(p);

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

        if(command.getName().equals("rat")) {
            if(senderPlayer == null) return true;
            senderPlayer.openInventory(ratpackInventories.get(senderPlayer.getUniqueId()));
            return true;
        }

        else if(command.getName().equals("level")) {
            levelCommand.onLevelCommand(sender, args);
            return true;
        }

        else if(command.getName().equals("rp")) {
            repairCommand.onRepairCommand(sender, args);
            return true;
        }
        
        else if (command.getName().equals("mason") || command.getName().equals("mg") || command.getName().equals("brick") || command.getName().equals("carp") || command.getName().equals("ns") || command.getName().equals("scuba") || command.getName().equals("concrete") || command.getName().equals("smelt")) {

            Set<UUID> typeSet;
            String typeName;
            String typeCommand = command.getName();
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
            else if(typeCommand.equals("concrete")) {
                typeSet = concreteActive;
                typeName = "concrete";
            }
            else if(typeCommand.equals("ns")) {
                typeSet = nightstalkerActive;
                typeName = "nightstalker";
            }
            else { // smelt
                typeSet = smeltActive;
                typeName = "instasmelt";
            }
            if(args.length > 1) {
                sender.sendMessage("Too many arguments.");
                sender.sendMessage("/" + typeCommand + " [on|off]");
            }
            else if(args.length == 0) {
                if(typeSet.contains(senderId)) {
                    sender.sendMessage("§aYour " + typeName + " ability is currently enabled.");
                    sender.sendMessage("§aYou can toggle it with /" + typeCommand + " off");
                }
                else {
                    sender.sendMessage("§cYour " + typeName + " ability is currently disabled.");
                    sender.sendMessage("§cYou may toggle it with /" + typeCommand + " on");
                }
            }
            else {
                if(args[0].equals("on")) {
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
                else if(args[0].equals("off")) {
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
                    sender.sendMessage("§c/" + typeCommand + " [on|off]");
                }
            }
            return true;
        }
        
        else if (command.getName().equals("doc")) {
            if(args.length == 0) {
                sender.sendMessage("§2Doctor Command List"); // TODO: dark green
                sender.sendMessage("§2----------------------"); // ^
                sender.sendMessage("/doc list - Lists online doctors and their recharge timers"); // TODO: light green
                sender.sendMessage("/doc me - Heals yourself");
                sender.sendMessage("/doc <player> - Heals another player");
            }
            else if(args.length > 1) {
                sender.sendMessage("Too many arguments.");
                sender.sendMessage("/doctor <list|me|player>");
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
                senderPlayer.teleport(deathLocation.get(senderId));
                deathLocation.remove(senderId);
                sender.sendMessage("§aYou have been returned to the point of your last death!");
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
    
    public void docPlayer(CommandSender sender, Player player) {
        boolean used = false;

        String senderName;

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(lastHeals.containsKey(p.getUniqueId())) {
                sender.sendMessage("§cYour doctor ability is not ready to use yet.");
                return;
            }
            if(p.getUniqueId().equals(player.getUniqueId())) {
                senderName = "yourself";
            }
            else {
                senderName = p.getDisplayName();
            }
        }
        else {
            senderName = "Console";
        }
        
        if (player.getHealth() < player.getMaxHealth()) {
            used = true;
            player.setHealth(player.getMaxHealth());
            player.sendMessage("§aYou have been healed by " + senderName + ".");
        }

        if (player.getFoodLevel() < 20) {
            used = true;
            player.setFoodLevel(20);
            player.setExhaustion(1.0F);
            player.setSaturation(5.0F);
            player.sendMessage("§aYour hunger has been refilled by " + senderName + ".");
        }

        if(sender.hasPermission("cvranks.service.svc")) {
            if (player.getFireTicks() > 0) {
                used = true;
                player.setFireTicks(0);
                player.sendMessage("§aYou have been extinguished by " + senderName + ".");
            }
            if (player.getRemainingAir() < player.getMaximumAir()) {
                used = true;
                player.setRemainingAir(player.getMaximumAir());
                player.sendMessage("§aYour air has been refilled by " + senderName + ".");
            }
        }

        if(used) {
            sender.sendMessage(player.getDisplayName() + " has been healed.");
            if(sender instanceof Player) {
                Player p = (Player) sender;
                lastHeals.put(p.getUniqueId(), uptime);
            }
        }
        else {
            sender.sendMessage(player.getDisplayName() + " does not need to be healed!");
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
                event.getBlockPlaced().setType(Material.BRICK);
        }
        else if(material == Material.CONCRETE_POWDER) {
            if(concreteActive.contains(playerId)) {
                byte meta = event.getBlockPlaced().getData();
                event.getBlockPlaced().setType(Material.CONCRETE);
                event.getBlockPlaced().setData(meta);
            }
        }
        else if(material == Material.SAND) {
            if(carpenterActive.contains(playerId))
                event.getBlockPlaced().setType(Material.GLASS);
        }
        else if(material == Material.SOUL_SAND) {
            if(carpenterActive.contains(playerId))
                event.getBlockPlaced().setType(Material.OBSIDIAN);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if(player.hasPermission("cvranks.death.ks")) {
            event.setKeepInventory(true);
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
        if(player.hasPermission("cvranks.death.te")) {
            // Death tax?
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
        if(player.hasPermission("cvranks.death.dm")) {
            deathLocation.put(player.getUniqueId(), player.getLocation());
        }
    }
        
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadInventory(player);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(!event.getInventory().getName().startsWith("Packrat ")) return;
        Player player = Bukkit.getPlayerExact(event.getInventory().getName().substring(8));
        if(player == null) {
            event.getPlayer().sendMessage("§cCould not save packrat inventory, unknown player " + event.getInventory().getName().substring(8));
            return;
        }
        saveInventory(player);
    }
    
    public void loadInventory(Player player) {
        if(!player.hasPermission("cvranks.death.ratpack")) return;
        
        UUID playerId = player.getUniqueId();
        int inventorySize = 18;
        if(player.hasPermission("cvranks.death.ratpack.master")) inventorySize = 27;

        if(ratpackInventories.containsKey(playerId)) {
            if(inventorySize == ratpackInventories.get(playerId).getSize()) {
                return;
            }
            ratpackInventories.remove(playerId);
        }

        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Packrat " + player.getName());
        ratpackInventories.put(playerId, inventory);

        File configFile = new File(getDataFolder(), playerId.toString());
        if(configFile.exists()) {
            YamlConfiguration config = new YamlConfiguration();
            try { config.load(configFile); } catch (Exception e) {}
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) config.getList("ratpack");
            int c = 0;
            for(Map<String, Object> ic: itemList) {
                if(ic != null) {
                    ItemStack item = ItemStack.deserialize(ic);
                    inventory.setItem(c, item);
                }
                c++;
                if(c == inventorySize) break;
            }
        }
    }

    public void saveInventory(Player player) {
        UUID playerId = player.getUniqueId();
        if(!ratpackInventories.containsKey(playerId)) return;
        List<Map<String, Object>> itemList = new ArrayList<>();
        Inventory inventory = ratpackInventories.get(playerId);
        for(int c = 0; c < inventory.getSize(); c++) {
            if(inventory.getItem(c) == null) {
                itemList.add(null);
            }
            else {
                itemList.add(inventory.getItem(c).serialize());
            }
        }
        YamlConfiguration config = new YamlConfiguration();
        config.set("ratpack", itemList);
        try {config.save(new File(getDataFolder(), playerId.toString())); } catch (IOException e) {}
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event)
    {
        if(event.isCancelled()) return;
        if (!(event.getRecipe() instanceof ShapedRecipe)) return;
        if (event.getRecipe().getResult().getType() != Material.SADDLE) return;
        if (!(event.getView().getPlayer() instanceof Player)) return;
        Player player = (Player)event.getView().getPlayer();
        if (!player.hasPermission("cvranks.leatherworker")) event.setCancelled(true);
    }
    
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
            if(tool == null || (tool.containsEnchantment(Enchantment.SILK_TOUCH) && (target.getType() != Material.LOG && target.getType() != Material.LOG_2))) return;
            
            int rand = (int)Math.floor(100.0D * Math.random()) + 1;
            ItemStack drop = null;
            int chance = -1;
            String message = "";
            
            if(target.getType() == Material.DIAMOND_ORE) {
                if(permPs || permPsExtraOre) {
                    if(tool.getType() == Material.DIAMOND_PICKAXE) chance = 8;
                    drop = new ItemStack(Material.DIAMOND);
                    message = "§aYou found an extra diamond.";
                }
            }
            else if(target.getType() == Material.COAL_ORE) {
                if(permPs || permPsExtraOre) {
                    if(tool.getType() == Material.STONE_PICKAXE) chance = 4;
                    else if(tool.getType() == Material.IRON_PICKAXE) chance = 8;
                    else if(tool.getType() == Material.DIAMOND_PICKAXE) chance = 16;
                    else if(tool.getType() == Material.GOLD_PICKAXE) chance = 24;
                    drop = new ItemStack(Material.COAL);
                    message = "§aYou found extra coal.";
                }
            }
            else if(target.getType() == Material.QUARTZ_ORE) {
                if(permPs || permPsExtraOre) {
                    if(tool.getType() == Material.STONE_PICKAXE) chance = 4;
                    else if(tool.getType() == Material.IRON_PICKAXE) chance = 8;
                    else if(tool.getType() == Material.DIAMOND_PICKAXE) chance = 16;
                    else if(tool.getType() == Material.GOLD_PICKAXE) chance = 24;
                    drop = new ItemStack(Material.QUARTZ);
                    message = "§aYou found extra quartz";
                }
            } 
            else if(target.getType() == Material.GRAVEL) {
                if(permPs || permPsExtraFlint) {
                    if(tool.getType() == Material.STONE_SPADE) chance = 4;
                    else if(tool.getType() == Material.IRON_SPADE) chance = 8;
                    else if(tool.getType() == Material.DIAMOND_SPADE) chance = 16;
                    else if(tool.getType() == Material.GOLD_SPADE) chance = 24;
                    drop = new ItemStack(Material.FLINT);
                    message = "§aYou found extra flint";
                }
            }
            else if(target.getType() == Material.LOG || target.getType() == Material.LOG_2) {
                if(permPs || permPsExtraLogs) {
                    if (target.getType() == Material.LOG) {
                        if(tool.getType() == Material.STONE_AXE) chance = 4;
                        else if(tool.getType() == Material.IRON_AXE) chance = 8;
                        else if(tool.getType() == Material.DIAMOND_AXE) chance = 16;
                        else if(tool.getType() == Material.GOLD_AXE) chance = 24;
                        drop = new ItemStack(Material.LOG);
                        drop.setDurability((short) (target.getData() % 4));
                        message = "§aYou found extra wood";
                    } else {
                        if(tool.getType() == Material.STONE_AXE) chance = 4;
                        else if(tool.getType() == Material.IRON_AXE) chance = 8;
                        else if(tool.getType() == Material.DIAMOND_AXE) chance = 16;
                        else if(tool.getType() == Material.GOLD_AXE) chance = 24;
                        drop = new ItemStack(Material.LOG_2);
                        drop.setDurability((short) (target.getData() % 4));
                        message = "§aYou found extra wood";
                    }
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
}
